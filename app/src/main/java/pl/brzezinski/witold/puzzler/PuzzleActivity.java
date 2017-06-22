package pl.brzezinski.witold.puzzler;

import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import junit.framework.Assert;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.brzezinski.witold.puzzler.puzzle.PuzzleData;
import pl.brzezinski.witold.puzzler.puzzle.PuzzleQuest;


public class PuzzleActivity extends AppCompatActivity {

    PuzzleFragment puzzleFragment = new PuzzleFragment();
    FragmentManager fragmentManager = getFragmentManager();
    @BindView(R.id.restart_button)
    AppCompatButton restartButton;
    @BindView(R.id.timer_textview)
    TextView textViewTimer;
    @BindView(R.id.times_moved_textview)
    TextView textViewTimesMoved;
    @BindView(R.id.hints_toggle_button)
    ToggleButton hintsToggleButton;
    PuzzleQuest quest;
    int movesNumber = 0;
    int solveTime;

    private long startTime = 0L;
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;
    private Handler customHandler = new Handler();
    FinishDialogFragment finishDialogFragment = new FinishDialogFragment();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);
        String questId = getIntent().getExtras().getString("puzzleQuestId");
        quest = PuzzleData.getQuestById(questId);
        ButterKnife.bind(this);
        showPuzzleImage();
        runNewTimer();
        setListeners();
    }

    private void showPuzzleImage() {
        Bundle bundle = new Bundle();
        bundle.putInt("image_id",getPuzzleImage(getApplicationContext(),quest.getImageName()));
        puzzleFragment.setArguments(bundle);
        fragmentManager.beginTransaction().replace(R.id.puzzle_container, puzzleFragment).commit();
    }

    private void setListeners(){
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restartPuzzle();
            }
        });
        hintsToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    puzzleFragment.showHints();
                } else {
                    puzzleFragment.hideHints();
                }
            }
        });

    }

    public void onMove() {
        movesNumber++;
        textViewTimesMoved.setText(Integer.toString(movesNumber));
    }

    public void restartPuzzle() {
        puzzleFragment.shuffle();
        textViewTimer.setText("0:00:000");
        restartMovesCounter();
        runNewTimer();
    }

    private void restartMovesCounter() {
        movesNumber = 0;
        textViewTimesMoved.setText(Integer.toString(movesNumber));
    }

    private void runNewTimer() {
        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 0);
    }

    private Runnable updateTimerThread = new Runnable() {

        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;
            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            int milliseconds = (int) (updatedTime % 1000);
            textViewTimer.setText("" + mins + ":"
                    + String.format("%02d", secs) + ":"
                    + String.format("%03d", milliseconds));
            customHandler.postDelayed(this, 0);
        }

    };

    public void showNumbersOnPuzzles(){
        puzzleFragment.showHints();
    }

    public void onPuzzleFinish() {
        customHandler.removeCallbacks(updateTimerThread);
        solveTime =(int) updatedTime;
        showFinishPopup();
    }

    private void showFinishPopup() {
        customHandler.removeCallbacks(updateTimerThread);
        finishDialogFragment.show(getFragmentManager(), "");
        Bundle bundle = new Bundle();
        bundle.putString("puzzleQuestId",quest.getId());
        bundle.putInt("time", solveTime);
        bundle.putInt("moves",movesNumber);
        finishDialogFragment.setArguments(bundle);
    }

    public int getPuzzleImage(Context context, String name) {
        Assert.assertNotNull(context);
        Assert.assertNotNull(name);
        return context.getResources().getIdentifier(name, "drawable", context.getPackageName());
    }


}
