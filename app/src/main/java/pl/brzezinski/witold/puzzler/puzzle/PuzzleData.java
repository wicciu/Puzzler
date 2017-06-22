package pl.brzezinski.witold.puzzler.puzzle;

import android.content.Context;

import java.util.ArrayList;

public class PuzzleData {

    private static ArrayList<PuzzleQuest> puzzleQuests = new ArrayList<>();

    public static ArrayList<PuzzleQuest> getPuzzleQuests() {
        return puzzleQuests;
    }

    public static void loadQuests(Context context) {
        puzzleQuests.add(new PuzzleQuest("zebra","zebra","1"));
        puzzleQuests.add(new PuzzleQuest("globe","globe","2"));
        puzzleQuests.add(new PuzzleQuest("landscape","landscape","3"));
        puzzleQuests.add(new PuzzleQuest("poland","poland","4"));
        puzzleQuests.add(new PuzzleQuest("europe","europe","5"));
        puzzleQuests.add(new PuzzleQuest("drum","drum","6"));
    }

    public synchronized static void removeFirstItem(Context context) {
        PuzzleQuest quest = puzzleQuests.get(0);
        puzzleQuests.remove(0);
        puzzleQuests.add(quest);
    }

    public synchronized static PuzzleQuest getQuestById(String id) {
        PuzzleQuest quest = null;
        for (PuzzleQuest q : puzzleQuests) {
            if (q.id.equals(id)) {
                quest = q;
                break;
            }
        }
        return quest;
    }
}
