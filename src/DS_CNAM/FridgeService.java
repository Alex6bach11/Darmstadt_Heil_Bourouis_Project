package DS_CNAM;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by aheil on 06/03/2017.
 */
public class FridgeService extends Thread{
    Socket client;
    String message = "tttttttttttt";
    FridgeService(Socket client){this.client = client;}

    public String getResponse(String content){
        String msg = "<html>\n" +
                "<body>\n" +
                content + "\n" +
                "</body>\n" +
                "</html>";
        String response = "HTTP/1.1 200 OK\n" +
                "Content-Length:" + msg.length() + "\n" +
                "Content-Type: text/html\n" +
                "Connection: Closed\r\n\n"+
                msg;

        return response;
    }

    @Override
    public void run (){
        String line;
        BufferedReader fromClient;
        DataOutputStream toClient;
        boolean verbunden = true;
        System.out.println("Thread started: "+this); // Display Thread-ID
        try{
            fromClient = new BufferedReader              // Datastream FROM Client
                    (new InputStreamReader(client.getInputStream()));
            toClient = new DataOutputStream (client.getOutputStream()); // TO Client
            while(verbunden){     // repeat as long as connection exists
                line = fromClient.readLine();              // Read Request
                System.out.println("Received: "+ line);

                if (line.isEmpty())
                    verbunden = false;   // Break Connection?
                else {
                    System.out.println(getResponse("Test"));
                    toClient.writeBytes(getResponse("Test") + '\n'); // Response
                }
            }
            fromClient.close();
            toClient.close();
            client.close(); // End
            System.out.println("Thread ended: "+this);
        }catch (Exception e){
            System.out.println(e);
        }
    }
}
