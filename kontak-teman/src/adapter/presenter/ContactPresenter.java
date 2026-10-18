package adapter.presenter;

import domain.entity.Contact;
import java.util.List;

/**
 * Presenter yang memformat data dari use case menjadi output layar.
 * Memisahkan logika tampilan dari entity, use case, dan view sehingga
 * format output bisa diubah tanpa menyentuh domain.
 */
public class ContactPresenter {
    /** Format pesan sukses setelah menambah data. */
    private static final String FORMAT_ADD_SUCCESS = "Berhasil menambah kontak: %s%n";

    /** Format satu baris data: [id] ... | ... sesuai ketentuan modul. */
    private static final String FORMAT_ENTITY = "[%d] %s | Telp: %s | Email: %s";

    /** Format pesan gagal menghapus data berdasarkan ID. */
    private static final String FORMAT_REMOVE_FAILED = "[!] Gagal menghapus kontak dengan ID: %d.%n";

    /** Format judul hasil pencarian; %s diisi kata kunci. */
    private static final String FORMAT_SEARCH_HEADER = "Hasil Pencarian: \"%s\"";

    /** Format pesan gagal mengubah data berdasarkan ID. */
    private static final String FORMAT_UPDATE_FAILED = "[!] Gagal mengubah kontak dengan ID: %d.%n";

    /** Format pesan kegagalan validasi dari domain. */
    private static final String FORMAT_VALIDATION_ERROR = "[!] %s!%n";

    /** Judul daftar seluruh data. */
    private static final String HEADER_LIST = "Daftar Kontak:";

    /** Judul daftar yang sudah diurutkan. */
    private static final String HEADER_SORTED_LIST = "Daftar Kontak (Terurut):";

    /** Pesan ketika daftar kosong. */
    private static final String MSG_EMPTY_LIST = "- Data kontak belum tersedia!";

    /** Pesan saat pilihan menu utama tidak dikenali. */
    private static final String MSG_INVALID_CHOICE = "[!] Pilihan tidak dimengerti.";

    /** Pesan saat ID yang dimasukkan bukan angka. */
    private static final String MSG_INVALID_ID = "[!] ID tidak valid!";

    /** Pesan saat pilihan pengurutan tidak valid. */
    private static final String MSG_INVALID_SORT_OPTION = "[!] Pilihan tidak valid!";

    /** Pesan ketika hasil pencarian kosong. */
    private static final String MSG_NOT_FOUND = "- Kontak tidak ditemukan!";

    /** Pesan sukses setelah menghapus data. */
    private static final String MSG_REMOVE_SUCCESS = "Berhasil menghapus kontak.";

    /** Pesan sukses setelah mengubah data. */
    private static final String MSG_UPDATE_SUCCESS = "Berhasil mengubah kontak.";

    /** Memformat satu kontak menjadi baris teks untuk ditampilkan. */
    private String format(Contact contact) {
        return String.format(FORMAT_ENTITY,
                contact.getId(), contact.getName(), contact.getPhone(), contact.getEmail());
    }

    /**
     * Helper umum untuk menampilkan daftar kontak.
     * Menampilkan pesan kosong jika list tidak berisi data.
     */
    private void printList(List<Contact> contacts, String header, String emptyMessage) {
        System.out.println(header);

        if (contacts.isEmpty()) {
            System.out.println(emptyMessage);
            return;
        }

        for (Contact contact : contacts) {
            System.out.println(format(contact));
        }
    }

    /** Menampilkan daftar semua kontak. */
    public void showContacts(List<Contact> contacts) {
        printList(contacts, HEADER_LIST, MSG_EMPTY_LIST);
    }

    /** Menampilkan hasil pencarian berdasarkan kata kunci. */
    public void showSearchResults(List<Contact> contacts, String keyword) {
        printList(contacts, String.format(FORMAT_SEARCH_HEADER, keyword), MSG_NOT_FOUND);
    }

    /** Menampilkan daftar kontak yang sudah diurutkan. */
    public void showSortedContacts(List<Contact> contacts) {
        printList(contacts, HEADER_SORTED_LIST, MSG_EMPTY_LIST);
    }

    /** Menampilkan pesan sukses setelah menambah kontak. */
    public void showAddSuccess(Contact contact) {
        System.out.printf(FORMAT_ADD_SUCCESS, format(contact));
    }

    /** Menampilkan pesan sukses setelah mengubah kontak. */
    public void showUpdateSuccess() {
        System.out.println(MSG_UPDATE_SUCCESS);
    }

    /** Menampilkan pesan gagal saat mengubah kontak. */
    public void showUpdateFailed(int id) {
        System.out.printf(FORMAT_UPDATE_FAILED, id);
    }

    /** Menampilkan pesan sukses setelah menghapus kontak. */
    public void showRemoveSuccess() {
        System.out.println(MSG_REMOVE_SUCCESS);
    }

    /** Menampilkan pesan gagal saat menghapus kontak. */
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

    /** Menampilkan pesan saat opsi pengurutan tidak valid. */
    public void showInvalidSortOption() {
        System.out.println(MSG_INVALID_SORT_OPTION);
    }

    /** Menampilkan pesan kegagalan validasi dari domain (mis. nama kosong). */
    public void showValidationError(String message) {
        System.out.printf(FORMAT_VALIDATION_ERROR, message);
    }
}
