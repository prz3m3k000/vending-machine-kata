package tdd.vendingMachine.order;

import tdd.vendingMachine.coin.Coin;

import java.util.LinkedList;
import java.util.List;

public class OrderContext {

    private int productShelfNumber;
    private int productPrice;
    private List<Coin> insertedCoins = new LinkedList<>();
    private int insertedCoinsValue;

    public OrderContext(int productShelfNumber, int productPrice) {
        this.productShelfNumber = productShelfNumber;
        this.productPrice = productPrice;
    }

    public int getProductShelfNumber() {
        return productShelfNumber;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public List<Coin> getInsertedCoins() {
        return insertedCoins;
    }

    public void insertCoin(Coin coin) {
        insertedCoins.add(coin);
        insertedCoinsValue += coin.getValue();
    }

    public boolean isProductPriceCovered() {
        return insertedCoinsValue >= productPrice;
    }
}
