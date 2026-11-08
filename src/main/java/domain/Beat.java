package domain;

public class Beat {

    private final int beatNumber;
    private final AccentType accentType;

    public Beat(int beatNumber, AccentType accentType) {
        this.beatNumber = beatNumber;
        this.accentType = accentType;
    }

    public int getBeatNumber() {
        return beatNumber;
    }

    public AccentType getAccentType() {
        return accentType;
    }

    public boolean isAccented() {
        return accentType != AccentType.NONE;
    }
}