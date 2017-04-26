package tdd.vendingMachine.order;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import tdd.vendingMachine.coin.Coin;
import tdd.vendingMachine.coin.CoinValue;
import tdd.vendingMachine.display.Display;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(JUnit4.class)
public class OrderContextTest {

    public static final int PRODUCT_PRICE = 100;
    public static final int PRODUCT_SHELF_NUMBER = 1;

    private OrderContext orderContext;
    private Display displayMock;

    @Before
    public void before() {
        displayMock = mock(Display.class);
        orderContext = new OrderContext(PRODUCT_SHELF_NUMBER, PRODUCT_PRICE, displayMock);
    }

    @Test
    public void order_context_has_shelf_number_test() {
        assertThat(orderContext.getProductShelfNumber()).isEqualTo(PRODUCT_SHELF_NUMBER);
    }

    @Test
    public void order_context_has_product_price_test() {
        assertThat(orderContext.getProductPrice()).isEqualTo(PRODUCT_PRICE);
    }

    @Test
    public void order_context_starts_with_empty_list_of_inserted_coins_test() {
        assertThat(orderContext.getInsertedCoins()).isEmpty();
    }

    @Test
    public void order_context_collects_inserted_coins_test() {
        Coin coin = new Coin(CoinValue.VALUE_10);
        orderContext.insertCoin(coin);

        assertThat(orderContext.getInsertedCoins()).containsExactlyInAnyOrder(coin);
    }

    @Test
    public void inserting_coin_updates_remaining_amount_on_display_test() {
        Coin coinToInsert = new Coin(CoinValue.VALUE_10);
        orderContext.insertCoin(coinToInsert);

        verify(displayMock).printMessage(Integer.toString(PRODUCT_PRICE - coinToInsert.getValue()));
    }

    @Test
    public void inserted_coins_value_is_equal_to_value_of_all_inserted_coins_test() {
        orderContext.insertCoin(new Coin(CoinValue.VALUE_50));
        orderContext.insertCoin(new Coin(CoinValue.VALUE_20));

        assertThat(orderContext.getInsertedCoinsValue()).isEqualTo(70);
    }

    @Test
    public void inserted_coins_covers_product_price_test() {
        orderContext.insertCoin(new Coin(CoinValue.VALUE_50));
        orderContext.insertCoin(new Coin(CoinValue.VALUE_20));
        orderContext.insertCoin(new Coin(CoinValue.VALUE_20));
        orderContext.insertCoin(new Coin(CoinValue.VALUE_10));

        assertThat(orderContext.isProductPriceCovered()).isTrue();
    }

    @Test
    public void inserted_coins_does_not_cover_product_price_test() {
        orderContext.insertCoin(new Coin(CoinValue.VALUE_10));

        assertThat(orderContext.isProductPriceCovered()).isFalse();
    }
}
