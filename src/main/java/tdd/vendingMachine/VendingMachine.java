package tdd.vendingMachine;

import tdd.vendingMachine.order.OrderContext;
import tdd.vendingMachine.order.OrderResult;

public interface VendingMachine {
    OrderContext createOrder(int shelfNumber);
    OrderResult completeOrder(OrderContext orderContext);
    OrderResult cancelOrder(OrderContext orderContext);
}
