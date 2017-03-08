package DS_CNAM;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.StringReader;
import java.net.*;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

public class UDPFridgeServer extends Thread {

    @Override
    public void run (){
        try
        {
            byte data[] = new byte[1024];
            byte msg[];
            DatagramPacket packet;
            DatagramSocket socket = new DatagramSocket(1313);
            System.out.println("UDP Fridge Server started at Port 1313");

            String message;
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
                orderProducts();
                msg = Fridge.getCurrentValues().toString().getBytes();

                packet = new DatagramPacket(msg, msg.length, packet.getAddress(), packet.getPort());
                socket.send(packet);
                System.out.println(Fridge.getCurrentValues());
            }
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }

    private void orderProducts() {
        String[] groceriesURLs = {"http://127.0.0.1:8080/xmlrpc", "http://127.0.0.1:8081/xmlrpc"};
        String next;
        Float curVal, warningVal;
        JsonObject currentValues = Fridge.getCurrentValues(), warningLevels = Fridge.getWarningLevels();
        Set<String> key = currentValues.keySet();
        Iterator iter = key.iterator();
        XmlRpcClientConfigImpl config;
        XmlRpcClient client;
        Object[] params;
        String result;
        boolean available;
        Random random = new Random();
        float newQuantity;
        float bestPrice;
        String cheapestGrocery;
        while (iter.hasNext()) {
            next = iter.next().toString();
            curVal = Float.parseFloat(currentValues.get(next).toString());
            warningVal = Float.parseFloat(warningLevels.get(next).toString());

            if(curVal <= warningVal) {
                try {
                    bestPrice = -1;
                    cheapestGrocery = "";
                    params = new Object[]{next, random.nextInt(Integer.SIZE - 1) + 1};
                    for (String grocery : groceriesURLs)
                    {
                        config = new XmlRpcClientConfigImpl();
                        config.setServerURL(new URL(grocery));

                        client = new XmlRpcClient();
                        client.setConfig(config);
                        available = (boolean) client.execute("Grocery.checkAvailability", params);
                        if (available) {
                            result = (String) client.execute("Grocery.getPrice", params);
                            if (bestPrice < 0 || bestPrice > Float.parseFloat(result)) {
                                bestPrice = Float.parseFloat(result);
                                cheapestGrocery = grocery;
                            }
                        }
                    }
                    if (bestPrice >= 0) {
                        config = new XmlRpcClientConfigImpl();
                        config.setServerURL(new URL(cheapestGrocery));

                        client = new XmlRpcClient();
                        client.setConfig(config);

                        result = (String) client.execute("Grocery.buy", params);
                        if (!result.equals("")) {
                            System.err.println("Error : " + result);
                        } else {
                            // Update current values
                            newQuantity = Float.parseFloat(Fridge.getCurrentValues().get(params[0].toString()).toString()) + Float.parseFloat(params[1].toString());
                            Fridge.setCurrentValues(Utils.replaceValue(Fridge.getCurrentValues(), params[0].toString(), newQuantity));
                            System.out.println("Bought " + params[1] + " " + params[0] + " successfully at Grocery " + cheapestGrocery);
                        }
                    } else {
                        System.err.println("The product is not available.");
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (XmlRpcException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
