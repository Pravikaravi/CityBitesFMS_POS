package citybitesfms.ui;

import citybitesfms.data.FoodDataStore;
import citybitesfms.model.FoodItem;
import citybitesfms.model.Order;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * AdminDashboard — full-screen administration panel for City Bites.
 *
 * Layout:
 *   WEST   — dark-green sidebar (200 px)
 *   CENTER — CardLayout content:
 *              "MENU"   : photo card grid + Add/Edit form (with image upload)
 *              "ORDERS" : customer orders table
 *
 * Image upload workflow:
 *   1. Admin clicks "Choose Image" in the form → JFileChooser opens
 *   2. Circular preview appears in the form
 *   3. On "Add Item" or "Update": image is written to both
 *        build/classes/.../resources/images/food_X.jpg  (live)
 *        src/.../resources/images/food_X.jpg            (persists after rebuild)
 *   4. Image cache is cleared so the grid reloads the new photo immediately
 *
 * @author NovaSoft Solutions (PVT) Ltd
 * @version 4.0
 */
public class AdminDashboard extends JFrame {

    // ── Card keys ─────────────────────────────────────────────────────────────
    private static final String CARD_MENU   = "MENU";
    private static final String CARD_ORDERS = "ORDERS";

    // ── Fallback plate colours ────────────────────────────────────────────────
    private static final Color[] PLATE_COLORS = {
        new Color(255, 183, 107), new Color(147, 197, 253), new Color(167, 243, 208),
        new Color(253, 164, 175), new Color(196, 181, 253), new Color(253, 230, 138),
        new Color(134, 239, 172), new Color(252, 165, 165),
    };

    // ── Image cache ───────────────────────────────────────────────────────────
    private static final Map<Integer, BufferedImage> IMAGE_CACHE = new HashMap<>();

    // ── Layout ────────────────────────────────────────────────────────────────
    private CardLayout contentCards;
    private JPanel     contentPanel;

    // ── Menu grid ─────────────────────────────────────────────────────────────
    private JPanel     foodGridPanel;
    private JTextField searchField;

    // ── Selection tracking ────────────────────────────────────────────────────
    private FoodItem   selectedItem = null;
    private JPanel     selectedCard = null;

    // ── Form fields ───────────────────────────────────────────────────────────
    private JTextField        nameField;
    private JTextField        priceField;
    private JComboBox<String> categoryCombo;
    private JCheckBox         availableBox;

    // ── Image upload state ────────────────────────────────────────────────────
    private File          pendingImageFile  = null;   // file chosen by admin
    private BufferedImage previewImg        = null;   // decoded for preview
    private JPanel        imagePreviewPanel = null;   // circular preview widget
    private JLabel        imageStatusLabel  = null;   // "Image selected" hint

    // ── Orders panel ─────────────────────────────────────────────────────────
    private DefaultTableModel ordersTableModel;
    private JTable            ordersTable;

    private final String adminName;

    // =========================================================================
    // CONSTRUCTOR
    // =========================================================================

    /**
     * Constructs and displays the AdminDashboard.
     *
     * @param adminName Username of the logged-in administrator
     */
    public AdminDashboard(String adminName) {
        this.adminName = adminName;
        setTitle("City Bites — Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1150, 700));
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        buildUI();
        setVisible(true);
    }

    // =========================================================================
    // ROOT LAYOUT
    // =========================================================================

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.add(buildSidebar(),     BorderLayout.WEST);
        root.add(buildContentArea(), BorderLayout.CENTER);
        setContentPane(root);
        loadMenuGrid(null);
        loadOrders();
    }

    // =========================================================================
    // SIDEBAR
    // =========================================================================

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(UITheme.SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JPanel nameCell = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 18));
        nameCell.setBackground(UITheme.SIDEBAR_BG);
        nameCell.setMaximumSize(new Dimension(200, 62));
        JLabel appName = new JLabel("City Bites");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 20));
        appName.setForeground(Color.WHITE);
        nameCell.add(appName);
        sidebar.add(nameCell);

        JSeparator sep = new JSeparator();
        sep.setForeground(UITheme.SIDEBAR_HOVER);
        sep.setMaximumSize(new Dimension(200, 1));
        sidebar.add(sep);
        sidebar.add(Box.createVerticalStrut(8));

        sidebar.add(makeSideBtn("  ⊞  Menu",     () -> contentCards.show(contentPanel, CARD_MENU),   true));
        sidebar.add(makeSideBtn("  ☰  Orders",   () -> contentCards.show(contentPanel, CARD_ORDERS), false));
        sidebar.add(makeSideBtn("  ⚙  Settings", () -> {},                                           false));
        sidebar.add(Box.createVerticalGlue());

        JPanel adminInfo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        adminInfo.setBackground(UITheme.SIDEBAR_BG);
        adminInfo.setMaximumSize(new Dimension(200, 40));
        JLabel adminLbl = new JLabel("Admin: " + adminName);
        adminLbl.setFont(UITheme.F_SMALL);
        adminLbl.setForeground(UITheme.SIDEBAR_ICON);
        adminInfo.add(adminLbl);
        sidebar.add(adminInfo);

        sidebar.add(makeSideBtn("  ⇦  Logout", () -> { dispose(); new LoginFrame(); }, false));
        sidebar.add(Box.createVerticalStrut(14));
        return sidebar;
    }

    private JPanel makeSideBtn(String label, Runnable action, boolean active) {
        JPanel btn = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 12));
        btn.setBackground(active ? UITheme.SIDEBAR_HOVER : UITheme.SIDEBAR_BG);
        btn.setMaximumSize(new Dimension(200, 46));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(active ? Color.WHITE : UITheme.SIDEBAR_ICON);
        btn.add(lbl);
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { action.run(); }
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(UITheme.SIDEBAR_HOVER); lbl.setForeground(Color.WHITE); }
            @Override public void mouseExited(MouseEvent e)  { btn.setBackground(active ? UITheme.SIDEBAR_HOVER : UITheme.SIDEBAR_BG); lbl.setForeground(active ? Color.WHITE : UITheme.SIDEBAR_ICON); }
        });
        return btn;
    }

    // =========================================================================
    // CONTENT AREA
    // =========================================================================

    private JPanel buildContentArea() {
        contentCards = new CardLayout();
        contentPanel = new JPanel(contentCards);
        contentPanel.setBackground(UITheme.BG);
        contentPanel.add(buildMenuPanel(),   CARD_MENU);
        contentPanel.add(buildOrdersPanel(), CARD_ORDERS);
        return contentPanel;
    }

    // =========================================================================
    // MENU PANEL — photo grid + form with image upload
    // =========================================================================

    private JPanel buildMenuPanel() {
        JPanel panel = new JPanel(new BorderLayout(14, 0));
        panel.setBackground(UITheme.BG);
        panel.setBorder(new EmptyBorder(18, 18, 18, 18));
        panel.add(buildMenuGridSection(), BorderLayout.CENTER);
        panel.add(buildMenuFormCard(),    BorderLayout.EAST);
        return panel;
    }

    // ── Left: photo card grid ─────────────────────────────────────────────────

    private JPanel buildMenuGridSection() {
        JPanel section = new JPanel(new BorderLayout(0, 10));
        section.setBackground(UITheme.BG);

        JLabel title = new JLabel("Food Menu Management");
        title.setFont(UITheme.F_HEADING);
        title.setForeground(UITheme.TEXT_PRI);

        searchField = new JTextField();
        searchField.setFont(UITheme.F_BODY);
        searchField.setPreferredSize(new Dimension(240, 36));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(8, UITheme.BORDER), new EmptyBorder(6, 12, 6, 12)));
        searchField.addActionListener(e -> loadMenuGrid(searchField.getText()));

        JButton searchBtn = makeGreenButton("Search");
        searchBtn.setPreferredSize(new Dimension(80, 36));
        searchBtn.addActionListener(e -> loadMenuGrid(searchField.getText()));

        JButton addItemBtn = makeGreenButton("+ Add Item");
        addItemBtn.setPreferredSize(new Dimension(105, 36));
        addItemBtn.addActionListener(e -> clearForm());   // just clear form — admin fills + clicks Add

        JPanel searchGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        searchGroup.setBackground(UITheme.BG);
        searchGroup.add(searchField);
        searchGroup.add(searchBtn);

        JPanel topBar = new JPanel(new BorderLayout(8, 0));
        topBar.setBackground(UITheme.BG);
        topBar.add(searchGroup, BorderLayout.WEST);
        topBar.add(addItemBtn,  BorderLayout.EAST);

        JPanel north = new JPanel(new BorderLayout(0, 10));
        north.setBackground(UITheme.BG);
        north.add(title,  BorderLayout.NORTH);
        north.add(topBar, BorderLayout.SOUTH);

        foodGridPanel = new JPanel(new GridLayout(0, 4, 12, 12));
        foodGridPanel.setBackground(UITheme.BG);

        JScrollPane scroll = new JScrollPane(foodGridPanel);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(UITheme.BG);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        section.add(north,  BorderLayout.NORTH);
        section.add(scroll, BorderLayout.CENTER);
        return section;
    }

    /**
     * Reloads the food card grid, optionally filtered by a search term.
     *
     * @param filter Search text — null or blank shows all items
     */
    private void loadMenuGrid(String filter) {
        foodGridPanel.removeAll();
        selectedItem = null;
        selectedCard = null;
        String lower = (filter == null) ? "" : filter.trim().toLowerCase();
        for (FoodItem item : FoodDataStore.getFoodItems()) {
            if (!lower.isBlank() && !item.getName().toLowerCase().contains(lower)) continue;
            foodGridPanel.add(buildAdminFoodCard(item));
        }
        foodGridPanel.revalidate();
        foodGridPanel.repaint();
    }

    /**
     * Builds a single admin food card.
     * Clicking selects it (green border) and populates the edit form.
     *
     * @param item FoodItem to display
     * @return Styled card JPanel
     */
    private JPanel buildAdminFoodCard(FoodItem item) {
        Color         plate = PLATE_COLORS[(item.getId() - 1) % PLATE_COLORS.length];
        BufferedImage img   = loadFoodImage(item.getId());
        final boolean[] hovered = {false};

        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 12));
                g2.fillRoundRect(3, 4, getWidth() - 3, getHeight() - 3, 14, 14);
                g2.setColor(UITheme.SURFACE);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                boolean sel = (selectedCard == this);
                g2.setColor(sel ? UITheme.PRIMARY : hovered[0] ? new Color(100, 180, 100) : UITheme.BORDER);
                g2.setStroke(new BasicStroke(sel ? 2.5f : hovered[0] ? 1.5f : 1f));
                g2.drawRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 14, 14);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(12, 10, 12, 10));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Circular image
        JPanel circle = buildCirclePanel(img, plate, item, 90);
        circle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nameLbl = new JLabel("<html><center>" + item.getName() + "</center></html>", SwingConstants.CENTER);
        nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        nameLbl.setForeground(UITheme.TEXT_PRI);
        nameLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel catLbl = new JLabel(item.getCategory(), SwingConstants.CENTER);
        catLbl.setFont(UITheme.F_SMALL);
        catLbl.setForeground(UITheme.TEXT_HINT);
        catLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel priceLbl = new JLabel("Rs. " + String.format("%.2f", item.getPrice()), SwingConstants.CENTER);
        priceLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        priceLbl.setForeground(UITheme.PRIMARY);
        priceLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel badge = new JLabel(item.isAvailable() ? "✔ Available" : "✘ Unavailable", SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(item.isAvailable() ? new Color(200, 245, 215) : new Color(255, 220, 220));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                g2.dispose(); super.paintComponent(g);
            }
        };
        badge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        badge.setForeground(item.isAvailable() ? new Color(27, 94, 32) : UITheme.DANGER);
        badge.setOpaque(false);
        badge.setHorizontalAlignment(SwingConstants.CENTER);
        badge.setAlignmentX(Component.CENTER_ALIGNMENT);
        badge.setMaximumSize(new Dimension(110, 22));
        badge.setBorder(new EmptyBorder(2, 8, 2, 8));

        card.add(circle);
        card.add(Box.createVerticalStrut(7));
        card.add(nameLbl);
        card.add(Box.createVerticalStrut(2));
        card.add(catLbl);
        card.add(Box.createVerticalStrut(3));
        card.add(priceLbl);
        card.add(Box.createVerticalStrut(5));
        card.add(badge);

        // Click → select card + fill form
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (selectedCard != null) selectedCard.repaint();
                selectedCard = card;
                selectedItem = item;
                card.repaint();
                // Populate form fields
                nameField    .setText(item.getName());
                priceField   .setText(String.format("%.2f", item.getPrice()));
                categoryCombo.setSelectedItem(item.getCategory());
                availableBox .setSelected(item.isAvailable());
                // Show existing image in preview
                pendingImageFile = null;
                previewImg       = loadFoodImage(item.getId());
                imageStatusLabel .setText(previewImg != null ? "Current image loaded" : "No image yet");
                imageStatusLabel .setForeground(previewImg != null ? UITheme.PRIMARY : UITheme.TEXT_HINT);
                if (imagePreviewPanel != null) imagePreviewPanel.repaint();
            }
            @Override public void mouseEntered(MouseEvent e) { hovered[0] = true;  card.repaint(); }
            @Override public void mouseExited(MouseEvent e)  { hovered[0] = false; card.repaint(); }
        });

        return card;
    }

    // ── Right: Add / Edit form with image upload ───────────────────────────────

    /**
     * Builds the right-side form card.
     *
     * Contains: image preview circle, "Choose Image" button, item fields,
     * and Add / Update / Delete / Clear action buttons.
     *
     * @return Form JPanel
     */
    private JPanel buildMenuFormCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(UITheme.SURFACE);
        card.setPreferredSize(new Dimension(280, 0));
        card.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(12, UITheme.BORDER),
            new EmptyBorder(20, 20, 20, 20)));

        JLabel heading = new JLabel("Add / Edit Item");
        heading.setFont(UITheme.F_HEADING);
        heading.setForeground(UITheme.TEXT_PRI);
        heading.setBorder(new EmptyBorder(0, 0, 14, 0));

        // ── Image upload area ──────────────────────────────────────────────
        imagePreviewPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING,      RenderingHints.VALUE_RENDER_QUALITY);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,  RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                int size = Math.min(getWidth(), getHeight()) - 6;
                int x = (getWidth() - size) / 2, y = (getHeight() - size) / 2;
                if (previewImg != null) {
                    // Shadow
                    g2.setColor(new Color(0, 0, 0, 15));
                    g2.fillOval(x - 2, y + 3, size + 4, size + 4);
                    // Circular clip
                    Shape clip = new Ellipse2D.Float(x, y, size, size);
                    g2.setClip(clip);
                    g2.drawImage(previewImg, x, y, size, size, null);
                    g2.setClip(null);
                    // Green border ring
                    g2.setColor(UITheme.PRIMARY);
                    g2.setStroke(new BasicStroke(2.5f));
                    g2.drawOval(x + 1, y + 1, size - 2, size - 2);
                } else {
                    // Dashed empty circle
                    g2.setColor(new Color(230, 230, 230));
                    g2.fillOval(x, y, size, size);
                    g2.setColor(UITheme.BORDER);
                    float[] dash = {6f, 4f};
                    g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND,
                        BasicStroke.JOIN_ROUND, 0, dash, 0));
                    g2.drawOval(x + 1, y + 1, size - 2, size - 2);
                    g2.setStroke(new BasicStroke(1f));
                    // Camera icon text
                    g2.setColor(UITheme.TEXT_HINT);
                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 22));
                    FontMetrics fm = g2.getFontMetrics();
                    String icon = "📷";
                    g2.drawString(icon, x + (size - fm.stringWidth(icon)) / 2,
                            y + (size / 2 + fm.getAscent() / 2));
                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                    fm = g2.getFontMetrics();
                    String hint = "No image";
                    g2.drawString(hint, x + (size - fm.stringWidth(hint)) / 2,
                            y + size / 2 + fm.getAscent() + 4);
                }
            }
        };
        imagePreviewPanel.setBackground(UITheme.SURFACE);
        imagePreviewPanel.setPreferredSize(new Dimension(100, 100));
        imagePreviewPanel.setMaximumSize(new Dimension(100, 100));
        imagePreviewPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Status text (e.g., "Image selected ✔")
        imageStatusLabel = new JLabel("No image selected", SwingConstants.CENTER);
        imageStatusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        imageStatusLabel.setForeground(UITheme.TEXT_HINT);
        imageStatusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // "Choose Image" button
        JButton chooseImgBtn = new JButton("📷  Choose Image") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed()
                    ? new Color(230, 230, 230)
                    : new Color(245, 245, 245));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(UITheme.BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        chooseImgBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        chooseImgBtn.setForeground(UITheme.TEXT_PRI);
        chooseImgBtn.setContentAreaFilled(false);
        chooseImgBtn.setBorderPainted(false);
        chooseImgBtn.setFocusPainted(false);
        chooseImgBtn.setOpaque(false);
        chooseImgBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        chooseImgBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        chooseImgBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        chooseImgBtn.addActionListener(e -> pickImage());

        // ── Item fields ────────────────────────────────────────────────────
        nameField     = makeFormField();
        priceField    = makeFormField();
        categoryCombo = new JComboBox<>(FoodDataStore.CATEGORIES);
        categoryCombo.setFont(UITheme.F_BODY);
        categoryCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        availableBox  = new JCheckBox("Available");
        availableBox.setFont(UITheme.F_BODY);
        availableBox.setBackground(UITheme.SURFACE);
        availableBox.setForeground(UITheme.TEXT_PRI);
        availableBox.setSelected(true);

        // ── Form assembly ──────────────────────────────────────────────────
        JPanel form = new JPanel();
        form.setBackground(UITheme.SURFACE);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        form.add(imagePreviewPanel);
        form.add(Box.createVerticalStrut(6));
        form.add(imageStatusLabel);
        form.add(Box.createVerticalStrut(6));
        form.add(chooseImgBtn);
        form.add(Box.createVerticalStrut(16));
        form.add(makeFormLabel("Item Name"));
        form.add(Box.createVerticalStrut(5)); form.add(nameField);
        form.add(Box.createVerticalStrut(12));
        form.add(makeFormLabel("Category"));
        form.add(Box.createVerticalStrut(5)); form.add(categoryCombo);
        form.add(Box.createVerticalStrut(12));
        form.add(makeFormLabel("Price (Rs.)"));
        form.add(Box.createVerticalStrut(5)); form.add(priceField);
        form.add(Box.createVerticalStrut(12));
        form.add(availableBox);

        // ── CRUD buttons ───────────────────────────────────────────────────
        JPanel buttons = new JPanel(new GridLayout(2, 2, 8, 8));
        buttons.setBackground(UITheme.SURFACE);
        buttons.setBorder(new EmptyBorder(16, 0, 0, 0));

        JButton addBtn    = makeGreenButton("Add Item");
        JButton updateBtn = makeGreenButton("Update");
        JButton deleteBtn = makeDangerButton("Delete");
        JButton clearBtn  = makeSecondaryButton("Clear");

        addBtn.addActionListener(e -> {
            String name = nameField.getText().trim(), priceStr = priceField.getText().trim();
            String cat  = (String) categoryCombo.getSelectedItem();
            if (!validate(name, priceStr)) return;
            int newId = FoodDataStore.getNextItemId();          // peek at next ID
            FoodDataStore.addFoodItem(
                new FoodItem(newId, name, cat, Double.parseDouble(priceStr), availableBox.isSelected()));
            saveImageForItem(newId);                            // save image with that ID
            clearForm();
            loadMenuGrid(null);
            showInfo("\"" + name + "\" added to the menu.");
        });

        updateBtn.addActionListener(e -> {
            if (selectedItem == null) { showWarn("Click a card first to select an item."); return; }
            String name = nameField.getText().trim(), priceStr = priceField.getText().trim();
            if (!validate(name, priceStr)) return;
            selectedItem.setName(name);
            selectedItem.setCategory((String) categoryCombo.getSelectedItem());
            selectedItem.setPrice(Double.parseDouble(priceStr));
            selectedItem.setAvailable(availableBox.isSelected());
            FoodDataStore.updateFoodItem(selectedItem);
            saveImageForItem(selectedItem.getId());             // save new image if chosen
            clearForm();
            loadMenuGrid(null);
            showInfo("Item updated successfully.");
        });

        deleteBtn.addActionListener(e -> {
            if (selectedItem == null) { showWarn("Click a card first to select an item."); return; }
            int choice = JOptionPane.showConfirmDialog(this,
                "Delete \"" + selectedItem.getName() + "\" from the menu?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (choice == JOptionPane.YES_OPTION) {
                FoodDataStore.deleteFoodItem(selectedItem.getId());
                clearForm();
                loadMenuGrid(null);
            }
        });

        clearBtn.addActionListener(e -> clearForm());

        buttons.add(addBtn); buttons.add(updateBtn);
        buttons.add(deleteBtn); buttons.add(clearBtn);

        card.add(heading, BorderLayout.NORTH);
        card.add(form,    BorderLayout.CENTER);
        card.add(buttons, BorderLayout.SOUTH);
        return card;
    }

    // =========================================================================
    // IMAGE PICK & SAVE
    // =========================================================================

    /**
     * Opens a JFileChooser filtered to image files.
     * On selection the image is decoded and shown in the circular preview.
     */
    private void pickImage() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Food Image");
        chooser.setFileFilter(new FileNameExtensionFilter(
            "Image Files (*.jpg, *.jpeg, *.png)", "jpg", "jpeg", "png"));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFile();
        try {
            BufferedImage raw = ImageIO.read(file);
            if (raw == null) { showError("Cannot read the selected file as an image."); return; }
            pendingImageFile = file;
            previewImg       = raw;
            imageStatusLabel.setText("Image selected: " + file.getName());
            imageStatusLabel.setForeground(UITheme.PRIMARY);
            imagePreviewPanel.repaint();
        } catch (IOException ex) {
            showError("Failed to open image: " + ex.getMessage());
        }
    }

    /**
     * Saves the pending image (if any) for the given food item ID.
     *
     * Writes a JPEG to two locations:
     *   build/classes/.../resources/images/food_X.jpg  — active at runtime
     *   src/.../resources/images/food_X.jpg            — persists after rebuild
     *
     * The image cache entry for this ID is invalidated so the grid reloads
     * the new photo immediately.
     *
     * @param itemId Target food item ID
     */
    private void saveImageForItem(int itemId) {
        if (pendingImageFile == null) return;

        String sep      = File.separator;
        String base     = System.getProperty("user.dir") + sep;
        String fileName = "food_" + itemId + ".jpg";

        File buildDest = new File(base + "build" + sep + "classes" + sep
            + "citybitesfms" + sep + "resources" + sep + "images" + sep + fileName);
        File srcDest   = new File(base + "src" + sep + "citybitesfms" + sep
            + "resources" + sep + "images" + sep + fileName);

        try {
            // Convert to RGB (strips alpha so JPEG encoder works correctly)
            BufferedImage rgb = new BufferedImage(
                previewImg.getWidth(), previewImg.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = rgb.createGraphics();
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, rgb.getWidth(), rgb.getHeight());
            g2.drawImage(previewImg, 0, 0, null);
            g2.dispose();

            buildDest.getParentFile().mkdirs();
            srcDest  .getParentFile().mkdirs();
            ImageIO.write(rgb, "jpg", buildDest);
            ImageIO.write(rgb, "jpg", srcDest);

            IMAGE_CACHE.remove(itemId);   // force fresh load from disk
        } catch (IOException ex) {
            showError("Could not save image:\n" + ex.getMessage());
        }
    }

    // =========================================================================
    // ORDERS PANEL
    // =========================================================================

    private JPanel buildOrdersPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(UITheme.BG);
        panel.setBorder(new EmptyBorder(22, 22, 22, 22));

        JLabel title = new JLabel("Customer Orders");
        title.setFont(UITheme.F_HEADING);
        title.setForeground(UITheme.TEXT_PRI);
        title.setBorder(new EmptyBorder(0, 0, 14, 0));

        JButton refreshBtn = makeGreenButton("↻  Refresh");
        refreshBtn.setPreferredSize(new Dimension(110, 36));
        refreshBtn.addActionListener(e -> loadOrders());

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(UITheme.BG);
        topBar.add(title,      BorderLayout.WEST);
        topBar.add(refreshBtn, BorderLayout.EAST);

        String[] cols = {"Order ID", "Customer", "Items", "Total (Rs.)", "Date", "Status"};
        ordersTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        ordersTable = buildStyledTable(ordersTableModel);
        ordersTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        ordersTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        ordersTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        ordersTable.getColumnModel().getColumn(3).setPreferredWidth(130);
        ordersTable.getColumnModel().getColumn(4).setPreferredWidth(180);
        ordersTable.getColumnModel().getColumn(5).setPreferredWidth(100);

        JScrollPane scroll = new JScrollPane(ordersTable);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));
        scroll.getViewport().setBackground(UITheme.SURFACE);

        panel.add(topBar, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    // =========================================================================
    // DATA
    // =========================================================================

    private void loadOrders() {
        ordersTableModel.setRowCount(0);
        for (Order order : FoodDataStore.getOrders()) {
            ordersTableModel.addRow(new Object[]{
                order.getOrderId(), order.getCustomerName(),
                order.getItems().size() + " item(s)",
                String.format("%.2f", order.getTotalAmount()),
                order.getOrderDate(),
                order.isConfirmed() ? "Confirmed" : "Pending"
            });
        }
    }

    // =========================================================================
    // HELPERS
    // =========================================================================

    /**
     * Builds a reusable circular image panel (used in grid cards).
     *
     * @param img   Image to display (null → coloured placeholder)
     * @param plate Fallback plate colour
     * @param item  FoodItem (used for initial letter fallback)
     * @param size  Diameter in pixels
     * @return Painted JPanel
     */
    private JPanel buildCirclePanel(BufferedImage img, Color plate, FoodItem item, int size) {
        JPanel circle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING,      RenderingHints.VALUE_RENDER_QUALITY);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,  RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                int s = Math.min(getWidth(), getHeight()) - 8;
                int x = (getWidth() - s) / 2, y = (getHeight() - s) / 2;
                if (img != null) {
                    g2.setColor(new Color(0, 0, 0, 15));
                    g2.fillOval(x - 2, y + 3, s + 4, s + 4);
                    Shape clip = new Ellipse2D.Float(x, y, s, s);
                    g2.setClip(clip);
                    g2.drawImage(img, x, y, s, s, null);
                    g2.setClip(null);
                    g2.setColor(new Color(255, 255, 255, 180));
                    g2.setStroke(new BasicStroke(2.5f));
                    g2.drawOval(x + 1, y + 1, s - 2, s - 2);
                    g2.setColor(UITheme.BORDER);
                    g2.setStroke(new BasicStroke(1f));
                    g2.drawOval(x, y, s, s);
                } else {
                    g2.setColor(new Color(plate.getRed(), plate.getGreen(), plate.getBlue(), 55));
                    g2.fillOval(x - 5, y - 5, s + 10, s + 10);
                    g2.setColor(plate);
                    g2.fillOval(x, y, s, s);
                    g2.setColor(new Color(0, 0, 0, 70));
                    g2.setFont(new Font("Segoe UI", Font.BOLD, s / 3));
                    FontMetrics fm = g2.getFontMetrics();
                    String t = item.getName().substring(0, 1).toUpperCase();
                    g2.drawString(t, x + (s - fm.stringWidth(t)) / 2,
                            y + (s + fm.getAscent() - fm.getDescent()) / 2);
                }
            }
        };
        circle.setOpaque(false);
        circle.setPreferredSize(new Dimension(size, size));
        circle.setMaximumSize(new Dimension(size, size));
        return circle;
    }

    /**
     * Loads a food image from the resources classpath by item ID.
     * Tries .jpg then .jpeg. Results are cached.
     *
     * @param foodId Food item ID
     * @return BufferedImage or null if not found
     */
    private BufferedImage loadFoodImage(int foodId) {
        if (IMAGE_CACHE.containsKey(foodId)) return IMAGE_CACHE.get(foodId);
        BufferedImage img = null;
        for (String ext : new String[]{".jpg", ".jpeg"}) {
            String path = "/citybitesfms/resources/images/food_" + foodId + ext;
            try (InputStream is = getClass().getResourceAsStream(path)) {
                if (is != null) { img = ImageIO.read(is); break; }
            } catch (Exception ignored) {}
        }
        IMAGE_CACHE.put(foodId, img);
        return img;
    }

    private void clearForm() {
        nameField    .setText("");
        priceField   .setText("");
        categoryCombo.setSelectedIndex(0);
        availableBox .setSelected(true);
        selectedItem      = null;
        pendingImageFile  = null;
        previewImg        = null;
        if (imagePreviewPanel != null) imagePreviewPanel.repaint();
        if (imageStatusLabel  != null) {
            imageStatusLabel.setText("No image selected");
            imageStatusLabel.setForeground(UITheme.TEXT_HINT);
        }
        if (selectedCard != null) { selectedCard.repaint(); selectedCard = null; }
    }

    private boolean validate(String name, String priceStr) {
        if (name.isEmpty()) { showError("Item name cannot be empty."); return false; }
        try {
            double p = Double.parseDouble(priceStr);
            if (p <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showError("Enter a valid price greater than 0."); return false;
        }
        return true;
    }

    private JTable buildStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model) {
            @Override public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (isRowSelected(row)) { c.setBackground(UITheme.TABLE_SEL); c.setForeground(UITheme.TEXT_PRI); }
                else { c.setBackground(row % 2 == 0 ? UITheme.TABLE_ODD : UITheme.TABLE_EVEN); c.setForeground(UITheme.TEXT_PRI); }
                return c;
            }
        };
        table.setFont(UITheme.F_BODY); table.setRowHeight(34); table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setFont(UITheme.F_LABEL);
        table.getTableHeader().setBackground(UITheme.TABLE_HDR_BG);
        table.getTableHeader().setForeground(UITheme.TABLE_HDR_FG);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setPreferredSize(new Dimension(0, 38));
        return table;
    }

    private JButton makeGreenButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? UITheme.PRIMARY_DARK : UITheme.PRIMARY);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose(); super.paintComponent(g);
            }
        };
        btn.setFont(UITheme.F_BUTTON); btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false); btn.setBorderPainted(false);
        btn.setFocusPainted(false); btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton makeDangerButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? UITheme.DANGER_DARK : UITheme.DANGER);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose(); super.paintComponent(g);
            }
        };
        btn.setFont(UITheme.F_BUTTON); btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false); btn.setBorderPainted(false);
        btn.setFocusPainted(false); btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton makeSecondaryButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(160, 160, 160));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose(); super.paintComponent(g);
            }
        };
        btn.setFont(UITheme.F_BUTTON); btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false); btn.setBorderPainted(false);
        btn.setFocusPainted(false); btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JLabel makeFormLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UITheme.F_LABEL); lbl.setForeground(UITheme.TEXT_SEC);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JTextField makeFormField() {
        JTextField f = new JTextField();
        f.setFont(UITheme.F_BODY);
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        f.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(8, UITheme.BORDER), new EmptyBorder(6, 10, 6, 10)));
        return f;
    }

    private void showInfo(String msg)  { JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE); }
    private void showWarn(String msg)  { JOptionPane.showMessageDialog(this, msg, "Warning", JOptionPane.WARNING_MESSAGE);     }
    private void showError(String msg) { JOptionPane.showMessageDialog(this, msg, "Error",   JOptionPane.ERROR_MESSAGE);       }
}
