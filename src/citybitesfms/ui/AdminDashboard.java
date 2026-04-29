package citybitesfms.ui;

import citybitesfms.data.DataStore;
import citybitesfms.model.FoodItem;
import citybitesfms.model.Order;
import citybitesfms.model.OrderItem;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * Full-screen Admin Dashboard for City Bites — styled to match the POS theme.
 *
 * Layout:
 *   WEST   — dark sidebar matching the customer POS sidebar
 *   CENTER — top bar (logo + admin badge + logout) above a tabbed content area:
 *              Tab 1 : Menu Management (CRUD)
 *              Tab 2 : Customer Orders viewer
 *
 * Programming paradigms demonstrated:
 *   OOP          — AdminDashboard extends JFrame; depends on DataStore, FoodItem, Order
 *   Event-Driven — every button uses an ActionListener; table uses ListSelectionListener
 *   Procedural   — loadMenuItems(), validateMenuForm() follow sequential steps
 *
 * @author NovaSoft Solutions (PVT) Ltd
 * @version 4.0
 */
public class AdminDashboard extends JFrame {

    private DefaultTableModel menuTableModel;
    private DefaultTableModel ordersTableModel;
    private JTable            menuTable;
    private JTable            ordersTable;
    private JTextField        nameField;
    private JTextField        priceField;
    private JComboBox<String> categoryCombo;
    private JTextArea         orderDetailArea;

    private final String adminName;

    // ─── Sidebar button list ───────────────────────────────────────────────────
    private final java.util.List<JPanel> sidebarBtns = new java.util.ArrayList<>();
    private JTabbedPane mainTabs;

    public AdminDashboard(String adminName) {
        this.adminName = adminName;
        setTitle("City Bites — Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 650));
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        buildUI();
        setVisible(true);
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG);

        root.add(buildSidebar(), BorderLayout.WEST);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(UITheme.BG);
        main.add(buildTopBar(),     BorderLayout.NORTH);
        main.add(buildTabbedPane(), BorderLayout.CENTER);

        root.add(main, BorderLayout.CENTER);
        setContentPane(root);

        loadMenuItems();
        loadOrders();
    }

    // =========================================================================
    // SIDEBAR
    // =========================================================================

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(UITheme.SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(82, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        sidebar.add(buildLogoCell());
        sidebar.add(Box.createVerticalStrut(8));

        sidebar.add(makeSideNavBtn("⊞",  "Menu",    0, () -> {
            mainTabs.setSelectedIndex(0);
            setActiveSideBtn(0);
        }));
        sidebar.add(makeSideNavBtn("☰",  "Orders",  1, () -> {
            mainTabs.setSelectedIndex(1);
            setActiveSideBtn(1);
        }));
        sidebar.add(makeSideNavBtn("⚙",  "Settings",2, () -> setActiveSideBtn(2)));

        sidebar.add(Box.createVerticalGlue());

        sidebar.add(makeSideNavBtn("⇦",  "Logout",  3, () -> {
            dispose(); new LoginSelectionFrame();
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

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e)  { action.run(); }
            @Override public void mouseEntered(java.awt.event.MouseEvent e)  {
                if (btn.getBackground() != UITheme.SIDEBAR_HOVER)
                    btn.setBackground(new Color(44, 55, 72));
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e)   {
                if (btn.getBackground() != UITheme.SIDEBAR_HOVER)
                    btn.setBackground(UITheme.SIDEBAR_BG);
            }
        });
        return btn;
    }

    private void setActiveSideBtn(int active) {
        for (int i = 0; i < sidebarBtns.size(); i++) {
            JPanel btn  = sidebarBtns.get(i);
            boolean act = (i == active);
            btn.setBackground(act ? UITheme.SIDEBAR_HOVER : UITheme.SIDEBAR_BG);
            Color col = act ? UITheme.PRIMARY : UITheme.SIDEBAR_ICON;
            for (Component c : btn.getComponents())
                if (c instanceof JLabel) ((JLabel) c).setForeground(col);
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

        JLabel adminBadge = new JLabel("  Admin Portal  ");
        adminBadge.setFont(UITheme.F_SMALL);
        adminBadge.setForeground(UITheme.PRIMARY);
        adminBadge.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.PRIMARY, 1),
            new EmptyBorder(2, 8, 2, 8)));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        left.setBackground(UITheme.SURFACE);
        left.add(logo);
        left.add(adminBadge);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        right.setBackground(UITheme.SURFACE);

        JLabel userLbl = new JLabel("Signed in as  " + adminName);
        userLbl.setFont(UITheme.F_SMALL);
        userLbl.setForeground(UITheme.TEXT_SEC);

        ModernButton logoutBtn = new ModernButton("Logout", UITheme.DANGER, 8);
        logoutBtn.setFont(UITheme.F_BUTTON);
        logoutBtn.setPreferredSize(new Dimension(90, 34));
        logoutBtn.addActionListener(e -> { dispose(); new LoginSelectionFrame(); });

        right.add(userLbl);
        right.add(logoutBtn);

        bar.add(left,  BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    // =========================================================================
    // TABBED PANE
    // =========================================================================

    private JTabbedPane buildTabbedPane() {
        mainTabs = new JTabbedPane(JTabbedPane.TOP);
        mainTabs.setBackground(UITheme.BG);
        mainTabs.setFont(UITheme.F_BODY);
        mainTabs.addTab("   Menu Management   ", buildMenuManagementPanel());
        mainTabs.addTab("   Customer Orders   ", buildOrdersPanel());
        return mainTabs;
    }

    // =========================================================================
    // TAB 1 — Menu Management
    // =========================================================================

    private JPanel buildMenuManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(16, 0));
        panel.setBackground(UITheme.BG);
        panel.setBorder(new EmptyBorder(18, 18, 18, 18));
        panel.add(buildMenuTableCard(), BorderLayout.CENTER);
        panel.add(buildMenuFormCard(),  BorderLayout.EAST);
        return panel;
    }

    private JPanel buildMenuTableCard() {
        JPanel card = buildCardPanel("Available Menu Items", UITheme.SIDEBAR_BG);

        String[] cols = {"ID", "Food Item Name", "Category", "Price (Rs.)"};
        menuTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        menuTable = buildStyledTable(menuTableModel);
        menuTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        menuTable.getColumnModel().getColumn(1).setPreferredWidth(340);
        menuTable.getColumnModel().getColumn(2).setPreferredWidth(110);
        menuTable.getColumnModel().getColumn(3).setPreferredWidth(120);

        menuTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && menuTable.getSelectedRow() != -1) {
                int row = menuTable.getSelectedRow();
                nameField.setText((String)  menuTableModel.getValueAt(row, 1));
                categoryCombo.setSelectedItem(menuTableModel.getValueAt(row, 2));
                priceField.setText((String) menuTableModel.getValueAt(row, 3));
            }
        });

        JScrollPane scroll = new JScrollPane(menuTable);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));
        scroll.getViewport().setBackground(UITheme.SURFACE);
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildMenuFormCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(UITheme.SURFACE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER),
            new EmptyBorder(24, 24, 24, 24)));
        card.setPreferredSize(new Dimension(290, 0));

        JLabel heading = new JLabel("Add / Edit Item");
        heading.setFont(UITheme.F_HEADING);
        heading.setForeground(UITheme.TEXT_PRI);
        heading.setBorder(new EmptyBorder(0, 0, 18, 0));

        JPanel form = new JPanel();
        form.setBackground(UITheme.SURFACE);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        nameField  = makeFormField();
        priceField = makeFormField();

        String[] categories = {"Main", "Starters", "Hoppers", "Burgers", "Rice", "Drinks", "Desserts"};
        categoryCombo = new JComboBox<>(categories);
        categoryCombo.setFont(UITheme.F_BODY);
        categoryCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        categoryCombo.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));

        form.add(makeFormLabel("Item Name"));
        form.add(Box.createVerticalStrut(6));
        form.add(nameField);
        form.add(Box.createVerticalStrut(14));
        form.add(makeFormLabel("Category"));
        form.add(Box.createVerticalStrut(6));
        form.add(categoryCombo);
        form.add(Box.createVerticalStrut(14));
        form.add(makeFormLabel("Price (Rs.)"));
        form.add(Box.createVerticalStrut(6));
        form.add(priceField);

        JPanel buttons = new JPanel(new GridLayout(2, 2, 10, 10));
        buttons.setBackground(UITheme.SURFACE);
        buttons.setBorder(new EmptyBorder(22, 0, 0, 0));

        ModernButton addBtn    = new ModernButton("Add Item",  UITheme.SUCCESS,   8);
        ModernButton updateBtn = new ModernButton("Update",    UITheme.PRIMARY,   8);
        ModernButton deleteBtn = new ModernButton("Delete",    UITheme.DANGER,    8);
        ModernButton clearBtn  = new ModernButton("Clear",     UITheme.SECONDARY, 8);

        addBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String priceStr = priceField.getText().trim();
            String cat = (String) categoryCombo.getSelectedItem();
            if (!validateMenuForm(name, priceStr)) return;
            DataStore.getInstance().addFoodItem(name, Double.parseDouble(priceStr), cat);
            loadMenuItems(); clearForm();
            showInfo("\"" + name + "\" added to the menu.");
        });
        updateBtn.addActionListener(e -> {
            if (menuTable.getSelectedRow() == -1) { showWarn("Select an item to update."); return; }
            String name = nameField.getText().trim();
            String priceStr = priceField.getText().trim();
            if (!validateMenuForm(name, priceStr)) return;
            int id = (int) menuTableModel.getValueAt(menuTable.getSelectedRow(), 0);
            DataStore.getInstance().updateFoodItem(id, name, Double.parseDouble(priceStr));
            loadMenuItems(); clearForm();
            showInfo("Item updated successfully.");
        });
        deleteBtn.addActionListener(e -> {
            if (menuTable.getSelectedRow() == -1) { showWarn("Select an item to delete."); return; }
            String itemName = (String) menuTableModel.getValueAt(menuTable.getSelectedRow(), 1);
            int choice = JOptionPane.showConfirmDialog(this,
                "Delete \"" + itemName + "\" from the menu?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (choice == JOptionPane.YES_OPTION) {
                int id = (int) menuTableModel.getValueAt(menuTable.getSelectedRow(), 0);
                DataStore.getInstance().removeFoodItem(id);
                loadMenuItems(); clearForm();
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
    // TAB 2 — Customer Orders
    // =========================================================================

    private JPanel buildOrdersPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(UITheme.BG);
        panel.setBorder(new EmptyBorder(18, 18, 18, 18));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        toolbar.setBackground(UITheme.BG);
        ModernButton refreshBtn = new ModernButton("↻  Refresh Orders", UITheme.SECONDARY, 6);
        refreshBtn.addActionListener(e -> loadOrders());
        toolbar.add(refreshBtn);

        JPanel tableCard = buildCardPanel("All Customer Orders", UITheme.SIDEBAR_BG);
        String[] cols = {"Order ID", "Customer", "Items", "Subtotal (Rs.)", "Date & Time", "Status"};
        ordersTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        ordersTable = buildStyledTable(ordersTableModel);
        ordersTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        ordersTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        ordersTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        ordersTable.getColumnModel().getColumn(3).setPreferredWidth(140);
        ordersTable.getColumnModel().getColumn(4).setPreferredWidth(200);
        ordersTable.getColumnModel().getColumn(5).setPreferredWidth(100);

        JScrollPane orderScroll = new JScrollPane(ordersTable);
        orderScroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));
        orderScroll.getViewport().setBackground(UITheme.SURFACE);
        tableCard.add(orderScroll, BorderLayout.CENTER);

        JPanel detailCard = buildCardPanel("Order Receipt  (click a row to view)", UITheme.PRIMARY);
        orderDetailArea = new JTextArea("Select an order from the table above to view the full receipt.");
        orderDetailArea.setFont(UITheme.F_MONO);
        orderDetailArea.setEditable(false);
        orderDetailArea.setBackground(new Color(250, 250, 250));
        orderDetailArea.setBorder(new EmptyBorder(12, 14, 12, 14));
        JScrollPane detailScroll = new JScrollPane(orderDetailArea);
        detailScroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));
        detailCard.add(detailScroll, BorderLayout.CENTER);
        detailCard.setPreferredSize(new Dimension(0, 250));

        ordersTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && ordersTable.getSelectedRow() != -1) {
                int orderId = (int) ordersTableModel.getValueAt(ordersTable.getSelectedRow(), 0);
                renderOrderDetails(orderId);
            }
        });

        panel.add(toolbar,    BorderLayout.NORTH);
        panel.add(tableCard,  BorderLayout.CENTER);
        panel.add(detailCard, BorderLayout.SOUTH);
        return panel;
    }

    // =========================================================================
    // DATA LOADING
    // =========================================================================

    private void loadMenuItems() {
        menuTableModel.setRowCount(0);
        for (FoodItem item : DataStore.getInstance().getFoodItems()) {
            menuTableModel.addRow(new Object[]{
                item.getId(), item.getName(), item.getCategory(),
                String.format("%.2f", item.getPrice())
            });
        }
    }

    private void loadOrders() {
        ordersTableModel.setRowCount(0);
        for (Order order : DataStore.getInstance().getOrders()) {
            ordersTableModel.addRow(new Object[]{
                order.getOrderId(), order.getCustomerName(),
                order.getItems().size() + " item(s)",
                String.format("%.2f", order.getTotalAmount()),
                order.getFormattedTime(),
                order.isConfirmed() ? "Confirmed" : "Pending"
            });
        }
    }

    private void renderOrderDetails(int orderId) {
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
            orderDetailArea.setText(sb.toString());
            orderDetailArea.setCaretPosition(0);
            return;
        }
    }

    // =========================================================================
    // VALIDATION & HELPERS
    // =========================================================================

    private boolean validateMenuForm(String name, String priceStr) {
        if (name.isEmpty()) { showError("Item name cannot be empty."); return false; }
        try {
            double p = Double.parseDouble(priceStr);
            if (p <= 0) { showError("Price must be greater than Rs. 0.00."); return false; }
        } catch (NumberFormatException ex) {
            showError("Please enter a valid numeric price (e.g. 250.00).");
            return false;
        }
        return true;
    }

    private JPanel buildCardPanel(String title, Color headerColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(UITheme.SURFACE);
        card.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        header.setBackground(headerColor);
        header.setBorder(new EmptyBorder(10, 16, 10, 16));
        JLabel lbl = new JLabel(title);
        lbl.setFont(UITheme.F_SUBHEAD);
        lbl.setForeground(Color.WHITE);
        header.add(lbl);
        card.add(header, BorderLayout.NORTH);
        return card;
    }

    private JTable buildStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
                Component c = super.prepareRenderer(renderer, row, col);
                if (isRowSelected(row)) {
                    c.setBackground(UITheme.TABLE_SEL); c.setForeground(UITheme.TEXT_PRI);
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

    private JLabel makeFormLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UITheme.F_LABEL);
        lbl.setForeground(UITheme.TEXT_SEC);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JTextField makeFormField() {
        JTextField f = new JTextField();
        f.setFont(UITheme.F_BODY);
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER),
            new EmptyBorder(6, 10, 6, 10)));
        return f;
    }

    private void clearForm() {
        nameField.setText(""); priceField.setText("");
        categoryCombo.setSelectedIndex(0);
        menuTable.clearSelection();
    }

    private void showInfo(String msg)  { JOptionPane.showMessageDialog(this, msg, "Success",         JOptionPane.INFORMATION_MESSAGE); }
    private void showWarn(String msg)  { JOptionPane.showMessageDialog(this, msg, "Warning",          JOptionPane.WARNING_MESSAGE);     }
    private void showError(String msg) { JOptionPane.showMessageDialog(this, msg, "Validation Error", JOptionPane.ERROR_MESSAGE);       }
}
