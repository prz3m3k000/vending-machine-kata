package tdd.vendingMachine.order;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnit4.class)
public class OrderResultTest {

    @Test
    public void order_result_creation_test() {
        assertThat(new OrderResult()).isNotNull();
    }
}
