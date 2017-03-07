package DS_CNAM;

import javax.json.JsonObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by aheil on 06/03/2017.
 */
public class FridgeService extends Thread{
    Socket client;

    FridgeService(Socket client){this.client = client;}

    public String getResponse(JsonObject content){
        String response = "";

        if (content != null) {
            response = "HTTP/1.1 200 OK\n" +
                    "Content-Length:" + content.toString().length() + "\n" +
                    "Content-Type: application/json\n" +
                    "\r\n" +
                    content.toString();
        }
        return response;
    }

    @Override
    public void run (){
        String line;
        BufferedReader fromClient;
        DataOutputStream toClient;
        boolean verbunden = true;
        int i =0;
        System.out.println("Thread started: "+this); // Display Thread-ID
        try{
            fromClient = new BufferedReader              // Datastream FROM Client
                    (new InputStreamReader(client.getInputStream()));
            toClient = new DataOutputStream (client.getOutputStream()); // TO Client
            while(verbunden && i < 2){     // repeat as long as connection exists
                line = fromClient.readLine();              // Read Request

                System.out.println("Received: "+ line != null ? line : "");

                if (line == null || line.isEmpty() ) {
                    System.out.println("Break Connection\n");
                    verbunden = false;   // Break Connection?
                }
                else {
                    toClient.writeBytes(getResponse(Fridge.getCurrentValues()) ); // Response
                }
                i++;
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
