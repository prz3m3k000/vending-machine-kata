package tdd.vendingMachine.order;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import tdd.vendingMachine.coin.Coin;
import tdd.vendingMachine.coin.CoinValue;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnit4.class)
public class OrderContextTest {

    public static final int PRODUCT_PRICE = 100;
    public static final int PRODUCT_SHELF_NUMBER = 1;

    @Test
    public void order_context_has_shelf_number_test() {
        OrderContext orderContext = createOrderContext();
        assertThat(orderContext.getProductShelfNumber()).isEqualTo(PRODUCT_SHELF_NUMBER);
    }

    @Test
    public void order_context_has_product_price_test() {
        OrderContext orderContext = createOrderContext();
        assertThat(orderContext.getProductPrice()).isEqualTo(PRODUCT_PRICE);
    }

    @Test
    public void order_context_starts_with_empty_list_of_inserted_coins_test() {
        OrderContext orderContext = createOrderContext();
        assertThat(orderContext.getInsertedCoins()).isEmpty();
    }

    @Test
    public void order_context_collects_inserted_coins_test() {
        OrderContext orderContext = createOrderContext();
        Coin coin = new Coin(CoinValue.VALUE_10);
        orderContext.insertCoin(coin);

        assertThat(orderContext.getInsertedCoins()).containsExactlyInAnyOrder(coin);
    }

    @Test
    public void inserted_coins_covers_product_price_test() {
        OrderContext orderContext = createOrderContext();
        orderContext.insertCoin(new Coin(CoinValue.VALUE_50));
        orderContext.insertCoin(new Coin(CoinValue.VALUE_20));
        orderContext.insertCoin(new Coin(CoinValue.VALUE_20));
        orderContext.insertCoin(new Coin(CoinValue.VALUE_10));

        assertThat(orderContext.isProductPriceCovered()).isTrue();
    }

    @Test
    public void inserted_coins_does_not_cover_product_price_test() {
        OrderContext orderContext = createOrderContext();
        orderContext.insertCoin(new Coin(CoinValue.VALUE_10));

        assertThat(orderContext.isProductPriceCovered()).isFalse();
    }

    private OrderContext createOrderContext() {
        return new OrderContext(PRODUCT_SHELF_NUMBER, PRODUCT_PRICE);
    }
}
