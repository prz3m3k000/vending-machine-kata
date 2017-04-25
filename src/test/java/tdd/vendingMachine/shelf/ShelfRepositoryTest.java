package tdd.vendingMachine.shelf;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class ShelfRepositoryTest {

    @Test
    public void shelf_repository_creation_test() {
        assertThat(new ShelfRepository()).isNotNull();
    }

    @Test
    public void adding_shelf_to_repository_test() throws ShelfNotFoundException {
        ShelfRepository shelfRepository = new ShelfRepository();

        shelfRepository.addShelf(5, mock(Shelf.class));

        assertThat(shelfRepository.getShelves()).hasSize(1);
    }

    @Test
    public void getting_shelf_from_repository_test() throws ShelfNotFoundException {
        Shelf shelfMock = mock(Shelf.class);
        ShelfRepository shelfRepository = new ShelfRepository();
        shelfRepository.addShelf(5, shelfMock);

        assertThat(shelfRepository.getShelf(5)).isSameAs(shelfMock);
    }

    @Test
    public void getting_shelf_from_empty_repository_test() {
        ShelfRepository shelfRepository = new ShelfRepository();
        assertThatThrownBy(() -> shelfRepository.getShelf(5)).isInstanceOf(ShelfNotFoundException.class);
    }
}
