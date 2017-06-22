package pl.brzezinski.witold.puzzler.puzzle;


public class PuzzleQuest {

    String puzzleName;
    String imageName;
    String id;


    public PuzzleQuest(String puzzleName, String imageName, String id) {
        this.puzzleName = puzzleName;
        this.imageName = imageName;
        this.id = id;
    }

    public PuzzleQuest() {
    }

    public String getPuzzleName() {
        return puzzleName;
    }

    public void setPuzzleName(String puzzleName) {
        this.puzzleName = puzzleName;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
