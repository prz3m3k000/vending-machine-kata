package tdd.vendingMachine;

import tdd.vendingMachine.coin.Coin;
import tdd.vendingMachine.coin.CoinRepository;
import tdd.vendingMachine.coin.payoutsolver.NotEnoughCoinsException;
import tdd.vendingMachine.display.Display;
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
    private Display display;

    public SimpleVendingMachine(CoinRepository coinRepository, ShelfRepository shelfRepository, Display display) {
        this.coinRepository = coinRepository;
        this.shelfRepository = shelfRepository;
        this.display = display;
    }

    @Override
    public OrderContext createOrderForShelf(int shelfNumber) throws OrderCreationException {
        try {
            Shelf shelf = getNotEmptyShelf(shelfNumber);
            printProductPrice(shelf.getProductPrice());
            return new OrderContext(shelfNumber, shelf.getProductPrice(), display);
        }
        catch (ShelfNotFoundException ex) {
            display.printMessage(String.format("Półka %s nie istnieje.", shelfNumber));
            throw new OrderCreationException(String.format("No order created, shelf %d does not exist.", shelfNumber), ex);
        }
        catch (EmptyShelfException ex) {
            display.printMessage(String.format("Półka %s jest pusta.", shelfNumber));
            throw new OrderCreationException(String.format("No order created, shelf %s is empty.", shelfNumber));
        }
    }

    @Override
    public OrderResult completeOrder(OrderContext orderContext) throws OrderProcessingException {
        Objects.requireNonNull(orderContext);

        validateIfProductPriceIsCovered(orderContext);
        List<Coin> change = getChangeForOrder(orderContext);
        Product product = getProductForOrder(orderContext);

        display.printMessage("Zamówienie gotowe.");
        return new OrderResult(product, change);
    }

    @Override
    public OrderResult cancelOrder(OrderContext orderContext) {
        display.printMessage("Zamówienie anulowane.");
        return new OrderResult(null, orderContext.getInsertedCoins());
    }

    private Shelf getNotEmptyShelf(int shelfNumber) throws ShelfNotFoundException, EmptyShelfException {
        Shelf shelf = shelfRepository.getShelf(shelfNumber);

        if (!shelf.hasProducts()) {
            throw new EmptyShelfException();
        }

        return shelf;
    }

    private void validateIfProductPriceIsCovered(OrderContext orderContext) throws OrderProcessingException {
        if (!orderContext.isProductPriceCovered()) {
            display.printMessage("Zamówienie odrzucone, produkt nie został w pełni opłacony.");
            int missingAmount = orderContext.getProductPrice() - orderContext.getInsertedCoinsValue();
            throw new OrderProcessingException(String.format("Products price not covered, missing %s.", missingAmount));
        }
    }

    private List<Coin> getChangeForOrder(OrderContext orderContext) throws OrderProcessingException {
        try {
            int amountToWithdraw = orderContext.getInsertedCoinsValue() - orderContext.getProductPrice();
            return coinRepository.withdrawAmount(amountToWithdraw);
        }
        catch (NotEnoughCoinsException ex) {
            display.printMessage("Zamówienie odrzucone, brak monet do wypłacenia reszty.");
            throw new OrderProcessingException("Cannot payout change, not enough coins.", ex);
        }
    }

    private Product getProductForOrder(OrderContext orderContext) throws OrderProcessingException {
        try {
            Shelf shelf = shelfRepository.getShelf(orderContext.getProductShelfNumber());
            return shelf.pickProduct();
        }
        catch (ShelfNotFoundException | EmptyShelfException ex) {
            display.printMessage("Zamówienie odrzucone, produkt jest niedostępny.");
            throw new OrderProcessingException("Ordered product is not available.", ex);
        }
    }

    private void printProductPrice(int productPrice) {
        display.printMessage(Integer.toString(productPrice));
    }
}
