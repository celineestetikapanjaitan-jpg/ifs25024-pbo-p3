package usecase;

import domain.entity.Item;
import domain.entity.SortOption;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;
import domain.repository.IItemRepository;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Use case yang menangani logika bisnis aplikasi inventaris barang.
 * Bebas dari I/O konsol dan melempar checked exception domain.
 */
public class ItemUseCase {
    /** Pesan validasi: nama barang tidak boleh kosong. */
    private static final String ERR_NAME_EMPTY = "Nama barang tidak boleh kosong";

    /** Pesan saat stok tidak berupa angka lebih dari 0. */
    private static final String ERR_INVALID_QUANTITY = "Jumlah stok tidak valid";

    /** Pesan validasi: kategori barang tidak boleh kosong. */
    private static final String ERR_CATEGORY_EMPTY = "Kategori barang tidak boleh kosong";

    /** Pesan saat ID tidak lebih dari 0. */
    private static final String ERR_INVALID_ID = "ID tidak valid";

    /** Port repository yang di-inject dari luar (Dependency Inversion). */
    private final IItemRepository itemRepository;

    public ItemUseCase(IItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    /** Mengambil semua barang yang tersedia. */
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    /** Menambahkan barang baru setelah validasi nama, stok, dan kategori. */
    public Item addItem(String name, int quantity, String category) throws ValidationException {
        requireFilled(name, ERR_NAME_EMPTY);
        if (quantity <= 0) {
            throw new ValidationException(ERR_INVALID_QUANTITY);
        }
        requireFilled(category, ERR_CATEGORY_EMPTY);
        return itemRepository.save(name.trim(), quantity, category.trim());
    }

    /** Mengubah stok barang secara parsial; stok {@code null} berarti tidak diubah. */
    public void updateStock(int id, Integer quantity) throws EntityNotFoundException, ValidationException {
        if (id <= 0) {
            throw new ValidationException(ERR_INVALID_ID);
        }
        Optional<Item> found = itemRepository.findById(id);
        if (found.isEmpty()) {
            throw new EntityNotFoundException(id);
        }
        if (quantity != null) {
            if (quantity <= 0) {
                throw new ValidationException(ERR_INVALID_QUANTITY);
            }
            Item item = found.get();
            item.changeQuantity(quantity);
            itemRepository.update(item);
        }
    }

    /** Menghapus barang berdasarkan ID; melempar exception jika tidak ditemukan. */
    public void removeItem(int id) throws EntityNotFoundException, ValidationException {
        if (id <= 0) {
            throw new ValidationException(ERR_INVALID_ID);
        }
        boolean deleted = itemRepository.deleteById(id);
        if (!deleted) {
            throw new EntityNotFoundException(id);
        }
    }

    /** Mencari barang yang namanya mengandung kata kunci (case-insensitive). */
    public List<Item> searchItems(String keyword) {
        if (keyword == null) {
            return List.of();
        }
        String lowerKeyword = keyword.toLowerCase(Locale.ROOT);
        return itemRepository.findBy(i -> i.getName().toLowerCase(Locale.ROOT).contains(lowerKeyword));
    }

    /** Mengurutkan barang sesuai kriteria {@link SortOption} yang dipilih. */
    public List<Item> sortItems(SortOption option) {
        if (option == null) {
            return List.of();
        }
        return itemRepository.findAll().stream()
                .sorted(option.comparator())
                .toList();
    }

    /** Memastikan nilai tidak null/kosong; melempar {@link ValidationException} jika tidak. */
    private void requireFilled(String value, String message) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(message);
        }
    }
}
