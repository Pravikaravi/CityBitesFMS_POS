package citybitesfms.ui;

import citybitesfms.data.DataStore;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/**
 * Full-screen customer login screen.
 *
 * Left  — dark sidebar with brand identity matching the POS theme.
 * Right — light panel with a centred white login card.
 *
 * Validates credentials against DataStore and navigates to CustomerDashboard
 * on success. Demonstrates Event-Driven Programming via ActionListeners.
 *
 * @author NovaSoft Solutions (PVT) Ltd
 * @version 4.0
 */
public class CustomerLoginFrame extends JFrame {

    private JTextField     usernameField;
    private JPasswordField passwordField;
    private JLabel         errorLabel;

    public CustomerLoginFrame() {
        setTitle("City Bites — Customer Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 580));
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        buildUI();
        setVisible(true);
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.add(buildHeroPanel(), BorderLayout.WEST);
        root.add(buildFormPanel(), BorderLayout.CENTER);
        setContentPane(root);
    }

    // ─── Left dark panel ──────────────────────────────────────────────────────

    private JPanel buildHeroPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UITheme.SIDEBAR_BG);
        panel.setPreferredSize(new Dimension(380, 0));

        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));

        // Logo
        JPanel logo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int cx = getWidth() / 2, cy = getHeight() / 2;
                g2.setColor(UITheme.PRIMARY);
                g2.fillOval(cx - 32, cy - 32, 64, 64);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 24));
                FontMetrics fm = g2.getFontMetrics();
                String t = "CB";
                g2.drawString(t, cx - fm.stringWidth(t) / 2, cy + fm.getAscent() / 2 - 1);
            }
        };
        logo.setOpaque(false);
        logo.setMaximumSize(new Dimension(80, 80));
        logo.setPreferredSize(new Dimension(80, 80));
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel brand = new JLabel("City Bites");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 42));
        brand.setForeground(Color.WHITE);
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sys = new JLabel("Order Fresh, Eat Happy!");
        sys.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        sys.setForeground(UITheme.PRIMARY);
        sys.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel div = new JPanel();
        div.setBackground(UITheme.PRIMARY);
        div.setMaximumSize(new Dimension(50, 3));
        div.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel desc = new JLabel(
            "<html>Browse our full menu, choose your<br>favourite items and place your<br>order in seconds.</html>");
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        desc.setForeground(UITheme.SIDEBAR_ICON);
        desc.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] features = {
            "  Browse Full Food Menu by Category",
            "  Tap-to-add Shopping Experience",
            "  Instant Order Confirmation",
            "  Fresh & Affordable Meals Daily"
        };
        JPanel featList = new JPanel();
        featList.setOpaque(false);
        featList.setLayout(new BoxLayout(featList, BoxLayout.Y_AXIS));
        featList.setAlignmentX(Component.LEFT_ALIGNMENT);
        for (String f : features) {
            JLabel lbl = new JLabel(f);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lbl.setForeground(UITheme.SIDEBAR_ICON);
            lbl.setBorder(new EmptyBorder(4, 0, 4, 0));
            lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            featList.add(lbl);
        }

        inner.add(logo);
        inner.add(Box.createVerticalStrut(16));
        inner.add(brand);
        inner.add(Box.createVerticalStrut(4));
        inner.add(sys);
        inner.add(Box.createVerticalStrut(24));
        inner.add(div);
        inner.add(Box.createVerticalStrut(20));
        inner.add(desc);
        inner.add(Box.createVerticalStrut(28));
        inner.add(featList);

        panel.add(inner);
        return panel;
    }

    // ─── Right form panel ─────────────────────────────────────────────────────

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UITheme.BG);

        JPanel wrap = new JPanel();
        wrap.setBackground(UITheme.BG);
        wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));

        JPanel card = new JPanel();
        card.setBackground(UITheme.SURFACE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(400, 510));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER),
            new EmptyBorder(44, 48, 44, 48)));

        JLabel heading = new JLabel("Customer Sign In");
        heading.setFont(UITheme.F_TITLE);
        heading.setForeground(UITheme.TEXT_PRI);
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Sign in to browse and order your meals");
        sub.setFont(UITheme.F_BODY);
        sub.setForeground(UITheme.TEXT_SEC);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        usernameField = makeField();
        passwordField = new JPasswordField();
        styleField(passwordField);
        passwordField.addActionListener(e -> handleLogin());

        errorLabel = new JLabel(" ");
        errorLabel.setFont(UITheme.F_SMALL);
        errorLabel.setForeground(UITheme.DANGER);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        ModernButton loginBtn = new ModernButton("Sign In", UITheme.PRIMARY, 10);
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.addActionListener(e -> handleLogin());

        ModernButton signupBtn = new ModernButton("Create New Account", UITheme.SECONDARY, 10);
        signupBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        signupBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        signupBtn.addActionListener(e -> { dispose(); new CustomerSignupFrame(); });

        ModernButton backBtn = new ModernButton("← Back to Selection", new Color(100, 116, 139), 10);
        backBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        backBtn.addActionListener(e -> { dispose(); new LoginSelectionFrame(); });

        getRootPane().setDefaultButton(loginBtn);

        card.add(heading);
        card.add(Box.createVerticalStrut(6));
        card.add(sub);
        card.add(Box.createVerticalStrut(30));
        card.add(makeLabel("Username"));
        card.add(Box.createVerticalStrut(6));
        card.add(usernameField);
        card.add(Box.createVerticalStrut(18));
        card.add(makeLabel("Password"));
        card.add(Box.createVerticalStrut(6));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(10));
        card.add(errorLabel);
        card.add(Box.createVerticalStrut(16));
        card.add(loginBtn);
        card.add(Box.createVerticalStrut(10));
        card.add(signupBtn);
        card.add(Box.createVerticalStrut(8));
        card.add(backBtn);

        JLabel hint = new JLabel("Demo: customer1 / pass123   |   john / john123");
        hint.setFont(UITheme.F_SMALL);
        hint.setForeground(UITheme.TEXT_HINT);
        hint.setAlignmentX(Component.CENTER_ALIGNMENT);

        wrap.add(card);
        wrap.add(Box.createVerticalStrut(14));
        wrap.add(hint);

        panel.add(wrap);
        return panel;
    }

    // ─── Public API ───────────────────────────────────────────────────────────

    public void prefillUsername(String username) {
        usernameField.setText(username);
        passwordField.requestFocus();
    }

    // ─── Auth logic ───────────────────────────────────────────────────────────

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter both username and password.");
            return;
        }
        if (DataStore.getInstance().authenticateCustomer(username, password)) {
            dispose();
            new CustomerDashboard(username);
        } else {
            errorLabel.setText("Invalid username or password. Please try again.");
            passwordField.setText("");
            usernameField.requestFocus();
        }
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UITheme.F_LABEL);
        lbl.setForeground(UITheme.TEXT_SEC);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JTextField makeField() { JTextField f = new JTextField(); styleField(f); return f; }

    private void styleField(JTextField f) {
        f.setFont(UITheme.F_BODY);
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER),
            new EmptyBorder(8, 12, 8, 12)));
    }
}
