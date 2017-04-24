package tdd.vendingMachine.shelf;

import tdd.vendingMachine.product.Product;

import java.util.Collection;
import java.util.EmptyStackException;
import java.util.Stack;

public class PreloadedShelf implements Shelf {

    private Stack<Product> products;
    private int productPrice;

    public PreloadedShelf(int productPrice, Collection<Product> products) {
        this.productPrice = productPrice;
        this.products = createPreloadedStackOfProducts(products);
    }

    @Override
    public Product pickProduct() throws EmptyShelfException {
        try {
            return products.pop();
        }
        catch (EmptyStackException esEx) {
            throw new EmptyShelfException();
        }
    }

    @Override
    public boolean hasProducts() {
        return products.size() > 0;
    }

    @Override
    public int getProductPrice() {
        return productPrice;
    }

    private Stack<Product> createPreloadedStackOfProducts(Collection<Product> products) {
        Stack<Product> productsStack = new Stack<>();
        productsStack.addAll(products);
        return productsStack;
    }
}
