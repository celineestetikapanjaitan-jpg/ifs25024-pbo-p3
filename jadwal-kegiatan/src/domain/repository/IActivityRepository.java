package domain.repository;

import domain.entity.Activity;

/**
 * Port spesifik Kegiatan yang memperluas generic {@link IRepository}.
 * Menambahkan method pembuatan data baru dengan penomoran ID otomatis.
 */
public interface IActivityRepository extends IRepository<Activity, Integer> {
    /**
     * Menyimpan data baru; ID dibuat oleh repository.
     *
     * @return data yang tersimpan (lengkap dengan ID)
     */
    Activity save(String title, String day, String time);
}
