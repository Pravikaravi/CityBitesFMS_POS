package citybitesfms.model;

public class OrderItem {

    private FoodItem foodItem;
    private int quantity;

    public OrderItem(FoodItem foodItem, int quantity) {
        this.foodItem = foodItem;
        this.quantity = quantity;
    }

    public FoodItem getFoodItem() {
        return foodItem;
    }

    public int getQuantity() {
        return quantity;
    }

    // calculate subtotal for this item
    public double getSubtotal() {
        return foodItem.getPrice() * quantity;
    }
}
