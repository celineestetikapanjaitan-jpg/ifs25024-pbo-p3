package usecase;

import domain.entity.Activity;
import domain.entity.SortOption;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;
import domain.repository.IActivityRepository;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Use case yang menangani logika bisnis jadwal kegiatan.
 * Tidak melakukan I/O - hanya memproses data dan melempar checked exception
 * domain jika operasi tidak valid atau data tidak ditemukan.
 */
public class ActivityUseCase {
    /** Pesan validasi: judul kegiatan tidak boleh kosong. */
    private static final String ERR_TITLE_EMPTY = "Judul kegiatan tidak boleh kosong";

    /** Pesan validasi: hari tidak boleh kosong. */
    private static final String ERR_DAY_EMPTY = "Hari tidak boleh kosong";

    /** Pesan validasi: waktu tidak boleh kosong. */
    private static final String ERR_TIME_EMPTY = "Waktu tidak boleh kosong";

    /** Port repository yang di-inject dari luar (Dependency Inversion). */
    private final IActivityRepository activityRepository;

    public ActivityUseCase(IActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    /** Mengambil semua kegiatan yang tersedia. */
    public List<Activity> getAllActivities() {
        return activityRepository.findAll();
    }

    /** Menambahkan kegiatan baru setelah validasi judul, hari, dan waktu. */
    public Activity addActivity(String title, String day, String time) throws ValidationException {
        requireFilled(title, ERR_TITLE_EMPTY);
        requireFilled(day, ERR_DAY_EMPTY);
        requireFilled(time, ERR_TIME_EMPTY);
        return activityRepository.save(title, day, time);
    }

    /** Menghapus kegiatan berdasarkan ID; melempar exception jika tidak ditemukan. */
    public void removeActivity(int id) throws EntityNotFoundException {
        boolean deleted = activityRepository.deleteById(id);
        if (!deleted) {
            throw new EntityNotFoundException(id);
        }
    }

    /**
     * Mengubah judul, hari, dan/atau waktu kegiatan.
     * Parameter {@code null} berarti field tersebut tidak diubah.
     */
    public void updateActivity(int id, String title, String day, String time)
            throws EntityNotFoundException, ValidationException {
        Optional<Activity> found = activityRepository.findById(id);
        if (found.isEmpty()) {
            throw new EntityNotFoundException(id);
        }

        // Validasi semua field yang diubah sebelum ada perubahan yang diterapkan
        if (title != null) {
            requireFilled(title, ERR_TITLE_EMPTY);
        }
        if (day != null) {
            requireFilled(day, ERR_DAY_EMPTY);
        }
        if (time != null) {
            requireFilled(time, ERR_TIME_EMPTY);
        }

        Activity activity = found.get();
        if (title != null) {
            activity.changeTitle(title);
        }
        if (day != null) {
            activity.changeDay(day);
        }
        if (time != null) {
            activity.changeTime(time);
        }

        activityRepository.update(activity);
    }

    /** Mencari kegiatan yang judulnya mengandung kata kunci (case-insensitive). */
    public List<Activity> searchActivities(String keyword) {
        String lowerKeyword = keyword.toLowerCase(Locale.ROOT);
        return activityRepository.findBy(
                activity -> activity.getTitle().toLowerCase(Locale.ROOT).contains(lowerKeyword));
    }

    /** Mengurutkan kegiatan sesuai kriteria {@link SortOption} yang dipilih. */
    public List<Activity> sortActivities(SortOption option) {
        return activityRepository.findAll().stream()
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
