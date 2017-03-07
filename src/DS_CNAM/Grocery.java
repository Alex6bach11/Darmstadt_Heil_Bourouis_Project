package DS_CNAM;

import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.webserver.WebServer;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class Grocery {

  private static ArrayList<Product> products = initializeProducts();
  private static File tmpFile;

  private static final int port = 8080;

  private static void addCommandToHistory(String product, float price, float quantity) {
      try {
          PrintWriter writer;
          if (tmpFile == null) {
              tmpFile = File.createTempFile("GroceryLog", ".csv");
              writer = new PrintWriter(tmpFile);
              writer.println("Product;Price;Quantity");
          } else {
              writer = new PrintWriter(new FileWriter(tmpFile, true));
          }

          writer.println(product + ";" + price + ";" + quantity + ";");
          writer.close();
      } catch (IOException e) {
          System.out.println(e);
      }
  }

  private Product getProductByName(String productName) {
    for (Product product : products)
    {
      if (product.getName().toLowerCase() == productName.toLowerCase())
      {
        return product;
      }
    }
    return null;
  }

  private static ArrayList<Product> initializeProducts() {
    ArrayList<Product> products = new ArrayList<>();
    Random random = new Random();
    products.add(new Product("Tequila", random.nextInt(), random.nextFloat()));
    products.add(new Product("Chicken", random.nextInt(), random.nextFloat()));
    products.add(new Product("Milk", random.nextInt(), random.nextFloat()));
    products.add(new Product("Limes", random.nextInt(), random.nextFloat()));
    return products;
  }

  /**
   *
   * @param productName The name of the product to buy
   * @param quantity The quantity to buy
   * @return an empty string if the transaction is successful or the error if it isn't
   */
  public String buy(String productName, int quantity) {
    Product product = getProductByName(productName);
    if (product != null) {
      if (product.getQuantity() >= quantity && quantity > 0) {
        try {
          product.sell(quantity);
          addCommandToHistory(productName, product.getPrice(), quantity);
          return "";
        } catch (Error e) {
          e.printStackTrace();
          return e.getMessage();
        }
      }
      else {
        return "Product not available in this quantity (" + product.getQuantity() + " remaining)";
      }
    }
    else {
      return "Product not available";
    }
  }

  public static void main (String [] args) {
    try {
      WebServer webServer = new WebServer(port);

      XmlRpcServer xmlRpcServer = webServer.getXmlRpcServer();
      PropertyHandlerMapping phm = new PropertyHandlerMapping();

      phm.addHandler( "Grocery", Grocery.class);
      xmlRpcServer.setHandlerMapping(phm);

     XmlRpcServerConfigImpl serverConfig =
              (XmlRpcServerConfigImpl) xmlRpcServer.getConfig();
     // serverConfig.setEnabledForExtensions(true);
     // serverConfig.setContentLengthOptional(false);

      webServer.start();
      System.out.println("The Grocery Server has been started..." );
      Random rand = new Random();
      while (true) { // reinitialize quantities and prices
          Thread.sleep(rand.nextInt() * 10000 + 1);
          products = initializeProducts();
      }
    } catch (Exception exception) {
       System.err.println("JavaServer: " + exception);
    }
  }
}
