package service;

import domain.AccentType;
import domain.Beat;
import domain.MetronomeSettings;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class MetronomeService {
    private final MetronomeSettings settings;
    private final Consumer<Beat> beatListener;

    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> scheduledTask;

    private int currentBeat=0;
    private boolean running=false;

    public MetronomeService(MetronomeSettings settings, Consumer<Beat> beatListener) {
        this.settings = settings;
        this.beatListener = beatListener;
    }

    public synchronized void start() {
        if(running) { return; }
        running=true;
        currentBeat=0;
        scheduler= Executors.newSingleThreadScheduledExecutor();
        long interval=Math.round(60000.0/settings.getBpm());
        scheduledTask=scheduler.scheduleAtFixedRate(this::generateBeat,0,interval, TimeUnit.MILLISECONDS);
    }

    private synchronized void generateBeat() {
        if(!running) { return; }
        currentBeat++;
        if(currentBeat>settings.getBeatsPerMeasure()) { currentBeat=1;}
        AccentType accentType = getAccentType(currentBeat);
        Beat beat = new Beat(currentBeat, accentType);
        beatListener.accept(beat);
    }

    public synchronized void stop() {
        if(!running) { return; }
        running=false;
        if(scheduledTask!=null) {
            scheduledTask.cancel(true);
            scheduledTask=null;
        }
        if(scheduler!=null) {
            scheduler.shutdown();
            scheduler=null;
        }
        currentBeat=0;
    }

    public synchronized boolean isRunning() {
        return running;
    }

    private AccentType getAccentType(int beatNumber) {
        if (beatNumber == 1) { return AccentType.PRIMARY; }

        int beats = settings.getBeatsPerMeasure();
        int unit = settings.getBeatUnit();

        if (unit == 4 && beats == 5) {
            return beatNumber == 4 ? AccentType.SECONDARY : AccentType.NONE;
        }

        if (unit == 8) {
            if (beats == 6 || beats == 9 || beats == 12) {
                return (beatNumber - 1) % 3 == 0 ? AccentType.SECONDARY : AccentType.NONE;
            }
            if (beats == 7) {
                return (beatNumber == 3 || beatNumber == 5) ? AccentType.SECONDARY : AccentType.NONE;
            }
        }

        return AccentType.NONE;
    }

    public synchronized void resetBeat() {
        currentBeat = 0;
    }
}
