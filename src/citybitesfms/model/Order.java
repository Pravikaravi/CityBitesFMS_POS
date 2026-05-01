package citybitesfms.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Order — represents a complete customer order containing one or more CartItems.
 *
 * Demonstrates OOP through:
 *   Encapsulation : private fields accessed via getters
 *   Composition   : an Order is composed of a list of CartItems
 *   Behaviour     : getTotalAmount() aggregates child subtotals
 *
 * @author NovaSoft Solutions (PVT) Ltd
 * @version 2.0
 */
public class Order {

    /** Unique order identifier assigned by FoodDataStore. */
    private final int    orderId;

    /** Username of the customer who placed this order. */
    private final String customerName;

    /** Line items in this order. */
    private final ArrayList<CartItem> items;

    /** Timestamp recorded when the Order object is created. */
    private final LocalDateTime orderTime;

    /** Whether the customer has confirmed (accepted) this order. */
    private boolean confirmed;

    /**
     * Constructs a new Order for the given customer.
     *
     * @param orderId      Unique identifier assigned by FoodDataStore
     * @param customerName Username of the customer placing the order
     */
    public Order(int orderId, String customerName) {
        this.orderId      = orderId;
        this.customerName = customerName;
        this.items        = new ArrayList<>();
        this.orderTime    = LocalDateTime.now();
        this.confirmed    = false;
    }

    // ─── Business Methods ─────────────────────────────────────────────────────

    /**
     * Appends a CartItem to this order.
     *
     * @param item The cart line to add
     */
    public void addItem(CartItem item) {
        items.add(item);
    }

    /**
     * Calculates the grand total by summing all CartItem subtotals.
     *
     * @return Total order amount
     */
    public double getTotalAmount() {
        double total = 0;
        for (CartItem item : items) total += item.getSubtotal();
        return total;
    }

    /**
     * Marks this order as confirmed by the customer.
     */
    public void confirm() { this.confirmed = true; }

    // ─── Getters ──────────────────────────────────────────────────────────────

    /** @return Unique order ID */
    public int    getOrderId()      { return orderId;      }

    /** @return Customer username */
    public String getCustomerName() { return customerName; }

    /** @return List of CartItems in this order */
    public ArrayList<CartItem> getItems() { return items; }

    /** @return true if the customer has confirmed this order */
    public boolean isConfirmed()    { return confirmed;    }

    /**
     * Returns the order date/time as a formatted string.
     *
     * @return "yyyy-MM-dd HH:mm:ss" formatted timestamp
     */
    public String getOrderDate() {
        return orderTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * Alias for {@link #getOrderDate()} — kept for compatibility.
     *
     * @return Formatted timestamp
     */
    public String getFormattedTime() { return getOrderDate(); }

    /**
     * Returns a short summary of this order.
     *
     * @return "Order #ID — Customer — Rs. X.XX"
     */
    @Override
    public String toString() {
        return String.format("Order #%d — %s — Rs. %.2f", orderId, customerName, getTotalAmount());
    }
}
