package citybitesfms.model;

/**
 * FoodItem — represents a single item on the City Bites restaurant menu.
 *
 * Demonstrates OOP encapsulation: all fields are private and accessed
 * only through public getters and setters.
 *
 * @author NovaSoft Solutions (PVT) Ltd
 * @version 2.0
 */
public class FoodItem {

    /** Unique identifier auto-assigned by FoodDataStore. */
    private int     id;

    /** Display name of the food item. */
    private String  name;

    /** Menu category (e.g. Pizza, Burger, Soup). */
    private String  category;

    /** Unit price. */
    private double  price;

    /** Whether the item is currently available to order. */
    private boolean available;

    /**
     * Constructs a FoodItem with all fields specified.
     *
     * @param id        Unique item ID
     * @param name      Display name
     * @param category  Menu category
     * @param price     Unit price
     * @param available Availability flag
     */
    public FoodItem(int id, String name, String category, double price, boolean available) {
        this.id        = id;
        this.name      = name;
        this.category  = category;
        this.price     = price;
        this.available = available;
    }

    /**
     * Convenience constructor — marks the item as available by default.
     *
     * @param id       Unique item ID
     * @param name     Display name
     * @param category Menu category
     * @param price    Unit price
     */
    public FoodItem(int id, String name, String category, double price) {
        this(id, name, category, price, true);
    }

    // ─── Getters ──────────────────────────────────────────────────────────────

    /** @return Unique ID */
    public int     getId()        { return id;        }

    /** @return Display name */
    public String  getName()      { return name;      }

    /** @return Menu category */
    public String  getCategory()  { return category;  }

    /** @return Unit price */
    public double  getPrice()     { return price;     }

    /** @return true if the item is available to order */
    public boolean isAvailable()  { return available; }

    // ─── Setters ──────────────────────────────────────────────────────────────

    /** @param name New display name */
    public void setName(String name)          { this.name      = name;      }

    /** @param category New category */
    public void setCategory(String category)  { this.category  = category;  }

    /** @param price New unit price */
    public void setPrice(double price)        { this.price     = price;     }

    /** @param available New availability flag */
    public void setAvailable(boolean available){ this.available = available; }

    /**
     * Returns a human-readable summary of this food item.
     *
     * @return "Name — Category — Rs. X.XX"
     */
    @Override
    public String toString() {
        return String.format("%s — %s — Rs. %.2f", name, category, price);
    }
}
