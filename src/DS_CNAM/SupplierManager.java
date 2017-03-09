package DS_CNAM;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by aheil on 08/03/2017.
 */
public class SupplierManager {

    private static int nbSupplier = 10;

    public static void main(String[] args) {
       Supplier sup;
        ArrayList<Product> products ;
        Random r = new Random();
        String msg;

            for (int i = 0; i < SupplierManager.nbSupplier; i++) {

                sup = new Supplier("Supplier " + i);
                msg = sup.getClientId();
                products = new ArrayList<>();
                products.add(new Product(Utils.productNames.get(i % Utils.productNames.size()), r.nextInt(Integer.SIZE - 1) * 1000, Math.round(r.nextFloat() * 1000) / 100f));
                sup.setProductsToSell(products);
                for (Product p : products) {
                    msg += "||" + p.display();
                }
                String topic = Utils.topics.get((r.nextInt(Integer.SIZE - 1)) % Utils.topics.size());
                System.out.println("Publish to topic : " + topic + " Message : " + msg);
                sup.publishMessage(topic, msg);

            }

    }
}
