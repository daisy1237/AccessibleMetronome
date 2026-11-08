package ui;

import javax.swing.*;
import java.awt.*;

public class VolumeIcon  implements Icon {
    private final boolean muted;
    private final Color color;

    public VolumeIcon(boolean muted, Color color) {
        this.muted = muted;
        this.color = color;
    }

    @Override
    public int getIconWidth() {
        return 28;
    }

    @Override
    public int getIconHeight() {
        return 28;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.translate(x, y);
        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );
        g2.setColor(color);
        g2.setStroke(new BasicStroke(
                2.5f, BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND
        ));
        Polygon p = new Polygon(
                new int[]{3,9,15,15,9,3},
                new int[]{11,11,6,22,17,17},
                6
        );
        g2.fillPolygon(p);
        if (muted) {
            g2.drawLine(19,10,26,18);
            g2.drawLine(26,10,19,18);
        } else {
            g2.drawArc(13,8,9,12,-65,130);
            g2.drawArc(11,4,16,20,-65,130);
        }
        g2.dispose();
    }
}
