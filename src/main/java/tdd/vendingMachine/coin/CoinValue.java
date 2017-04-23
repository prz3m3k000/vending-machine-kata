package tdd.vendingMachine.coin;

public enum CoinValue {

    VALUE_10(10),
    VALUE_20(20),
    VALUE_50(50),
    VALUE_100(100),
    VALUE_200(200),
    VALUE_500(500);

    private int value;

    CoinValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
