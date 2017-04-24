package tdd.vendingMachine.coin;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import tdd.vendingMachine.coin.payoutsolver.NotEnoughCoinsException;
import tdd.vendingMachine.coin.payoutsolver.PayoutSolver;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class CoinRepositoryTest {

    @Test
    public void coin_repository_creation_test() {
        assertThat(new CoinRepository(mock(PayoutSolver.class))).isNotNull();
    }

    @Test
    public void repository_collects_added_coins_test() {
        // given
        List<Coin> coinsToAdd = createCoinList(CoinValue.VALUE_50, CoinValue.VALUE_100, CoinValue.VALUE_200);
        CoinRepository coinRepository = new CoinRepository(mock(PayoutSolver.class));

        // when
        coinRepository.addCoins(coinsToAdd);

        // then
        assertThat(coinRepository.getCoinsInRepository()).containsExactlyElementsOf(coinsToAdd);
    }

    @Test
    public void value_of_withdrawn_coins_equals_to_requested_amount_test() throws NotEnoughCoinsException {
        // given
        List<Coin> coinsToPayout = createCoinList(CoinValue.VALUE_20, CoinValue.VALUE_10);
        CoinRepository coinRepository = new CoinRepository(createPayoutSolverMock(coinsToPayout));
        int withdrawalAmount = sumUpCoinValues(coinsToPayout);

        // when
        List<Coin> withdrawCoins = coinRepository.withdrawAmount(withdrawalAmount);

        // then
        assertThat(sumUpCoinValues(withdrawCoins)).isEqualTo(withdrawalAmount);
    }

    @Test
    public void repository_removes_withdrawn_coins_test() throws NotEnoughCoinsException {
        // given
        List<Coin> coinsToAdd = createCoinList(CoinValue.VALUE_20, CoinValue.VALUE_10, CoinValue.VALUE_50);
        List<Coin> coinsToPayout = coinsToAdd.subList(0, 1);
        int withdrawalAmount = sumUpCoinValues(coinsToPayout);

        CoinRepository coinRepository = new CoinRepository(createPayoutSolverMock(coinsToPayout));
        coinRepository.addCoins(coinsToAdd);

        // when
        List<Coin> withdrawnCoins = coinRepository.withdrawAmount(withdrawalAmount);

        // then
        assertThat(coinRepository.getCoinsInRepository()).doesNotContainAnyElementsOf(withdrawnCoins);
    }

    private int sumUpCoinValues(List<Coin> coins) {
        return coins.stream().mapToInt(Coin::getValue).sum();
    }

    private PayoutSolver createPayoutSolverMock(List<Coin> payoutCoins) throws NotEnoughCoinsException {
        PayoutSolver payoutSolverMock = mock(PayoutSolver.class);
        when(payoutSolverMock.solvePayout(anyInt(), anyListOf(Coin.class))).thenReturn(payoutCoins);
        return payoutSolverMock;
    }

    private List<Coin> createCoinList(CoinValue... coinValues) {
        return Arrays.stream(coinValues).map(Coin::new).collect(Collectors.toList());
    }
}

