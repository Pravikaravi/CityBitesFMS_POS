package citybitesfms.ui;

import citybitesfms.data.FoodDataStore;
import citybitesfms.model.CartItem;
import citybitesfms.model.FoodItem;
import citybitesfms.model.Order;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.*;

public class CustomerDashboard extends JFrame {

    private static final String CARD_MENU   = "MENU";
    private static final String CARD_ORDERS = "MYORDERS";

    private static final double TAX_RATE = 0.10;

    private static final Color[] PLATE_COLORS = {
        new Color(255, 183, 107), new Color(147, 197, 253), new Color(167, 243, 208),
        new Color(253, 164, 175), new Color(196, 181, 253), new Color(253, 230, 138),
        new Color(134, 239, 172), new Color(252, 165, 165),
    };

    private static final Map<Integer, BufferedImage> IMAGE_CACHE = new HashMap<>();

    private final LinkedHashMap<Integer, CartItem> cart = new LinkedHashMap<>();

    private final String customerName;
    private String       selectedCategory = "All";

    private JPanel   categoryBar;
    private JPanel   foodGrid;
    private CardLayout contentCards;
    private JPanel     contentPanel;

    private JPanel  cartItemsPanel;
    private JLabel  subtotalLabel;
    private JLabel  discountLabel;
    private JLabel  taxLabel;
    private JLabel  totalLabel;

    private DefaultTableModel myOrdersModel;
    private JTable            myOrdersTable;

    public CustomerDashboard(String customerName) {
        this.customerName = customerName;
        setTitle("City Bites — Customer Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 680));
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        buildUI();
        setVisible(true);
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.add(buildSidebar(),      BorderLayout.WEST);
        root.add(buildContentArea(),  BorderLayout.CENTER);
        setContentPane(root);
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(UITheme.SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JPanel nameCell = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 18));
        nameCell.setBackground(UITheme.SIDEBAR_BG);
        nameCell.setMaximumSize(new Dimension(200, 62));
        JLabel appName = new JLabel("City Bites");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 22));
        appName.setForeground(Color.WHITE);
        nameCell.add(appName);
        sidebar.add(nameCell);

        JSeparator sep = new JSeparator();
        sep.setForeground(UITheme.SIDEBAR_HOVER);
        sep.setMaximumSize(new Dimension(200, 1));
        sidebar.add(sep);

        JPanel welcome = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        welcome.setBackground(UITheme.SIDEBAR_BG);
        welcome.setMaximumSize(new Dimension(200, 46));
        JLabel wlbl = new JLabel("Hi, " + customerName + " 👋");
        wlbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        wlbl.setForeground(UITheme.SIDEBAR_ICON);
        welcome.add(wlbl);
        sidebar.add(welcome);

        sidebar.add(Box.createVerticalStrut(6));
        sidebar.add(makeSideBtn("  ⌂  Menu",      () -> contentCards.show(contentPanel, CARD_MENU),   true));
        sidebar.add(makeSideBtn("  ☰  My Orders", () -> { loadMyOrders(); contentCards.show(contentPanel, CARD_ORDERS); }, false));
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(makeSideBtn("  ⇦  Logout",    () -> { dispose(); new LoginFrame(); }, false));
        sidebar.add(Box.createVerticalStrut(14));
        return sidebar;
    }

    private JPanel makeSideBtn(String label, Runnable action, boolean active) {
        JPanel btn = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 12));
        btn.setBackground(active ? UITheme.SIDEBAR_HOVER : UITheme.SIDEBAR_BG);
        btn.setMaximumSize(new Dimension(200, 46));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lbl.setForeground(active ? Color.WHITE : UITheme.SIDEBAR_ICON);
        btn.add(lbl);
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e)  { action.run(); }
            @Override public void mouseEntered(MouseEvent e)  { btn.setBackground(UITheme.SIDEBAR_HOVER); lbl.setForeground(Color.WHITE); }
            @Override public void mouseExited(MouseEvent e)   { btn.setBackground(active ? UITheme.SIDEBAR_HOVER : UITheme.SIDEBAR_BG); lbl.setForeground(active ? Color.WHITE : UITheme.SIDEBAR_ICON); }
        });
        return btn;
    }

    private JPanel buildContentArea() {
        contentCards = new CardLayout();
        contentPanel = new JPanel(contentCards);
        contentPanel.setBackground(UITheme.BG);
        contentPanel.add(buildMenuPanel(),     CARD_MENU);
        contentPanel.add(buildMyOrdersPanel(), CARD_ORDERS);
        return contentPanel;
    }

    private JPanel buildMenuPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG);
        panel.add(buildFoodArea(),  BorderLayout.CENTER);
        panel.add(buildOrderPanel(), BorderLayout.EAST);
        return panel;
    }

    private JPanel buildFoodArea() {
        JPanel area = new JPanel(new BorderLayout(0, 0));
        area.setBackground(UITheme.BG);
        area.setBorder(new EmptyBorder(18, 18, 18, 10));

        JLabel title = new JLabel("Food Menu");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(UITheme.TEXT_PRI);
        title.setBorder(new EmptyBorder(0, 0, 12, 0));

        categoryBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        categoryBar.setBackground(UITheme.BG);
        buildCategoryButtons();

        JPanel north = new JPanel(new BorderLayout());
        north.setBackground(UITheme.BG);
        north.add(title,       BorderLayout.NORTH);
        north.add(categoryBar, BorderLayout.SOUTH);

        foodGrid = new JPanel(new GridLayout(0, 3, 14, 14));
        foodGrid.setBackground(UITheme.BG);
        refreshGrid();

        JScrollPane scroll = new JScrollPane(foodGrid);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(UITheme.BG);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBorder(new EmptyBorder(12, 0, 0, 0));

        area.add(north,  BorderLayout.NORTH);
        area.add(scroll, BorderLayout.CENTER);
        return area;
    }

    private void buildCategoryButtons() {
        categoryBar.removeAll();
        String[] cats = buildCategoryList();
        for (String cat : cats) {
            boolean active = cat.equals(selectedCategory);
            JButton btn = new JButton(cat) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(active ? UITheme.PRIMARY : UITheme.SURFACE);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                    if (!active) { g2.setColor(UITheme.BORDER); g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, getHeight(), getHeight()); }
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
            btn.setForeground(active ? Color.WHITE : UITheme.TEXT_SEC);
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setOpaque(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.setPreferredSize(new Dimension(btn.getPreferredSize().width + 22, 36));
            btn.addActionListener(e -> {
                selectedCategory = cat;
                buildCategoryButtons();
                categoryBar.revalidate();
                categoryBar.repaint();
                refreshGrid();
            });
            categoryBar.add(btn);
        }
        categoryBar.revalidate();
        categoryBar.repaint();
    }

    private String[] buildCategoryList() {
        java.util.List<String> list = new ArrayList<>();
        list.add("All");
        Set<String> seen = new LinkedHashSet<>();
        for (FoodItem item : FoodDataStore.getFoodItems()) seen.add(item.getCategory());
        list.addAll(seen);
        return list.toArray(new String[0]);
    }

    private void refreshGrid() {
        foodGrid.removeAll();
        for (FoodItem item : FoodDataStore.getFoodItems()) {
            if (!item.isAvailable()) continue;
            if (!"All".equals(selectedCategory) && !item.getCategory().equals(selectedCategory)) continue;
            foodGrid.add(buildFoodCard(item));
        }
        foodGrid.revalidate();
        foodGrid.repaint();
    }

    private JPanel buildFoodCard(FoodItem item) {
        Color plate   = PLATE_COLORS[(item.getId() - 1) % PLATE_COLORS.length];
        BufferedImage img = loadFoodImage(item.getId());
        final boolean[] hovered = {false};

        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(new Color(0, 0, 0, 12));
                g2.fillRoundRect(3, 4, getWidth() - 3, getHeight() - 3, 14, 14);
                
                g2.setColor(UITheme.SURFACE);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                
                g2.setColor(hovered[0] ? UITheme.PRIMARY : UITheme.BORDER);
                g2.setStroke(new BasicStroke(hovered[0] ? 2f : 1f));
                g2.drawRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 14, 14);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(12, 12, 14, 12));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel circle = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING,      RenderingHints.VALUE_RENDER_QUALITY);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,  RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                int size = Math.min(getWidth(), getHeight()) - 8;
                int x = (getWidth() - size) / 2, y = (getHeight() - size) / 2;
                if (img != null) {
                    g2.setColor(new Color(0, 0, 0, 15));
                    g2.fillOval(x - 2, y + 3, size + 4, size + 4);
                    Shape clip = new Ellipse2D.Float(x, y, size, size);
                    g2.setClip(clip);
                    g2.drawImage(img, x, y, size, size, null);
                    g2.setClip(null);
                    g2.setColor(new Color(255, 255, 255, 180));
                    g2.setStroke(new BasicStroke(2.5f));
                    g2.drawOval(x + 1, y + 1, size - 2, size - 2);
                    g2.setColor(UITheme.BORDER);
                    g2.setStroke(new BasicStroke(1f));
                    g2.drawOval(x, y, size, size);
                } else {
                    g2.setColor(new Color(plate.getRed(), plate.getGreen(), plate.getBlue(), 55));
                    g2.fillOval(x - 5, y - 5, size + 10, size + 10);
                    g2.setColor(plate);
                    g2.fillOval(x, y, size, size);
                    g2.setColor(new Color(0, 0, 0, 70));
                    g2.setFont(new Font("Segoe UI", Font.BOLD, size / 3));
                    FontMetrics fm = g2.getFontMetrics();
                    String t = item.getName().substring(0, 1).toUpperCase();
                    g2.drawString(t, x + (size - fm.stringWidth(t)) / 2,
                            y + (size + fm.getAscent() - fm.getDescent()) / 2);
                }
            }
        };
        circle.setOpaque(false);
        circle.setPreferredSize(new Dimension(100, 100));
        circle.setMaximumSize(new Dimension(100, 100));
        circle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nameLbl = new JLabel("<html><center>" + item.getName() + "</center></html>",
                SwingConstants.CENTER);
        nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLbl.setForeground(UITheme.TEXT_PRI);
        nameLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel priceLbl = new JLabel("Rs. " + String.format("%.2f", item.getPrice()),
                SwingConstants.CENTER);
        priceLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        priceLbl.setForeground(UITheme.PRIMARY);
        priceLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton addBtn = new JButton("+ Add") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? UITheme.PRIMARY_DARK : UITheme.PRIMARY);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        addBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        addBtn.setForeground(Color.WHITE);
        addBtn.setContentAreaFilled(false);
        addBtn.setBorderPainted(false);
        addBtn.setFocusPainted(false);
        addBtn.setOpaque(false);
        addBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        addBtn.setMaximumSize(new Dimension(100, 34));
        addBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addBtn.addActionListener(e -> addToCart(item));

        card.add(circle);
        card.add(Box.createVerticalStrut(8));
        card.add(nameLbl);
        card.add(Box.createVerticalStrut(4));
        card.add(priceLbl);
        card.add(Box.createVerticalStrut(8));
        card.add(addBtn);

        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hovered[0] = true;  card.repaint(); }
            @Override public void mouseExited(MouseEvent e)  { hovered[0] = false; card.repaint(); }
        });
        return card;
    }

    private JPanel buildOrderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.SURFACE);
        panel.setPreferredSize(new Dimension(360, 0));
        panel.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, UITheme.BORDER));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.SIDEBAR_BG);
        header.setBorder(new EmptyBorder(14, 16, 14, 16));
        JLabel title = new JLabel("Order Details");
        title.setFont(new Font("Segoe UI", Font.BOLD, 17));
        title.setForeground(Color.WHITE);
        JLabel custLbl = new JLabel(customerName);
        custLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        custLbl.setForeground(UITheme.SIDEBAR_ICON);
        header.add(title,   BorderLayout.WEST);
        header.add(custLbl, BorderLayout.EAST);

        cartItemsPanel = new JPanel();
        cartItemsPanel.setLayout(new BoxLayout(cartItemsPanel, BoxLayout.Y_AXIS));
        cartItemsPanel.setBackground(UITheme.SURFACE);
        JScrollPane cartScroll = new JScrollPane(cartItemsPanel);
        cartScroll.setBorder(null);
        cartScroll.getViewport().setBackground(UITheme.SURFACE);
        cartScroll.getVerticalScrollBar().setUnitIncrement(10);

        JPanel totals = new JPanel();
        totals.setBackground(UITheme.SURFACE);
        totals.setLayout(new BoxLayout(totals, BoxLayout.Y_AXIS));
        totals.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER),
            new EmptyBorder(14, 16, 0, 16)));

        subtotalLabel = new JLabel("Rs. 0.00");
        discountLabel = new JLabel("Rs. 0.00");
        taxLabel      = new JLabel("Rs. 0.00");
        totalLabel    = new JLabel("Rs. 0.00");

        for (JLabel l : new JLabel[]{subtotalLabel, discountLabel, taxLabel}) {
            l.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            l.setForeground(UITheme.TEXT_PRI);
        }
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        totalLabel.setForeground(UITheme.PRIMARY);

        totals.add(makeTotalRow("Subtotal",  subtotalLabel));
        totals.add(Box.createVerticalStrut(6));
        totals.add(makeTotalRow("Discount",  discountLabel));
        totals.add(Box.createVerticalStrut(6));
        totals.add(makeTotalRow("Tax (10%)", taxLabel));
        totals.add(Box.createVerticalStrut(8));
        JSeparator div = new JSeparator();
        div.setForeground(UITheme.BORDER);
        div.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        totals.add(div);
        totals.add(Box.createVerticalStrut(8));
        totals.add(makeTotalRow("Total", totalLabel));
        totals.add(Box.createVerticalStrut(14));

        JButton confirmBtn = new JButton("Confirm Order") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? UITheme.PRIMARY_DARK : UITheme.PRIMARY);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        confirmBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        confirmBtn.setForeground(Color.WHITE);
        confirmBtn.setContentAreaFilled(false);
        confirmBtn.setBorderPainted(false);
        confirmBtn.setFocusPainted(false);
        confirmBtn.setOpaque(false);
        confirmBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        confirmBtn.addActionListener(e -> confirmOrder());

        JButton clearBtn = new JButton("Clear") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(180, 180, 180));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        clearBtn.setFont(UITheme.F_BUTTON);
        clearBtn.setForeground(Color.WHITE);
        clearBtn.setContentAreaFilled(false);
        clearBtn.setBorderPainted(false);
        clearBtn.setFocusPainted(false);
        clearBtn.setOpaque(false);
        clearBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        clearBtn.addActionListener(e -> { cart.clear(); updateOrderDisplay(); });

        JPanel btnRow = new JPanel(new GridLayout(1, 2, 10, 0));
        btnRow.setBackground(UITheme.SURFACE);
        btnRow.setBorder(new EmptyBorder(0, 16, 16, 16));
        btnRow.add(confirmBtn);
        btnRow.add(clearBtn);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(UITheme.SURFACE);
        bottomPanel.add(totals,  BorderLayout.CENTER);
        bottomPanel.add(btnRow,  BorderLayout.SOUTH);

        panel.add(header,     BorderLayout.NORTH);
        panel.add(cartScroll, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        updateOrderDisplay();
        return panel;
    }

    private JPanel makeTotalRow(String title, JLabel value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(UITheme.SURFACE);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        JLabel tl = new JLabel(title);
        tl.setFont(value.getFont());
        tl.setForeground(UITheme.TEXT_SEC);
        row.add(tl,    BorderLayout.WEST);
        row.add(value, BorderLayout.EAST);
        return row;
    }

    private void addToCart(FoodItem food) {
        int id = food.getId();
        if (cart.containsKey(id)) {
            CartItem existing = cart.get(id);
            existing.setQuantity(existing.getQuantity() + 1);
        } else {
            cart.put(id, new CartItem(food, 1));
        }
        updateOrderDisplay();
    }

    private void updateOrderDisplay() {
        cartItemsPanel.removeAll();

        if (cart.isEmpty()) {
            JLabel empty = new JLabel("No items added yet", SwingConstants.CENTER);
            empty.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            empty.setForeground(UITheme.TEXT_HINT);
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            cartItemsPanel.add(Box.createVerticalStrut(40));
            cartItemsPanel.add(empty);
        }

        double subtotal = 0;
        for (Map.Entry<Integer, CartItem> entry : cart.entrySet()) {
            CartItem ci = entry.getValue();
            subtotal += ci.getSubtotal();
            cartItemsPanel.add(buildCartRow(entry.getKey(), ci));
            JSeparator sep = new JSeparator();
            sep.setForeground(UITheme.BORDER);
            sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
            cartItemsPanel.add(sep);
        }

        double tax     = subtotal * TAX_RATE;
        double total   = subtotal + tax;
        subtotalLabel.setText(String.format("Rs. %.2f", subtotal));
        discountLabel .setText("Rs. 0.00");
        taxLabel      .setText(String.format("Rs. %.2f", tax));
        totalLabel    .setText(String.format("Rs. %.2f", total));

        cartItemsPanel.revalidate();
        cartItemsPanel.repaint();
    }

    private JPanel buildCartRow(int itemId, CartItem ci) {
        
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(UITheme.SURFACE);
        row.setBorder(new EmptyBorder(10, 14, 10, 14));
        
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        JPanel left = new JPanel();
        left.setBackground(UITheme.SURFACE);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel nameLbl = new JLabel(ci.getFoodItem().getName());
        nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLbl.setForeground(UITheme.TEXT_PRI);
        nameLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel unitLbl = new JLabel(
            "Rs. " + String.format("%.2f", ci.getFoodItem().getPrice()) + " / unit");
        unitLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        unitLbl.setForeground(UITheme.TEXT_SEC);
        unitLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel qtyRow = new JPanel(new GridBagLayout());
        qtyRow.setBackground(UITheme.SURFACE);
        qtyRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        qtyRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        qtyRow.setPreferredSize(new Dimension(120, 38));

        JButton minusBtn = makeQtyButton("−");
        JButton plusBtn  = makeQtyButton("+");

        JLabel qtyLbl = new JLabel(String.valueOf(ci.getQuantity()), SwingConstants.CENTER);
        qtyLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        qtyLbl.setForeground(UITheme.TEXT_PRI);
        qtyLbl.setOpaque(true);
        qtyLbl.setBackground(UITheme.BG);
        qtyLbl.setPreferredSize(new Dimension(40, 32));
        qtyLbl.setMaximumSize(new Dimension(40, 32));
        qtyLbl.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(0, 0, 0, 4);
        gc.gridy  = 0;
        gc.gridx  = 0; qtyRow.add(minusBtn, gc);
        gc.gridx  = 1; gc.insets = new Insets(0, 0, 0, 4); qtyRow.add(qtyLbl, gc);
        gc.gridx  = 2; gc.insets = new Insets(0, 0, 0, 0); qtyRow.add(plusBtn, gc);

        minusBtn.addActionListener(e -> {
            int newQty = ci.getQuantity() - 1;
            if (newQty <= 0) cart.remove(itemId);
            else             ci.setQuantity(newQty);
            updateOrderDisplay();
        });
        plusBtn.addActionListener(e -> {
            ci.setQuantity(ci.getQuantity() + 1);
            updateOrderDisplay();
        });

        left.add(nameLbl);
        left.add(Box.createVerticalStrut(3));
        left.add(unitLbl);
        left.add(Box.createVerticalStrut(6));
        left.add(qtyRow);

        JPanel right = new JPanel();
        right.setBackground(UITheme.SURFACE);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

        JLabel subtotalLbl = new JLabel(
            String.format("Rs. %.2f", ci.getSubtotal()), SwingConstants.RIGHT);
        subtotalLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        subtotalLbl.setForeground(UITheme.PRIMARY);
        subtotalLbl.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JLabel xBtn = new JLabel("✕ Remove", SwingConstants.RIGHT);
        xBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        xBtn.setForeground(UITheme.TEXT_HINT);
        xBtn.setAlignmentX(Component.RIGHT_ALIGNMENT);
        xBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        xBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                e.consume(); cart.remove(itemId); updateOrderDisplay();
            }
            @Override public void mouseEntered(MouseEvent e) {
                xBtn.setForeground(UITheme.DANGER);
            }
            @Override public void mouseExited(MouseEvent e) {
                xBtn.setForeground(UITheme.TEXT_HINT);
            }
        });

        right.add(subtotalLbl);
        right.add(Box.createVerticalStrut(6));
        right.add(xBtn);

        row.add(left,  BorderLayout.CENTER);
        row.add(right, BorderLayout.EAST);

        row.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                row.setBackground(new Color(245, 252, 247));
                left.setBackground(new Color(245, 252, 247));
                right.setBackground(new Color(245, 252, 247));
                qtyRow.setBackground(new Color(245, 252, 247));
            }
            @Override public void mouseExited(MouseEvent e) {
                row.setBackground(UITheme.SURFACE);
                left.setBackground(UITheme.SURFACE);
                right.setBackground(UITheme.SURFACE);
                qtyRow.setBackground(UITheme.SURFACE);
            }
        });

        return row;
    }

    private JButton makeQtyButton(String symbol) {
        JButton btn = new JButton(symbol) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? UITheme.PRIMARY_DARK : UITheme.PRIMARY);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 7, 7);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setPreferredSize(new Dimension(30, 30));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void confirmOrder() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your cart is empty.\nAdd some items first!",
                "Empty Cart", JOptionPane.WARNING_MESSAGE); return;
        }
        StringBuilder sb = new StringBuilder("Order for: ").append(customerName).append("\n");
        sb.append("────────────────────────────────\n");
        double sub = 0;
        for (CartItem ci : cart.values()) {
            sb.append(String.format("  %-22s x%d  Rs. %.2f%n",
                ci.getFoodItem().getName(), ci.getQuantity(), ci.getSubtotal()));
            sub += ci.getSubtotal();
        }
        double tax = sub * TAX_RATE, total = sub + tax;
        sb.append("────────────────────────────────\n");
        sb.append(String.format("  Subtotal:     Rs. %.2f%n", sub));
        sb.append(String.format("  Tax (10%%):    Rs. %.2f%n", tax));
        sb.append(String.format("  TOTAL:        Rs. %.2f%n", total));
        sb.append("\nConfirm and place this order?");

        int choice = JOptionPane.showConfirmDialog(this, sb.toString(),
            "Confirm Order", JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) return;

        int orderId   = FoodDataStore.getNextOrderId();
        Order order   = new Order(orderId, customerName);
        for (CartItem ci : cart.values()) order.addItem(ci);
        order.confirm();
        FoodDataStore.addOrder(order);

        JOptionPane.showMessageDialog(this,
            String.format("Order #%d confirmed!%nThank you, %s!%nTotal: Rs. %.2f",
                orderId, customerName, total),
            "Order Placed", JOptionPane.INFORMATION_MESSAGE);

        cart.clear();
        updateOrderDisplay();
    }

    private JPanel buildMyOrdersPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(UITheme.BG);
        panel.setBorder(new EmptyBorder(22, 22, 22, 22));

        JLabel title = new JLabel("My Orders");
        title.setFont(UITheme.F_HEADING);
        title.setForeground(UITheme.TEXT_PRI);
        title.setBorder(new EmptyBorder(0, 0, 14, 0));

        JButton refreshBtn = new JButton("↻  Refresh") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.PRIMARY);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        refreshBtn.setFont(UITheme.F_BUTTON);
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setContentAreaFilled(false);
        refreshBtn.setBorderPainted(false);
        refreshBtn.setFocusPainted(false);
        refreshBtn.setOpaque(false);
        refreshBtn.setPreferredSize(new Dimension(110, 36));
        refreshBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        refreshBtn.addActionListener(e -> loadMyOrders());

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(UITheme.BG);
        topBar.add(title,      BorderLayout.WEST);
        topBar.add(refreshBtn, BorderLayout.EAST);

        String[] cols = {"Order ID", "Items", "Total (Rs.)", "Date", "Status"};
        myOrdersModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        myOrdersTable = buildStyledTable(myOrdersModel);
        myOrdersTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        myOrdersTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        myOrdersTable.getColumnModel().getColumn(2).setPreferredWidth(140);
        myOrdersTable.getColumnModel().getColumn(3).setPreferredWidth(200);
        myOrdersTable.getColumnModel().getColumn(4).setPreferredWidth(100);

        JScrollPane scroll = new JScrollPane(myOrdersTable);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));
        scroll.getViewport().setBackground(UITheme.SURFACE);

        panel.add(topBar, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        loadMyOrders();
        return panel;
    }

    private void loadMyOrders() {
        if (myOrdersModel == null) return;
        myOrdersModel.setRowCount(0);
        for (Order order : FoodDataStore.getOrders()) {
            if (!order.getCustomerName().equals(customerName)) continue;
            myOrdersModel.addRow(new Object[]{
                order.getOrderId(),
                order.getItems().size() + " item(s)",
                String.format("%.2f", order.getTotalAmount() * (1 + TAX_RATE)),
                order.getOrderDate(),
                order.isConfirmed() ? "Confirmed" : "Pending"
            });
        }
    }

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

    private JTable buildStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model) {
            @Override public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (isRowSelected(row)) { c.setBackground(UITheme.TABLE_SEL); c.setForeground(UITheme.TEXT_PRI); }
                else { c.setBackground(row % 2 == 0 ? UITheme.TABLE_ODD : UITheme.TABLE_EVEN); c.setForeground(UITheme.TEXT_PRI); }
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
