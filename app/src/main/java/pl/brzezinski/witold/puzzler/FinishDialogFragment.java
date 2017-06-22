package pl.brzezinski.witold.puzzler;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import junit.framework.Assert;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.brzezinski.witold.puzzler.puzzle.PuzzleData;

public class FinishDialogFragment extends DialogFragment {

    @BindView(R.id.try_again_button)
    AppCompatButton tryAgainButton;
    @BindView(R.id.moves_counter_textview)
    TextView movesTextview;
    @BindView(R.id.time_textview)
    TextView timeTextView;
    @BindView(R.id.imageview_mini)
    ImageView imageView;

    PuzzleActivity activity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.popup_finish,container);
        activity = (PuzzleActivity)getActivity();
        ButterKnife.bind(this,view);
        setListeners();
        String imageName = getArguments().getString("puzzleQuestId");
        imageView.setImageResource(getDrawable(getActivity(), PuzzleData.getQuestById(imageName).getImageName()));
        movesTextview.setText(String.valueOf(getArguments().getInt("moves")));
        timeTextView.setText(parseTimeToText());
        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    private void setListeners() {
        tryAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                activity.restartPuzzle();
            }
        });
    }

    private int getDrawable(Context context, String name) {
        Assert.assertNotNull(context);
        Assert.assertNotNull(name);
        return context.getResources().getIdentifier(name,
                "drawable", context.getPackageName());
    }

    public String parseTimeToText(){
        String timeText = "";
        int time = getArguments().getInt("time");
        int secs = (int) (time / 1000);
        int mins = secs / 60;
        secs = secs % 60;
        int milliseconds = (int) (time % 1000);
        timeText = "" + mins + ":"
                + String.format("%02d", secs) + ":"
                + String.format("%03d", milliseconds);
        return timeText;
    }
}
