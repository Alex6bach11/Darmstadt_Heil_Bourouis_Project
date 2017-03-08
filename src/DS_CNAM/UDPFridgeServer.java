package DS_CNAM;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.metadata.Util;

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
        String next;
        Float curVal, warningVal;
        JsonObject currentValues = Fridge.getCurrentValues(), warningLevels = Fridge.getWarningLevels();
        Set<String> key = currentValues.keySet();
        Iterator iter = key.iterator();
        XmlRpcClientConfigImpl config;
        XmlRpcClient client;
        Object[] params;
        String result;
        Random random = new Random();
        float newQuantity;
        while (iter.hasNext()) {
            next = iter.next().toString();
            curVal = Float.parseFloat(currentValues.get(next).toString());
            warningVal = Float.parseFloat(warningLevels.get(next).toString());

            if(curVal <= warningVal) {
                try {
                    config = new XmlRpcClientConfigImpl();
                    config.setServerURL(new URL("http://127.0.0.1:8080/xmlrpc"));

                    client = new XmlRpcClient();
                    client.setConfig(config);

                    params = new Object[]{next, random.nextInt(Integer.SIZE - 1)};

                    result = (String) client.execute("Grocery.buy", params);
                    if (!result.equals("")) {
                        System.err.println("Error : " + result);
                    } else {
                        System.out.println("bought " + params[1] + " " + params[0] + " successfully");
                        // Update current values
                        newQuantity = Float.parseFloat(Fridge.getCurrentValues().get(params[0].toString()).toString()) + Float.parseFloat(params[1].toString());
                        Fridge.setCurrentValues(Utils.replaceValue(Fridge.getCurrentValues(), params[0].toString(), newQuantity));
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
