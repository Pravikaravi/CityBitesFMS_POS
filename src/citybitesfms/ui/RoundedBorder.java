package citybitesfms.ui;

import javax.swing.border.Border;
import java.awt.*;

/**
 * RoundedBorder — a custom Swing Border that draws a rounded rectangle outline.
 *
 * Used to give buttons, cards and text fields a modern, pill-style appearance
 * without relying on external libraries.
 *
 * @author NovaSoft Solutions (PVT) Ltd
 * @version 1.0
 */
public class RoundedBorder implements Border {

    /** Corner arc radius in pixels. */
    private final int   radius;

    /** Border line colour. */
    private final Color color;

    /**
     * Creates a RoundedBorder with the given radius and a default light-grey colour.
     *
     * @param radius Arc radius for the rounded corners
     */
    public RoundedBorder(int radius) {
        this(radius, new Color(210, 210, 210));
    }

    /**
     * Creates a RoundedBorder with a specific radius and colour.
     *
     * @param radius Arc radius for the rounded corners
     * @param color  Colour of the border line
     */
    public RoundedBorder(int radius, Color color) {
        this.radius = radius;
        this.color  = color;
    }

    /**
     * Paints the rounded rectangle border around the component.
     *
     * @param c      The component whose border is being painted
     * @param g      Graphics context
     * @param x      X origin
     * @param y      Y origin
     * @param width  Component width
     * @param height Component height
     */
    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        g2.dispose();
    }

    /**
     * Returns the insets (padding) introduced by this border.
     *
     * @param c The component
     * @return Insets equal to half the radius on each side
     */
    @Override
    public Insets getBorderInsets(Component c) {
        int pad = Math.max(4, radius / 2);
        return new Insets(pad, pad, pad, pad);
    }

    /**
     * Returns false because the rounded corners leave transparent corners.
     *
     * @return false
     */
    @Override
    public boolean isBorderOpaque() {
        return false;
    }
}
