package DS_CNAM;

import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.webserver.WebServer;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Grocery {

    ArrayList<Product> products = new ArrayList<>();
    private static File tmpFile;

  private static final int port = 8080;

    private ArrayList<Product> initializeProducts() {
        return null;
    }
  private static void addCommandToHistory(String product, float price, float quantity) {
      try{
          PrintWriter writer;
          if( tmpFile == null) {
              tmpFile = File.createTempFile("GroceryLog", ".csv");
              writer = new PrintWriter(tmpFile);
              writer.println("Product;Price;Quantity");
          }
          else {
              writer = new PrintWriter(new FileWriter(tmpFile, true));
          }

          writer.println(product + ";" +price +";"+ quantity +";");
          writer.close();
      } catch (IOException e) {
          System.out.println(e);
      }
  }

  public static void main (String [] args) {

      addCommandToHistory("Product 1", 1.4f, 25.0f);
      addCommandToHistory("Product 2", 1.8f, 26.0f);
    /*try {

      WebServer webServer = new WebServer(port);

      XmlRpcServer xmlRpcServer = webServer.getXmlRpcServer();
      PropertyHandlerMapping phm = new PropertyHandlerMapping();

      phm.addHandler( "Calculator", Grocery.class);
      xmlRpcServer.setHandlerMapping(phm);

     XmlRpcServerConfigImpl serverConfig =
              (XmlRpcServerConfigImpl) xmlRpcServer.getConfig();
     // serverConfig.setEnabledForExtensions(true);
     // serverConfig.setContentLengthOptional(false);

      webServer.start();

      System.out.println("The Calculator Server has been started..." );

    } catch (Exception exception) {
       System.err.println("JavaServer: " + exception);
    }*/
  }
}
