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

public class Sensor {
    static String host = new String("localhost");
    static int port = 1313;
    private static JsonObject currentValues ;

    private static JsonObject initializeValues() {
        Random r = new Random();
        int modulo = 20;
        JsonObject obj = Json.createObjectBuilder()
                .add("Tequila", (r.nextInt(Integer.SIZE-1)%modulo) + modulo)
                .add("Chicken", (r.nextInt(Integer.SIZE-1)%modulo) + modulo)
                .add("Milk", (r.nextInt(Integer.SIZE-1)%modulo) + modulo)
                .add("Limes", (r.nextInt(Integer.SIZE-1)%modulo) + modulo).build();
        return obj;
    }

    public static void main(String[] args) throws IOException {
        System.out.println("UDP Sensor Client started...");
        DatagramSocket socket;
        int sleepTime;
        byte msg[];
        byte data[] = new byte[1024];
        String message;
        InetAddress address;
        DatagramPacket packet;
        Random r = new Random();
        JsonReader reader;
        while (true) {
            // Construct and send Request
            socket = new DatagramSocket();
            decrease();
            msg = currentValues.toString().getBytes();

            address = InetAddress.getByName(host);

            packet = new DatagramPacket(msg, msg.length, address, port);
            socket.send(packet);
            packet = new DatagramPacket(data, data.length);
            socket.receive(packet);

            message = new String(packet.getData());
            System.out.println(message);
            reader = Json.createReader(new StringReader(message));

            Fridge.setCurrentValues(reader.readObject());
            socket.close();

            try {
                sleepTime = r.nextInt(Integer.SIZE-1) + 1000;
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
                    currentValues = Utils.replaceValue(currentValues, next, f);
                } else {
                    currentValues = Utils.replaceValue(currentValues, next, 0f);
                }
            }
        }
        else {
            currentValues = initializeValues();
        }

    }
}
