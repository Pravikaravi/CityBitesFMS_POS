package citybitesfms.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a complete customer order containing one or more OrderItems.
 *
 * This class demonstrates OOP through:
 *  - Encapsulation: private fields exposed via getters
 *  - Composition:   an Order is composed of a List of OrderItems
 *  - Behaviour:     getTotalAmount() aggregates child subtotals
 *
 * @author NovaSoft Solutions (PVT) Ltd
 * @version 1.0
 */
public class Order {

    private int orderId;
    private String customerName;
    private List<OrderItem> items;      // Composition — Order owns its items
    private LocalDateTime orderTime;
    private boolean confirmed;

    /**
     * Constructs a new Order for a given customer.
     *
     * @param orderId      Unique order identifier assigned by DataStore
     * @param customerName Username of the customer placing the order
     */
    public Order(int orderId, String customerName) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.items = new ArrayList<>();
        this.orderTime = LocalDateTime.now();
        this.confirmed = false;
    }

    // ─── Business Methods ──────────────────────────────────────────────────────

    /**
     * Adds an OrderItem to this order.
     *
     * @param item The OrderItem to add
     */
    public void addItem(OrderItem item) {
        items.add(item);
    }

    /**
     * Calculates the grand total for the entire order.
     * Iterates through all items and sums their subtotals.
     *
     * @return Total amount in Sri Lankan Rupees
     */
    public double getTotalAmount() {
        double total = 0;
        for (OrderItem item : items) {
            total += item.getSubtotal();
        }
        return total;
    }

    /**
     * Marks this order as confirmed (customer has accepted the summary).
     */
    public void confirm() {
        this.confirmed = true;
    }

    // ─── Getters ───────────────────────────────────────────────────────────────

    public int getOrderId() {
        return orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    /**
     * Returns the order timestamp formatted as "yyyy-MM-dd HH:mm:ss".
     *
     * @return Human-readable date/time string
     */
    public String getFormattedTime() {
        return orderTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
