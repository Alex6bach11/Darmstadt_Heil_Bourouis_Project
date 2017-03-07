package DS_CNAM;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Date;
import javax.json.Json;
import javax.json.*;


/**
 * Created by aheil on 06/03/2017.
 */
public class Fridge {

    private static JsonObject warningLevels = initializeWarningLevels();
    private static JsonObject currentValues = initializeValues();

    private static JsonObject initializeWarningLevels() {
        JsonObject obj = Json.createObjectBuilder()
                .add("Tequila", 1.0f)
                .add("Chicken", 1.0f)
                .add("Milk", 1.0f)
                .add("Limes", 2.0f).build();
        return obj;
    }

    private static JsonObject initializeValues() {
        JsonObject obj = Json.createObjectBuilder()
                .add("Tequila", 1.0f)
                .add("Chicken", 1.0f)
                .add("Milk", 1.0f)
                .add("Limes", 2.0f).build();
        return obj;
    }
    public static void main(String[] args) {
        try
        {

            byte data[] = new byte[1024];
            /*DatagramPacket packet;
            DatagramSocket socket = new DatagramSocket(1313);
            System.out.println("UDP Time Server started at Port 1313");*/
            //treatment with the sensors
            new UDPFridgeServer().start();
            new TCPFridgeServer().start();
            //treatment with the browser


          /*  while ( true )
            {
                // Wait for request
                packet = new DatagramPacket(data, data.length);
                socket.receive(packet);
                // Decode sender, ignore all other content
                InetAddress address = packet.getAddress();
                int         port   = packet.getPort();
                // Encode answer
                String s = "Time for " + address
                        + " Port "  + port + " = "
                        + new Date().toString() + "\n";
                data = s.getBytes();
                System.out.print( s );
                // Send ansswer
                packet = new DatagramPacket(data,data.length,address,port);
                socket.send(packet);
            }*/
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }
}
