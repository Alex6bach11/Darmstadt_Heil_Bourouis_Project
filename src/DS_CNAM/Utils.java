package DS_CNAM;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class Utils {

    public static ArrayList<String> productNames = new ArrayList<String>() {{
        add("Tequila");
        add("Chicken");
        add("Milk");
        add("Limes");
    }};
    public static String broker = "tcp://141.100.45.127:1883"; // Address:port of Mosquitto
    public static ArrayList<String> topics = new ArrayList<String>() {{
        add("Topic 1");
        add("Topic 2");
        add("Topic 3");
        add("Topic 4");
    }};

    /**
     * Replace a value in a JsonObject.
     *
     * @param currentValues The JsonObject where the value has to be replaced.
     * @param key           The key to find in the object, where the value has to be replaced.
     * @param value         The new value.
     * @return The new JsonObject where the value has been replaced.
     */
    public static JsonObject replaceValue(JsonObject currentValues, String key, float value) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        Set<String> set = currentValues.keySet();
        Iterator iter = set.iterator();
        String next;
        while (iter.hasNext()) {
            next = iter.next().toString();
            if (!next.equals(key)) {
                builder.add(next, Float.parseFloat(currentValues.get(next).toString()));
            } else { // add the new value
                builder.add(next, value);
            }
        }
        currentValues = builder.build();
        return currentValues;
    }
}
