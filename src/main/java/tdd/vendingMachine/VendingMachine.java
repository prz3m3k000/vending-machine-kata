package tdd.vendingMachine;

import tdd.vendingMachine.order.OrderContext;
import tdd.vendingMachine.order.OrderCreationException;
import tdd.vendingMachine.order.OrderProcessingException;
import tdd.vendingMachine.order.OrderResult;

public interface VendingMachine {
    OrderContext createOrderForShelf(int shelfNumber) throws OrderCreationException;
    OrderResult completeOrder(OrderContext orderContext) throws OrderProcessingException;
    OrderResult cancelOrder(OrderContext orderContext);
}
