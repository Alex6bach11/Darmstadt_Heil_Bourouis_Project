package DS_CNAM;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.stream.JsonParser;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.*;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by aheil on 06/03/2017.
 */
public class UDPFridgeServer extends Thread {


    @Override
    public void run (){
        try
        {

            byte data[] = new byte[1024];
            DatagramPacket packet;
            DatagramSocket socket = new DatagramSocket(1313);
            System.out.println("UDP Fridge Server started at Port 1313");

            String message ="";
            JsonReader reader;
            while ( true )
            {
                // Wait for request
                packet = new DatagramPacket(data, data.length);
                socket.receive(packet);
                // Decode sender, ignore all other content
                message = new String(packet.getData());
                System.out.println(message);
                reader = Json.createReader(new StringReader(message));

                Fridge.setCurrentValues(reader.readObject());
                orderedProducts();
            }
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }

    private void orderedProducts() {
        String next;
        Float curVal, warningVal;
        JsonObject currentValues = Fridge.getCurrentValues(), warningLevels = Fridge.getWarningLevels();
        Set<String> key = currentValues.keySet();
        Iterator iter = key.iterator();
        while (iter.hasNext()) {
            next = iter.next().toString();
            curVal = Float.parseFloat(currentValues.get(next).toString());
            warningVal = Float.parseFloat(warningLevels.get(next).toString());

            if(curVal <= warningVal) {
                try {
                    XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
                    config.setServerURL(new URL("http://127.0.0.1:8080/xmlrpc"));

                    XmlRpcClient client = new XmlRpcClient();
                    client.setConfig(config);

                    //TODO a modifier avec une constante ou une valeur random je sais pas
                    Object[] params = new Object[]{next, 1000};

                    String result = (String) client.execute("Grocery.buy", params);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (XmlRpcException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
