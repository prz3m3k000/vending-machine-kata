package tdd.vendingMachine.coin;

import tdd.vendingMachine.coin.payoutsolver.NotEnoughCoinsException;
import tdd.vendingMachine.coin.payoutsolver.PayoutSolver;

import java.util.ArrayList;
import java.util.List;

public class CoinRepository {

    private PayoutSolver payoutSolver;
    private List<Coin> coins = new ArrayList<>();

    public CoinRepository(PayoutSolver payoutSolver) {
        this.payoutSolver = payoutSolver;
    }

    public void addCoins(List<Coin> coins) {
        this.coins.addAll(coins);
    }

    public List<Coin> withdrawAmount(int amount) throws NotEnoughCoinsException {
        List<Coin> coinsToWithdraw = payoutSolver.solvePayout(amount, coins);
        coins.removeAll(coinsToWithdraw);
        return coinsToWithdraw;
    }

    /** Dodane na potrzeby testów. */
    List<Coin> getCoinsInRepository() {
        return new ArrayList<>(coins);
    }
}
