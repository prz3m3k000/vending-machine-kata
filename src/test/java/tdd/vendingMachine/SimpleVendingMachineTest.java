package tdd.vendingMachine;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import tdd.vendingMachine.coin.Coin;
import tdd.vendingMachine.coin.CoinRepository;
import tdd.vendingMachine.coin.CoinValue;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SimpleVendingMachineTest {

    public static final int NON_EXISTENT_SHELF = 1;
    public static final int EMPTY_SHELF = 2;
    public static final int SHELF_WITH_PRODUCTS = 3;

    public static final int POSSIBLE_CHANGE = 60;
    public static final int IMPOSSIBLE_CHANGE = 120;

    public static final List<Coin> POSSIBLE_CHANGE_COINS = createCoinList(CoinValue.VALUE_20, CoinValue.VALUE_20, CoinValue.VALUE_20);
    public static final List<Coin> INSERTED_COINS = createCoinList(CoinValue.VALUE_10, CoinValue.VALUE_20, CoinValue.VALUE_50);

    public static final int PRODUCT_PRICE = 100;
    public static final String PRODUCT_NAME = "TestProduct";

    private VendingMachine vendingMachine;
    private Display displayMock;

    @Before
    public void before() throws Exception {
        vendingMachine = createMockedVendingMachine();
    }

    @Test
    public void vending_machine_creation_test() throws Exception {
        assertThat(vendingMachine).isNotNull();
    }

    @Test
    public void create_order_for_non_existent_shelf_test() throws Exception {
        assertThatThrownBy(() -> vendingMachine.createOrderForShelf(NON_EXISTENT_SHELF))
            .isInstanceOf(OrderCreationException.class)
            .hasMessage("No order created, shelf %d does not exist.", NON_EXISTENT_SHELF);

        verify(displayMock).printMessage(String.format("Półka %s nie istnieje.", NON_EXISTENT_SHELF));
    }

    @Test
    public void create_order_for_empty_shelf_test() throws Exception {
        assertThatThrownBy(() -> vendingMachine.createOrderForShelf(EMPTY_SHELF))
            .isInstanceOf(OrderCreationException.class)
            .hasMessage("No order created, shelf %s is empty.", EMPTY_SHELF);

        verify(displayMock).printMessage(String.format("Półka %s jest pusta.", EMPTY_SHELF));
    }

    @Test
    public void order_created_successfully_test() throws Exception {
        assertThat(vendingMachine.createOrderForShelf(SHELF_WITH_PRODUCTS)).isNotNull();
        verify(displayMock).printMessage(String.format("%d", PRODUCT_PRICE));
    }

    @Test
    public void created_order_has_order_details_test() throws Exception {
        OrderContext orderContext = vendingMachine.createOrderForShelf(SHELF_WITH_PRODUCTS);

        assertThat(orderContext.getProductShelfNumber()).isEqualTo(SHELF_WITH_PRODUCTS);
        assertThat(orderContext.getProductPrice()).isEqualTo(PRODUCT_PRICE);
    }

    @Test
    public void complete_non_existent_order_test() {
        assertThatThrownBy(() -> vendingMachine.completeOrder(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void complete_order_when_not_enough_coins_inserted_test() {
        OrderContext orderContextMock = createOrderContextWithWithNotEnoughCoins();

        assertThatThrownBy(() -> vendingMachine.completeOrder(orderContextMock))
            .isInstanceOf(OrderProcessingException.class)
            .hasMessage("Products price not covered, missing %s.", PRODUCT_PRICE);

        verify(displayMock).printMessage("Zamówienie odrzucone, produkt nie został w pełni opłacony.");
    }

    @Test
    public void complete_order_when_not_enough_coins_to_make_change_test() {
        OrderContext orderContextMock = createOrderContextThatRequiresToPayoutImpossibleChange();

        assertThatThrownBy(() -> vendingMachine.completeOrder(orderContextMock))
            .isInstanceOf(OrderProcessingException.class)
            .hasMessage("Cannot payout change, not enough coins.");

        verify(displayMock).printMessage("Zamówienie odrzucone, brak monet do wypłacenia reszty.");
    }

    @Test
    public void complete_order_for_shelf_that_was_removed_test() {
        OrderContext orderContextMock = createOrderContextForShelfThatIsRemoved();

        assertThatThrownBy(() -> vendingMachine.completeOrder(orderContextMock))
            .isInstanceOf(OrderProcessingException.class)
            .hasMessage("Ordered product is not available.");

        verify(displayMock).printMessage("Zamówienie odrzucone, produkt jest niedostępny.");
    }

    @Test
    public void complete_order_with_product_and_change_test() throws Exception {
        OrderContext orderContextMock = createOrderContextThatRequiresToPayoutPossibleChange();

        OrderResult orderResult = vendingMachine.completeOrder(orderContextMock);

        assertThat(orderResult).isNotNull();
        assertThat(orderResult.getProduct()).isPresent();
        assertThat(orderResult.getChange()).isPresent();

        verify(displayMock).printMessage("Zamówienie gotowe.");
    }

    @Test
    public void cancel_order_test() {
        OrderContext orderContextMock = createOrderContextToBeCanceled();

        OrderResult orderResult = vendingMachine.cancelOrder(orderContextMock);

        assertThat(orderResult).isNotNull();
        assertThat(orderResult.getProduct()).isNotPresent();
        assertThat(orderResult.getChange()).isPresent();
        assertThat(orderResult.getChange().get()).containsExactlyElementsOf(INSERTED_COINS);

        verify(displayMock).printMessage("Zamówienie anulowane.");
    }

    private VendingMachine createMockedVendingMachine() throws Exception {
        CoinRepository coinRepositoryMock = createCoinRepositoryMock();
        ShelfRepository shelfRepositoryMock = createShelfRepositoryMock();
        displayMock = mock(Display.class);

        return new SimpleVendingMachine(coinRepositoryMock, shelfRepositoryMock, displayMock);
    }

    private CoinRepository createCoinRepositoryMock() throws Exception {
        CoinRepository coinRepositoryMock = mock(CoinRepository.class);
        when(coinRepositoryMock.withdrawAmount(0)).thenReturn(Collections.emptyList());
        when(coinRepositoryMock.withdrawAmount(POSSIBLE_CHANGE)).thenReturn(POSSIBLE_CHANGE_COINS);
        when(coinRepositoryMock.withdrawAmount(IMPOSSIBLE_CHANGE)).thenThrow(new NotEnoughCoinsException());

        return coinRepositoryMock;
    }

    private ShelfRepository createShelfRepositoryMock() throws Exception {
        Shelf emptyShelfMock = createEmptyShelfMock();
        Shelf shelfWithProductsMock = createShelfWithProductsMock();

        ShelfRepository shelfRepository = mock(ShelfRepository.class);
        when(shelfRepository.getShelf(NON_EXISTENT_SHELF)).thenThrow(new ShelfNotFoundException());
        when(shelfRepository.getShelf(EMPTY_SHELF)).thenReturn(emptyShelfMock);
        when(shelfRepository.getShelf(SHELF_WITH_PRODUCTS)).thenReturn(shelfWithProductsMock);

        return shelfRepository;
    }

    private Shelf createEmptyShelfMock() throws Exception {
        Shelf shelf = mock(Shelf.class);
        when(shelf.pickProduct()).thenThrow(new EmptyShelfException());
        when(shelf.hasProducts()).thenReturn(false);
        return shelf;
    }

    private Shelf createShelfWithProductsMock() throws Exception {
        Product product = new Product(PRODUCT_NAME);
        Shelf shelf = mock(Shelf.class);
        when(shelf.pickProduct()).thenReturn(product);
        when(shelf.getProductPrice()).thenReturn(PRODUCT_PRICE);
        when(shelf.hasProducts()).thenReturn(true);
        return shelf;
    }

    private OrderContext createOrderContextWithWithNotEnoughCoins() {
        OrderContext orderContextMock = mock(OrderContext.class);
        when(orderContextMock.isProductPriceCovered()).thenReturn(false);
        when(orderContextMock.getProductPrice()).thenReturn(PRODUCT_PRICE);
        when(orderContextMock.getInsertedCoinsValue()).thenReturn(0);
        return orderContextMock;
    }

    private OrderContext createOrderContextThatRequiresToPayoutImpossibleChange() {
        OrderContext orderContextMock = mock(OrderContext.class);
        when(orderContextMock.getProductShelfNumber()).thenReturn(SHELF_WITH_PRODUCTS);
        when(orderContextMock.getProductPrice()).thenReturn(PRODUCT_PRICE);
        when(orderContextMock.isProductPriceCovered()).thenReturn(true);
        when(orderContextMock.getInsertedCoinsValue()).thenReturn(PRODUCT_PRICE + IMPOSSIBLE_CHANGE);
        return orderContextMock;
    }

    private OrderContext createOrderContextThatRequiresToPayoutPossibleChange() {
        OrderContext orderContextMock = mock(OrderContext.class);
        when(orderContextMock.getProductShelfNumber()).thenReturn(SHELF_WITH_PRODUCTS);
        when(orderContextMock.getProductPrice()).thenReturn(PRODUCT_PRICE);
        when(orderContextMock.isProductPriceCovered()).thenReturn(true);
        when(orderContextMock.getInsertedCoinsValue()).thenReturn(PRODUCT_PRICE + POSSIBLE_CHANGE);
        return orderContextMock;
    }

    private OrderContext createOrderContextForShelfThatIsRemoved() {
        OrderContext orderContextMock = mock(OrderContext.class);
        when(orderContextMock.isProductPriceCovered()).thenReturn(true);
        when(orderContextMock.getProductPrice()).thenReturn(PRODUCT_PRICE);
        when(orderContextMock.getInsertedCoinsValue()).thenReturn(PRODUCT_PRICE);
        when(orderContextMock.getProductShelfNumber()).thenReturn(NON_EXISTENT_SHELF);
        return orderContextMock;
    }

    private OrderContext createOrderContextToBeCanceled() {
        OrderContext orderContextMock = mock(OrderContext.class);
        when(orderContextMock.getInsertedCoins()).thenReturn(INSERTED_COINS);
        return orderContextMock;
    }

    private static List<Coin> createCoinList(CoinValue... coinValues) {
        return Arrays.stream(coinValues).map(Coin::new).collect(Collectors.toList());
    }
}
