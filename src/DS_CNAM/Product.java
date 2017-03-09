package DS_CNAM;

public class Product {
    private String name; // The name of the product
    private float quantity; // The quantity of the product in the grocery
    private float price; // The price of the product in the grocery

    /**
     * Creates a new Product.
     *
     * @param name     The name of the product.
     * @param quantity The quantity of the product in the grocery.
     * @param price    The price of the product in the grocery.
     */
    public Product(String name, float quantity, float price) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    /**
     * @return The name of the product.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets a new name to the product.
     *
     * @param name New name of the product.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The quantity of the product in the grocery.
     */
    public float getQuantity() {
        return quantity;
    }

    /**
     * Sets a new quantity to the product.
     *
     * @param quantity New quantity of this product in the grocery.
     */
    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    /**
     * @return The price of the product in the grocery.
     */
    public float getPrice() {
        return price;
    }

    /**
     * Sets a new price to the product.
     *
     * @param price New price of this product in the grocery.
     */
    public void setPrice(float price) {
        this.price = price;
    }

    /**
     * Sell the quantity of product.
     *
     * @param quantity Quantity to sell.
     */
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
