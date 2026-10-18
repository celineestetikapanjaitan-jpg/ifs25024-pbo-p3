package adapter.presenter;

import domain.entity.Item;
import java.util.List;

/**
 * Presenter yang memformat data dari use case menjadi output layar untuk aplikasi inventaris barang.
 */
public class ItemPresenter {
    /** Awalan semua pesan kesalahan. */
    private static final String ERROR_PREFIX = "[!] ";

    /** Format pesan sukses setelah menambah data. */
    private static final String FORMAT_ADD_SUCCESS = "Berhasil menambah barang: %s%n";

    /** Format satu baris data: [id] ... | ... sesuai ketentuan modul. */
    private static final String FORMAT_ENTITY = "[%d] %s | Stok: %d | Kategori: %s";

    /** Format pesan gagal menghapus data berdasarkan ID. */
    private static final String FORMAT_REMOVE_FAILED = "[!] Gagal menghapus barang dengan ID: %d.%n";

    /** Format judul hasil pencarian; %s diisi kata kunci. */
    private static final String FORMAT_SEARCH_HEADER = "Hasil Pencarian: \"%s\"";

    /** Format pesan gagal mengubah data berdasarkan ID. */
    private static final String FORMAT_UPDATE_FAILED = "[!] Gagal mengubah stok barang dengan ID: %d.%n";

    /** Judul daftar seluruh data. */
    private static final String HEADER_LIST = "Daftar Barang:";

    /** Judul daftar yang sudah diurutkan. */
    private static final String HEADER_SORTED_LIST = "Daftar Barang (Terurut):";

    /** Pesan ketika daftar kosong. */
    private static final String MSG_EMPTY_LIST = "- Data barang belum tersedia!";

    /** Pesan saat pilihan menu utama tidak dikenali. */
    private static final String MSG_INVALID_CHOICE = "[!] Pilihan tidak dimengerti.";

    /** Pesan saat ID yang dimasukkan bukan angka. */
    private static final String MSG_INVALID_ID = "[!] ID tidak valid!";

    /** Pesan saat jumlah stok tidak valid. */
    private static final String MSG_INVALID_QUANTITY = "[!] Jumlah stok tidak valid!";

    /** Pesan saat pilihan pengurutan tidak valid. */
    private static final String MSG_INVALID_SORT_OPTION = "[!] Pilihan tidak valid!";

    /** Pesan ketika hasil pencarian kosong. */
    private static final String MSG_NOT_FOUND = "- Barang tidak ditemukan!";

    /** Pesan sukses setelah menghapus data. */
    private static final String MSG_REMOVE_SUCCESS = "Berhasil menghapus barang.";

    /** Pesan sukses setelah mengubah data. */
    private static final String MSG_UPDATE_SUCCESS = "Berhasil mengubah stok barang.";

    /** Memformat satu barang menjadi baris teks untuk ditampilkan sesuai format modul P3. */
    private String format(Item item) {
        return String.format(FORMAT_ENTITY,
                item.getId(), item.getName(), item.getQuantity(), item.getCategory());
    }

    /** Mencetak daftar barang dengan judul, atau pesan kosong jika tidak ada data. */
    private void printList(List<Item> items, String header, String emptyMessage) {
        System.out.println(header);

        if (items.isEmpty()) {
            System.out.println(emptyMessage);
            return;
        }

        for (Item item : items) {
            System.out.println(format(item));
        }
    }

    /** Menampilkan daftar semua barang. */
    public void showItems(List<Item> items) {
        printList(items, HEADER_LIST, MSG_EMPTY_LIST);
    }

    /** Menampilkan hasil pencarian berdasarkan kata kunci. */
    public void showSearchResults(List<Item> items, String keyword) {
        printList(items, String.format(FORMAT_SEARCH_HEADER, keyword), MSG_NOT_FOUND);
    }

    /** Menampilkan daftar barang yang sudah diurutkan. */
    public void showSortedItems(List<Item> items) {
        printList(items, HEADER_SORTED_LIST, MSG_EMPTY_LIST);
    }

    /** Menampilkan pesan sukses setelah menambah barang. */
    public void showAddSuccess(Item item) {
        System.out.printf(FORMAT_ADD_SUCCESS, format(item));
    }

    /** Menampilkan pesan sukses setelah mengubah stok barang. */
    public void showUpdateSuccess() {
        System.out.println(MSG_UPDATE_SUCCESS);
    }

    /** Menampilkan pesan gagal saat mengubah stok barang. */
    public void showUpdateFailed(int id) {
        System.out.printf(FORMAT_UPDATE_FAILED, id);
    }

    /** Menampilkan pesan sukses setelah menghapus barang. */
    public void showRemoveSuccess() {
        System.out.println(MSG_REMOVE_SUCCESS);
    }

    /** Menampilkan pesan gagal saat menghapus barang. */
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

    /** Menampilkan pesan saat jumlah stok tidak valid. */
    public void showInvalidQuantity() {
        System.out.println(MSG_INVALID_QUANTITY);
    }

    /** Menampilkan pesan saat opsi pengurutan tidak valid. */
    public void showInvalidSortOption() {
        System.out.println(MSG_INVALID_SORT_OPTION);
    }

    /** Menampilkan pesan kegagalan validasi dari domain. */
    public void showValidationError(String message) {
        System.out.println(ERROR_PREFIX + message);
    }
}
