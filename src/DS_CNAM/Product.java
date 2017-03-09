package DS_CNAM;

public class Product {
    private String name;
    private float quantity;
    private float price;

    public Product(String name, float quantity, float price){
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
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

    public void sell(int quantity) {
        if (quantity <= this.quantity) {
            this.quantity -= quantity;
        } else {
            System.err.println("Not enough products !");
        }
    }

    public String display() {
        return this.getName() + ":" + this.getQuantity() + ":" + this.getPrice();
    }

    public Product(String productAsString) {
        if( productAsString != null && !productAsString.isEmpty()) {
            String[] infos = productAsString.split(":");
            if(infos.length == 3) {
                this.name = infos[0];
                this.quantity = Float.valueOf(infos[1]);
                this.price = Float.valueOf(infos[2]);
            }

        }
    }

}
