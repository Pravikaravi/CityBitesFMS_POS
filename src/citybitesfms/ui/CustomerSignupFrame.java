package citybitesfms.ui;

import citybitesfms.data.DataStore;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/**
 * Full-screen customer registration screen.
 *
 * Left  — dark sidebar matching the POS brand theme.
 * Right — light panel with a centred white signup card.
 *
 * Validates input, checks for duplicate usernames via DataStore, then
 * registers the account and navigates to CustomerLoginFrame with the
 * new username pre-filled.
 *
 * Demonstrates Event-Driven Programming via ActionListeners on the
 * Sign Up button and the confirm-password field (Enter key to submit).
 *
 * @author NovaSoft Solutions (PVT) Ltd
 * @version 4.0
 */
public class CustomerSignupFrame extends JFrame {

    private JTextField     usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmField;
    private JLabel         errorLabel;

    public CustomerSignupFrame() {
        setTitle("City Bites — Create Account");
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

        JLabel brand = new JLabel("Join City Bites");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 36));
        brand.setForeground(Color.WHITE);
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sys = new JLabel("Create your free account today");
        sys.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        sys.setForeground(UITheme.PRIMARY);
        sys.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel div = new JPanel();
        div.setBackground(UITheme.PRIMARY);
        div.setMaximumSize(new Dimension(50, 3));
        div.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel desc = new JLabel(
            "<html>Sign up in seconds and start ordering<br>fresh meals from City Bites.<br>Your account is free forever.</html>");
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        desc.setForeground(UITheme.SIDEBAR_ICON);
        desc.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] perks = {
            "  Free account — no subscription",
            "  Access the full POS menu instantly",
            "  Place orders at any time",
            "  Secure login every session"
        };
        JPanel perkList = new JPanel();
        perkList.setOpaque(false);
        perkList.setLayout(new BoxLayout(perkList, BoxLayout.Y_AXIS));
        perkList.setAlignmentX(Component.LEFT_ALIGNMENT);
        for (String p : perks) {
            JLabel lbl = new JLabel(p);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lbl.setForeground(UITheme.SIDEBAR_ICON);
            lbl.setBorder(new EmptyBorder(4, 0, 4, 0));
            lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            perkList.add(lbl);
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
        inner.add(perkList);

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
        card.setPreferredSize(new Dimension(400, 530));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER),
            new EmptyBorder(44, 48, 44, 48)));

        JLabel heading = new JLabel("Create Account");
        heading.setFont(UITheme.F_TITLE);
        heading.setForeground(UITheme.TEXT_PRI);
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Fill in the details below to register");
        sub.setFont(UITheme.F_BODY);
        sub.setForeground(UITheme.TEXT_SEC);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        usernameField = makeField();
        passwordField = new JPasswordField();
        confirmField  = new JPasswordField();
        styleField(passwordField);
        styleField(confirmField);
        confirmField.addActionListener(e -> handleSignup());

        errorLabel = new JLabel(" ");
        errorLabel.setFont(UITheme.F_SMALL);
        errorLabel.setForeground(UITheme.DANGER);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        ModernButton signupBtn = new ModernButton("Create Account", UITheme.PRIMARY, 10);
        signupBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        signupBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        signupBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        signupBtn.addActionListener(e -> handleSignup());

        ModernButton backBtn = new ModernButton("← Back to Login", UITheme.SECONDARY, 10);
        backBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        backBtn.addActionListener(e -> { dispose(); new CustomerLoginFrame(); });

        getRootPane().setDefaultButton(signupBtn);

        card.add(heading);
        card.add(Box.createVerticalStrut(6));
        card.add(sub);
        card.add(Box.createVerticalStrut(28));
        card.add(makeLabel("Username"));
        card.add(Box.createVerticalStrut(6));
        card.add(usernameField);
        card.add(Box.createVerticalStrut(16));
        card.add(makeLabel("Password"));
        card.add(Box.createVerticalStrut(6));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(16));
        card.add(makeLabel("Confirm Password"));
        card.add(Box.createVerticalStrut(6));
        card.add(confirmField);
        card.add(Box.createVerticalStrut(10));
        card.add(errorLabel);
        card.add(Box.createVerticalStrut(16));
        card.add(signupBtn);
        card.add(Box.createVerticalStrut(10));
        card.add(backBtn);

        JLabel hint = new JLabel("Already have an account? Click \"Back to Login\"");
        hint.setFont(UITheme.F_SMALL);
        hint.setForeground(UITheme.TEXT_HINT);
        hint.setAlignmentX(Component.CENTER_ALIGNMENT);

        wrap.add(card);
        wrap.add(Box.createVerticalStrut(14));
        wrap.add(hint);

        panel.add(wrap);
        return panel;
    }

    // ─── Signup logic ─────────────────────────────────────────────────────────

    /**
     * Validates the form and registers a new customer account.
     *
     * Sequential validation steps:
     *   1. Username must not be blank
     *   2. Username must not already exist
     *   3. Password must be at least 4 characters
     *   4. Confirm password must match
     *   5. Register and navigate to login
     */
    private void handleSignup() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirm  = new String(confirmField.getPassword());

        if (username.isEmpty()) {
            showError("Username cannot be empty."); return;
        }
        if (DataStore.getInstance().customerExists(username)) {
            showError("Username \"" + username + "\" is already taken.");
            usernameField.selectAll(); usernameField.requestFocus(); return;
        }
        if (password.length() < 4) {
            showError("Password must be at least 4 characters.");
            passwordField.setText(""); confirmField.setText("");
            passwordField.requestFocus(); return;
        }
        if (!password.equals(confirm)) {
            showError("Passwords do not match. Please try again.");
            confirmField.setText(""); confirmField.requestFocus(); return;
        }

        DataStore.getInstance().registerCustomer(username, password);
        JOptionPane.showMessageDialog(this,
            "Account created!\n\nWelcome to City Bites, " + username + "!\n"
            + "You can now sign in with your new credentials.",
            "Registration Successful", JOptionPane.INFORMATION_MESSAGE);

        dispose();
        CustomerLoginFrame loginFrame = new CustomerLoginFrame();
        loginFrame.prefillUsername(username);
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

    private void showError(String msg) { errorLabel.setText(msg); }
}
