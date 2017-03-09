package DS_CNAM;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

/**
 * A UDP client that initializes a random quantity of product in the fridge and decrease this quantity randomly.
 */
public class Sensor {
    private static String host = new String("141.100.45.89"); // The UDP server address
    private static int port = 1313; // The UDP server port
    private static JsonObject currentValues; // The quantities of products.

    /**
     * Initializes the product json object with random quantities.
     *
     * @return The initialized JsonObject.
     */
    private static JsonObject initializeValues() {
        Random r = new Random();
        int minimum = 20, maximum = 30; // 20 products minimum
        JsonObject obj = Json.createObjectBuilder()
                .add("Tequila", r.nextInt(maximum) + minimum)
                .add("Chicken", r.nextInt(maximum) + minimum)
                .add("Milk", r.nextInt(maximum) + minimum)
                .add("Limes", r.nextInt(maximum) + minimum).build();
        return obj;
    }

    /**
     * Decreases the quantity randomly or initializes the quantities if it is not initialized yet.
     */
    private static void decrease() {
        if (currentValues != null) {
            Random r = new Random();
            Set<String> keys = currentValues.keySet();
            Iterator iter = keys.iterator();
            String product;
            float quantity;
            // iterate on keys
            while (iter.hasNext()) {
                // update values with the current values
                product = iter.next().toString();
                quantity = Float.parseFloat(currentValues.get(product).toString());

                // decrease only if quantity is positive
                if (quantity > 0) {
                    // decrease the quantity randomly (between 1 and 5)
                    quantity -= r.nextInt(5) + 1;

                    // if the decreased quantity is less than 0
                    if (quantity > 0) {
                        currentValues = Utils.replaceValue(currentValues, product, quantity);
                    } else {
                        currentValues = Utils.replaceValue(currentValues, product, 0f);
                    }
                }
            }
        } else {
            currentValues = initializeValues();
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println("UDP Sensor Client started...");

        DatagramSocket socket;
        byte msg[]; // the message to send to the server
        byte data[] = new byte[1024]; // the received message
        String message; // the received message
        InetAddress address;
        DatagramPacket packet;

        int sleepTime; // a random sleep time of the thread
        Random r = new Random();

        JsonReader reader; // to read the received data and get it into a JsonObject
        while (true) {
            // Construct and send Request
            socket = new DatagramSocket();
            decrease(); // decrease or initialize quantities
            msg = currentValues.toString().getBytes();
            address = InetAddress.getByName(host);
            packet = new DatagramPacket(msg, msg.length, address, port);
            socket.send(packet);

            // Get the quantities from the fridge
            packet = new DatagramPacket(data, data.length);
            socket.receive(packet);

            // Update the quantities with the received quantities
            message = new String(packet.getData());
            System.out.println(message);
            reader = Json.createReader(new StringReader(message));
            currentValues = reader.readObject();
            socket.close();

            // sleep a random time (between 1 and 5 seconds)
            try {
                sleepTime = r.nextInt(5000) + 1000;
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
