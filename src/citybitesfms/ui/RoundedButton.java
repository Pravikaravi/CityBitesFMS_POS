package citybitesfms.ui;

import javax.swing.JButton;
import java.awt.*;

public class RoundedButton extends JButton {

    private final Color baseColor;
    private final int   arcRadius;

    public RoundedButton(String text, Color baseColor) {
        this(text, baseColor, 10);
    }

    public RoundedButton(String text, Color baseColor, int arcRadius) {
        super(text);
        this.baseColor  = baseColor;
        this.arcRadius  = arcRadius;

        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setForeground(Color.WHITE);
        setFont(new Font("Arial", Font.BOLD, 14));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color fill = baseColor;
        if (getModel().isPressed()) {
            fill = baseColor.darker().darker();
        } else if (getModel().isRollover()) {
            fill = baseColor.darker();
        }

        g2.setColor(fill);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arcRadius, arcRadius);
        g2.dispose();

        super.paintComponent(g);
    }
}
