package citybitesfms.ui;

import javax.swing.JPanel;
import java.awt.*;

/**
 * A JPanel that paints a vertical linear gradient as its background.
 * Used on the left branding side of login screens.
 *
 * @author NovaSoft Solutions (PVT) Ltd
 * @version 1.0
 */
public class GradientPanel extends JPanel {

    private final Color topColor;
    private final Color bottomColor;

    /**
     * @param topColor    Colour at the top of the gradient
     * @param bottomColor Colour at the bottom of the gradient
     */
    public GradientPanel(Color topColor, Color bottomColor) {
        this.topColor    = topColor;
        this.bottomColor = bottomColor;
        setOpaque(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setPaint(new GradientPaint(0, 0, topColor, 0, getHeight(), bottomColor));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
    }
}
