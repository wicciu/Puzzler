package pl.brzezinski.witold.puzzler.puzzle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;

import java.util.HashSet;
import java.util.Set;

import pl.brzezinski.witold.puzzler.PuzzleFragment;

public class SlidePuzzleView extends View {

    private static final int FRAME_SHRINK = 1;

    private static final long VIBRATE_DRAG = 5;
    private static final long VIBRATE_MATCH = 50;
    private static final long VIBRATE_SOLVED = 250;
    private static final int COLOR_SOLVED = 0xff86c1df;
    private static final int COLOR_ACTIVE = 0xff86c1df;
    private Bitmap bitmap;
    private Rect sourceRect;
    private RectF targetRect;
    private SlidePuzzle slidePuzzle;
    private int targetWidth;
    private int targetHeight;
    private int targetOffsetX;
    private int targetOffsetY;
    private int puzzleWidth;
    private int puzzleHeight;
    private int targetColumnWidth;
    private int targetRowHeight;
    private int sourceColumnWidth;
    private int sourceRowHeight;
    private int sourceWidth;
    private int sourceHeight;
    private Set<Integer> dragging = null;
    private int dragStartX;
    private int dragStartY;
    private int dragOffsetX;
    private int dragOffsetY;
    private int dragDirection;
    public ShowNumbers showNumbers = ShowNumbers.NONE;
    private Paint textPaint;
    private int canvasWidth;
    private int canvasHeight;
    private Paint framePaint;
    private boolean dragInTarget = false;
    private int[] tiles;
    private Paint tilePaint;
    private PuzzleFragment puzzleFragment;


    public SlidePuzzleView(Context context, SlidePuzzle slidePuzzle, PuzzleFragment puzzleFragment) {
        super(context);
        sourceRect = new Rect();
        targetRect = new RectF();
        this.puzzleFragment = puzzleFragment;
        this.slidePuzzle = slidePuzzle;

        tilePaint = new Paint();
        tilePaint.setAntiAlias(true);
        tilePaint.setDither(true);
        tilePaint.setFilterBitmap(true);

        textPaint = new Paint();
        textPaint.setARGB(0xff, 0xff, 0xff, 0xff);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(40);
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        textPaint.setShadowLayer(1, 2, 2, 0xff000000);

        framePaint = new Paint();
        framePaint.setARGB(0xff, 0x80, 0x80, 0x80);
        framePaint.setStyle(Paint.Style.STROKE);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        puzzleWidth = puzzleHeight = 0;
    }

    private void refreshDimensions(){
        targetWidth = canvasWidth;
        targetHeight = canvasHeight;

        sourceWidth = bitmap.getWidth();
        sourceHeight = bitmap.getHeight();

        double targetRatio = (double) targetWidth / (double) targetHeight;
        double sourceRatio = (double) sourceWidth / (double) sourceHeight;

        targetOffsetX = 0;
        targetOffsetY = 0;

        if (sourceRatio > targetRatio) {
            int newTargetHeight = (int) (targetWidth / sourceRatio);
            int delta = targetHeight - newTargetHeight;
            targetOffsetY = delta / 2;
            targetHeight = newTargetHeight;
        } else if (sourceRatio < targetRatio) {
            int newTargetWidth = (int) (targetHeight * sourceRatio);
            int delta = targetWidth - newTargetWidth;
            targetOffsetX = delta / 2;
            targetWidth = newTargetWidth;
        }

        puzzleWidth = slidePuzzle.getWidth();
        puzzleHeight = slidePuzzle.getHeight();

        targetColumnWidth = targetWidth / puzzleWidth;
        targetRowHeight = targetHeight / puzzleHeight;
        sourceColumnWidth = sourceWidth / puzzleWidth;
        sourceRowHeight = sourceHeight / puzzleHeight;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        if (slidePuzzle == null || bitmap == null) {
            return;
        }
        if (puzzleWidth != slidePuzzle.getWidth() || puzzleHeight != slidePuzzle.getHeight()) {
            refreshDimensions();
        }

        boolean solved = slidePuzzle.isSolved();
        canvas.drawColor(solved ? COLOR_SOLVED : COLOR_ACTIVE);

        int[] originalTiles = slidePuzzle.getTiles();

        if (tiles == null || tiles.length != originalTiles.length) {
            tiles = new int[originalTiles.length];
        }

        for (int i = 0; i < tiles.length; i++) {
            if (originalTiles[i] == originalTiles.length - 1) {
                continue;
            }
            if (dragInTarget && dragging.contains(i)) {
                tiles[i - SlidePuzzle.DIRECTION_X[dragDirection] - puzzleWidth * SlidePuzzle.DIRECTION_Y[dragDirection]] = originalTiles[i];
            } else {
                tiles[i] = originalTiles[i];
            }
        }

        int delta = !dragInTarget ? 0 : (SlidePuzzle.DIRECTION_X[dragDirection] + puzzleWidth * SlidePuzzle.DIRECTION_Y[dragDirection]) * dragging.size();
        int shownHandleLocation = slidePuzzle.getHandleLocation() + delta;
        tiles[shownHandleLocation] = tiles.length - 1;
        int emptyTile = tiles.length - 1;

        for (int i = 0; i < tiles.length; i++) {
            if (!solved && originalTiles[i] == emptyTile) {
                continue;
            }

            int targetColumn = slidePuzzle.getColumnAt(i);
            int targetRow = slidePuzzle.getRowAt(i);
            int sourceColumn = slidePuzzle.getColumnAt(originalTiles[i]);
            int sourceRow = slidePuzzle.getRowAt(originalTiles[i]);

            targetRect.left = targetOffsetX + targetColumnWidth * targetColumn;
            targetRect.top = targetOffsetY + targetRowHeight * targetRow;
            targetRect.right = targetColumn < puzzleWidth - 1 ? targetRect.left + targetColumnWidth : targetOffsetX + targetWidth;
            targetRect.bottom = targetRow < puzzleHeight - 1 ? targetRect.top + targetRowHeight : targetOffsetY + targetHeight;

            sourceRect.left = sourceColumnWidth * sourceColumn;
            sourceRect.top = sourceRowHeight * sourceRow;
            sourceRect.right = sourceColumn < puzzleWidth - 1 ? sourceRect.left + sourceColumnWidth : sourceWidth;
            sourceRect.bottom = sourceRow < puzzleHeight - 1 ? sourceRect.top + sourceRowHeight : sourceHeight;

            boolean isDragTile = dragging != null && dragging.contains(i);
            boolean matchLeft;
            boolean matchRight;
            boolean matchTop;
            boolean matchBottom;
            int di = i;

            if (dragInTarget && dragging.contains(i)) {
                di = di - SlidePuzzle.DIRECTION_X[dragDirection] - puzzleWidth * SlidePuzzle.DIRECTION_Y[dragDirection];
            }
            if (di == tiles[di]) {
                matchLeft = matchRight = matchTop = matchBottom = true;
            } else {
                matchLeft = (di - 1) >= 0 && di % puzzleWidth > 0 && tiles[di] % puzzleWidth > 0 && tiles[di - 1] == tiles[di] - 1;
                matchRight = tiles[di] + 1 < tiles.length - 1 && (di + 1) % puzzleWidth > 0 && (tiles[di] + 1) % puzzleWidth > 0 && (di + 1) < tiles.length && (di + 1) % puzzleWidth > 0 && tiles[di + 1] == tiles[di] + 1;
                matchTop = (di - puzzleWidth) >= 0 && tiles[di - puzzleWidth] == tiles[di] - puzzleWidth;
                matchBottom = tiles[di] + puzzleWidth < tiles.length - 1 && (di + puzzleWidth) < tiles.length && tiles[di + puzzleWidth] == tiles[di] + puzzleWidth;
            }
            if (!matchLeft) {
                sourceRect.left += FRAME_SHRINK;
                targetRect.left += FRAME_SHRINK;
            }
            if (!matchRight) {
                sourceRect.right -= FRAME_SHRINK;
                targetRect.right -= FRAME_SHRINK;
            }
            if (!matchTop) {
                sourceRect.top += FRAME_SHRINK;
                targetRect.top += FRAME_SHRINK;
            }
            if (!matchBottom) {
                sourceRect.bottom -= FRAME_SHRINK;
                targetRect.bottom -= FRAME_SHRINK;
            }
            if (isDragTile) {
                targetRect.left += dragOffsetX;
                targetRect.right += dragOffsetX;
                targetRect.top += dragOffsetY;
                targetRect.bottom += dragOffsetY;
            }
            canvas.drawBitmap(bitmap, sourceRect, targetRect, tilePaint);

            if (!solved && (showNumbers == ShowNumbers.ALL || (showNumbers == ShowNumbers.SOME && di != tiles[di]))) {
                drawHints(canvas,originalTiles,i);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (slidePuzzle == null || bitmap == null) {
            return false;
        }

        if (slidePuzzle.isSolved()) {
            return false;
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            return startDrag(event);
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            return updateDrag(event);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            return finishDrag(event);
        } else {
            return false;
        }
    }

    private boolean finishDrag(MotionEvent event) {
        if (dragging == null) {
            return false;
        }
        updateDrag(event);

        if (dragInTarget) {
            puzzleFragment.onMove();
            doMove(dragDirection, dragging.size());
        } else {
            vibrate(VIBRATE_DRAG);
        }

        dragInTarget = false;
        dragging = null;
        invalidate();

        return true;
    }

    private boolean startDrag(MotionEvent event) {
        if (dragging != null) {
            return false;
        }

        if(targetColumnWidth==0||targetRowHeight==0){
            return false;
        }

        int x = ((int) event.getX() - targetOffsetX) / targetColumnWidth;
        int y = ((int) event.getY() - targetOffsetY) / targetRowHeight;

        if (x < 0 || x >= puzzleWidth || y < 0 || y >= puzzleHeight) {
            return false;
        }

        int direction = slidePuzzle.getDirection(x + puzzleWidth * y);

        if (direction >= 0) {
            dragging = new HashSet<Integer>();

            while (x + puzzleWidth * y != slidePuzzle.getHandleLocation()) {
                dragging.add(x + puzzleWidth * y);
                dragStartX = (int) event.getX();
                dragStartY = (int) event.getY();
                dragOffsetX = 0;
                dragOffsetY = 0;
                dragDirection = direction;

                x -= SlidePuzzle.DIRECTION_X[direction];
                y -= SlidePuzzle.DIRECTION_Y[direction];
            }
        }

        dragInTarget = false;
        vibrate(VIBRATE_DRAG);

        return true;
    }

    private boolean updateDrag(MotionEvent event) {
        if (dragging == null) {
            return false;
        }

        int directionX = SlidePuzzle.DIRECTION_X[dragDirection] * -1;
        int directionY = SlidePuzzle.DIRECTION_Y[dragDirection] * -1;

        if (directionX != 0) {
            dragOffsetX = (int) event.getX() - dragStartX;

            if (Math.signum(dragOffsetX) != directionX) {
                dragOffsetX = 0;
            } else if (Math.abs(dragOffsetX) > targetColumnWidth) {
                dragOffsetX = directionX * targetColumnWidth;
            }
        }

        if (directionY != 0) {
            dragOffsetY = (int) event.getY() - dragStartY;

            if (Math.signum(dragOffsetY) != directionY) {
                dragOffsetY = 0;
            } else if (Math.abs(dragOffsetY) > targetRowHeight) {
                dragOffsetY = directionY * targetRowHeight;
            }
        }

        dragInTarget = Math.abs(dragOffsetX) > targetColumnWidth / 2 ||
                Math.abs(dragOffsetY) > targetRowHeight / 2;

        invalidate();

        return true;
    }

    private void doMove(int dragDirection, int count) {
        if (slidePuzzle.moveTile(dragDirection, count)) {
            vibrate(slidePuzzle.isSolved() ? VIBRATE_SOLVED : VIBRATE_MATCH);
        } else {
            vibrate(VIBRATE_DRAG);
        }
        invalidate();
        if (slidePuzzle.isSolved()) {
            puzzleFragment.onPuzzleSolved();
        }
    }

    private void vibrate(long d) {
        Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        if (v != null) {
            v.vibrate(d);
        }
    }

    public int getTargetWidth() {
        return targetWidth;
    }

    public int getTargetHeight() {
        return targetHeight;
    }

    public enum ShowNumbers {NONE, SOME, ALL}

    public ShowNumbers getShowNumbers() {
        return showNumbers;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int canvasWidth = MeasureSpec.getSize(widthMeasureSpec);
        int canvasHeight = MeasureSpec.getSize(heightMeasureSpec);

        if (this.canvasWidth != canvasWidth || this.canvasHeight != canvasHeight) {
            this.canvasWidth = canvasWidth;
            this.canvasHeight = canvasHeight;
            puzzleWidth = 0;
            puzzleHeight = 0;
        }
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        puzzleWidth = 0;
        puzzleHeight = 0;
    }

    public void drawHints(Canvas canvas,int[] originalTiles,int i){
        canvas.drawText(String.valueOf(originalTiles[i] + 1), (targetRect.left + targetRect.right) / 2, (targetRect.top + targetRect.bottom) / 2 - (textPaint.descent() + textPaint.ascent()) / 2, textPaint);
    }


}
