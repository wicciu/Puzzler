package pl.brzezinski.witold.puzzler.tools;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.List;

import pl.brzezinski.witold.puzzler.R;
import pl.brzezinski.witold.puzzler.puzzle.PuzzleQuest;

public class ChoosePuzzleAdapter extends ArrayAdapter<PuzzleQuest> {


    Context context;
    CardView card_view;
    ImageView logo_image_view;
    PuzzleQuest puzzleQuest;

    public ChoosePuzzleAdapter(@NonNull Context context, @NonNull List<PuzzleQuest> objects) {
        super(context, 0, objects);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        puzzleQuest = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.drawable_item, parent, false);
        }
        card_view=(CardView)convertView.findViewById(R.id.flyin_tile_card_view);
        logo_image_view=(ImageView) convertView.findViewById(R.id.flying_tile_logo);
        logo_image_view.setImageDrawable(DrawableSwipeCards.getDrawableFromBuffer(context, puzzleQuest.getImageName()));
        return convertView;
    }
}
