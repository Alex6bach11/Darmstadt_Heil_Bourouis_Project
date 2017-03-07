package DS_CNAM;

import java.io.IOException;
import java.net.*;

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
                client = listenSocket.accept();
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
