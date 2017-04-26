package tdd.vendingMachine.order;

import tdd.vendingMachine.coin.Coin;
import tdd.vendingMachine.product.Product;

import java.util.List;
import java.util.Optional;

public class OrderResult {

    private Product product;
    private List<Coin> change;

    public OrderResult(Product product, List<Coin> change) {
        this.product = product;
        this.change = change;
    }

    public Optional<Product> getProduct() {
        return Optional.ofNullable(product);
    }

    public Optional<List<Coin>> getChange() {
        return Optional.ofNullable(change);
    }
}
