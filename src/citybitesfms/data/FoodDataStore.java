package citybitesfms.data;

import citybitesfms.model.FoodItem;
import citybitesfms.model.Order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FoodDataStore {

    public static final String[] CATEGORIES = {
        "Pizza", "Burger", "Soup", "Pasta", "Fish", "Chicken", "Chips", "Wine", "Dessert"
    };

    private static final ArrayList<FoodItem> FOOD_ITEMS = new ArrayList<>();

    private static final ArrayList<Order> ORDERS = new ArrayList<>();

    private static final Map<String, String> CUSTOMER_ACCOUNTS = new HashMap<>();

    private static int nextItemId  = 1;

    private static int nextOrderId = 1;

    static {
        
        CUSTOMER_ACCOUNTS.put("user", "user123");

        seed("Margherita Pizza",   "Pizza",   850.00);
        seed("Pepperoni Pizza",    "Pizza",   950.00);
        seed("BBQ Chicken Pizza",  "Pizza",  1050.00);

        seed("Classic Beef Burger","Burger",  750.00);
        seed("Cheese Burger",      "Burger",  800.00);
        seed("Veggie Burger",      "Burger",  650.00);

        seed("Tomato Soup",        "Soup",    450.00);
        seed("Mushroom Soup",      "Soup",    480.00);

        seed("Chicken Alfredo",    "Pasta",   880.00);
        seed("Spaghetti Bolognese","Pasta",   820.00);

        seed("Grilled Fish",       "Fish",    950.00);
        seed("Fish and Chips",     "Fish",    780.00);

        seed("Fried Chicken",      "Chicken", 700.00);
        seed("Chicken Wings",      "Chicken", 720.00);

        seed("French Fries",       "Chips",   350.00);
        seed("Loaded Nachos",      "Chips",   480.00);

        seed("Red Wine (Glass)",   "Wine",    650.00);
        seed("White Wine (Glass)", "Wine",    600.00);

        seed("Chocolate Cake",     "Dessert", 380.00);
        seed("Ice Cream Sundae",   "Dessert", 320.00);
        seed("Cheesecake",         "Dessert", 420.00);
    }

    private FoodDataStore() {}

    public static boolean customerExists(String username) {
        return CUSTOMER_ACCOUNTS.containsKey(username);
    }

    public static boolean registerCustomer(String username, String password) {
        if (CUSTOMER_ACCOUNTS.containsKey(username)) return false;
        CUSTOMER_ACCOUNTS.put(username, password);
        return true;
    }

    public static boolean authenticateCustomer(String username, String password) {
        String stored = CUSTOMER_ACCOUNTS.get(username);
        return stored != null && stored.equals(password);
    }

    public static ArrayList<FoodItem> getFoodItems() { return FOOD_ITEMS; }

    public static void addFoodItem(FoodItem item) {
        item.setName(item.getName()); 
        FOOD_ITEMS.add(new FoodItem(
            nextItemId++, item.getName(), item.getCategory(), item.getPrice(), item.isAvailable()));
    }

    public static void updateFoodItem(FoodItem updated) {
        for (int i = 0; i < FOOD_ITEMS.size(); i++) {
            if (FOOD_ITEMS.get(i).getId() == updated.getId()) {
                FOOD_ITEMS.set(i, updated);
                return;
            }
        }
    }

    public static void deleteFoodItem(int id) {
        FOOD_ITEMS.removeIf(item -> item.getId() == id);
    }

    public static int getNextItemId() { return nextItemId; }

    public static ArrayList<Order> getOrders() { return ORDERS; }

    public static void addOrder(Order order) { ORDERS.add(order); }

    public static int getNextOrderId() { return nextOrderId++; }

    private static void seed(String name, String category, double price) {
        FOOD_ITEMS.add(new FoodItem(nextItemId++, name, category, price, true));
    }
}
