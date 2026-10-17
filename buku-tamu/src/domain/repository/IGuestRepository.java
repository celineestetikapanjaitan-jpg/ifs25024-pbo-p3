package domain.repository;

import domain.entity.Guest;

/**
 * Port spesifik Tamu yang memperluas generic {@link IRepository}.
 * Menambahkan method pembuatan data baru dengan penomoran ID otomatis.
 */
public interface IGuestRepository extends IRepository<Guest, Integer> {
    /**
     * Menyimpan data baru; ID dibuat oleh repository.
     *
     * @return data yang tersimpan (lengkap dengan ID)
     */
    Guest save(String name, String purpose);
}
