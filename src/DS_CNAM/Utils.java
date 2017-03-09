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

    public static String broker = "tcp://localhost:1883";
    public static ArrayList<String> topics = new ArrayList<String>() {{
        add("Topic 1");
        add("Topic 2");
        add("Topic 3");
        add("Topic 4");
    }};

    public static JsonObject replaceValue(JsonObject currentValues, String key, float value) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        Set<String> set = currentValues.keySet();
        Iterator iter = set.iterator();
        String next;
        while(iter.hasNext()) {
            next = iter.next().toString();
            if (!next.equals(key)) {
                builder.add(next, Float.parseFloat(currentValues.get(next).toString()));
            } else {
                builder.add(next, value);
            }
        }
        currentValues = builder.build();
        return currentValues;
    }
}
