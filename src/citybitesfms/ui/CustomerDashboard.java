package citybitesfms.ui;

import citybitesfms.data.DataStore;
import citybitesfms.model.FoodItem;
import citybitesfms.model.Order;
import citybitesfms.model.OrderItem;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * Full-screen Customer Dashboard styled as a modern POS terminal.
 *
 * Layout mirrors the Restro POS design:
 *   WEST   — narrow dark sidebar with icon navigation
 *   CENTER — top bar (logo + search + table button) above a split view:
 *              LEFT  : category pill tabs + scrollable food-card grid
 *              RIGHT : live order list + totals + Hold / Proceed actions
 *
 * A CardLayout in the content area switches between the POS view and the
 * My Orders history view when the sidebar navigation is used.
 *
 * Programming paradigms demonstrated:
 *   OOP          — CustomerDashboard extends JFrame; Order / OrderItem are objects
 *   Event-Driven — every click fires an ActionListener or MouseListener
 *   Procedural   — confirmOrder(), updateOrderDisplay() follow sequential steps
 *
 * @author NovaSoft Solutions (PVT) Ltd
 * @version 4.0
 */
public class CustomerDashboard extends JFrame {

    // ─── POS state ────────────────────────────────────────────────────────────
    private final Map<Integer, OrderItem> cart = new LinkedHashMap<>();
    private final String customerName;
    private String  selectedCategory = "All";
    private Integer expandedItemId   = null;

    // ─── Menu area ────────────────────────────────────────────────────────────
    private JPanel categoryBarPanel;
    private JPanel foodGridPanel;

    // ─── Order panel labels ───────────────────────────────────────────────────
    private JPanel  orderItemsPanel;
    private JLabel  subtotalValueLabel;
    private JLabel  taxValueLabel;
    private JLabel  payableValueLabel;

    // ─── Content card panels ──────────────────────────────────────────────────
    private CardLayout contentCards;
    private JPanel     contentCardPanel;

    // ─── My Orders tab ────────────────────────────────────────────────────────
    private DefaultTableModel myOrdersTableModel;
    private JTable            myOrdersTable;
    private JTextArea         myOrderDetailArea;

    // ─── Sidebar button tracking ──────────────────────────────────────────────
    private final List<JPanel> sidebarBtns = new ArrayList<>();

    // Plate colours — rotate through for each food card (warm, vivid pastels)
    private static final Color[] PLATE_COLORS = {
        new Color(255, 183, 107),
        new Color(147, 197, 253),
        new Color(167, 243, 208),
        new Color(253, 164, 175),
        new Color(196, 181, 253),
        new Color(253, 230, 138),
        new Color(134, 239, 172),
        new Color(252, 165, 165),
    };

    // ─── Constructor ──────────────────────────────────────────────────────────

    public CustomerDashboard(String customerName) {
        this.customerName = customerName;
        setTitle("City Bites POS");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 700));
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        buildUI();
        setVisible(true);
    }

    // ─── Root layout ─────────────────────────────────────────────────────────

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG);

        root.add(buildSidebar(), BorderLayout.WEST);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(UITheme.BG);
        main.add(buildTopBar(), BorderLayout.NORTH);

        contentCards      = new CardLayout();
        contentCardPanel  = new JPanel(contentCards);
        contentCardPanel.setBackground(UITheme.BG);
        contentCardPanel.add(buildPOSPanel(),      "POS");
        contentCardPanel.add(buildMyOrdersPanel(), "ORDERS");
        contentCards.show(contentCardPanel, "POS");

        main.add(contentCardPanel, BorderLayout.CENTER);
        root.add(main, BorderLayout.CENTER);
        setContentPane(root);
    }

    // =========================================================================
    // SIDEBAR
    // =========================================================================

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(UITheme.SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(82, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        // Logo badge
        sidebar.add(buildLogoCell());
        sidebar.add(Box.createVerticalStrut(8));

        // Nav items (index 0 = Home = active initially)
        sidebar.add(makeSideNavBtn("⌂",  "Home",      0, () -> {
            contentCards.show(contentCardPanel, "POS");
            setActiveSideBtn(0);
        }));
        sidebar.add(makeSideNavBtn("☰",  "Orders",    1, () -> {
            loadMyOrders();
            contentCards.show(contentCardPanel, "ORDERS");
            setActiveSideBtn(1);
        }));
        sidebar.add(makeSideNavBtn("⚙",  "Settings",  2, () -> setActiveSideBtn(2)));

        sidebar.add(Box.createVerticalGlue());

        sidebar.add(makeSideNavBtn("⇦",  "Logout",    3, () -> {
            dispose();
            new LoginSelectionFrame();
        }));
        sidebar.add(Box.createVerticalStrut(14));

        return sidebar;
    }

    private JPanel buildLogoCell() {
        JPanel cell = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int cx = getWidth() / 2, cy = getHeight() / 2;
                g2.setColor(UITheme.PRIMARY);
                g2.fillOval(cx - 20, cy - 20, 40, 40);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                String t = "CB";
                g2.drawString(t, cx - fm.stringWidth(t) / 2, cy + fm.getAscent() / 2 - 1);
            }
        };
        cell.setBackground(UITheme.SIDEBAR_BG);
        cell.setMaximumSize(new Dimension(82, 68));
        cell.setPreferredSize(new Dimension(82, 68));
        return cell;
    }

    private JPanel makeSideNavBtn(String icon, String label, int index, Runnable action) {
        boolean active = (index == 0);
        JPanel btn = new JPanel();
        btn.setLayout(new BoxLayout(btn, BoxLayout.Y_AXIS));
        btn.setBackground(active ? UITheme.SIDEBAR_HOVER : UITheme.SIDEBAR_BG);
        btn.setMaximumSize(new Dimension(82, 70));
        btn.setPreferredSize(new Dimension(82, 70));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 0, 10, 0));

        Color col = active ? UITheme.PRIMARY : UITheme.SIDEBAR_ICON;

        JLabel iconLbl = new JLabel(icon, SwingConstants.CENTER);
        iconLbl.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        iconLbl.setForeground(col);
        iconLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel textLbl = new JLabel(label, SwingConstants.CENTER);
        textLbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        textLbl.setForeground(col);
        textLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        btn.add(iconLbl);
        btn.add(Box.createVerticalStrut(3));
        btn.add(textLbl);

        sidebarBtns.add(btn);

        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e)  { action.run(); }
            @Override public void mouseEntered(MouseEvent e)  {
                if (btn.getBackground() != UITheme.SIDEBAR_HOVER)
                    btn.setBackground(new Color(44, 55, 72));
            }
            @Override public void mouseExited(MouseEvent e)   {
                if (btn.getBackground() != UITheme.SIDEBAR_HOVER)
                    btn.setBackground(UITheme.SIDEBAR_BG);
            }
        });

        return btn;
    }

    private void setActiveSideBtn(int activeIndex) {
        for (int i = 0; i < sidebarBtns.size(); i++) {
            JPanel btn = sidebarBtns.get(i);
            boolean active = (i == activeIndex);
            btn.setBackground(active ? UITheme.SIDEBAR_HOVER : UITheme.SIDEBAR_BG);
            Color col = active ? UITheme.PRIMARY : UITheme.SIDEBAR_ICON;
            for (Component c : btn.getComponents()) {
                if (c instanceof JLabel) ((JLabel) c).setForeground(col);
            }
        }
    }

    // =========================================================================
    // TOP BAR
    // =========================================================================

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout(16, 0));
        bar.setBackground(UITheme.SURFACE);
        bar.setPreferredSize(new Dimension(0, 60));
        bar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER),
            new EmptyBorder(0, 22, 0, 22)));

        JLabel logo = new JLabel("City Bites");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logo.setForeground(UITheme.TEXT_PRI);

        // Search bar
        JTextField search = new JTextField("Search products....");
        search.setFont(UITheme.F_BODY);
        search.setForeground(UITheme.TEXT_HINT);
        search.setBackground(UITheme.BG);
        search.setPreferredSize(new Dimension(320, 36));
        search.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER),
            new EmptyBorder(4, 12, 4, 12)));
        search.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if ("Search products....".equals(search.getText())) {
                    search.setText(""); search.setForeground(UITheme.TEXT_PRI);
                }
            }
            @Override public void focusLost(FocusEvent e) {
                if (search.getText().isEmpty()) {
                    search.setText("Search products...."); search.setForeground(UITheme.TEXT_HINT);
                }
            }
        });
        search.addActionListener(e -> filterBySearch(search.getText()));

        JPanel center = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 12));
        center.setBackground(UITheme.SURFACE);
        center.add(search);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        right.setBackground(UITheme.SURFACE);

        JLabel userLbl = new JLabel("Hi, " + customerName);
        userLbl.setFont(UITheme.F_SMALL);
        userLbl.setForeground(UITheme.TEXT_SEC);

        ModernButton tableBtn = new ModernButton("  Select Table  ", UITheme.PRIMARY, 8);
        tableBtn.setFont(UITheme.F_LABEL);
        tableBtn.setPreferredSize(new Dimension(130, 36));

        right.add(userLbl);
        right.add(tableBtn);

        bar.add(logo,   BorderLayout.WEST);
        bar.add(center, BorderLayout.CENTER);
        bar.add(right,  BorderLayout.EAST);
        return bar;
    }

    private void filterBySearch(String text) {
        if (text.isBlank() || "Search products....".equals(text)) {
            refreshFoodGrid(); return;
        }
        foodGridPanel.removeAll();
        String lower = text.toLowerCase();
        for (FoodItem item : DataStore.getInstance().getFoodItems()) {
            if (item.getName().toLowerCase().contains(lower))
                foodGridPanel.add(buildFoodCard(item));
        }
        foodGridPanel.revalidate();
        foodGridPanel.repaint();
    }

    // =========================================================================
    // POS PANEL — category bar + food grid + order panel
    // =========================================================================

    private JPanel buildPOSPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG);

        JPanel left = new JPanel(new BorderLayout(0, 0));
        left.setBackground(UITheme.BG);
        left.setBorder(new EmptyBorder(14, 16, 14, 8));

        categoryBarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        categoryBarPanel.setBackground(UITheme.BG);
        buildCategoryPills();

        foodGridPanel = new JPanel(new GridLayout(0, 4, 12, 12));
        foodGridPanel.setBackground(UITheme.BG);
        refreshFoodGrid();

        JScrollPane gridScroll = new JScrollPane(foodGridPanel);
        gridScroll.setBorder(null);
        gridScroll.getViewport().setBackground(UITheme.BG);
        gridScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        gridScroll.getVerticalScrollBar().setUnitIncrement(16);

        left.add(categoryBarPanel, BorderLayout.NORTH);
        left.add(gridScroll,       BorderLayout.CENTER);

        panel.add(left,             BorderLayout.CENTER);
        panel.add(buildOrderPanel(), BorderLayout.EAST);
        return panel;
    }

    // ─── Category pills ───────────────────────────────────────────────────────

    private void buildCategoryPills() {
        categoryBarPanel.removeAll();

        List<String> cats = new ArrayList<>();
        cats.add("All");
        cats.addAll(DataStore.getInstance().getCategories());

        for (String cat : cats) {
            categoryBarPanel.add(makePill(cat, cat.equals(selectedCategory)));
        }
        categoryBarPanel.revalidate();
        categoryBarPanel.repaint();
    }

    private JPanel makePill(String label, boolean active) {
        JPanel pill = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 5)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(active ? UITheme.PRIMARY : UITheme.SURFACE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                if (!active) {
                    g2.setColor(UITheme.BORDER);
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, getHeight(), getHeight());
                }
            }
        };
        pill.setOpaque(false);
        pill.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(active ? Color.WHITE : UITheme.TEXT_SEC);
        pill.add(lbl);

        pill.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                selectedCategory = label;
                buildCategoryPills();
                refreshFoodGrid();
            }
        });
        return pill;
    }

    // ─── Food grid ────────────────────────────────────────────────────────────

    private void refreshFoodGrid() {
        foodGridPanel.removeAll();
        for (FoodItem item : DataStore.getInstance().getFoodItems()) {
            if (!"All".equals(selectedCategory) && !item.getCategory().equals(selectedCategory))
                continue;
            foodGridPanel.add(buildFoodCard(item));
        }
        foodGridPanel.revalidate();
        foodGridPanel.repaint();
    }

    private JPanel buildFoodCard(FoodItem item) {
        Color plate = PLATE_COLORS[(item.getId() - 1) % PLATE_COLORS.length];
        final boolean[] hovered = {false};

        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.SURFACE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(hovered[0] ? UITheme.PRIMARY : UITheme.BORDER);
                g2.setStroke(new BasicStroke(hovered[0] ? 2f : 1f));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 14, 14);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(10, 10, 12, 10));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Circular plate image (colour placeholder)
        JPanel circle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int size = Math.min(getWidth(), getHeight()) - 8;
                int x = (getWidth()  - size) / 2;
                int y = (getHeight() - size) / 2;
                // Soft halo
                g2.setColor(new Color(plate.getRed(), plate.getGreen(), plate.getBlue(), 55));
                g2.fillOval(x - 5, y - 5, size + 10, size + 10);
                // Main plate
                g2.setColor(plate);
                g2.fillOval(x, y, size, size);
                // First letter of item name centred in circle
                g2.setColor(new Color(0, 0, 0, 80));
                g2.setFont(new Font("Segoe UI", Font.BOLD, size / 3));
                FontMetrics fm = g2.getFontMetrics();
                String t = item.getName().substring(0, 1).toUpperCase();
                g2.drawString(t, x + (size - fm.stringWidth(t)) / 2,
                        y + (size + fm.getAscent() - fm.getDescent()) / 2);
            }
        };
        circle.setOpaque(false);
        int circleSize = 88;
        circle.setPreferredSize(new Dimension(circleSize, circleSize));
        circle.setMaximumSize(new Dimension(circleSize, circleSize));
        circle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nameLbl = new JLabel("<html><center>" + item.getName() + "</center></html>",
                SwingConstants.CENTER);
        nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        nameLbl.setForeground(UITheme.TEXT_PRI);
        nameLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel priceLbl = new JLabel("Rs. " + String.format("%.2f", item.getPrice()),
                SwingConstants.CENTER);
        priceLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        priceLbl.setForeground(UITheme.PRIMARY);
        priceLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(circle);
        card.add(Box.createVerticalStrut(7));
        card.add(nameLbl);
        card.add(Box.createVerticalStrut(3));
        card.add(priceLbl);

        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { addToCart(item, 1); }
            @Override public void mouseEntered(MouseEvent e) { hovered[0] = true;  card.repaint(); }
            @Override public void mouseExited(MouseEvent e)  { hovered[0] = false; card.repaint(); }
        });
        return card;
    }

    // ─── Cart logic ───────────────────────────────────────────────────────────

    private void addToCart(FoodItem food, int qty) {
        int id = food.getId();
        if (cart.containsKey(id)) {
            OrderItem existing = cart.get(id);
            cart.put(id, new OrderItem(food, existing.getQuantity() + qty));
        } else {
            cart.put(id, new OrderItem(food, qty));
            expandedItemId = id; // auto-expand newly added item
        }
        updateOrderDisplay();
    }

    // =========================================================================
    // ORDER PANEL (right side)
    // =========================================================================

    private JPanel buildOrderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.SURFACE);
        panel.setPreferredSize(new Dimension(370, 0));
        panel.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, UITheme.BORDER));

        panel.add(buildOrderHeader(),  BorderLayout.NORTH);

        orderItemsPanel = new JPanel();
        orderItemsPanel.setLayout(new BoxLayout(orderItemsPanel, BoxLayout.Y_AXIS));
        orderItemsPanel.setBackground(UITheme.SURFACE);

        JScrollPane scroll = new JScrollPane(orderItemsPanel);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(UITheme.SURFACE);
        scroll.getVerticalScrollBar().setUnitIncrement(10);
        panel.add(scroll, BorderLayout.CENTER);

        panel.add(buildOrderBottom(), BorderLayout.SOUTH);
        updateOrderDisplay();
        return panel;
    }

    private JPanel buildOrderHeader() {
        JPanel hdr = new JPanel(new BorderLayout(8, 0));
        hdr.setBackground(UITheme.SURFACE);
        hdr.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER),
            new EmptyBorder(11, 16, 11, 16)));

        ModernButton addCust = new ModernButton("+ Add Customer", UITheme.PRIMARY, 8);
        addCust.setFont(UITheme.F_SMALL);
        addCust.setPreferredSize(new Dimension(140, 34));

        // Right-side icon buttons
        JPanel icons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        icons.setBackground(UITheme.SURFACE);
        for (String ic : new String[]{"+", "⊞", "↻"}) {
            JLabel btn = new JLabel(ic, SwingConstants.CENTER);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
            btn.setForeground(UITheme.TEXT_SEC);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.setPreferredSize(new Dimension(30, 30));
            btn.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));
            if ("↻".equals(ic)) btn.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) { updateOrderDisplay(); }
            });
            icons.add(btn);
        }

        hdr.add(addCust, BorderLayout.WEST);
        hdr.add(icons,   BorderLayout.EAST);
        return hdr;
    }

    private JPanel buildOrderBottom() {
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(UITheme.SURFACE);
        bottom.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER));

        // ── "Add" tab bar ──────────────────────────────────────────────────
        JPanel addBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 8));
        addBar.setBackground(UITheme.BG);
        addBar.setBorder(new EmptyBorder(0, 14, 0, 14));

        JLabel addLbl = new JLabel("Add");
        addLbl.setFont(UITheme.F_SUBHEAD);
        addLbl.setForeground(UITheme.TEXT_PRI);
        addBar.add(addLbl);

        for (String tab : new String[]{"Discount", "Coupon Code", "Note"}) {
            JLabel t = new JLabel("  " + tab);
            t.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            t.setForeground(UITheme.PRIMARY);
            t.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addBar.add(t);
        }

        // ── Summary ────────────────────────────────────────────────────────
        JPanel summary = new JPanel();
        summary.setBackground(UITheme.SURFACE);
        summary.setLayout(new BoxLayout(summary, BoxLayout.Y_AXIS));
        summary.setBorder(new EmptyBorder(10, 16, 10, 16));

        subtotalValueLabel = new JLabel("Rs. 0.00");
        taxValueLabel      = new JLabel("Rs. 0.00");
        payableValueLabel  = new JLabel("Rs. 0.00");

        subtotalValueLabel.setFont(UITheme.F_BODY);
        subtotalValueLabel.setForeground(UITheme.TEXT_PRI);
        taxValueLabel.setFont(UITheme.F_BODY);
        taxValueLabel.setForeground(UITheme.TEXT_PRI);
        payableValueLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        payableValueLabel.setForeground(UITheme.TEXT_PRI);

        summary.add(makeSummaryRow("Subtotal",       subtotalValueLabel, false));
        summary.add(Box.createVerticalStrut(5));
        summary.add(makeSummaryRow("Tax",            taxValueLabel,      false));
        summary.add(Box.createVerticalStrut(8));
        JSeparator sep = new JSeparator();
        sep.setForeground(UITheme.BORDER);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        summary.add(sep);
        summary.add(Box.createVerticalStrut(8));
        summary.add(makeSummaryRow("Payable Amount", payableValueLabel,  true));

        // ── Action buttons ─────────────────────────────────────────────────
        JPanel btnRow = new JPanel(new GridLayout(1, 2, 10, 0));
        btnRow.setBackground(UITheme.SURFACE);
        btnRow.setBorder(new EmptyBorder(10, 16, 16, 16));

        ModernButton holdBtn    = new ModernButton("⊙  Hold Order", UITheme.SECONDARY, 10);
        ModernButton proceedBtn = new ModernButton("⊙  Proceed",    UITheme.SUCCESS,   10);
        holdBtn.setFont(UITheme.F_BUTTON);
        proceedBtn.setFont(UITheme.F_BUTTON);

        holdBtn.addActionListener(e ->
            JOptionPane.showMessageDialog(this,
                "Order held. You can resume it at any time.",
                "Order On Hold", JOptionPane.INFORMATION_MESSAGE));
        proceedBtn.addActionListener(e -> confirmOrder());

        btnRow.add(holdBtn);
        btnRow.add(proceedBtn);

        bottom.add(addBar,  BorderLayout.NORTH);
        bottom.add(summary, BorderLayout.CENTER);
        bottom.add(btnRow,  BorderLayout.SOUTH);
        return bottom;
    }

    private JPanel makeSummaryRow(String title, JLabel valueLabel, boolean bold) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(UITheme.SURFACE);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(bold
            ? new Font("Segoe UI", Font.BOLD, 14)
            : UITheme.F_BODY);
        titleLbl.setForeground(bold ? UITheme.TEXT_PRI : UITheme.TEXT_SEC);
        row.add(titleLbl,   BorderLayout.WEST);
        row.add(valueLabel, BorderLayout.EAST);
        return row;
    }

    // ─── Update order display ─────────────────────────────────────────────────

    private void updateOrderDisplay() {
        orderItemsPanel.removeAll();

        if (cart.isEmpty()) {
            JLabel empty = new JLabel("Tap a food item to add it to the order",
                    SwingConstants.CENTER);
            empty.setFont(UITheme.F_SMALL);
            empty.setForeground(UITheme.TEXT_HINT);
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            orderItemsPanel.add(Box.createVerticalStrut(40));
            orderItemsPanel.add(empty);
        }

        int num = 1;
        double subtotal = 0;
        for (Map.Entry<Integer, OrderItem> entry : cart.entrySet()) {
            int id        = entry.getKey();
            OrderItem oi  = entry.getValue();
            subtotal     += oi.getSubtotal();

            orderItemsPanel.add(buildOrderItemRow(id, oi, num++));

            if (Integer.valueOf(id).equals(expandedItemId))
                orderItemsPanel.add(buildExpandedPanel(id, oi));

            JSeparator line = new JSeparator();
            line.setForeground(UITheme.BORDER);
            line.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
            orderItemsPanel.add(line);
        }

        double tax     = subtotal * 0.10;
        double payable = subtotal + tax;
        subtotalValueLabel.setText(String.format("Rs. %.2f", subtotal));
        taxValueLabel     .setText(String.format("Rs. %.2f", tax));
        payableValueLabel .setText(String.format("Rs. %.2f", payable));

        orderItemsPanel.revalidate();
        orderItemsPanel.repaint();
    }

    private JPanel buildOrderItemRow(int itemId, OrderItem oi, int num) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setBackground(UITheme.SURFACE);
        row.setBorder(new EmptyBorder(10, 14, 10, 14));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Number badge
        JLabel numBadge = new JLabel(String.valueOf(num), SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.PRIMARY);
                g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                super.paintComponent(g);
            }
        };
        numBadge.setForeground(Color.WHITE);
        numBadge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        numBadge.setPreferredSize(new Dimension(26, 26));
        numBadge.setOpaque(false);

        JPanel numWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 6));
        numWrap.setBackground(UITheme.SURFACE);
        numWrap.setPreferredSize(new Dimension(30, 40));
        numWrap.add(numBadge);

        // Centre: name + qty subtitle
        JPanel center = new JPanel();
        center.setBackground(UITheme.SURFACE);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        JLabel nameLbl = new JLabel(oi.getFoodItem().getName());
        nameLbl.setFont(UITheme.F_SUBHEAD);
        nameLbl.setForeground(UITheme.TEXT_PRI);
        JLabel qtyLbl = new JLabel("Qty: " + oi.getQuantity());
        qtyLbl.setFont(UITheme.F_SMALL);
        qtyLbl.setForeground(UITheme.TEXT_SEC);
        center.add(nameLbl);
        center.add(qtyLbl);

        // Right: price + remove
        JPanel right = new JPanel(new BorderLayout(6, 0));
        right.setBackground(UITheme.SURFACE);
        JLabel priceLbl = new JLabel(String.format("Rs.%.0f", oi.getSubtotal()));
        priceLbl.setFont(UITheme.F_SUBHEAD);
        priceLbl.setForeground(UITheme.TEXT_PRI);

        JLabel xBtn = new JLabel("✕");
        xBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        xBtn.setForeground(UITheme.TEXT_HINT);
        xBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        xBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                e.consume();
                cart.remove(itemId);
                if (Integer.valueOf(itemId).equals(expandedItemId)) expandedItemId = null;
                updateOrderDisplay();
            }
            @Override public void mouseEntered(MouseEvent e) { xBtn.setForeground(UITheme.DANGER); }
            @Override public void mouseExited(MouseEvent e)  { xBtn.setForeground(UITheme.TEXT_HINT); }
        });
        right.add(priceLbl, BorderLayout.WEST);
        right.add(xBtn,     BorderLayout.EAST);

        row.add(numWrap, BorderLayout.WEST);
        row.add(center,  BorderLayout.CENTER);
        row.add(right,   BorderLayout.EAST);

        // Toggle expansion on row click
        row.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                expandedItemId = Integer.valueOf(itemId).equals(expandedItemId) ? null : itemId;
                updateOrderDisplay();
            }
            @Override public void mouseEntered(MouseEvent e) {
                setRowBg(row, UITheme.BG);
            }
            @Override public void mouseExited(MouseEvent e) {
                setRowBg(row, UITheme.SURFACE);
            }
        });
        return row;
    }

    private void setRowBg(JPanel row, Color c) {
        row.setBackground(c);
        for (Component comp : row.getComponents())
            if (comp instanceof JPanel) ((JPanel) comp).setBackground(c);
    }

    private JPanel buildExpandedPanel(int itemId, OrderItem oi) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        panel.setBackground(new Color(248, 250, 252));
        panel.setBorder(BorderFactory.createMatteBorder(0, 3, 0, 0, UITheme.PRIMARY));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));

        JLabel qtyLbl = new JLabel("Quantity");
        qtyLbl.setFont(UITheme.F_SMALL);
        qtyLbl.setForeground(UITheme.TEXT_SEC);

        SpinnerNumberModel spinModel = new SpinnerNumberModel(oi.getQuantity(), 1, 99, 1);
        JSpinner spinner = new JSpinner(spinModel);
        spinner.setFont(UITheme.F_BODY);
        spinner.setPreferredSize(new Dimension(72, 30));
        spinner.addChangeListener(e -> {
            int newQty = (int) spinner.getValue();
            cart.put(itemId, new OrderItem(oi.getFoodItem(), newQty));
            updateOrderDisplay();
        });

        JLabel discLbl = new JLabel("Discount(%)");
        discLbl.setFont(UITheme.F_SMALL);
        discLbl.setForeground(UITheme.TEXT_SEC);

        JTextField discField = new JTextField("0", 4);
        discField.setFont(UITheme.F_BODY);
        discField.setPreferredSize(new Dimension(60, 30));
        discField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER),
            new EmptyBorder(2, 6, 2, 6)));

        panel.add(qtyLbl);
        panel.add(spinner);
        panel.add(discLbl);
        panel.add(discField);
        return panel;
    }

    // =========================================================================
    // ORDER CONFIRMATION
    // =========================================================================

    private void confirmOrder() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Your order is empty.\nTap food cards to add items first.",
                "Empty Order", JOptionPane.WARNING_MESSAGE);
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Order for: ").append(customerName).append("\n");
        sb.append("─────────────────────────────────\n");
        double total = 0;
        for (OrderItem oi : cart.values()) {
            sb.append(String.format("  %-24s x%d  Rs. %.2f%n",
                oi.getFoodItem().getName(), oi.getQuantity(), oi.getSubtotal()));
            total += oi.getSubtotal();
        }
        double tax = total * 0.10;
        sb.append("─────────────────────────────────\n");
        sb.append(String.format("  Subtotal:         Rs. %.2f%n", total));
        sb.append(String.format("  Tax (10%%):        Rs. %.2f%n", tax));
        sb.append(String.format("  PAYABLE AMOUNT:   Rs. %.2f%n", total + tax));
        sb.append("\nConfirm and place this order?");

        int choice = JOptionPane.showConfirmDialog(this,
            sb.toString(), "Confirm Order", JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) return;

        int orderId = DataStore.getInstance().getNextOrderId();
        Order order = new Order(orderId, customerName);
        for (OrderItem oi : cart.values()) order.addItem(oi);
        order.confirm();
        DataStore.getInstance().addOrder(order);

        JOptionPane.showMessageDialog(this,
            String.format("Order #%d placed!%n%nThank you, %s!%nPayable: Rs. %.2f",
                orderId, customerName, order.getTotalAmount() * 1.10),
            "Order Confirmed!", JOptionPane.INFORMATION_MESSAGE);

        cart.clear();
        expandedItemId = null;
        updateOrderDisplay();
        loadMyOrders();
    }

    // =========================================================================
    // MY ORDERS PANEL
    // =========================================================================

    private JPanel buildMyOrdersPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setBackground(UITheme.BG);
        panel.setBorder(new EmptyBorder(18, 18, 18, 18));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        toolbar.setBackground(UITheme.BG);
        ModernButton refreshBtn = new ModernButton("↻  Refresh", UITheme.SECONDARY, 6);
        refreshBtn.addActionListener(e -> loadMyOrders());
        toolbar.add(refreshBtn);

        // Orders table card
        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(UITheme.SURFACE);
        tableCard.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));

        JPanel tableHdr = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tableHdr.setBackground(UITheme.SIDEBAR_BG);
        tableHdr.setBorder(new EmptyBorder(10, 16, 10, 16));
        JLabel tableTitle = new JLabel("My Order History  —  " + customerName);
        tableTitle.setFont(UITheme.F_SUBHEAD);
        tableTitle.setForeground(Color.WHITE);
        tableHdr.add(tableTitle);

        String[] cols = {"Order ID", "Items", "Total (Rs.)", "Date & Time", "Status"};
        myOrdersTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        myOrdersTable = buildStyledTable(myOrdersTableModel);
        myOrdersTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        myOrdersTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        myOrdersTable.getColumnModel().getColumn(2).setPreferredWidth(140);
        myOrdersTable.getColumnModel().getColumn(3).setPreferredWidth(220);
        myOrdersTable.getColumnModel().getColumn(4).setPreferredWidth(100);

        JScrollPane tableScroll = new JScrollPane(myOrdersTable);
        tableScroll.setBorder(null);
        tableScroll.getViewport().setBackground(UITheme.SURFACE);
        tableCard.add(tableHdr,    BorderLayout.NORTH);
        tableCard.add(tableScroll, BorderLayout.CENTER);

        // Receipt card
        JPanel detailCard = new JPanel(new BorderLayout());
        detailCard.setBackground(UITheme.SURFACE);
        detailCard.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));

        JPanel detailHdr = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        detailHdr.setBackground(UITheme.PRIMARY_DARK);
        detailHdr.setBorder(new EmptyBorder(10, 16, 10, 16));
        JLabel detailTitle = new JLabel("Order Receipt  (click a row to view)");
        detailTitle.setFont(UITheme.F_SUBHEAD);
        detailTitle.setForeground(Color.WHITE);
        detailHdr.add(detailTitle);

        myOrderDetailArea = new JTextArea("Click any order row above to see the full receipt.");
        myOrderDetailArea.setFont(UITheme.F_MONO);
        myOrderDetailArea.setEditable(false);
        myOrderDetailArea.setBackground(new Color(250, 250, 250));
        myOrderDetailArea.setBorder(new EmptyBorder(12, 14, 12, 14));
        JScrollPane detailScroll = new JScrollPane(myOrderDetailArea);
        detailScroll.setBorder(null);
        detailCard.add(detailHdr,    BorderLayout.NORTH);
        detailCard.add(detailScroll, BorderLayout.CENTER);
        detailCard.setPreferredSize(new Dimension(0, 240));

        myOrdersTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && myOrdersTable.getSelectedRow() != -1) {
                int orderId = (int) myOrdersTableModel.getValueAt(
                    myOrdersTable.getSelectedRow(), 0);
                renderMyOrderDetail(orderId);
            }
        });

        panel.add(toolbar,    BorderLayout.NORTH);
        panel.add(tableCard,  BorderLayout.CENTER);
        panel.add(detailCard, BorderLayout.SOUTH);

        loadMyOrders();
        return panel;
    }

    private void loadMyOrders() {
        if (myOrdersTableModel == null) return;
        myOrdersTableModel.setRowCount(0);
        for (Order order : DataStore.getInstance().getOrders()) {
            if (!order.getCustomerName().equals(customerName)) continue;
            myOrdersTableModel.addRow(new Object[]{
                order.getOrderId(),
                order.getItems().size() + " item(s)",
                String.format("%.2f", order.getTotalAmount() * 1.10),
                order.getFormattedTime(),
                order.isConfirmed() ? "Confirmed" : "Pending"
            });
        }
        if (myOrdersTableModel.getRowCount() == 0 && myOrderDetailArea != null)
            myOrderDetailArea.setText("You have not placed any orders yet.\nGo back to POS to place your first order!");
    }

    private void renderMyOrderDetail(int orderId) {
        for (Order order : DataStore.getInstance().getOrders()) {
            if (order.getOrderId() != orderId) continue;
            StringBuilder sb = new StringBuilder();
            sb.append("══════════════════════════════════════════\n");
            sb.append("   ORDER #").append(orderId).append("   —   CITY BITES POS\n");
            sb.append("══════════════════════════════════════════\n");
            sb.append(String.format("  Customer : %s%n",  order.getCustomerName()));
            sb.append(String.format("  Date     : %s%n",  order.getFormattedTime()));
            sb.append(String.format("  Status   : %s%n",  order.isConfirmed() ? "Confirmed" : "Pending"));
            sb.append("──────────────────────────────────────────\n");
            sb.append(String.format("  %-26s %5s  %10s%n", "Item", "Qty", "Subtotal"));
            sb.append("──────────────────────────────────────────\n");
            for (OrderItem oi : order.getItems())
                sb.append(String.format("  %-26s %5d  Rs.%8.2f%n",
                    oi.getFoodItem().getName(), oi.getQuantity(), oi.getSubtotal()));
            sb.append("══════════════════════════════════════════\n");
            sb.append(String.format("  Subtotal :            Rs.%8.2f%n", order.getTotalAmount()));
            sb.append(String.format("  Tax (10%%) :           Rs.%8.2f%n", order.getTotalAmount() * 0.10));
            sb.append(String.format("  PAYABLE AMOUNT :      Rs.%8.2f%n", order.getTotalAmount() * 1.10));
            sb.append("══════════════════════════════════════════\n");
            myOrderDetailArea.setText(sb.toString());
            myOrderDetailArea.setCaretPosition(0);
            return;
        }
    }

    // ─── Table helper ─────────────────────────────────────────────────────────

    private JTable buildStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
                Component c = super.prepareRenderer(renderer, row, col);
                if (isRowSelected(row)) {
                    c.setBackground(UITheme.TABLE_SEL);
                    c.setForeground(UITheme.TEXT_PRI);
                } else {
                    c.setBackground(row % 2 == 0 ? UITheme.TABLE_ODD : UITheme.TABLE_EVEN);
                    c.setForeground(UITheme.TEXT_PRI);
                }
                return c;
            }
        };
        table.setFont(UITheme.F_BODY);
        table.setRowHeight(34);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setFont(UITheme.F_LABEL);
        table.getTableHeader().setBackground(UITheme.TABLE_HDR_BG);
        table.getTableHeader().setForeground(UITheme.TABLE_HDR_FG);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setPreferredSize(new Dimension(0, 38));
        return table;
    }
}
