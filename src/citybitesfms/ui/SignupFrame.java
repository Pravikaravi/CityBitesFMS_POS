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

public class SignupFrame extends JFrame {

    private static final Color BG_CREAM   = new Color(248, 252, 248);
    private static final Color STRIP_CLR  = new Color(190, 228, 205, 200);
    private static final Color GREEN_DARK = new Color( 27,  94,  32);
    private static final Color GREEN_PRI  = new Color(  0, 200,  83);
    private static final Color TEXT_DARK  = new Color( 22,  30,  22);
    private static final Color TEXT_MUT   = new Color(100, 130, 110);
    private static final Color BORDER_CLR = new Color(200, 230, 210);

    private BufferedImage imgA, imgB, imgC;

    private JTextField     usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmField;
    private JLabel         errorLabel;

    public SignupFrame() {
        setTitle("City Bites — Create Account");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 660));
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        loadImages();
        buildUI();
        setVisible(true);
    }

    private void loadImages() {
        imgA = loadImg(16);   
        imgB = loadImg(7);    
        imgC = loadImg(3);    
    }

    private BufferedImage loadImg(int id) {
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

                int slant  = 70;
                int stripW = (int)(w * 0.75);
                int[] xs = {0, stripW - slant, stripW + slant, 0};
                int[] ys = {0, 0, h, h};
                g2.setColor(STRIP_CLR);
                g2.fillPolygon(xs, ys, 4);

                g2.setColor(new Color(248, 252, 248, 220));
                g2.fillRect(0, 0, w, 70);

                g2.setColor(new Color(150, 200, 170, 50));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawLine((int)(w*0.08),(int)(h*0.10),(int)(w*0.42),(int)(h*0.92));
                g2.drawLine((int)(w*0.22),(int)(h*0.05),(int)(w*0.55),(int)(h*0.88));

                int sA = (int)(Math.min(w, h) * 0.44);
                drawCircle(g2, imgA, (int)(w*0.04), (int)(h*0.42), sA,
                    new Color(200, 235, 220));

                int sB = (int)(Math.min(w, h) * 0.30);
                drawCircle(g2, imgB, (int)(w*0.42), (int)(h*0.10), sB,
                    new Color(255, 215, 160));

                int sC = (int)(Math.min(w, h) * 0.25);
                drawCircle(g2, imgC, (int)(w*0.54), (int)(h*0.52), sC,
                    new Color(180, 220, 190));
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

        JLabel h1  = leftLabel("Join the",   new Font("Segoe UI", Font.BOLD, 52), GREEN_DARK);
        JLabel h2  = leftLabel("Family!",    new Font("Segoe UI", Font.BOLD, 52), GREEN_PRI);
        JLabel sub = leftLabel("Create your free account today",
                               new Font("Segoe UI", Font.PLAIN, 18), TEXT_MUT);
        sub.setBorder(new EmptyBorder(4, 0, 28, 0));

        usernameField = makeField();

        passwordField = new JPasswordField();
        styleField(passwordField);

        confirmField = new JPasswordField();
        styleField(confirmField);
        confirmField.addActionListener(e -> handleSignup());

        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        errorLabel.setForeground(new Color(211, 47, 47));
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton createBtn = new JButton("Create Account  →") {
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
        createBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        createBtn.setForeground(Color.WHITE);
        createBtn.setContentAreaFilled(false);
        createBtn.setBorderPainted(false);
        createBtn.setFocusPainted(false);
        createBtn.setOpaque(false);
        createBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        createBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        createBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        createBtn.addActionListener(e -> handleSignup());
        getRootPane().setDefaultButton(createBtn);

        JPanel backRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        backRow.setOpaque(false);
        backRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel hasAcc = new JLabel("Already have an account?");
        hasAcc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        hasAcc.setForeground(TEXT_MUT);
        JButton backBtn = new JButton("Sign in here") {{
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setForeground(GREEN_PRI);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }};
        backBtn.addActionListener(e -> { dispose(); new LoginSelectionFrame(); });
        backRow.add(hasAcc);
        backRow.add(backBtn);

        JPanel perks = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        perks.setOpaque(false);
        perks.setAlignmentX(Component.LEFT_ALIGNMENT);
        perks.setBorder(new EmptyBorder(18, 0, 0, 0));
        for (String p : new String[]{"✔ Free account", "✔ Full menu", "✔ Order anytime"}) {
            perks.add(makePill(p));
        }

        inner.add(h1);
        inner.add(h2);
        inner.add(sub);

        inner.add(fieldLabel("Username"));
        inner.add(Box.createVerticalStrut(7));
        inner.add(usernameField);
        inner.add(Box.createVerticalStrut(16));

        inner.add(fieldLabel("Password  (min. 6 characters)"));
        inner.add(Box.createVerticalStrut(7));
        inner.add(passwordField);
        inner.add(Box.createVerticalStrut(16));

        inner.add(fieldLabel("Confirm Password"));
        inner.add(Box.createVerticalStrut(7));
        inner.add(confirmField);
        inner.add(Box.createVerticalStrut(8));

        inner.add(errorLabel);
        inner.add(Box.createVerticalStrut(14));
        inner.add(createBtn);
        inner.add(Box.createVerticalStrut(14));
        inner.add(backRow);
        inner.add(perks);

        outer.add(inner);
        return outer;
    }

    private void handleSignup() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirm  = new String(confirmField.getPassword());
        errorLabel.setText(" ");

        if (username.isEmpty()) {
            errorLabel.setText("Username cannot be empty."); return;
        }
        if (FoodDataStore.customerExists(username)) {
            errorLabel.setText("Username \"" + username + "\" is already taken.");
            usernameField.selectAll(); usernameField.requestFocus(); return;
        }
        if (password.length() < 6) {
            errorLabel.setText("Password must be at least 6 characters.");
            passwordField.setText(""); confirmField.setText("");
            passwordField.requestFocus(); return;
        }
        if (!password.equals(confirm)) {
            errorLabel.setText("Passwords do not match. Please try again.");
            confirmField.setText(""); confirmField.requestFocus(); return;
        }

        FoodDataStore.registerCustomer(username, password);

        JOptionPane.showMessageDialog(this,
            "<html><b>Account created!</b><br><br>"
            + "Welcome to City Bites, <b>" + username + "</b>!<br>"
            + "You can now sign in with your new credentials.</html>",
            "Registration Successful", JOptionPane.INFORMATION_MESSAGE);

        dispose();
        new LoginSelectionFrame();
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
        pill.setForeground(new Color(27, 94, 32));
        pill.setOpaque(false);
        pill.setBorder(new EmptyBorder(5, 14, 5, 14));
        return pill;
    }
}
