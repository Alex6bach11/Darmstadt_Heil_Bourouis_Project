package DS_CNAM;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.ArrayList;
import java.util.Random;

public class Supplier extends Thread implements MqttCallback {

    private ArrayList<Product> productsToSell = initializeProducts(); // A list of products to sell
    private String clientId;  // The id of the supplier
    private MqttClient sampleClient;

    public Supplier(String id) {
        this.clientId = id;
    }

    /**
     * Initializes the list of product randomly.
     *
     * @return The initialized list of product.
     */
    private ArrayList<Product> initializeProducts() {
        ArrayList<Product> res = new ArrayList<>();
        Random r = new Random();
        res.add(new Product(Utils.productNames.get(r.nextInt(Utils.productNames.size() - 1)), r.nextInt(Integer.SIZE - 1) * 1000, Math.round(r.nextFloat() * 1000) / 100f));
        res.add(new Product(Utils.productNames.get(r.nextInt(Utils.productNames.size() - 1)), r.nextInt(Integer.SIZE - 1) * 1000, Math.round(r.nextFloat() * 1000) / 100f));
        return res;
    }

    @Override
    public void run() {
        MemoryPersistence persistence = new MemoryPersistence();
        try {

            sampleClient = new MqttClient(Utils.broker, clientId, persistence);
            sampleClient.setCallback(this);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);

            System.out.println("Connecting to broker: " + Utils.broker);
            sampleClient.connect(connOpts);
            sampleClient.subscribe(this.getClientId());
            Random r = new Random();
            while (true) {
                produce();
                try {
                    Thread.sleep(r.nextInt(Integer.SIZE - 1)*1000);
                }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
            }

        } catch (MqttSecurityException e) {
            e.printStackTrace();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void produce() {
        ArrayList<Product> products = new ArrayList<>();

        Random r = new Random();
        products.add(new Product(Utils.productNames.get(r.nextInt(Integer.SIZE - 1) % Utils.productNames.size()), r.nextInt(Integer.SIZE - 1) * 1000, Math.round(r.nextFloat() * 1000) / 100f));
        this.setProductsToSell(products);

        String msg = this.getClientId();
        for (Product p : products) {
            msg += "__" + p.display();
        }

        String topic = Utils.topics.get((r.nextInt(Integer.SIZE - 1)) % Utils.topics.size());
        System.out.println("Publish to topic : " + topic + " Message : " + msg);
        this.publishMessage(topic, msg);
    }

    public void publishMessage(String topic, String content) {
        int qos = 2;

        try {
            System.out.println("Publishing on :" + topic + " message: " + content);
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(qos);

            sampleClient.publish(topic, message);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (MqttException me) {
            System.out.println("reason : " + me.getReasonCode());
            System.out.println("message : " + me.getMessage());
            System.out.println("localized message : " + me.getLocalizedMessage());
            System.out.println("cause : " + me.getCause());
            System.out.println("exception : " + me);
            me.printStackTrace();
        }
    }

    @Override
    public void connectionLost(Throwable throwable) {
        System.out.println("Error : Mosquitto : Connection Lost ");
    }
    
    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        if (mqttMessage != null) {
            String[] msg = new String(mqttMessage.getPayload()).split(":");
            if (msg.length == 2) {
                reduceProductQuantity(msg[0], Float.parseFloat(msg[1]));
            }
        }
    }

    /**
     * Reduce the quantity of product.
     * @param productName The name of the product.
     * @param productQuantity The quantity to remove from the stock.
     */
    private void reduceProductQuantity(String productName, float productQuantity) {
        productsToSell.stream().filter(p -> p.getName().equals(productName) && productQuantity <= p.getQuantity()).forEach(p -> {
            p.setQuantity(p.getQuantity() - productQuantity);
        });
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
    }

    /**
     * @return The product to sell list.
     */
    public ArrayList<Product> getProductsToSell() {
        return productsToSell;
    }

    /**
     * Sets a new product list to the supplier.
     *
     * @param productsToSell The new product list.
     */
    public void setProductsToSell(ArrayList<Product> productsToSell) {
        this.productsToSell = productsToSell;
    }

    /**
     * @return The id of the supplier.
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * Sets a new id to the supplier.
     *
     * @param id The new id.
     */
    public void setClientId(String id) {
        this.clientId = id;
    }
}
