package citybitesfms.ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Full-screen landing page — the application's entry screen.
 *
 * Styled to match the City Bites POS theme: dark sidebar on the left with
 * the brand logo, and a light content area on the right with two role cards.
 *
 * Event-Driven Programming: button clicks fire ActionEvents that navigate
 * to the relevant login screen.
 *
 * @author NovaSoft Solutions (PVT) Ltd
 * @version 4.0
 */
public class LoginSelectionFrame extends JFrame {

    public LoginSelectionFrame() {
        setTitle("City Bites — Food Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 620));
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        buildUI();
        setVisible(true);
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());

        root.add(buildBrandPanel(), BorderLayout.WEST);
        root.add(buildCardArea(),   BorderLayout.CENTER);
        root.add(buildFooter(),     BorderLayout.SOUTH);

        setContentPane(root);
    }

    // ─── Left brand panel ─────────────────────────────────────────────────────

    private JPanel buildBrandPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UITheme.SIDEBAR_BG);
        panel.setPreferredSize(new Dimension(340, 0));

        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));

        // Logo circle
        JPanel logo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int cx = getWidth() / 2, cy = getHeight() / 2;
                g2.setColor(UITheme.PRIMARY);
                g2.fillOval(cx - 36, cy - 36, 72, 72);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 28));
                FontMetrics fm = g2.getFontMetrics();
                String t = "CB";
                g2.drawString(t, cx - fm.stringWidth(t) / 2, cy + fm.getAscent() / 2 - 2);
            }
        };
        logo.setOpaque(false);
        logo.setPreferredSize(new Dimension(100, 100));
        logo.setMaximumSize(new Dimension(100, 100));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel brand = new JLabel("City Bites");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 36));
        brand.setForeground(Color.WHITE);
        brand.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Food Management System");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sub.setForeground(UITheme.SIDEBAR_ICON);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel div = new JPanel();
        div.setBackground(UITheme.PRIMARY);
        div.setMaximumSize(new Dimension(50, 3));
        div.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tagline = new JLabel("<html><center>Fresh & Affordable Meals<br>in Jaffna, Sri Lanka</center></html>");
        tagline.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        tagline.setForeground(UITheme.SIDEBAR_ICON);
        tagline.setHorizontalAlignment(SwingConstants.CENTER);
        tagline.setAlignmentX(Component.CENTER_ALIGNMENT);

        inner.add(logo);
        inner.add(Box.createVerticalStrut(12));
        inner.add(brand);
        inner.add(Box.createVerticalStrut(6));
        inner.add(sub);
        inner.add(Box.createVerticalStrut(20));
        inner.add(div);
        inner.add(Box.createVerticalStrut(18));
        inner.add(tagline);

        panel.add(inner);
        return panel;
    }

    // ─── Right: role selection cards ──────────────────────────────────────────

    private JPanel buildCardArea() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(UITheme.BG);

        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));

        JLabel choose = new JLabel("Welcome! Select your role to continue");
        choose.setFont(new Font("Segoe UI", Font.BOLD, 20));
        choose.setForeground(UITheme.TEXT_PRI);
        choose.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel hint = new JLabel("Choose how you would like to access City Bites POS");
        hint.setFont(UITheme.F_BODY);
        hint.setForeground(UITheme.TEXT_SEC);
        hint.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel cards = new JPanel(new GridLayout(1, 2, 24, 0));
        cards.setOpaque(false);
        cards.setMaximumSize(new Dimension(820, 320));
        cards.setAlignmentX(Component.CENTER_ALIGNMENT);

        cards.add(buildRoleCard(
            "Admin Portal",
            "⊞",
            "<html><center>Manage the food menu,<br>monitor orders and<br>control the system.</center></html>",
            "Admin Sign In",
            UITheme.DANGER,
            "admin / admin123",
            e -> { dispose(); new AdminLoginFrame(); }
        ));
        cards.add(buildRoleCard(
            "Customer Portal",
            "⌂",
            "<html><center>Browse the menu, add<br>items to your order<br>and place it instantly.</center></html>",
            "Customer Sign In",
            UITheme.PRIMARY,
            "customer1 / pass123",
            e -> { dispose(); new CustomerLoginFrame(); }
        ));

        inner.add(choose);
        inner.add(Box.createVerticalStrut(8));
        inner.add(hint);
        inner.add(Box.createVerticalStrut(32));
        inner.add(cards);

        outer.add(inner);
        return outer;
    }

    private JPanel buildRoleCard(String title, String icon, String desc,
            String btnText, Color accent, String demoHint, ActionListener action) {

        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.SURFACE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(UITheme.BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Accent top bar
        JPanel accentBar = new JPanel();
        accentBar.setBackground(accent);
        accentBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 4));

        // Icon circle
        JLabel iconLbl = new JLabel(icon, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 30));
                g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                super.paintComponent(g);
            }
        };
        iconLbl.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        iconLbl.setForeground(accent);
        iconLbl.setPreferredSize(new Dimension(64, 64));
        iconLbl.setMaximumSize(new Dimension(64, 64));
        iconLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLbl.setForeground(UITheme.TEXT_PRI);
        titleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLbl.setBorder(new EmptyBorder(14, 0, 8, 0));

        JLabel descLbl = new JLabel(desc);
        descLbl.setFont(UITheme.F_BODY);
        descLbl.setForeground(UITheme.TEXT_SEC);
        descLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        descLbl.setHorizontalAlignment(SwingConstants.CENTER);

        ModernButton btn = new ModernButton(btnText, accent, 10);
        btn.setFont(UITheme.F_BUTTON);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.addActionListener(action);

        JLabel demoLbl = new JLabel("Demo: " + demoHint);
        demoLbl.setFont(UITheme.F_SMALL);
        demoLbl.setForeground(UITheme.TEXT_HINT);
        demoLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(accentBar);
        card.add(Box.createVerticalStrut(16));
        card.add(iconLbl);
        card.add(titleLbl);
        card.add(descLbl);
        card.add(Box.createVerticalStrut(22));
        card.add(btn);
        card.add(Box.createVerticalStrut(10));
        card.add(demoLbl);
        return card;
    }

    // ─── Footer ───────────────────────────────────────────────────────────────

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(UITheme.SIDEBAR_BG);
        footer.setBorder(new EmptyBorder(10, 0, 12, 0));
        JLabel copy = new JLabel("© 2025 NovaSoft Solutions (PVT) Ltd  |  City Bites POS  |  Jaffna, Sri Lanka");
        copy.setFont(UITheme.F_SMALL);
        copy.setForeground(UITheme.SIDEBAR_ICON);
        footer.add(copy);
        return footer;
    }
}
