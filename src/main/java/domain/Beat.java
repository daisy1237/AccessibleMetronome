package domain;

public class Beat {
    private final int beatNumber;
    private final boolean accented;

    public Beat(int beatNumber, boolean accented) {
        if(beatNumber < 1) {
            throw new IllegalArgumentException("Beat number must be greater than 0.");
        }
        this.beatNumber = beatNumber;
        this.accented = accented;
    }

    public int getBeatNumber() {
        return beatNumber;
    }
    public boolean isAccented() {
        return accented;
    }
}
