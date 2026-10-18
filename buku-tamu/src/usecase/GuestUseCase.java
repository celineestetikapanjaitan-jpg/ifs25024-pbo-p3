package usecase;

import domain.entity.Guest;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;
import domain.repository.IGuestRepository;
import java.util.List;
import java.util.Locale;

/**
 * Use case yang menangani logika bisnis buku tamu.
 * Tidak melakukan I/O - hanya memproses data dan melempar checked exception
 * domain jika operasi tidak valid atau data tidak ditemukan.
 */
public class GuestUseCase {
    /** Pesan validasi: nama tamu tidak boleh kosong. */
    private static final String ERR_NAME_EMPTY = "Nama tamu tidak boleh kosong";

    /** Pesan validasi: tujuan kunjungan tidak boleh kosong. */
    private static final String ERR_PURPOSE_EMPTY = "Tujuan kunjungan tidak boleh kosong";

    /** Port repository yang di-inject dari luar (Dependency Inversion). */
    private final IGuestRepository guestRepository;

    public GuestUseCase(IGuestRepository guestRepository) {
        this.guestRepository = guestRepository;
    }

    /** Mengambil semua tamu yang terdaftar. */
    public List<Guest> getAllGuests() {
        return guestRepository.findAll();
    }

    /** Mendaftarkan tamu baru setelah validasi nama dan tujuan. */
    public Guest registerGuest(String name, String purpose) throws ValidationException {
        requireFilled(name, ERR_NAME_EMPTY);
        requireFilled(purpose, ERR_PURPOSE_EMPTY);
        return guestRepository.save(name, purpose);
    }

    /** Menghapus tamu berdasarkan ID; melempar exception jika tidak ditemukan. */
    public void removeGuest(int id) throws EntityNotFoundException {
        boolean deleted = guestRepository.deleteById(id);
        if (!deleted) {
            throw new EntityNotFoundException(id);
        }
    }

    /** Mencari tamu yang namanya mengandung kata kunci (case-insensitive). */
    public List<Guest> searchGuests(String keyword) {
        String lowerKeyword = keyword.toLowerCase(Locale.ROOT);
        return guestRepository.findBy(guest -> guest.getName().toLowerCase(Locale.ROOT).contains(lowerKeyword));
    }

    /** Memastikan nilai tidak null/kosong; melempar {@link ValidationException} jika tidak. */
    private void requireFilled(String value, String message) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(message);
        }
    }
}
