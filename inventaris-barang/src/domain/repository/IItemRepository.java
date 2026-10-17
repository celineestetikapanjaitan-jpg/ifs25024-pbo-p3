package domain.repository;

import domain.entity.Item;

/**
 * Port spesifik Item yang memperluas generic {@link IRepository}.
 */
public interface IItemRepository extends IRepository<Item, Integer> {
    /**
     * Menyimpan barang baru dengan nama, jumlah stok, dan kategori.
     *
     * @param name nama barang
     * @param quantity jumlah stok
     * @param category kategori barang
     * @return barang yang tersimpan lengkap dengan ID
     */
    Item save(String name, int quantity, String category);
}
