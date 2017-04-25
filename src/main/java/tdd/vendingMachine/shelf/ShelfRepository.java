package tdd.vendingMachine.shelf;

import java.util.HashMap;
import java.util.Map;

public class ShelfRepository {

    private Map<Integer, Shelf> shelves = new HashMap<>();

    public void addShelf(int shelfNumber, Shelf shelf) {
        shelves.put(shelfNumber, shelf);
    }

    public Shelf getShelf(int shelfNumber) throws ShelfNotFoundException {
        Shelf shelf = shelves.get(shelfNumber);

        if (shelf == null) {
            throw new ShelfNotFoundException();
        }

        return shelf;
    }

    /** na potrzeby testów */
    Map<Integer, Shelf> getShelves() {
        return new HashMap<>(shelves);
    }

}
