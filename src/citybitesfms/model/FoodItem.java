package citybitesfms.model;

/**
 * Represents a food item in the City Bites menu.
 *
 * Demonstrates OOP encapsulation — all fields are private and accessed
 * through public getters and setters.
 *
 * @author NovaSoft Solutions (PVT) Ltd
 * @version 2.0
 */
public class FoodItem {

    private int    id;
    private String name;
    private double price;
    private String category;

    public FoodItem(int id, String name, double price) {
        this(id, name, price, "Main");
    }

    public FoodItem(int id, String name, double price, String category) {
        this.id       = id;
        this.name     = name;
        this.price    = price;
        this.category = category;
    }

    // ─── Getters ───────────────────────────────────────────────────────────────

    public int    getId()       { return id;       }
    public String getName()     { return name;     }
    public double getPrice()    { return price;    }
    public String getCategory() { return category; }

    // ─── Setters ───────────────────────────────────────────────────────────────

    public void setName(String name)         { this.name     = name;     }
    public void setPrice(double price)       { this.price    = price;    }
    public void setCategory(String category) { this.category = category; }

    @Override
    public String toString() {
        return name + " - Rs. " + String.format("%.2f", price);
    }
}
