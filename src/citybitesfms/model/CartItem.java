package citybitesfms.model;

public class CartItem {

    private FoodItem foodItem;
    private int quantity;

    public CartItem(FoodItem foodItem, int quantity) {
        this.foodItem = foodItem;
        this.quantity = quantity;
    }

    public FoodItem getFoodItem() { return foodItem; }

    public int getQuantity() { return quantity; }

    public void setQuantity(int quantity) { this.quantity = quantity; }

    // price x quantity gives the line total
    public double getSubtotal() {
        return foodItem.getPrice() * quantity;
    }

    @Override
    public String toString() {
        return String.format("%s  x%d  =  Rs. %.2f",
                foodItem.getName(), quantity, getSubtotal());
    }
}
