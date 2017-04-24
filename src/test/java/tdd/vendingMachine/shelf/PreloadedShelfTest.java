package tdd.vendingMachine.shelf;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import tdd.vendingMachine.product.Product;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(JUnit4.class)
public class PreloadedShelfTest {

    public static final String PRODUCT_NAME = "TestProduct";
    public static final int PRODUCT_PRICE = 100;

    @Test
    public void preloaded_shelf_creation_test() {
        assertThat(new PreloadedShelf(PRODUCT_PRICE, Collections.emptyList())).isNotNull();
    }

    @Test
    public void get_products_price_test() {
        Shelf shelf = new PreloadedShelf(PRODUCT_PRICE, Collections.emptyList());
        assertThat(shelf.getProductPrice()).isEqualTo(PRODUCT_PRICE);
    }

    @Test
    public void picking_product_from_empty_shelf_raise_exception_test() {
        Shelf shelf = new PreloadedShelf(PRODUCT_PRICE, Collections.emptyList());
        assertThatThrownBy(shelf::pickProduct).isInstanceOf(EmptyShelfException.class);
    }

    @Test
    public void picking_product_from_shelf_returns_product_test() throws EmptyShelfException {
        Product productFromShelf = new Product(PRODUCT_NAME);
        Shelf shelf = new PreloadedShelf(PRODUCT_PRICE, Collections.singletonList(productFromShelf));
        assertThat(shelf.pickProduct()).isSameAs(productFromShelf);
    }

    @Test
    public void picking_product_removes_it_from_shelf_test() throws EmptyShelfException {
        // given
        Product productFromShelf = new Product(PRODUCT_NAME);
        Shelf shelf = new PreloadedShelf(PRODUCT_PRICE, Collections.singletonList(productFromShelf));

        // when
        Product lastProductFromShelf = shelf.pickProduct();

        // then
        assertThat(shelf.hasProducts()).isFalse();
    }



}
