package tdd.vendingMachine.coin.payoutsolver;

import tdd.vendingMachine.coin.Coin;

import java.util.LinkedList;
import java.util.List;

public class OptimalPayoutSolver implements PayoutSolver {

    @Override
    public List<Coin> solvePayout(int amount, List<Coin> availableCoins) throws NotEnoughCoinsException {
        return getPayoutForAmount(amount, calculateAllPayoutSolutions(amount, availableCoins));
    }

    private List<Coin> getPayoutForAmount(int amount, PayoutSolution[] solutions) throws NotEnoughCoinsException {
        PayoutSolution solution = solutions[amount];

        if (solution == null) {
            throw new NotEnoughCoinsException();
        }

        List<Coin> payout = new LinkedList<>();
        while (solution.coin != null) {
            payout.add(solution.coin);
            solution = solutions[solution.baseSolutionIndex];
        }

        return payout;
    }

    private PayoutSolution[] calculateAllPayoutSolutions(int amount, List<Coin> availableCoins) {
        PayoutSolution[] solutions = new PayoutSolution[amount + 1];
        solutions[0] = new PayoutSolution(0, 0, null);

        for (Coin coin: availableCoins) {
            for (int i = amount - coin.getValue(); i >= 0; i--) {
                PayoutSolution currentSolution = solutions[i];

                if (currentSolution != null) {
                    PayoutSolution nextSolution = solutions[i + coin.getValue()];

                    if (nextSolution == null || currentSolution.rank + 1 < nextSolution.rank) {
                        solutions[i + coin.getValue()] = new PayoutSolution(i, currentSolution.rank + 1, coin);
                    }
                }
            }
        }

        return solutions;
    }

    private static class PayoutSolution {

        private int baseSolutionIndex;
        private int rank;
        private Coin coin;

        PayoutSolution(int baseSolutionIndex, int rank, Coin coin) {
            this.baseSolutionIndex = baseSolutionIndex;
            this.rank = rank;
            this.coin = coin;
        }
    }
}
