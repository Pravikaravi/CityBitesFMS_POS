package citybitesfms.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ModernButton extends JButton {
    private final Color baseColor;
    private final Color hoverColor;
    private final Color pressColor;
    private final int arc;
    private boolean hovered = false;
    private boolean pressed = false;

    public ModernButton(String text, Color base) {
        this(text, base, 8);
    }

    public ModernButton(String text, Color base, int arc) {
        super(text);
        this.baseColor  = base;
        this.arc        = arc;
        this.hoverColor = base.brighter();
        this.pressColor = base.darker();
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setFont(UITheme.F_BUTTON);
        setForeground(Color.WHITE);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
            @Override public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
            @Override public void mousePressed(MouseEvent e) { pressed = true;  repaint(); }
            @Override public void mouseReleased(MouseEvent e){ pressed = false; repaint(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color fill;
        if (!isEnabled())    fill = new Color(189, 189, 189);
        else if (pressed)    fill = pressColor;
        else if (hovered)    fill = hoverColor;
        else                 fill = baseColor;
        g2.setColor(fill);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
        g2.dispose();
        super.paintComponent(g);
    }
}
