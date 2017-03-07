package DS_CNAM;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

public class Sensor {
    //  static String host = new String("192.168.178.20");
    static String host = new String("141.100.45.127");
    static int port = 1313;
    private static JsonObject currentValues ;

    private static JsonObject initializeValues() {
        Random r = new Random();
        int modulo = 1000;
        JsonObject obj = Json.createObjectBuilder()
                .add("Tequila", (r.nextInt(Integer.SIZE-1)%modulo) + modulo)
                .add("Chicken", (r.nextInt(Integer.SIZE-1)%modulo) + modulo)
                .add("Milk", (r.nextInt(Integer.SIZE-1)%modulo) + modulo)
                .add("Limes", (r.nextInt(Integer.SIZE-1)%modulo) + modulo).build();
        System.out.println(obj);
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
        int sleepTime;
        byte msg[];
        InetAddress address;
        DatagramPacket packet;
        Random r = new Random();
        while (true) {
            // Construct and send Request
            socket = new DatagramSocket();
            decrease();
            System.out.println(currentValues.toString());
            msg = currentValues.toString().getBytes();

            address = InetAddress.getByName(host);

            packet = new DatagramPacket(msg, msg.length, address, port);
            socket.send(packet);

            socket.close();

            try {
                sleepTime = r.nextInt(Integer.SIZE-1) + 1000;
                System.out.println(sleepTime);
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void decrease() {
        Float f;

        if( currentValues != null) {
            Random r = new Random();
            Set<String> key = currentValues.keySet();
            Iterator iter = key.iterator();
            String next = "";
            while (iter.hasNext()) {
                next = iter.next().toString();
                f = Float.parseFloat(currentValues.get(next).toString());

                //to have a positive value
                f -= r.nextInt(Integer.SIZE-1)%5;

                if (f > 0) {
                    currentValues = replaceValue(currentValues, next, f);
                } else {
                    currentValues = replaceValue(currentValues, next, 0f);
                }
            }
        }
        else {
            currentValues = initializeValues();
        }

    }
}
