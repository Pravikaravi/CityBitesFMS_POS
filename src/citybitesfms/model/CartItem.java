package citybitesfms.model;

/**
 * CartItem — represents a single line in a customer's shopping cart.
 *
 * Pairs a FoodItem with a quantity and exposes a getSubtotal() convenience
 * method so that the UI never has to perform price arithmetic directly.
 *
 * Demonstrates OOP encapsulation: all fields are private; state is mutated
 * only through setQuantity() which could validate the value if needed.
 *
 * @author NovaSoft Solutions (PVT) Ltd
 * @version 1.0
 */
public class CartItem {

    /** The food item chosen by the customer. */
    private FoodItem foodItem;

    /** How many units of the food item were requested. */
    private int quantity;

    /**
     * Constructs a CartItem with the given food item and initial quantity.
     *
     * @param foodItem The food item selected by the customer
     * @param quantity Number of units ordered (must be >= 1)
     */
    public CartItem(FoodItem foodItem, int quantity) {
        this.foodItem = foodItem;
        this.quantity = quantity;
    }

    // ─── Getters / Setters ────────────────────────────────────────────────────

    /**
     * Returns the food item associated with this cart line.
     *
     * @return The FoodItem
     */
    public FoodItem getFoodItem() { return foodItem; }

    /**
     * Returns the quantity of units in this cart line.
     *
     * @return Quantity
     */
    public int getQuantity() { return quantity; }

    /**
     * Updates the quantity for this cart line.
     *
     * @param quantity New quantity (should be >= 1)
     */
    public void setQuantity(int quantity) { this.quantity = quantity; }

    // ─── Business Logic ───────────────────────────────────────────────────────

    /**
     * Calculates and returns the subtotal for this cart line.
     * Subtotal = unit price × quantity.
     *
     * @return Line subtotal in the system currency
     */
    public double getSubtotal() {
        return foodItem.getPrice() * quantity;
    }

    /**
     * Returns a human-readable summary of this cart line.
     *
     * @return Formatted string: "Name xQty = $X.XX"
     */
    @Override
    public String toString() {
        return String.format("%s  x%d  =  Rs. %.2f",
                foodItem.getName(), quantity, getSubtotal());
    }
}
