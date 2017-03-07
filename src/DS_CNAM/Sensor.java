package DS_CNAM;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

/**
 * Created by aheil on 06/03/2017.
 */
public class Sensor {
    //  static String host = new String("192.168.178.20");
    static String host = new String("localhost");
    static int port = 1313;

    private static JsonObject initializeValues() {
        Random r = new Random();
        JsonObject obj = Json.createObjectBuilder()
                .add("Tequila", r.nextFloat())
                .add("Chicken", r.nextFloat())
                .add("Milk", r.nextFloat())
                .add("Limes", r.nextFloat()).build();
        return obj;
    }

    private static JsonObject replaceValue(JsonObject currentValues, String key, float value) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        Set<String> set = currentValues.keySet();
        Iterator iter = set.iterator();
        String next;
        while(iter.hasNext()) {
            next = iter.next().toString();
            if (next != key) {
                builder.add(next, Float.parseFloat(currentValues.get(next).toString()));
            } else {
                builder.add(next, value);
            }
        }
        currentValues = builder.build();
        return currentValues;
    }

    public static void main(String[] args) throws IOException {
        System.out.println("UDP Sensor Client started...");
        DatagramSocket socket;
        byte msg[];
        InetAddress address;
        DatagramPacket packet;
        while (true) {
            // Construct and send Request
            socket = new DatagramSocket();
            msg = Fridge.getCurrentValues().toString().getBytes();
            address = InetAddress.getByName(host);

            packet = new DatagramPacket(msg, msg.length, address, port);
            socket.send(packet);

            socket.close();
            decrease();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void decrease() {
        Float f;
        int i = 0;
        JsonObject currentValues = Fridge.getCurrentValues();
        Random r = new Random();
        Set<String> key = currentValues.keySet();
        Iterator iter = key.iterator();
        String next = "";
        while(iter.hasNext()) {
            next = iter.next().toString();
            f= Float.parseFloat(currentValues.get(next).toString());
            System.out.println("f avant=" +  f);
            i = r.nextInt();
            f -= i;
            System.out.println("f apres=" +  f);
            if (f > 0) {
                currentValues = replaceValue(currentValues, next, f);
            }
            else {
                currentValues = replaceValue(currentValues, next, 0f);
            }
        }
        System.out.println(currentValues);
        Fridge.setCurrentValues(currentValues);
        System.out.println(Fridge.getCurrentValues());
        // currentValues.forEach();
    }
}
