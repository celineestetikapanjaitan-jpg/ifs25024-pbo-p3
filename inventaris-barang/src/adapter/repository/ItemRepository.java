package adapter.repository;

import domain.entity.Item;
import domain.repository.IItemRepository;

/**
 * Implementasi repository barang menggunakan basis generic {@link InMemoryRepository}.
 */
public class ItemRepository extends InMemoryRepository<Item, Integer> implements IItemRepository {
    private int idCounter = 0;

    public ItemRepository() {
        super(Item::getId);
    }

    @Override
    public Item save(String name, int quantity, String category) {
        Item item = new Item(nextId(), name, quantity, category);
        return save(item);
    }

    private int nextId() {
        return ++idCounter;
    }
}
