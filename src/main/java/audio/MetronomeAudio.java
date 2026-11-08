package audio;

import domain.Beat;

import javax.sound.sampled.*;

public class MetronomeAudio implements AutoCloseable {
    private static final int SAMPLE_RATE=44100;
    private static final int CLICK_DURATION_MS=40;

    private final Clip normalClip;
    private final Clip accentedClip;
    private final Clip secondaryClip;
    private boolean muted=false;

    public MetronomeAudio() throws LineUnavailableException {
        normalClip = createClip(800);
        secondaryClip = createClip(1000);
        accentedClip = createClip(1200);
    }

    private Clip createClip(int frequency) throws LineUnavailableException {
        int sampleCount=SAMPLE_RATE*CLICK_DURATION_MS/1000;
        byte[] audioData=new byte[sampleCount*2];

        for(int i=0;i<sampleCount;i++) {

            double time=(double) i / SAMPLE_RATE;
            double wave=Math.sin(2*Math.PI*time*frequency);
            double envelope=1.0-(double) i /sampleCount;
            short sample=(short) (wave * envelope * Short.MAX_VALUE * 0.5);

            audioData[i*2] = (byte) (sample & 0xFF);
            audioData[i*2+1] = (byte) ((sample>>8) & 0xFF);
        }

        AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);
        Clip clip = AudioSystem.getClip();

        try{
            clip.open(format, audioData, 0, audioData.length);
            return clip;
        } catch (LineUnavailableException | IllegalArgumentException e) {
            clip.close();
            throw e;
        }
    }

    public synchronized void playBeat(Beat beat) {
        if (muted) {
            return;
        }

        Clip clip;

        switch (beat.getAccentType()) {
            case PRIMARY:
                clip = accentedClip;
                break;
            case SECONDARY:
                clip = secondaryClip;
                break;
            case NONE:
            default:
                clip = normalClip;
                break;
        }

        clip.stop();
        clip.setFramePosition(0);
        clip.start();
    }

    @Override
    public synchronized void close(){
        normalClip.stop();
        secondaryClip.stop();
        accentedClip.stop();

        normalClip.close();
        secondaryClip.close();
        accentedClip.close();
    }

    public synchronized void setMuted(boolean muted) {
        this.muted = muted;
        if (muted) {
            normalClip.stop();
            secondaryClip.stop();
            accentedClip.stop();
        }
    }

    public synchronized boolean isMuted() {
        return muted;
    }
}
