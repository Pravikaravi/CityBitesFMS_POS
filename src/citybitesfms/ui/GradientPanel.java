package citybitesfms.ui;

import javax.swing.JPanel;
import java.awt.*;

// panel that draws a top-to-bottom gradient background
public class GradientPanel extends JPanel {

    private final Color topColor;
    private final Color bottomColor;

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
        // draw gradient from top colour to bottom colour
        g2.setPaint(new GradientPaint(0, 0, topColor, 0, getHeight(), bottomColor));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
    }
}
