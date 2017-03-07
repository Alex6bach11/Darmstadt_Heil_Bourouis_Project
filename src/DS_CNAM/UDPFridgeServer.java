package DS_CNAM;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.stream.JsonParser;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

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
                reader = Json.createReader(new StringReader(message));
                //System.out.println("Test " + reader.readObject());
                Fridge.setCurrentValues(reader.readObject());
            }
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }

}
