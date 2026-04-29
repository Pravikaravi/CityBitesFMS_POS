package citybitesfms.ui;

import javax.swing.JButton;
import java.awt.*;

/**
 * A JButton with a rounded-rectangle background — giving a modern,
 * website-style appearance. Includes a subtle hover colour shift.
 *
 * Usage:
 *   RoundedButton btn = new RoundedButton("Login", new Color(230, 81, 0));
 *
 * @author NovaSoft Solutions (PVT) Ltd
 * @version 1.0
 */
public class RoundedButton extends JButton {

    private final Color baseColor;
    private final int   arcRadius;

    /**
     * @param text      Button label
     * @param baseColor Fill colour (hover becomes slightly darker)
     */
    public RoundedButton(String text, Color baseColor) {
        this(text, baseColor, 10);
    }

    /**
     * @param text      Button label
     * @param baseColor Fill colour
     * @param arcRadius Corner arc radius in pixels (higher = more rounded)
     */
    public RoundedButton(String text, Color baseColor, int arcRadius) {
        super(text);
        this.baseColor  = baseColor;
        this.arcRadius  = arcRadius;

        // Let paintComponent handle all drawing
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

        // Shift shade on hover / press
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
