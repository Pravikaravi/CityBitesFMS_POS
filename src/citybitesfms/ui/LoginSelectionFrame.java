package citybitesfms.ui;

import citybitesfms.data.FoodDataStore;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public class LoginSelectionFrame extends JFrame {

    private static final Color BG_CREAM   = new Color(248, 252, 248);
    private static final Color STRIP_CLR  = new Color(190, 228, 205, 200);
    private static final Color GREEN_DARK = new Color( 27,  94,  32);
    private static final Color GREEN_PRI  = new Color(  0, 200,  83);
    private static final Color TEXT_DARK  = new Color( 22,  30,  22);
    private static final Color TEXT_MUT   = new Color(100, 130, 110);
    private static final Color BORDER_CLR = new Color(200, 230, 210);

    // hardcoded admin credentials
    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PASS = "admin123";

    // three food images shown on the left decorative panel
    private BufferedImage imgA, imgB, imgC;

    // tracks whether admin or customer tab is selected
    private boolean        isAdminMode = false;
    private JButton        adminTab, custTab;
    private JTextField     usernameField;
    private JPasswordField passwordField;
    private JLabel         errorLabel;

    public LoginSelectionFrame() {
        setTitle("City Bites — Welcome");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 660));
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        loadImages();
        buildUI();
        setVisible(true);
    }

    private void loadImages() {
        // pick three food photos to display on the left panel
        imgA = loadImg(4);
        imgB = loadImg(1);
        imgC = loadImg(11);
    }

    private BufferedImage loadImg(int id) {
        // try common image extensions until one works
        for (String ext : new String[]{".jpg", ".jpeg", ".png"}) {
            try (InputStream is = getClass().getResourceAsStream(
                    "/citybitesfms/resources/images/food_" + id + ext)) {
                if (is != null) return ImageIO.read(is);
            } catch (Exception ignored) {}
        }
        return null;
    }

    private void buildUI() {
        JPanel root = new JPanel(null) {
            @Override
            public void doLayout() {
                int w = getWidth(), h = getHeight();
                int leftW = (int)(w * 0.45);
                getComponent(0).setBounds(0,     0, leftW,     h);
                getComponent(1).setBounds(leftW, 0, w - leftW, h);
            }
        };
        root.setBackground(BG_CREAM);
        root.add(buildPhotoPanel());
        root.add(buildRightPanel());
        setContentPane(root);
    }

    private JPanel buildPhotoPanel() {
        JPanel panel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING,      RenderingHints.VALUE_RENDER_QUALITY);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,  RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                int w = getWidth(), h = getHeight();

                g2.setColor(BG_CREAM);
                g2.fillRect(0, 0, w, h);

                int slant = 70;
                int stripW = (int)(w * 0.75);
                int[] xs = {0, stripW - slant, stripW + slant, 0};
                int[] ys = {0, 0, h, h};
                g2.setColor(STRIP_CLR);
                g2.fillPolygon(xs, ys, 4);

                g2.setColor(new Color(248, 252, 248, 230));
                g2.fillRect(0, 0, w, 70);

                g2.setColor(new Color(150, 200, 170, 50));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawLine((int)(w*0.08),(int)(h*0.10),(int)(w*0.42),(int)(h*0.92));
                g2.drawLine((int)(w*0.22),(int)(h*0.05),(int)(w*0.55),(int)(h*0.88));

                int sA = (int)(Math.min(w, h) * 0.44);
                int xA = (int)(w * 0.04);
                int yA = (int)(h * 0.42);
                drawCircle(g2, imgA, xA, yA, sA, new Color(180, 220, 190));

                int sB = (int)(Math.min(w, h) * 0.30);
                int xB = (int)(w * 0.42);
                int yB = (int)(h * 0.10);
                drawCircle(g2, imgB, xB, yB, sB, new Color(255, 215, 160));

                int sC = (int)(Math.min(w, h) * 0.25);
                int xC = (int)(w * 0.54);
                int yC = (int)(h * 0.52);
                drawCircle(g2, imgC, xC, yC, sC, new Color(200, 235, 220));
            }

            private void drawCircle(Graphics2D g2, BufferedImage img,
                                    int x, int y, int size, Color fallback) {
                
                g2.setColor(new Color(0, 0, 0, 20));
                g2.fillOval(x + 4, y + 8, size, size);
                
                g2.setColor(Color.WHITE);
                g2.fillOval(x - 6, y - 6, size + 12, size + 12);

                if (img != null) {
                    Shape clip = new Ellipse2D.Float(x, y, size, size);
                    Shape old  = g2.getClip();
                    g2.setClip(clip);
                    g2.drawImage(img, x, y, size, size, null);
                    g2.setClip(old);
                } else {
                    g2.setColor(fallback);
                    g2.fillOval(x, y, size, size);
                }
                
                g2.setColor(new Color(0, 200, 83, 140));
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawOval(x, y, size, size);
            }
        };
        panel.setOpaque(false);

        JPanel navOverlay = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 18));
        navOverlay.setOpaque(false);
        navOverlay.setPreferredSize(new Dimension(0, 70));

        JPanel logoBadge = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(GREEN_DARK);
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(1, 1, getWidth() - 3, getHeight() - 3);
                int cx = getWidth()/2, cy = getHeight()/2, r = 8;
                int[] tx = {cx, cx - r, cx + r};
                int[] ty = {cy - r + 1, cy + r, cy + r};
                g2.setColor(GREEN_DARK);
                g2.fillPolygon(tx, ty, 3);
            }
        };
        logoBadge.setOpaque(false);
        logoBadge.setPreferredSize(new Dimension(32, 32));

        JLabel logoLbl = new JLabel("City Bites");
        logoLbl.setFont(new Font("Segoe UI", Font.BOLD, 19));
        logoLbl.setForeground(GREEN_DARK);

        navOverlay.add(logoBadge);
        navOverlay.add(logoLbl);

        panel.setLayout(new BorderLayout());
        panel.add(navOverlay, BorderLayout.NORTH);
        return panel;
    }

    private JPanel buildRightPanel() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(BG_CREAM);

        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setMaximumSize(new Dimension(480, Integer.MAX_VALUE));

        JLabel h1 = leftLabel("Craving", new Font("Segoe UI", Font.BOLD, 52), GREEN_DARK);
        JLabel h2 = leftLabel("Something?", new Font("Segoe UI", Font.BOLD, 52), GREEN_PRI);
        JLabel sub = leftLabel("Sign in to get started!", new Font("Segoe UI", Font.PLAIN, 18), TEXT_MUT);
        sub.setBorder(new EmptyBorder(4, 0, 28, 0));

        JPanel togglePanel = new JPanel(new GridLayout(1, 2, 0, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(230, 245, 235));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(BORDER_CLR);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                g2.dispose();
            }
        };
        togglePanel.setOpaque(false);
        togglePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        togglePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        togglePanel.setBorder(new EmptyBorder(2, 2, 2, 2));

        adminTab = buildToggleBtn("  Admin  ", false);
        custTab  = buildToggleBtn("  Customer  ", true);   

        adminTab.addActionListener(e -> {
            isAdminMode = true;
            adminTab.setBackground(GREEN_DARK);
            adminTab.setForeground(Color.WHITE);
            custTab .setBackground(new Color(230, 245, 235));
            custTab .setForeground(TEXT_DARK);
            errorLabel.setText(" ");
        });
        custTab.addActionListener(e -> {
            isAdminMode = false;
            custTab .setBackground(GREEN_DARK);
            custTab .setForeground(Color.WHITE);
            adminTab.setBackground(new Color(230, 245, 235));
            adminTab.setForeground(TEXT_DARK);
            errorLabel.setText(" ");
        });

        togglePanel.add(adminTab);
        togglePanel.add(custTab);

        JLabel userLbl = fieldLabel("Username");
        usernameField  = makeField();

        JLabel passLbl = fieldLabel("Password");
        passwordField  = new JPasswordField();
        styleField(passwordField);
        passwordField.addActionListener(e -> handleLogin());

        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        errorLabel.setForeground(new Color(211, 47, 47));
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton loginBtn = new JButton("Login  →") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed()  ? new Color(0, 160, 60)
                          : getModel().isRollover() ? new Color(0, 185, 72)
                          : GREEN_PRI);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setContentAreaFilled(false);
        loginBtn.setBorderPainted(false);
        loginBtn.setFocusPainted(false);
        loginBtn.setOpaque(false);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginBtn.addActionListener(e -> handleLogin());
        getRootPane().setDefaultButton(loginBtn);

        JPanel signupRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        signupRow.setOpaque(false);
        signupRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel newHere = new JLabel("New here?");
        newHere.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        newHere.setForeground(TEXT_MUT);
        JButton signupBtn = new JButton("Create a free account") {{
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setForeground(GREEN_PRI);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }};
        signupBtn.addActionListener(e -> { dispose(); new SignupFrame(); });
        signupRow.add(newHere);
        signupRow.add(signupBtn);

        JLabel hint = new JLabel(
            "<html><font color='#8FB89A'>Admin: admin / admin123 &nbsp;|&nbsp; "
            + "Customer: user / user123</font></html>");
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel pills = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pills.setOpaque(false);
        pills.setAlignmentX(Component.LEFT_ALIGNMENT);
        pills.setBorder(new EmptyBorder(18, 0, 0, 0));
        for (String p : new String[]{"🍕 Pizza", "🍔 Burger", "🍜 Noodles", "🍰 Desserts"}) {
            pills.add(makePill(p));
        }

        inner.add(h1);
        inner.add(h2);
        inner.add(sub);
        inner.add(leftLabel("Select Role", new Font("Segoe UI", Font.BOLD, 13), new Color(60, 90, 65)));
        inner.add(Box.createVerticalStrut(8));
        inner.add(togglePanel);
        inner.add(Box.createVerticalStrut(20));
        inner.add(userLbl);
        inner.add(Box.createVerticalStrut(7));
        inner.add(usernameField);
        inner.add(Box.createVerticalStrut(16));
        inner.add(passLbl);
        inner.add(Box.createVerticalStrut(7));
        inner.add(passwordField);
        inner.add(Box.createVerticalStrut(8));
        inner.add(errorLabel);
        inner.add(Box.createVerticalStrut(14));
        inner.add(loginBtn);
        inner.add(Box.createVerticalStrut(14));
        inner.add(signupRow);
        inner.add(Box.createVerticalStrut(10));
        inner.add(hint);
        inner.add(pills);

        outer.add(inner);
        return outer;
    }

    private void handleLogin() {
        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword());
        errorLabel.setText(" ");

        // check fields are not empty
        if (user.isEmpty() || pass.isEmpty()) {
            errorLabel.setText("Please enter both username and password.");
            return;
        }

        if (isAdminMode) {
            // check against hardcoded admin credentials
            if (ADMIN_USER.equals(user) && ADMIN_PASS.equals(pass)) {
                dispose(); new AdminDashboard(user);
            } else {
                errorLabel.setText("Invalid admin credentials. Try admin / admin123.");
                passwordField.setText("");
            }
        } else {
            // check against registered customer accounts
            if (FoodDataStore.authenticateCustomer(user, pass)) {
                dispose(); new CustomerDashboard(user);
            } else {
                errorLabel.setText("Invalid credentials. Try user / user123 or sign up.");
                passwordField.setText("");
            }
        }
    }

    private JButton buildToggleBtn(String text, boolean selected) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(selected ? GREEN_DARK : new Color(230, 245, 235));
        btn.setForeground(selected ? Color.WHITE : TEXT_DARK);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JLabel leftLabel(String text, Font font, Color color) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(font);
        lbl.setForeground(color);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JLabel fieldLabel(String text) {
        return leftLabel(text, new Font("Segoe UI", Font.BOLD, 13), new Color(60, 90, 65));
    }

    private JTextField makeField() {
        JTextField f = new JTextField();
        styleField(f);
        return f;
    }

    private void styleField(JTextField f) {
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        f.setBackground(Color.WHITE);
        f.setCaretColor(GREEN_DARK);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_CLR, 1),
            new EmptyBorder(10, 14, 10, 14)));
        f.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(GREEN_PRI, 2),
                    new EmptyBorder(9, 13, 9, 13)));
            }
            @Override public void focusLost(FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_CLR, 1),
                    new EmptyBorder(10, 14, 10, 14)));
            }
        });
    }

    private JLabel makePill(String text) {
        JLabel pill = new JLabel(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                g2.setColor(BORDER_CLR);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, getHeight(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        pill.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        pill.setForeground(TEXT_DARK);
        pill.setOpaque(false);
        pill.setBorder(new EmptyBorder(5, 14, 5, 14));
        return pill;
    }
}
