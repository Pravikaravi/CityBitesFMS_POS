package citybitesfms.ui;

import citybitesfms.data.FoodDataStore;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/**
 * LoginFrame — full-screen login screen for City Bites.
 *
 * Layout: custom percentage-based split (40% brand | 60% form).
 * This guarantees correct proportions on any screen size / resolution.
 *
 * @author NovaSoft Solutions (PVT) Ltd
 * @version 3.0
 */
public class LoginFrame extends JFrame {

    // ── Credentials ───────────────────────────────────────────────────────────
    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PASS = "admin123";

    // ── Input components ──────────────────────────────────────────────────────
    private JTextField     usernameField;
    private JPasswordField passwordField;
    private JRadioButton   adminRadio;
    private JRadioButton   customerRadio;
    private JLabel         errorLabel;

    public LoginFrame() {
        setTitle("City Bites — Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 600));
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        buildUI();
        setVisible(true);
    }

    // =========================================================================
    // ROOT  —  custom 40 / 60 split
    // =========================================================================

    private void buildUI() {
        // Custom layout: left = 40 %, right = 60 %
        JPanel root = new JPanel(null) {
            @Override
            public void doLayout() {
                int w = getWidth(), h = getHeight();
                int leftW = (int)(w * 0.40);
                getComponent(0).setBounds(0,     0, leftW,     h);
                getComponent(1).setBounds(leftW, 0, w - leftW, h);
            }
        };
        root.add(buildBrandPanel());
        root.add(buildFormPanel());
        setContentPane(root);
    }

    // =========================================================================
    // LEFT  —  brand panel
    // =========================================================================

    private JPanel buildBrandPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(27, 94, 32));   // dark green

        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setMaximumSize(new Dimension(380, Integer.MAX_VALUE));

        // ── CB logo circle ────────────────────────────────────────────────
        JPanel logoBadge = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 200, 83));
                g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 32));
                FontMetrics fm = g2.getFontMetrics();
                String t = "CB";
                int cx = getWidth() / 2, cy = getHeight() / 2;
                g2.drawString(t, cx - fm.stringWidth(t) / 2, cy + fm.getAscent() / 2 - 2);
            }
        };
        logoBadge.setOpaque(false);
        logoBadge.setPreferredSize(new Dimension(90, 90));
        logoBadge.setMaximumSize(new Dimension(90, 90));
        logoBadge.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel brandName = new JLabel("City Bites");
        brandName.setFont(new Font("Segoe UI", Font.BOLD, 40));
        brandName.setForeground(Color.WHITE);
        brandName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sys = new JLabel("Food Management System");
        sys.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        sys.setForeground(new Color(165, 214, 167));
        sys.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel divider = new JPanel();
        divider.setBackground(new Color(0, 200, 83));
        divider.setMaximumSize(new Dimension(56, 4));
        divider.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tagline = new JLabel(
            "<html><div style='text-align:center;'>"
            + "Fresh &amp; Affordable Meals<br>delivered to your table"
            + "</div></html>");
        tagline.setFont(new Font("Segoe UI", Font.ITALIC, 15));
        tagline.setForeground(new Color(165, 214, 167));
        tagline.setHorizontalAlignment(SwingConstants.CENTER);
        tagline.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Feature list
        String[] features = {
            "✔  Full Menu Browsing by Category",
            "✔  Live Cart with Quantity Control",
            "✔  Instant Order Confirmation",
            "✔  Admin Menu Management"
        };
        JPanel featPanel = new JPanel();
        featPanel.setOpaque(false);
        featPanel.setLayout(new BoxLayout(featPanel, BoxLayout.Y_AXIS));
        featPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        for (String f : features) {
            JLabel lbl = new JLabel(f);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            lbl.setForeground(new Color(165, 214, 167));
            lbl.setBorder(new EmptyBorder(5, 0, 5, 0));
            lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            featPanel.add(lbl);
        }

        inner.add(logoBadge);
        inner.add(Box.createVerticalStrut(18));
        inner.add(brandName);
        inner.add(Box.createVerticalStrut(6));
        inner.add(sys);
        inner.add(Box.createVerticalStrut(24));
        inner.add(divider);
        inner.add(Box.createVerticalStrut(22));
        inner.add(tagline);
        inner.add(Box.createVerticalStrut(36));
        inner.add(featPanel);

        panel.add(inner);
        return panel;
    }

    // =========================================================================
    // RIGHT  —  form panel
    // =========================================================================

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 248, 245));

        // ── Card ──────────────────────────────────────────────────────────
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 235, 215), 1),
            new EmptyBorder(44, 52, 44, 52)));
        card.setPreferredSize(new Dimension(460, 570));
        card.setMaximumSize(new Dimension(460, 570));

        // ── Heading ────────────────────────────────────────────────────────
        JLabel heading = centeredLabel("Welcome Back!", new Font("Segoe UI", Font.BOLD, 30), new Color(22, 30, 22));
        JLabel sub     = centeredLabel("Sign in to continue", new Font("Segoe UI", Font.PLAIN, 15), new Color(100, 130, 110));

        // ── Role selector ──────────────────────────────────────────────────
        adminRadio    = styledRadio("Admin");
        customerRadio = styledRadio("Customer");
        customerRadio.setSelected(true);
        ButtonGroup bg = new ButtonGroup();
        bg.add(adminRadio);
        bg.add(customerRadio);

        JPanel roleBox = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(240, 250, 242));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(new Color(190, 225, 200));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                g2.dispose();
            }
        };
        roleBox.setOpaque(false);
        roleBox.setLayout(new FlowLayout(FlowLayout.CENTER, 28, 10));
        roleBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        roleBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        roleBox.add(adminRadio);
        roleBox.add(new JSeparator(JSeparator.VERTICAL) {{
            setPreferredSize(new Dimension(1, 24));
            setForeground(new Color(190, 225, 200));
        }});
        roleBox.add(customerRadio);

        // ── Username ────────────────────────────────────────────────────────
        usernameField = styledField();

        // ── Password ────────────────────────────────────────────────────────
        passwordField = new JPasswordField();
        styleTextField(passwordField);
        passwordField.addActionListener(e -> handleLogin());

        // ── Error label ─────────────────────────────────────────────────────
        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        errorLabel.setForeground(new Color(211, 47, 47));
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ── Login button ─────────────────────────────────────────────────────
        JButton loginBtn = makeButton("Login", new Color(0, 200, 83), Color.WHITE);
        loginBtn.addActionListener(e -> handleLogin());
        getRootPane().setDefaultButton(loginBtn);

        // ── Create Account button ─────────────────────────────────────────────
        JButton signupBtn = makeOutlineButton("Don't have an account?  Create one →");
        signupBtn.addActionListener(e -> { dispose(); new SignupFrame(); });
        signupBtn.setVisible(customerRadio.isSelected());
        customerRadio.addActionListener(e -> signupBtn.setVisible(true));
        adminRadio   .addActionListener(e -> signupBtn.setVisible(false));

        // ── Hint ──────────────────────────────────────────────────────────────
        JLabel hint = centeredLabel(
            "Admin: admin / admin123  |  Customer: user / user123",
            new Font("Segoe UI", Font.PLAIN, 11), new Color(180, 200, 185));

        // ── Assemble card ─────────────────────────────────────────────────────
        card.add(heading);
        card.add(Box.createVerticalStrut(5));
        card.add(sub);
        card.add(Box.createVerticalStrut(26));
        card.add(fieldLabel("Select Role"));
        card.add(Box.createVerticalStrut(8));
        card.add(roleBox);
        card.add(Box.createVerticalStrut(22));
        card.add(fieldLabel("Username"));
        card.add(Box.createVerticalStrut(7));
        card.add(usernameField);
        card.add(Box.createVerticalStrut(18));
        card.add(fieldLabel("Password"));
        card.add(Box.createVerticalStrut(7));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(8));
        card.add(errorLabel);
        card.add(Box.createVerticalStrut(14));
        card.add(loginBtn);
        card.add(Box.createVerticalStrut(10));
        card.add(signupBtn);
        card.add(Box.createVerticalStrut(16));
        card.add(hint);

        panel.add(card);
        return panel;
    }

    // =========================================================================
    // AUTH LOGIC
    // =========================================================================

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        errorLabel.setText(" ");

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter both username and password.");
            return;
        }
        boolean isAdmin   = adminRadio.isSelected();
        boolean validAdmin = isAdmin  && ADMIN_USER.equals(username) && ADMIN_PASS.equals(password);
        boolean validCust  = !isAdmin && FoodDataStore.authenticateCustomer(username, password);

        if (validAdmin) {
            dispose(); new AdminDashboard(username);
        } else if (validCust) {
            dispose(); new CustomerDashboard(username);
        } else {
            errorLabel.setText("Invalid username or password. Please try again.");
            passwordField.setText("");
            usernameField.requestFocus();
        }
    }

    // =========================================================================
    // PUBLIC API
    // =========================================================================

    /** Pre-fills username and selects Customer role — called after signup. */
    public void prefillCustomer(String username) {
        customerRadio.setSelected(true);
        usernameField.setText(username);
        passwordField.requestFocus();
    }

    /** Pre-selects the Admin role — called from the landing page. */
    public void selectAdmin() {
        adminRadio.setSelected(true);
        usernameField.requestFocus();
    }

    // =========================================================================
    // HELPERS
    // =========================================================================

    private JLabel centeredLabel(String text, Font font, Color color) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(font);
        lbl.setForeground(color);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }

    private JLabel fieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(new Color(60, 90, 65));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JTextField styledField() {
        JTextField f = new JTextField();
        styleTextField(f);
        return f;
    }

    private void styleTextField(JTextField f) {
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 225, 205), 1),
            new EmptyBorder(10, 14, 10, 14)));
        f.setBackground(new Color(248, 252, 249));
        f.setCaretColor(new Color(27, 94, 32));
        // Focus highlight
        f.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0, 200, 83), 2),
                    new EmptyBorder(9, 13, 9, 13)));
            }
            @Override public void focusLost(FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 225, 205), 1),
                    new EmptyBorder(10, 14, 10, 14)));
            }
        });
    }

    private JRadioButton styledRadio(String text) {
        JRadioButton rb = new JRadioButton(text);
        rb.setFont(new Font("Segoe UI", Font.BOLD, 14));
        rb.setForeground(new Color(22, 30, 22));
        rb.setOpaque(false);
        rb.setFocusPainted(false);
        rb.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return rb;
    }

    private JButton makeButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed()
                    ? new Color(0, 160, 65)
                    : getModel().isRollover() ? new Color(0, 185, 75) : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(fg);
        btn.setPreferredSize(new Dimension(0, 48));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton makeOutlineButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover()
                    ? new Color(230, 248, 235) : new Color(245, 252, 246));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(new Color(0, 200, 83));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(new Color(0, 150, 60));
        btn.setPreferredSize(new Dimension(0, 42));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
