package tdd.vendingMachine;

import tdd.vendingMachine.coin.Coin;
import tdd.vendingMachine.coin.CoinRepository;
import tdd.vendingMachine.coin.payoutsolver.NotEnoughCoinsException;
import tdd.vendingMachine.order.OrderContext;
import tdd.vendingMachine.order.OrderCreationException;
import tdd.vendingMachine.order.OrderProcessingException;
import tdd.vendingMachine.order.OrderResult;
import tdd.vendingMachine.product.Product;
import tdd.vendingMachine.shelf.EmptyShelfException;
import tdd.vendingMachine.shelf.Shelf;
import tdd.vendingMachine.shelf.ShelfNotFoundException;
import tdd.vendingMachine.shelf.ShelfRepository;

import java.util.List;
import java.util.Objects;

public class SimpleVendingMachine implements VendingMachine {

    private CoinRepository coinRepository;
    private ShelfRepository shelfRepository;

    public SimpleVendingMachine(CoinRepository coinRepository, ShelfRepository shelfRepository) {
        this.coinRepository = coinRepository;
        this.shelfRepository = shelfRepository;
    }

    @Override
    public OrderContext createOrderForShelf(int shelfNumber) throws OrderCreationException {
        try {
            Shelf shelf = getNotEmptyShelf(shelfNumber);
            return new OrderContext(shelfNumber, shelf.getProductPrice());
        }
        catch (ShelfNotFoundException ex) {
            throw new OrderCreationException(String.format("No order created, shelf %d does not exist.", shelfNumber), ex);
        }
        catch (EmptyShelfException ex) {
            throw new OrderCreationException(String.format("No order created, shelf %s is empty.", shelfNumber));
        }
    }

    @Override
    public OrderResult completeOrder(OrderContext orderContext) throws OrderProcessingException {
        Objects.requireNonNull(orderContext);

        if (!orderContext.isProductPriceCovered()) {
            int missingAmount = orderContext.getProductPrice() - orderContext.getInsertedCoinsValue();
            throw new OrderProcessingException(String.format("Products price not covered, missing %s.", missingAmount));
        }

        List<Coin> change = getChangeForOrder(orderContext);
        Product product = getProductForOrder(orderContext);

        return new OrderResult(product, change);
    }

    @Override
    public OrderResult cancelOrder(OrderContext orderContext) {
        return new OrderResult(null, orderContext.getInsertedCoins());
    }

    private Shelf getNotEmptyShelf(int shelfNumber) throws ShelfNotFoundException, EmptyShelfException {
        Shelf shelf = shelfRepository.getShelf(shelfNumber);

        if (!shelf.hasProducts()) {
            throw new EmptyShelfException();
        }

        return shelf;
    }

    private List<Coin> getChangeForOrder(OrderContext orderContext) throws OrderProcessingException {
        try {
            int amountToWithdraw = orderContext.getInsertedCoinsValue() - orderContext.getProductPrice();
            return coinRepository.withdrawAmount(amountToWithdraw);
        }
        catch (NotEnoughCoinsException ex) {
            throw new OrderProcessingException("Cannot payout change, not enough coins.", ex);
        }
    }

    private Product getProductForOrder(OrderContext orderContext) throws OrderProcessingException {
        try {
            Shelf shelf = shelfRepository.getShelf(orderContext.getProductShelfNumber());
            return shelf.pickProduct();
        }
        catch (ShelfNotFoundException | EmptyShelfException ex) {
            throw new OrderProcessingException("Ordered product is not available.", ex);
        }
    }
}
