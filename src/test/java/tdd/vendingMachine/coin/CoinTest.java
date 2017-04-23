package tdd.vendingMachine.coin;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(JUnit4.class)
public class CoinTest {

    @Test
    public void coin_creation_test() {
        assertThat(new Coin(CoinValue.VALUE_10)).isNotNull();
    }

    @Test
    public void created_coin_has_correct_value_test() {
        Coin coin = new Coin(CoinValue.VALUE_10);
        assertThat(coin.getValue()).isEqualTo(CoinValue.VALUE_10.getValue());
    }

    @Test
    public void cannot_create_coin_from_null_test() {
        assertThatThrownBy(() -> new Coin(null)).isInstanceOf(NullPointerException.class);
    }
}
