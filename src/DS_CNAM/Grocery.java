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

    private static ArrayList<Product> products = initializeProducts();
    private static ArrayList<Supplier> suppliers = new ArrayList<>();
    private static File tmpFile;

    private static final int port = 8081;

    private static void addCommandToHistory(String product, float price, float quantity) {
        try {
            PrintWriter writer;
            if (tmpFile == null) {
                tmpFile = File.createTempFile("GroceryLog", ".csv");
                writer = new PrintWriter(tmpFile);
                writer.println("Product;Price;Quantity");
            } else {
                writer = new PrintWriter(new FileWriter(tmpFile, true));
            }

            writer.println(product + ";" + price + ";" + quantity + ";");
            writer.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private Product getProductByName(String productName) {
        for (Product product : products) {
            if (product.getName().toLowerCase().trim().equals(productName.toLowerCase().trim())) {
                return product;
            }
        }
        return null;
    }

    public boolean checkAvailability(String productName, int quantity) {
        Product product = getProductByName(productName);
        return product != null && product.getQuantity() >= quantity;
    }

    public String getPrice(String productName, int quantity) {
        Product product = getProductByName(productName);
        float price = product != null ? product.getPrice() * quantity : -1;
        return String.format("00.00", price);
    }

    private static ArrayList<Product> initializeProducts() {
        ArrayList<Product> products = new ArrayList<>();
        Random random = new Random();

        products.add(new Product("Tequila", random.nextInt(Integer.SIZE - 1) * 100, Math.round(random.nextFloat() * 1000) / 100f));
        products.add(new Product("Chicken", random.nextInt(Integer.SIZE - 1) * 100, Math.round(random.nextFloat() * 1000) / 100f));
        products.add(new Product("Milk", random.nextInt(Integer.SIZE - 1) * 100, Math.round(random.nextFloat() * 1000) / 100f));
        products.add(new Product("Limes", random.nextInt(Integer.SIZE - 1) * 100, Math.round(random.nextFloat() * 1000) / 100f));
        System.out.println(products.get(0).getName() + " : " + products.get(0).getQuantity() + " (" + products.get(0).getPrice() + " €)");
        System.out.println(products.get(1).getName() + " : " + products.get(1).getQuantity() + " (" + products.get(1).getPrice() + " €)");
        System.out.println(products.get(2).getName() + " : " + products.get(2).getQuantity() + " (" + products.get(2).getPrice() + " €)");
        System.out.println(products.get(3).getName() + " : " + products.get(3).getQuantity() + " (" + products.get(3).getPrice() + " €)");
        return products;
    }

    /**
     * @param productName The name of the product to buy
     * @param quantity    The quantity to buy
     * @return an empty string if the transaction is successful or the error if it isn't
     */
    public String buy(String productName, int quantity) {
        System.out.println("Start selling " + quantity + " " + productName + "...");
        Product product = getProductByName(productName);
        if (product != null) {
            if (product.getQuantity() >= quantity && quantity > 0) {
                try {
                    product.sell(quantity);
                    addCommandToHistory(productName, product.getPrice(), quantity);
                    return "";
                } catch (Error e) {
                    e.printStackTrace();
                    return e.getMessage();
                }
            } else {
                return "Product not available in this quantity (" + product.getQuantity() + " remaining)";
            }
        } else {
            return "Product not available";
        }
    }


    private void subscribe(String topic) {

        MemoryPersistence persistence = new MemoryPersistence();

        try {
            MqttClient sampleClient = new MqttClient(Utils.broker, "Test", persistence);

            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            System.out.println("Connecting to broker: " + Utils.broker);
            sampleClient.connect(connOpts);
            System.out.println("Connected");

            sampleClient.subscribe(topic);
            sampleClient.setCallback(new MqttCallback() {
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

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // sampleClient.disconnect();
            System.out.println("Disconnected");

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
