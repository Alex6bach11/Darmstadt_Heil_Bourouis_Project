package DS_CNAM;

public class SupplierManager {

    private static int nbSupplier = 10;

    public static void main(String[] args) {
        // Start 10 suppliers
        for (int i = 0; i < SupplierManager.nbSupplier; i++) {
            new Supplier("Supplier " + i).start();
        }
    }
}
