package adapter.presenter;

import domain.entity.Guest;
import java.util.List;

/**
 * Presenter yang memformat data dari use case menjadi output layar.
 * Memisahkan logika tampilan dari entity, use case, dan view sehingga
 * format output bisa diubah tanpa menyentuh domain.
 */
public class GuestPresenter {
    /** Format pesan sukses setelah menambah data. */
    private static final String FORMAT_ADD_SUCCESS = "Berhasil mendaftarkan tamu: %s%n";

    /** Format satu baris data: [id] ... | ... sesuai ketentuan modul. */
    private static final String FORMAT_ENTITY = "[%d] %s | Tujuan: %s";

    /** Format pesan gagal menghapus data berdasarkan ID. */
    private static final String FORMAT_REMOVE_FAILED = "[!] Gagal menghapus tamu dengan ID: %d.%n";

    /** Format judul hasil pencarian; %s diisi kata kunci. */
    private static final String FORMAT_SEARCH_HEADER = "Hasil Pencarian: \"%s\"";

    /** Format pesan kegagalan validasi dari domain. */
    private static final String FORMAT_VALIDATION_ERROR = "[!] %s!%n";

    /** Judul daftar seluruh data. */
    private static final String HEADER_LIST = "Daftar Tamu:";

    /** Pesan ketika daftar kosong. */
    private static final String MSG_EMPTY_LIST = "- Data tamu belum tersedia!";

    /** Pesan saat pilihan menu utama tidak dikenali. */
    private static final String MSG_INVALID_CHOICE = "[!] Pilihan tidak dimengerti.";

    /** Pesan saat ID yang dimasukkan bukan angka. */
    private static final String MSG_INVALID_ID = "[!] ID tidak valid!";

    /** Pesan ketika hasil pencarian kosong. */
    private static final String MSG_NOT_FOUND = "- Tamu tidak ditemukan!";

    /** Pesan sukses setelah menghapus data. */
    private static final String MSG_REMOVE_SUCCESS = "Berhasil menghapus tamu.";

    /** Memformat satu tamu menjadi baris teks untuk ditampilkan. */
    private String format(Guest guest) {
        return String.format(FORMAT_ENTITY, guest.getId(), guest.getName(), guest.getPurpose());
    }

    /**
     * Helper umum untuk menampilkan daftar tamu.
     * Menampilkan pesan kosong jika list tidak berisi data.
     */
    private void printList(List<Guest> guests, String header, String emptyMessage) {
        System.out.println(header);

        if (guests.isEmpty()) {
            System.out.println(emptyMessage);
            return;
        }

        for (Guest guest : guests) {
            System.out.println(format(guest));
        }
    }

    /** Menampilkan daftar semua tamu. */
    public void showGuests(List<Guest> guests) {
        printList(guests, HEADER_LIST, MSG_EMPTY_LIST);
    }

    /** Menampilkan hasil pencarian berdasarkan kata kunci. */
    public void showSearchResults(List<Guest> guests, String keyword) {
        printList(guests, String.format(FORMAT_SEARCH_HEADER, keyword), MSG_NOT_FOUND);
    }

    /** Menampilkan pesan sukses setelah mendaftarkan tamu. */
    public void showRegisterSuccess(Guest guest) {
        System.out.printf(FORMAT_ADD_SUCCESS, format(guest));
    }

    /** Menampilkan pesan sukses setelah menghapus tamu. */
    public void showRemoveSuccess() {
        System.out.println(MSG_REMOVE_SUCCESS);
    }

    /** Menampilkan pesan gagal saat menghapus tamu. */
    public void showRemoveFailed(int id) {
        System.out.printf(FORMAT_REMOVE_FAILED, id);
    }

    /** Menampilkan pesan saat pilihan menu tidak dikenali. */
    public void showInvalidChoice() {
        System.out.println(MSG_INVALID_CHOICE);
    }

    /** Menampilkan pesan saat ID yang dimasukkan tidak valid. */
    public void showInvalidId() {
        System.out.println(MSG_INVALID_ID);
    }

    /** Menampilkan pesan kegagalan validasi dari domain (mis. nama kosong). */
    public void showValidationError(String message) {
        System.out.printf(FORMAT_VALIDATION_ERROR, message);
    }
}
