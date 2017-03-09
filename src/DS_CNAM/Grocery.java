package DS_CNAM;

import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.webserver.WebServer;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class Grocery {

    private static ArrayList<Product> products = initializeProducts(); // The products of the Grocery
    private static File tmpFile; // The file that contains the commands history of the Grocery
    private static ArrayList<Supplier> suppliers = new ArrayList<>();

    private static final int port = 8081; // The port of the XML-RPC server

    /**
     * Add a command to the command history.
     *
     * @param product  The name of the product to add to the history
     * @param price    The price of the product to add to the history
     * @param quantity The quantity of the product to add to the history
     */
    private static void addCommandToHistory(String product, float price, float quantity) {
        try {
            PrintWriter writer;
            if (tmpFile == null) { // write the columns name
                tmpFile = File.createTempFile("GroceryLog", ".csv");
                writer = new PrintWriter(tmpFile);
                writer.println("Product;Price;Quantity");
            } else {
                writer = new PrintWriter(new FileWriter(tmpFile, true));
            }

            // write the content
            writer.println(product + ";" + price + ";" + quantity + ";");
            writer.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    /**
     * Returns the product that has the name given in parameters.
     *
     * @param productName The name of the product.
     * @return The product that has the name given in parameters.
     */
    private Product getProductByName(String productName) {
        for (Product product : products) {
            if (product.getName().toLowerCase().trim().equals(productName.toLowerCase().trim())) {
                return product;
            }
        }
        return null;
    }

    /**
     * Check if a product is available in a given quantity.
     *
     * @param productName The name of the product.
     * @param quantity    The quantity.
     * @return True if the product is available in the given quantity, false otherwise.
     */
    public boolean checkAvailability(String productName, int quantity) {
        Product product = getProductByName(productName);
        return product != null && product.getQuantity() >= quantity;
    }

    /**
     * Returns the total price of the quantity of products given in parameters.
     *
     * @param productName The name of the product.
     * @param quantity    The quantity.
     * @return Total price of the quantity of products given in parameters.
     */
    public String getPrice(String productName, int quantity) {
        Product product = getProductByName(productName);
        float price = product != null ? product.getPrice() * quantity : -1;
        return String.format("00.00", price);
    }

    /**
     * Initializes the list of products with a random price and quantity.
     *
     * @return The initialized product list.
     */
    private static ArrayList<Product> initializeProducts() {
        ArrayList<Product> products = new ArrayList<>();
        // Add the four products with random prices and quantities
        Random random = new Random();
        products.add(new Product("Tequila", random.nextInt(Integer.SIZE - 1) * 100, Math.round(random.nextFloat() * 1000) / 100f));
        products.add(new Product("Chicken", random.nextInt(Integer.SIZE - 1) * 100, Math.round(random.nextFloat() * 1000) / 100f));
        products.add(new Product("Milk", random.nextInt(Integer.SIZE - 1) * 100, Math.round(random.nextFloat() * 1000) / 100f));
        products.add(new Product("Limes", random.nextInt(Integer.SIZE - 1) * 100, Math.round(random.nextFloat() * 1000) / 100f));
        // Print the new prices
        System.out.println("\nProducts : ");
        System.out.println(products.get(0).getName() + " : " + products.get(0).getQuantity() + " (" + products.get(0).getPrice() + " €)");
        System.out.println(products.get(1).getName() + " : " + products.get(1).getQuantity() + " (" + products.get(1).getPrice() + " €)");
        System.out.println(products.get(2).getName() + " : " + products.get(2).getQuantity() + " (" + products.get(2).getPrice() + " €)");
        System.out.println(products.get(3).getName() + " : " + products.get(3).getQuantity() + " (" + products.get(3).getPrice() + " €)");
        return products;
    }

    /**
     * Sell a product in the quantity given in parameters.
     *
     * @param productName The name of the product to buy
     * @param quantity    The quantity to buy
     * @return an empty string if the transaction is successful or the error if it isn't
     */
    public String buy(String productName, int quantity) {
        System.out.println("Start selling " + quantity + " " + productName + "...");
        Product product = getProductByName(productName);
        String err;
        if (product != null) {
            if (product.getQuantity() >= quantity && quantity > 0) {
                product.sell(quantity);
                addCommandToHistory(productName, product.getPrice(), quantity);
                return "";
            } else {
                err = "Product not available in this quantity (" + product.getQuantity() + " remaining)";
                System.out.println(err);
                return err;
            }
        } else {
            err = "Product not available";
            System.out.println(err);
            return err;

    private void subscribe(String topic) {
        MemoryPersistence persistence = new MemoryPersistence();

        try {
            MqttClient sampleClient = new MqttClient(Utils.broker, "Test", persistence);

            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            System.out.println("Connecting to broker: " + Utils.broker);
            sampleClient.connect(connOpts);
            System.out.println("Connected");

            sampleClient.setCallback(new MqttCallback() {
            sampleClient.subscribe(topic);
                @Override
                public void connectionLost(Throwable throwable) {
                    System.out.println("Error : Mosquitto : Connection Lost ");
                }

                @Override
                public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                    System.out.println("######################### Message reçu ");
                    if (mqttMessage != null) {
                        updateSuppliers(s);
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                }
            });


            System.out.println("Disconnected");
            }
            // sampleClient.disconnect();
                e.printStackTrace();
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            try {
        } catch (MqttException me) {
            System.out.println("reason " + me.getReasonCode());
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("excep " + me);
            me.printStackTrace();
        }
    }

    private void updateSuppliers(String s) {
        String[] supplier = s.split("||");
        boolean trouve = false;
        if (supplier.length >= 2) {
            System.out.println("Supplier : " + supplier[0]);
            ArrayList<Product> products = new ArrayList<>();
            for (Supplier sup : suppliers) {
                if (supplier[0].equals(sup.getClientId())) {
                    trouve = true;
                    for (int i = 1; i < supplier.length; i++) {
                        products.add(new Product(supplier[i]));
                    }
                    sup.setProductsToSell(products);
                }
            }
            if(!trouve) {
                Supplier sup = new Supplier(supplier[0]);
                for (int i = 1; i < supplier.length; i++) {
                    products.add(new Product(supplier[i]));
                }
                sup.setProductsToSell(products);
                suppliers.add(sup);
            }
            updateProducts();
        }

    }

    private void updateProducts() {
        boolean first ;
        for (Product p : products ) {
            first = true;
            for (Supplier s : suppliers ) {
                for (Product prod : s.getProductsToSell() ) {
                    if(first && p.getName().equals(prod.getName())) {
                        p.setPrice(prod.getPrice());
                        p.setQuantity(prod.getQuantity());
                        first = false;
                    }
                    else if(p.getName().equals(prod.getName()) && p.getPrice() > prod.getPrice()){
                        p.setPrice(prod.getPrice());
                        p.setQuantity(prod.getQuantity());
                    }
                }
            }
        }
    }
    public static void main(String[] args) {
        try {
            WebServer webServer = new WebServer(port);


<<<<<<<
    public static void main(String[] args) {
        try {
            WebServer webServer = new WebServer(port);
=======
            phm.addHandler("Grocery", Grocery.class);
            xmlRpcServer.setHandlerMapping(phm);
>>>>>>>

            XmlRpcServer xmlRpcServer = webServer.getXmlRpcServer();
            PropertyHandlerMapping phm = new PropertyHandlerMapping();

            phm.addHandler("Grocery", Grocery.class);
            xmlRpcServer.setHandlerMapping(phm);

            XmlRpcServerConfigImpl serverConfig =
                    (XmlRpcServerConfigImpl) xmlRpcServer.getConfig();

            webServer.start();
            Grocery g = new Grocery();
            Random r = new Random();
            String topic = Utils.topics.get((r.nextInt(Integer.SIZE-1))%Utils.topics.size());
            System.out.println("Subscribe to topic : "+ topic);
            g.subscribe(topic);
            Random rand = new Random();
            while (true) { // reinitialize quantities and prices
                Thread.sleep(rand.nextInt(Integer.SIZE - 1) * 1000);
                products = initializeProducts();
            }
        } catch (Exception exception) {
            System.err.println("JavaServer: " + exception);
        }
    }
}
