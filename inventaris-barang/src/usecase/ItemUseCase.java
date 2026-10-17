package usecase;

import domain.entity.Item;
import domain.entity.SortOption;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;
import domain.repository.IItemRepository;
import java.util.List;
import java.util.Optional;

/**
 * Use case yang menangani logika bisnis aplikasi inventaris barang.
 * Bebas dari I/O konsol dan melempar checked exception domain.
 */
public class ItemUseCase {
    private final IItemRepository itemRepository;

    public ItemUseCase(IItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public Item addItem(String name, int quantity, String category) throws ValidationException {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Nama barang tidak boleh kosong");
        }
        if (quantity <= 0) {
            throw new ValidationException("Jumlah stok tidak valid");
        }
        if (category == null || category.trim().isEmpty()) {
            throw new ValidationException("Kategori barang tidak boleh kosong");
        }
        return itemRepository.save(name.trim(), quantity, category.trim());
    }

    public void updateStock(int id, Integer quantity) throws EntityNotFoundException, ValidationException {
        if (id <= 0) {
            throw new ValidationException("ID tidak valid");
        }
        Optional<Item> found = itemRepository.findById(id);
        if (found.isEmpty()) {
            throw new EntityNotFoundException(id);
        }
        if (quantity != null) {
            if (quantity <= 0) {
                throw new ValidationException("Jumlah stok tidak valid");
            }
            Item item = found.get();
            item.changeQuantity(quantity);
            itemRepository.update(item);
        }
    }

    public void removeItem(int id) throws EntityNotFoundException, ValidationException {
        if (id <= 0) {
            throw new ValidationException("ID tidak valid");
        }
        boolean deleted = itemRepository.deleteById(id);
        if (!deleted) {
            throw new EntityNotFoundException(id);
        }
    }

    public List<Item> searchItems(String keyword) {
        if (keyword == null) {
            return List.of();
        }
        String lowerKeyword = keyword.toLowerCase();
        return itemRepository.findBy(i -> i.getName().toLowerCase().contains(lowerKeyword));
    }

    public List<Item> sortItems(SortOption option) {
        if (option == null) {
            return List.of();
        }
        return itemRepository.findAll().stream()
                .sorted(option.comparator())
                .toList();
    }
}
