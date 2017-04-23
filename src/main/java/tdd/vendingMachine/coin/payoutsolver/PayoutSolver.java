package tdd.vendingMachine.coin.payoutsolver;

import tdd.vendingMachine.coin.Coin;

import java.util.List;

public interface PayoutSolver {

    List<Coin> solvePayout(int amount, List<Coin> availableCoins) throws NotEnoughCoinsException;

}
