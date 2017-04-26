package tdd.vendingMachine.order;

import tdd.vendingMachine.coin.Coin;
import tdd.vendingMachine.display.Display;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class OrderContext {

    private Display display;

    private int productShelfNumber;
    private int productPrice;
    private List<Coin> insertedCoins = new LinkedList<>();
    private int insertedCoinsValue;

    public OrderContext(int productShelfNumber, int productPrice, Display display) {
        this.productShelfNumber = productShelfNumber;
        this.productPrice = productPrice;
        this.display = Objects.requireNonNull(display);
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
        updateRemainingAmountMessage();
    }

    public boolean isProductPriceCovered() {
        return insertedCoinsValue >= productPrice;
    }

    public int getInsertedCoinsValue() {
        return insertedCoinsValue;
    }

    private void updateRemainingAmountMessage() {
        int remainingAmount = Math.max(0, productPrice - insertedCoinsValue);
        display.printMessage(Integer.toString(remainingAmount));
    }
}
