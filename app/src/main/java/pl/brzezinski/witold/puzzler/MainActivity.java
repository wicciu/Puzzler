package pl.brzezinski.witold.puzzler;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import pl.brzezinski.witold.puzzler.puzzle.PuzzleData;

public class MainActivity extends AppCompatActivity {

    ChoosePuzzleFragment choosePuzzleFragment;
    FragmentManager fragmentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        choosePuzzleFragment = new ChoosePuzzleFragment();
        startChooserFragment();
        PuzzleData.loadQuests(getApplicationContext());
    }


    public void startChooserFragment(){
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main_activity_container,choosePuzzleFragment).addToBackStack("fragment").commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
