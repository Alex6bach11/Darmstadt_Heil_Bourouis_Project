package DS_CNAM;

import javax.json.Json;
import javax.json.*;

public class Fridge {

    private static JsonObject warningLevels = initializeWarningLevels();
    private static JsonObject currentValues ;

    private static JsonObject initializeWarningLevels() {
        JsonObject obj = Json.createObjectBuilder()
                .add("Tequila", 8.0f)
                .add("Chicken", 5.0f)
                .add("Milk", 10.0f)
                .add("Limes", 10.0f).build();
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

    public static JsonObject getWarningLevels() {
        return warningLevels;
    }

    public static void setWarningLevels(JsonObject warningLevels) {
        Fridge.warningLevels = warningLevels;
    }
}
