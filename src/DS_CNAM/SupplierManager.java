package DS_CNAM;

import java.util.ArrayList;
import java.util.Random;

public class SupplierManager {

    private static int nbSupplier = 10;

    public static void main(String[] args) {

        for (int i = 0; i < SupplierManager.nbSupplier; i++) {
            new Supplier("Supplier " + i).start();
        }

    }
}
