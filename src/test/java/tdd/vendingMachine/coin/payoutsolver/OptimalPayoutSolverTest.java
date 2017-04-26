package tdd.vendingMachine.coin.payoutsolver;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import tdd.vendingMachine.coin.Coin;
import tdd.vendingMachine.coin.CoinValue;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(JUnit4.class)
public class OptimalPayoutSolverTest {

    @Test
    public void optimal_payout_solver_creation_test() {
        assertThat(new OptimalPayoutSolver()).isNotNull();
    }

    @Test
    public void negative_amount_is_illegal_argument_test() {
        PayoutSolver ps = new OptimalPayoutSolver();
        assertThatThrownBy(() -> ps.solvePayout(-1, Collections.emptyList())).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void no_coins_required_to_payout_zero_test() throws NotEnoughCoinsException {
        PayoutSolver ps = new OptimalPayoutSolver();

        List<Coin> payout = ps.solvePayout(0, Collections.emptyList());
        assertThat(payout).isEmpty();
    }

    @Test
    public void payout_is_done_using_provided_coins_test() throws NotEnoughCoinsException {
        PayoutSolver ps = new OptimalPayoutSolver();

        Coin coin50 = new Coin(CoinValue.VALUE_50);
        List<Coin> payout = ps.solvePayout(50, Collections.singletonList(coin50));

        assertThat(payout).containsExactlyInAnyOrder(coin50);
    }

    @Test
    public void payout_is_done_using_least_amount_of_coins_test() throws NotEnoughCoinsException {
        PayoutSolver ps = new OptimalPayoutSolver();

        List<Coin> availableCoins = new LinkedList<>();
        availableCoins.add(new Coin(CoinValue.VALUE_10));
        availableCoins.add(new Coin(CoinValue.VALUE_20));
        availableCoins.add(new Coin(CoinValue.VALUE_20));
        availableCoins.add(new Coin(CoinValue.VALUE_20));
        availableCoins.add(new Coin(CoinValue.VALUE_50));

        List<Coin> payout = ps.solvePayout(60, availableCoins);
        assertThat(payout).hasSize(2);
    }

    @Test
    public void payout_has_correct_value_test() throws NotEnoughCoinsException {
        PayoutSolver ps = new OptimalPayoutSolver();

        List<Coin> availableCoins = new LinkedList<>();
        availableCoins.add(new Coin(CoinValue.VALUE_10));
        availableCoins.add(new Coin(CoinValue.VALUE_20));
        availableCoins.add(new Coin(CoinValue.VALUE_20));
        availableCoins.add(new Coin(CoinValue.VALUE_20));
        availableCoins.add(new Coin(CoinValue.VALUE_50));

        List<Coin> payout = ps.solvePayout(60, availableCoins);
        assertThat(sumUpCoinValues(payout)).isEqualTo(60);
    }

    @Test
    public void cannot_solve_payout_without_enough_coins_test() {
        PayoutSolver ps = new OptimalPayoutSolver();

        List<Coin> availableCoins = new LinkedList<>();
        availableCoins.add(new Coin(CoinValue.VALUE_10));
        availableCoins.add(new Coin(CoinValue.VALUE_20));

        assertThatThrownBy(() -> ps.solvePayout(50, availableCoins)).isInstanceOf(NotEnoughCoinsException.class);

    }

    private int sumUpCoinValues(List<Coin> coins) {
        return coins.stream().mapToInt(Coin::getValue).sum();
    }
}
