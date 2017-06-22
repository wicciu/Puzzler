package pl.brzezinski.witold.puzzler;

import android.app.Fragment;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.MessageFormat;

import pl.brzezinski.witold.puzzler.puzzle.SlidePuzzle;
import pl.brzezinski.witold.puzzler.puzzle.SlidePuzzleView;


public class PuzzleFragment extends Fragment {

    private SlidePuzzleView slidePuzzleView;
    private SlidePuzzle slidePuzzle;
    private int puzzleWidth = 1;
    private int puzzleHeight = 1;
    private Uri imageUri;
    private boolean portrait;
    private BitmapFactory.Options bitmapOptions;
    public static final int puzzleSize = 4;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        slidePuzzle = new SlidePuzzle();
        int selected_image = this.getArguments().getInt("image_id");
        slidePuzzleView = new SlidePuzzleView(getActivity(), slidePuzzle, this);
        Uri path = Uri.parse("android.resource://pl.brzezinski.witold.puzzler/" + selected_image);
        bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inScaled = true;
        loadBitmap(path);
        setPuzzleSize(puzzleSize,true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        shuffle();
        View view = slidePuzzleView;
        return view;
    }

    public void onMove() {
        PuzzleActivity activity = (PuzzleActivity) getActivity();
        activity.onMove();
    }

    public void onPuzzleSolved() {
        PuzzleActivity puzzleLandingActivity = (PuzzleActivity) getActivity();
        puzzleLandingActivity.onPuzzleFinish();

    }

    protected void loadBitmap(Uri uri) {
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;

            InputStream imageStream = getActivity().getContentResolver().openInputStream(uri);
            BitmapFactory.decodeStream(imageStream, null, o);

            int targetWidth = slidePuzzleView.getTargetWidth();
            int targetHeight = slidePuzzleView.getTargetHeight();

            if (o.outWidth > o.outHeight && targetWidth < targetHeight) {
                int i = targetWidth;
                targetWidth = targetHeight;
                targetHeight = i;
            }
            if (targetWidth < o.outWidth || targetHeight < o.outHeight) {
                double widthRatio = (double) targetWidth / (double) o.outWidth;
                double heightRatio = (double) targetHeight / (double) o.outHeight;
                double ratio = Math.max(widthRatio, heightRatio);

                o.inSampleSize = (int) Math.pow(2, (int) Math.round(Math.log(ratio) / Math.log(0.5)));
            } else {
                o.inSampleSize = 1;
            }
            o.inScaled = false;
            o.inJustDecodeBounds = false;
            imageStream = getActivity().getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(imageStream, null, o);

            if (bitmap == null) {
                Toast.makeText(getActivity(), getString(R.string.error_image), Toast.LENGTH_LONG).show();
                return;
            }
            int rotate = 0;
            Cursor cursor = getActivity().getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.ORIENTATION}, null, null, null);
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        rotate = cursor.getInt(0);

                        if (rotate == -1) {
                            rotate = 0;
                        }
                    }
                } finally {
                    cursor.close();
                }
            }
            if (rotate != 0) {
                Matrix matrix = new Matrix();
                matrix.postRotate(rotate);

                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            }
            setBitmap(bitmap);
            imageUri = uri;
        } catch (FileNotFoundException ex) {
            Toast.makeText(getActivity(), MessageFormat.format(getString(R.string.error_image), ex.getMessage()), Toast.LENGTH_LONG).show();
            return;
        }
    }

    private void setBitmap(Bitmap bitmap) {
        portrait = bitmap.getWidth() < bitmap.getHeight();
        slidePuzzleView.setBitmap(bitmap);
        setPuzzleSize(Math.min(puzzleWidth, puzzleHeight), true);
        getActivity().setRequestedOrientation(portrait ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    protected void setPuzzleSize(int size, boolean scramble) {
        float ratio = getImageAspectRatio();
        if (ratio < 1) {
            ratio = 1f / ratio;
        }
        int newWidth;
        int newHeight;
        if (portrait) {
            newWidth = size;
            newHeight = (int) (size * ratio);
        } else {
            newWidth = (int) (size * ratio);
            newHeight = size;
        }
        if (scramble || newWidth != puzzleWidth || newHeight != puzzleHeight) {
            puzzleWidth = newWidth;
            puzzleHeight = newHeight;
            shuffle();
        }
    }


    private float getImageAspectRatio() {
        Bitmap bitmap = slidePuzzleView.getBitmap();
        if (bitmap == null) {
            return 1;
        }
        float width = bitmap.getWidth();
        float height = bitmap.getHeight();

        return width / height;
    }

    public void shuffle() {
        slidePuzzle.init(puzzleWidth, puzzleHeight);
        slidePuzzle.shuffle();
        slidePuzzleView.invalidate();
    }

    public void showHints(){
        slidePuzzleView.showNumbers= SlidePuzzleView.ShowNumbers.SOME;
        slidePuzzleView.invalidate();
    }

    public void hideHints(){
        slidePuzzleView.showNumbers= SlidePuzzleView.ShowNumbers.NONE;
        slidePuzzleView.invalidate();
    }

}
