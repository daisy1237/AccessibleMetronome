
package ui;

import java.awt.Color;
import java.awt.Font;

public final class Theme {

    public static final Color BACKGROUND =
            new Color(247, 250, 253);
    public static final Color SURFACE =
            new Color(255, 255, 255);
    public static final Color PRIMARY =
            new Color(40, 93, 131);
    public static final Color ACCENT =
            new Color(40, 93, 131);
    public static final Color CONTROL_BACKGROUND =
            new Color(229, 240, 248);
    public static final Color TEXT =
            new Color(32, 44, 57);
    public static final Color TEXT_SECONDARY =
            new Color(72, 91, 107);
    public static final Color BUTTON_BACKGROUND = PRIMARY;
    public static final Color BUTTON_TEXT = Color.WHITE;
    public static final Color STOP_BACKGROUND =
            new Color(142, 57, 52);
    public static final Color BEAT_ACTIVE = PRIMARY;
    public static final Color BEAT_INACTIVE =
            new Color(244, 36, 72);
    public static final Color FOCUS =
            new Color(18, 48, 75);

    public static final Font TITLE_FONT =
            new Font("SansSerif", Font.BOLD, 22);
    public static final Font BPM_FONT =
            new Font("SansSerif", Font.BOLD, 48);
    public static final Font BODY_FONT =
            new Font("SansSerif", Font.PLAIN, 16);
    public static final Font BUTTON_FONT =
            new Font("SansSerif", Font.BOLD, 18);

    private Theme() {
    }
}
