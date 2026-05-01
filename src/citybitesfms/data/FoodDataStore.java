package citybitesfms.data;

import citybitesfms.model.FoodItem;
import citybitesfms.model.Order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * FoodDataStore — static shared data store for the City Bites application.
 *
 * Holds all FoodItems and Orders in memory for the lifetime of the application.
 * Using static collections means every class accesses the exact same data
 * without needing to pass a reference around — a simple alternative to the
 * Singleton pattern suited for a single-JVM desktop application.
 *
 * Pre-loaded with sample items across the restaurant's main categories.
 *
 * @author NovaSoft Solutions (PVT) Ltd
 * @version 1.0
 */
public class FoodDataStore {

    // ─── Constants ────────────────────────────────────────────────────────────

    public static final String[] CATEGORIES = {
        "Pizza", "Burger", "Soup", "Pasta", "Fish", "Chicken", "Chips", "Wine", "Dessert"
    };

    // ─── Static collections ───────────────────────────────────────────────────

    /** Master list of all menu food items. */
    private static final ArrayList<FoodItem> FOOD_ITEMS = new ArrayList<>();

    /** Master list of all placed orders. */
    private static final ArrayList<Order> ORDERS = new ArrayList<>();

    /** Registered customer accounts: username → password. */
    private static final Map<String, String> CUSTOMER_ACCOUNTS = new HashMap<>();

    /** Auto-increment counter for food item IDs. */
    private static int nextItemId  = 1;

    /** Auto-increment counter for order IDs. */
    private static int nextOrderId = 1;

    // ─── Data seeding ─────────────────────────────────────────────────────────

    static {
        // ── Default customer account ──────────────────────────────────────────
        CUSTOMER_ACCOUNTS.put("user", "user123");

        // Pizza
        seed("Margherita Pizza",   "Pizza",   850.00);
        seed("Pepperoni Pizza",    "Pizza",   950.00);
        seed("BBQ Chicken Pizza",  "Pizza",  1050.00);

        // Burger
        seed("Classic Beef Burger","Burger",  750.00);
        seed("Cheese Burger",      "Burger",  800.00);
        seed("Veggie Burger",      "Burger",  650.00);

        // Soup
        seed("Tomato Soup",        "Soup",    450.00);
        seed("Mushroom Soup",      "Soup",    480.00);

        // Pasta
        seed("Chicken Alfredo",    "Pasta",   880.00);
        seed("Spaghetti Bolognese","Pasta",   820.00);

        // Fish
        seed("Grilled Fish",       "Fish",    950.00);
        seed("Fish and Chips",     "Fish",    780.00);

        // Chicken
        seed("Fried Chicken",      "Chicken", 700.00);
        seed("Chicken Wings",      "Chicken", 720.00);

        // Chips
        seed("French Fries",       "Chips",   350.00);
        seed("Loaded Nachos",      "Chips",   480.00);

        // Wine
        seed("Red Wine (Glass)",   "Wine",    650.00);
        seed("White Wine (Glass)", "Wine",    600.00);

        // Dessert
        seed("Chocolate Cake",     "Dessert", 380.00);
        seed("Ice Cream Sundae",   "Dessert", 320.00);
        seed("Cheesecake",         "Dessert", 420.00);
    }

    /** Private constructor — prevents instantiation; all access is static. */
    private FoodDataStore() {}

    // ─── Customer Account Operations ──────────────────────────────────────────

    /**
     * Checks whether a customer username is already registered.
     *
     * @param username Username to check
     * @return true if the username already exists
     */
    public static boolean customerExists(String username) {
        return CUSTOMER_ACCOUNTS.containsKey(username);
    }

    /**
     * Registers a new customer account.
     *
     * @param username Desired username
     * @param password Desired password (plain-text for demo purposes)
     * @return true if registration succeeded; false if username is already taken
     */
    public static boolean registerCustomer(String username, String password) {
        if (CUSTOMER_ACCOUNTS.containsKey(username)) return false;
        CUSTOMER_ACCOUNTS.put(username, password);
        return true;
    }

    /**
     * Verifies customer login credentials.
     *
     * @param username Entered username
     * @param password Entered password
     * @return true if credentials match a registered account
     */
    public static boolean authenticateCustomer(String username, String password) {
        String stored = CUSTOMER_ACCOUNTS.get(username);
        return stored != null && stored.equals(password);
    }

    // ─── Food Item Operations ─────────────────────────────────────────────────

    /**
     * Returns the full list of food items.
     *
     * @return ArrayList of all FoodItems
     */
    public static ArrayList<FoodItem> getFoodItems() { return FOOD_ITEMS; }

    /**
     * Adds a new food item to the store with an auto-assigned ID.
     *
     * @param item FoodItem to add (its id field will be overwritten)
     */
    public static void addFoodItem(FoodItem item) {
        item.setName(item.getName()); // no-op but keeps the setter usage explicit
        FOOD_ITEMS.add(new FoodItem(
            nextItemId++, item.getName(), item.getCategory(), item.getPrice(), item.isAvailable()));
    }

    /**
     * Updates an existing food item matched by ID.
     *
     * @param updated FoodItem carrying new field values
     */
    public static void updateFoodItem(FoodItem updated) {
        for (int i = 0; i < FOOD_ITEMS.size(); i++) {
            if (FOOD_ITEMS.get(i).getId() == updated.getId()) {
                FOOD_ITEMS.set(i, updated);
                return;
            }
        }
    }

    /**
     * Removes a food item from the store by ID.
     *
     * @param id ID of the item to remove
     */
    public static void deleteFoodItem(int id) {
        FOOD_ITEMS.removeIf(item -> item.getId() == id);
    }

    /**
     * Returns the next available food item ID without incrementing the counter.
     *
     * @return Next ID
     */
    public static int getNextItemId() { return nextItemId; }

    // ─── Order Operations ─────────────────────────────────────────────────────

    /**
     * Returns the full list of placed orders.
     *
     * @return ArrayList of all Orders
     */
    public static ArrayList<Order> getOrders() { return ORDERS; }

    /**
     * Saves a confirmed order to the store.
     *
     * @param order The Order to persist
     */
    public static void addOrder(Order order) { ORDERS.add(order); }

    /**
     * Allocates and returns the next unique order ID (auto-increment).
     *
     * @return Unique order ID
     */
    public static int getNextOrderId() { return nextOrderId++; }

    // ─── Private Helper ───────────────────────────────────────────────────────

    /**
     * Seeds a single food item during static initialisation.
     *
     * @param name     Item name
     * @param category Menu category
     * @param price    Unit price
     */
    private static void seed(String name, String category, double price) {
        FOOD_ITEMS.add(new FoodItem(nextItemId++, name, category, price, true));
    }
}
