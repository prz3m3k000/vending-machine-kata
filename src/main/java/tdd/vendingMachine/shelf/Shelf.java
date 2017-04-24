package tdd.vendingMachine.shelf;

import tdd.vendingMachine.product.Product;

public interface Shelf {
    Product pickProduct() throws EmptyShelfException;
    boolean hasProducts();
    int getProductPrice();
}
