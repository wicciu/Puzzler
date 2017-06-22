package pl.brzezinski.witold.puzzler;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.mikhaellopez.circularimageview.CircularImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.brzezinski.witold.puzzler.puzzle.PuzzleData;
import pl.brzezinski.witold.puzzler.puzzle.PuzzleQuest;
import pl.brzezinski.witold.puzzler.tools.ChoosePuzzleAdapter;
import pl.brzezinski.witold.puzzler.tools.DrawableSwipeCards;

public class ChoosePuzzleFragment extends Fragment {

    @BindView(R.id.swipe_adapter)
    public SwipeFlingAdapterView flingContainer;
    @BindView(R.id.button_ok)
    CircularImageView buttonOk;
    @BindView(R.id.button_reject)
    CircularImageView buttonReject;
    ChoosePuzzleAdapter choosePuzzleAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_puzzle_fragment, container, false);
        ButterKnife.bind(this,view);
        prepareTiles();
        setListeners();
        return view;
    }

    private void prepareTiles(){
        choosePuzzleAdapter = new ChoosePuzzleAdapter(getActivity(), PuzzleData.getPuzzleQuests());
        flingContainer.setAdapter(choosePuzzleAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                PuzzleData.removeFirstItem(getActivity());
                DrawableSwipeCards.removeFirstDrawableLogo();
                choosePuzzleAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object o) {
            }

            @Override
            public void onRightCardExit(Object o) {
                PuzzleQuest puzzleQuest = (PuzzleQuest) o;
                Intent intent = new Intent(getActivity(),PuzzleActivity.class);
                intent.putExtra("puzzleQuestId",puzzleQuest.getId());
                getActivity().startActivityForResult(intent,1);
            }

            @Override
            public void onAdapterAboutToEmpty(int i) {
                choosePuzzleAdapter.notifyDataSetChanged();
            }

            @Override
            public void onScroll(float v) {

            }
        });

    }

    private void setListeners() {
        buttonReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flingContainer.getTopCardListener().selectLeft();
            }
        });
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flingContainer.getTopCardListener().selectRight();
            }
        });

    }
}
