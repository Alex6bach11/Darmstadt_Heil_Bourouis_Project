package DS_CNAM;

import javax.json.JsonArray;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
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
            int port;
            String s = "", message ="";
            while ( true )
            {
                // Wait for request
                packet = new DatagramPacket(data, data.length);
                socket.receive(packet);
                // Decode sender, ignore all other content
                InetAddress address = packet.getAddress();

                port   = packet.getPort();
                message = new String(packet.getData());

                System.out.println("Test " +message);
            }
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }

}
