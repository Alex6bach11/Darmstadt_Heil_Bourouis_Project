package DS_CNAM;

/**
 * Created by aheil on 07/03/2017.
 */
public class Product {
    private String name;
    private float quantity;
    private float price;

    public Product(String name, float quantity, float price){
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    public String getNom() {
        return name;
    }

    public void setNom(String name) {
        this.name = name;
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}
