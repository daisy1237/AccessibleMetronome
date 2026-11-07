
package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RoundedButton extends JButton {
    private Color backgroundColor;
    private final Color textColor;
    private static final int RADIUS = 18;

    public RoundedButton(String text, Color backgroundColor, Color textColor) {
        super(text);
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;

        setFont(Theme.BUTTON_FONT);
        setForeground(textColor);
        setBackground(backgroundColor);

        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);

        setBorder(new EmptyBorder(12, 22, 12, 22));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                repaint();
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color fill = backgroundColor;

        if (getModel().isPressed()) {
            fill = backgroundColor.darker();
        } else if (getModel().isRollover()) {
            fill = backgroundColor.brighter();
        }

        g2.setColor(fill);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), RADIUS, RADIUS);

        if (isFocusOwner()) {
            g2.setColor(Theme.FOCUS);
            g2.setStroke(new BasicStroke(3));
            g2.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, RADIUS, RADIUS);
        }

        g2.dispose();
        super.paintComponent(g);
    }

    public void setButtonBackground(Color color) {
        this.backgroundColor = color;
        setBackground(color);
        repaint();
    }
}
