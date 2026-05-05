package citybitesfms.data;

import citybitesfms.model.FoodItem;
import citybitesfms.model.Order;

import java.util.*;

public class DataStore {

    // singleton instance
    private static DataStore instance;

    public static DataStore getInstance() {
        if (instance == null) instance = new DataStore();
        return instance;
    }

    private final List<FoodItem>        foodItems           = new ArrayList<>();
    private final List<Order>           orders              = new ArrayList<>();
    private final Map<String, String>   customerCredentials = new HashMap<>();

    private int nextFoodItemId = 1;
    private int nextOrderId    = 1;

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";

    private DataStore() {
        // seed initial food items
        seed("Spring Rolls",         180.00, "Starters");
        seed("Prawn Toast",          220.00, "Starters");
        seed("Crispy Wontons",       200.00, "Starters");

        seed("Rice and Curry",       250.00, "Main");
        seed("Kottu Roti",           300.00, "Main");
        seed("Fish Curry",           200.00, "Main");
        seed("Prawn Noodles",        380.00, "Main");

        seed("String Hoppers",       150.00, "Hoppers");
        seed("Plain Hoppers",         80.00, "Hoppers");
        seed("Egg Hoppers",          120.00, "Hoppers");

        seed("Chicken Burger",       350.00, "Burgers");
        seed("Veg Fried Rice",       220.00, "Rice");
        seed("Egg Fried Rice",       240.00, "Rice");

        seed("Soft Drink (330 ml)",  100.00, "Drinks");
        seed("Fresh Fruit Juice",    120.00, "Drinks");

        seed("Ice Cream",            180.00, "Desserts");
        seed("Watalappam",           150.00, "Desserts");

        // default customer accounts for testing
        customerCredentials.put("customer1", "pass123");
        customerCredentials.put("john",      "john123");
        customerCredentials.put("mary",      "mary123");
        customerCredentials.put("demo",      "demo");
    }

    // check admin credentials
    public boolean authenticateAdmin(String u, String p) {
        return ADMIN_USERNAME.equals(u) && ADMIN_PASSWORD.equals(p);
    }

    // check customer credentials
    public boolean authenticateCustomer(String u, String p) {
        String s = customerCredentials.get(u);
        return s != null && s.equals(p);
    }

    public void addFoodItem(String name, double price) {
        foodItems.add(new FoodItem(nextFoodItemId++, name, "Main", price, true));
    }

    public void addFoodItem(String name, double price, String category) {
        foodItems.add(new FoodItem(nextFoodItemId++, name, category, price, true));
    }

    public void updateFoodItem(int id, String name, double price) {
        for (FoodItem item : foodItems) {
            if (item.getId() == id) { item.setName(name); item.setPrice(price); return; }
        }
    }

    public void removeFoodItem(int id) {
        foodItems.removeIf(item -> item.getId() == id);
    }

    public List<FoodItem> getFoodItems() { return new ArrayList<>(foodItems); }

    // collect unique categories from the food list
    public List<String> getCategories() {
        Set<String> seen = new LinkedHashSet<>();
        for (FoodItem item : foodItems) seen.add(item.getCategory());
        return new ArrayList<>(seen);
    }

    public void    addOrder(Order order) { orders.add(order); }
    public List<Order> getOrders()       { return new ArrayList<>(orders); }
    public int     getNextOrderId()      { return nextOrderId++; }

    public boolean customerExists(String username) {
        return customerCredentials.containsKey(username);
    }

    public boolean registerCustomer(String username, String password) {
        if (customerCredentials.containsKey(username)) return false;
        customerCredentials.put(username, password);
        return true;
    }

    private void seed(String name, double price, String category) {
        foodItems.add(new FoodItem(nextFoodItemId++, name, category, price, true));
    }
}
