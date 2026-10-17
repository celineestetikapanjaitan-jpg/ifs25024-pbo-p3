package domain.repository;

import domain.entity.Contact;

/**
 * Port spesifik Kontak yang memperluas generic {@link IRepository}.
 * Menambahkan method pembuatan data baru dengan penomoran ID otomatis.
 */
public interface IContactRepository extends IRepository<Contact, Integer> {
    /**
     * Menyimpan data baru; ID dibuat oleh repository.
     *
     * @return data yang tersimpan (lengkap dengan ID)
     */
    Contact save(String name, String phone, String email);
}
