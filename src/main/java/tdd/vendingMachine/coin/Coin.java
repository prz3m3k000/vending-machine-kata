package tdd.vendingMachine.coin;

import java.util.Objects;

public class Coin {

    private int value;

    public Coin(CoinValue coinValue) {
        this.value = Objects.requireNonNull(coinValue).getValue();
    }

    public int getValue() {
        return value;
    }
}
