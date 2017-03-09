package DS_CNAM;

import javax.json.Json;
import javax.json.*;

public class Fridge {

    // If the quantity of a product is lower than the warning level, a command to a grocery store is processed.
    private static JsonObject warningLevels = initializeWarningLevels();
    private static JsonObject currentValues; // The quantities of each product

    /**
     * Initializes the level that if it is reached, a command to a grocery is processed.
     *
     * @return A JsonObject that contains the warning levels.
     */
    private static JsonObject initializeWarningLevels() {
        JsonObject obj = Json.createObjectBuilder()
                .add("Tequila", 8.0f)
                .add("Chicken", 5.0f)
                .add("Milk", 10.0f)
                .add("Limes", 10.0f).build();
        return obj;
    }

    /**
     * @return The quantity of products.
     */
    public static synchronized JsonObject getCurrentValues() {
        return Fridge.currentValues;
    }

    /**
     * Sets new quantities of products.
     *
     * @param currentValues The new quantity of products.
     */
    public static synchronized void setCurrentValues(JsonObject currentValues) {
        Fridge.currentValues = currentValues;
    }

    /**
     * @return The warning levels.
     */
    public static JsonObject getWarningLevels() {
        return warningLevels;
    }

    /**
     * Sets new warning levels.
     *
     * @param warningLevels New warning levels.
     */
    public static void setWarningLevels(JsonObject warningLevels) {
        Fridge.warningLevels = warningLevels;
    }

    public static void main(String[] args) {
        try {
            // Launch the servers
            new UDPFridgeServer().start();
            new TCPFridgeServer().start();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
