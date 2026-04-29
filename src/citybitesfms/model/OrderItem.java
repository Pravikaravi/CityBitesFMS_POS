package citybitesfms.model;

/**
 * Represents a single line item within a customer's order.
 *
 * An OrderItem links a FoodItem to a specific quantity chosen by the
 * customer, and can calculate its own subtotal — demonstrating the
 * OOP principle of placing behaviour close to the data it operates on.
 *
 * @author NovaSoft Solutions (PVT) Ltd
 * @version 1.0
 */
public class OrderItem {

    private FoodItem foodItem;  // The food item being ordered
    private int quantity;        // How many units the customer wants

    /**
     * Constructs an OrderItem.
     *
     * @param foodItem The food item selected by the customer
     * @param quantity Number of units ordered (must be >= 1)
     */
    public OrderItem(FoodItem foodItem, int quantity) {
        this.foodItem = foodItem;
        this.quantity = quantity;
    }

    // ─── Getters ───────────────────────────────────────────────────────────────

    public FoodItem getFoodItem() {
        return foodItem;
    }

    public int getQuantity() {
        return quantity;
    }

    /**
     * Calculates and returns the subtotal for this order line.
     * Subtotal = unit price × quantity.
     *
     * @return Subtotal amount in Sri Lankan Rupees
     */
    public double getSubtotal() {
        return foodItem.getPrice() * quantity;
    }
}
