package domain;

public class MetronomeSettings {
    public static final int MIN_BPM=40;
    public static final int MAX_BPM=220;
    public static final int DEFAULT_BPM=100;
    public static final int DEFAULT_BEATS_PER_MEASURE =4;

    private int bpm;
    private int beatsPerMeasure; // for now supports only 2/4, 3/4 or 4/4 time signatures
    private int beatUnit=4;

    public MetronomeSettings() {
        this(DEFAULT_BPM, DEFAULT_BEATS_PER_MEASURE);
    }

    public MetronomeSettings(int bpm, int timeSignature) {
        setBpm(bpm);
        setBeatsPerMeasure(timeSignature);
    }

    public int getBpm() {
        return bpm;
    }

    public void setBpm(int bpm) {
        if (bpm < MIN_BPM || bpm > MAX_BPM) {
            throw new IllegalArgumentException("Invalid bpm value: must be between 40 and 220.");
        }
        this.bpm = bpm;
    }

    public int getBeatsPerMeasure() {
        return beatsPerMeasure;
    }

    public void setBeatsPerMeasure(int beatsPerMeasure) {
        if(beatsPerMeasure <2 || beatsPerMeasure >12) {
            throw new IllegalArgumentException("Invalid beats per measure value: must be between 2 and 12.");
        }
        this.beatsPerMeasure = beatsPerMeasure;
    }

    public int getBeatUnit() {
        return beatUnit;
    }

    public void setBeatUnit(int beatUnit) {
        if(beatUnit!=4 && beatUnit!=8) {
            throw new IllegalArgumentException("Invalid beats unit value: must be 4 or 8.");
        }
        this.beatUnit = beatUnit;
    }

}
