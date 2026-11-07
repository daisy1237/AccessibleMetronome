import audio.MetronomeAudio;
import domain.MetronomeSettings;
import service.MetronomeService;

public class Main {

    public static void main(String[] args) throws Exception {

        MetronomeSettings settings =
                new MetronomeSettings(100, 4);

        try (MetronomeAudio audio = new MetronomeAudio()) {

            MetronomeService service =
                    new MetronomeService(settings, beat -> {

                        System.out.println(
                                "Beat " + beat.getBeatNumber()
                                        + (beat.isAccented()
                                        ? " (accent)"
                                        : "")
                        );

                        audio.playBeat(beat);
                    });

            service.start();

            // Let the metronome play for 10 seconds
            Thread.sleep(10000);

            service.stop();
        }
    }
}
