package DS_CNAM;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.StringReader;
import java.net.*;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

/**
 * UDP server used to communicate with the sensors and the groceries.
 */
public class UDPFridgeServer extends Thread {

    private static String[] groceriesURLs = {"http://127.0.0.1:8081/xmlrpc"}; // The URLs of the groceries

    @Override
    public void run() {
        int port = 1313;
        byte data[] = new byte[1024];
        byte msg[];
        DatagramPacket packet;

        String message;
        JsonReader reader;
        try {
            DatagramSocket socket = new DatagramSocket(port);
            System.out.println("UDP Fridge Server started at Port " + port);

            while (true) {
                // Wait for request
                packet = new DatagramPacket(data, data.length);
                socket.receive(packet);
                // Decode sender, ignore all other content
                message = new String(packet.getData());
                System.out.println(message);
                reader = Json.createReader(new StringReader(message));

                // Update quantities with the received values
                Fridge.setCurrentValues(reader.readObject());
                orderProducts(); // order products if the warning level is reached
                msg = Fridge.getCurrentValues().toString().getBytes();

                // Send quantities to the client
                packet = new DatagramPacket(msg, msg.length, packet.getAddress(), packet.getPort());
                socket.send(packet);
                System.out.println(Fridge.getCurrentValues());
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Compares the prices of the products to order if the warning limit is reached and order the available cheapest products.
     */
    private void orderProducts() {
        String product;
        Float curVal, warningVal;
        JsonObject currentValues = Fridge.getCurrentValues(), warningLevels = Fridge.getWarningLevels();
        Set<String> key = currentValues.keySet();
        Iterator iter = key.iterator();
        XmlRpcClientConfigImpl config;
        XmlRpcClient client;
        Object[] params;
        String result;
        boolean available;
        Random random = new Random();
        float newQuantity,  bestPrice;
        String cheapestGrocery;
        while (iter.hasNext()) {
            product = iter.next().toString();
            curVal = Float.parseFloat(currentValues.get(product).toString());
            warningVal = Float.parseFloat(warningLevels.get(product).toString());

            // if the warning level is reached for this product
            if (curVal <= warningVal) {
                try {
                    bestPrice = -1;
                    cheapestGrocery = "";
                    params = new Object[]{product, random.nextInt(10) + 1};
                    for (String grocery : groceriesURLs) {
                        // connect to the groceries
                        config = new XmlRpcClientConfigImpl();
                        config.setServerURL(new URL(grocery));

                        client = new XmlRpcClient();
                        client.setConfig(config);
                        // check availability of the products in the grocery
                        available = (boolean) client.execute("Grocery.checkAvailability", params);
                        if (available) { // get the total price if the product is available
                            result = (String) client.execute("Grocery.getPrice", params);
                            if (bestPrice < 0 || bestPrice > Float.parseFloat(result)) { // update the best price if necessary
                                bestPrice = Float.parseFloat(result);
                                cheapestGrocery = grocery;
                            }
                        }
                    }
                    // if the product is available in a grocery in this quantity, process the order to the cheapest one
                    if (bestPrice >= 0) {
                        // connect to the cheapest known grocery
                        config = new XmlRpcClientConfigImpl();
                        config.setServerURL(new URL(cheapestGrocery));

                        client = new XmlRpcClient();
                        client.setConfig(config);

                        // process the order
                        result = (String) client.execute("Grocery.buy", params);
                        if (!result.equals("")) {
                            // print errors
                            System.err.println("Error : " + result);
                        } else {
                            // Update current values
                            newQuantity = Float.parseFloat(Fridge.getCurrentValues().get(params[0].toString()).toString()) + Float.parseFloat(params[1].toString());
                            Fridge.setCurrentValues(Utils.replaceValue(Fridge.getCurrentValues(), params[0].toString(), newQuantity));
                            System.out.println("Bought " + params[1] + " " + params[0] + " successfully at Grocery " + cheapestGrocery);
                        }
                    } else {
                        System.err.println("The product is not available.");
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (XmlRpcException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}