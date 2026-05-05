package citybitesfms.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Order {

    private final int    orderId;

    private final String customerName;

    private final ArrayList<CartItem> items;

    private final LocalDateTime orderTime;

    private boolean confirmed;

    public Order(int orderId, String customerName) {
        this.orderId      = orderId;
        this.customerName = customerName;
        this.items        = new ArrayList<>();
        this.orderTime    = LocalDateTime.now();
        this.confirmed    = false;
    }

    public void addItem(CartItem item) {
        items.add(item);
    }

    public double getTotalAmount() {
        double total = 0;
        for (CartItem item : items) total += item.getSubtotal();
        return total;
    }

    public void confirm() { this.confirmed = true; }

    public int    getOrderId()      { return orderId;      }

    public String getCustomerName() { return customerName; }

    public ArrayList<CartItem> getItems() { return items; }

    public boolean isConfirmed()    { return confirmed;    }

    public String getOrderDate() {
        return orderTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public String getFormattedTime() { return getOrderDate(); }

    @Override
    public String toString() {
        return String.format("Order #%d — %s — Rs. %.2f", orderId, customerName, getTotalAmount());
    }
}
