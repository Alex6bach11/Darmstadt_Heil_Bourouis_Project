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
    private static JsonObject currentValues ;



    private static JsonObject initializeWarningLevels() {
        JsonObject obj = Json.createObjectBuilder()
                .add("Tequila", 15555.0f)
                .add("Chicken", 1656.0f)
                .add("Milk", 1555.0f)
                .add("Limes", 2222.0f).build();
        return obj;
    }


    public static void main(String[] args) {
        try
        {
            //treatment with the sensors
            new UDPFridgeServer().start();
            new TCPFridgeServer().start();
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }

    public static synchronized JsonObject getCurrentValues() {
        return Fridge.currentValues;
    }

    public static synchronized void setCurrentValues(JsonObject currentValues) {
        Fridge.currentValues = currentValues;
    }
}
