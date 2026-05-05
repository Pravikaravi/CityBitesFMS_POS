package citybitesfms.model;

public class FoodItem {

    private int     id;

    private String  name;

    private String  category;

    private double  price;

    private boolean available;

    public FoodItem(int id, String name, String category, double price, boolean available) {
        this.id        = id;
        this.name      = name;
        this.category  = category;
        this.price     = price;
        this.available = available;
    }

    public FoodItem(int id, String name, String category, double price) {
        this(id, name, category, price, true);
    }

    public int     getId()        { return id;        }

    public String  getName()      { return name;      }

    public String  getCategory()  { return category;  }

    public double  getPrice()     { return price;     }

    public boolean isAvailable()  { return available; }

    public void setName(String name)          { this.name      = name;      }

    public void setCategory(String category)  { this.category  = category;  }

    public void setPrice(double price)        { this.price     = price;     }

    public void setAvailable(boolean available){ this.available = available; }

    @Override
    public String toString() {
        return String.format("%s — %s — Rs. %.2f", name, category, price);
    }
}
