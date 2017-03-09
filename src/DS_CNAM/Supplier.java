package DS_CNAM;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Created by aheil on 08/03/2017.
 */
public class Supplier extends Thread implements MqttCallback {


    private ArrayList<Product> productsToSell = initializeProducts();
    private String clientId;
    MqttClient sampleClient;

    public Supplier(String id) {
        this.clientId = id;
    }

    private ArrayList<Product> initializeProducts() {
        ArrayList<Product> res = new ArrayList<>();
        Random r = new Random();
        res.add(new Product(Utils.productNames.get((Integer.SIZE - 1) % Utils.productNames.size()), r.nextInt(Integer.SIZE - 1) * 1000, Math.round(r.nextFloat() * 1000) / 100f));
        res.add(new Product(Utils.productNames.get((Integer.SIZE - 1) % Utils.productNames.size()), r.nextInt(Integer.SIZE - 1) * 1000, Math.round(r.nextFloat() * 1000) / 100f));
        //res.addAll(Utils.productNames.stream().map(name -> new Product(name, r.nextInt(Integer.SIZE - 1) * 1000, Math.round(r.nextFloat() * 1000) / 100f)).collect(Collectors.toList()));
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
        } catch (MqttSecurityException e) {
            e.printStackTrace();
        } catch (MqttException e) {
            e.printStackTrace();
        }
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
            System.out.println("reason " + me.getReasonCode());
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("excep " + me);
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
                setQuantityProduct(msg[0], Float.parseFloat(msg[1]));
            }
        }
        System.out.println("Test : " + s + " :" + mqttMessage.getPayload());
    }

    private void setQuantityProduct(String s, float y) {
        productsToSell.stream().filter(p -> p.getName().equals(s) && y <= p.getQuantity()).forEach(p -> {
            p.setQuantity(p.getQuantity() - y);
        });
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }

    public ArrayList<Product> getProductsToSell() {
        return productsToSell;
    }

    public void setProductsToSell(ArrayList<Product> productsToSell) {
        this.productsToSell = productsToSell;
    }

    public String getClientId() {
        return clientId;
    }

    public void setProductsToSell(String id) {
        this.clientId = id;
    }
}
