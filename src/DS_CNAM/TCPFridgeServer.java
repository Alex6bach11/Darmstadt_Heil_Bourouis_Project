package DS_CNAM;

import Example.EchoService;

import java.io.IOException;
import java.net.*;

/**
 * Created by aheil on 06/03/2017.
 */
public class TCPFridgeServer extends Thread {

    Socket client;

    @Override
    public void run () {
        int port = 9999;
        ServerSocket listenSocket = null;
        try {
            listenSocket = new ServerSocket(port);

            System.out.println("Multithreaded Server starts on Port " + port);
            while (true) {
                Socket client = listenSocket.accept();
                System.out.println("Connection with: " +     // Output connection
                        client.getRemoteSocketAddress());   // (Client) address
                new FridgeService(client).start();

            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
