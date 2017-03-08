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

  private static final int port = 8081;

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
      if (product.getName().toLowerCase().trim().equals(productName.toLowerCase().trim()))
      {
        return product;
      }
    }
    return null;
  }

  public boolean checkAvailability(String productName, int quantity) {
      Product product = getProductByName(productName);
      return product != null && product.getQuantity() >= quantity;
  }

  public String getPrice(String productName, int quantity) {
      Product product = getProductByName(productName);
      float price = product != null? product.getPrice() * quantity : -1;
      return String.format("00.00", price);
  }

  private static ArrayList<Product> initializeProducts() {
    ArrayList<Product> products = new ArrayList<>();
    Random random = new Random();
    products.add(new Product("Tequila", random.nextInt(Integer.SIZE - 1) * 100, Math.round(random.nextFloat() * 1000) / 100f));
    products.add(new Product("Chicken", random.nextInt(Integer.SIZE - 1) * 100, Math.round(random.nextFloat() * 1000) / 100f));
    products.add(new Product("Milk", random.nextInt(Integer.SIZE - 1) * 100, Math.round(random.nextFloat() * 1000) / 100f));
    products.add(new Product("Limes", random.nextInt(Integer.SIZE - 1) * 100, Math.round(random.nextFloat() * 1000) / 100f));
      System.out.println(products.get(0).getName() + " : " + products.get(0).getQuantity() + " (" + products.get(0).getPrice() + " €)");
      System.out.println(products.get(1).getName() + " : " + products.get(1).getQuantity() + " (" + products.get(1).getPrice() + " €)");
      System.out.println(products.get(2).getName() + " : " + products.get(2).getQuantity() + " (" + products.get(2).getPrice() + " €)");
      System.out.println(products.get(3).getName() + " : " + products.get(3).getQuantity() + " (" + products.get(3).getPrice() + " €)");
    return products;
  }

  /**
   *
   * @param productName The name of the product to buy
   * @param quantity The quantity to buy
   * @return an empty string if the transaction is successful or the error if it isn't
   */
  public String buy(String productName, int quantity) {
      System.out.println("Start selling " + quantity + " " + productName + "...");
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

      webServer.start();
      System.out.println("The Grocery Server has been started..." );
      Random rand = new Random();
      while (true) { // reinitialize quantities and prices
          Thread.sleep(rand.nextInt(Integer.SIZE - 1) * 1000);
          products = initializeProducts();
      }
    } catch (Exception exception) {
       System.err.println("JavaServer: " + exception);
    }
  }
}
