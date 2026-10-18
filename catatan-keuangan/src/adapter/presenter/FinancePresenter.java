package adapter.presenter;

import domain.entity.Transaction;
import domain.entity.TransactionType;
import java.util.List;

/**
 * Presenter yang memformat data dari use case menjadi output layar.
 * Memisahkan logika tampilan dari entity, use case, dan view sehingga
 * format output bisa diubah tanpa menyentuh domain.
 */
public class FinancePresenter {
    /** Format pesan sukses setelah menambah data. */
    private static final String FORMAT_ADD_SUCCESS = "Berhasil menambah transaksi: %s%n";

    /** Format ringkasan saldo di bawah daftar transaksi. */
    private static final String FORMAT_BALANCE_SUMMARY = "Saldo: Rp %d%n";

    /** Format pesan menu lihat saldo. */
    private static final String FORMAT_CURRENT_BALANCE = "Saldo saat ini: Rp %d%n";

    /** Format satu baris data: [id] ... | ... sesuai ketentuan modul. */
    private static final String FORMAT_ENTITY = "[%d] %s | Rp %d | [%s]";

    /** Format pesan gagal menghapus data berdasarkan ID. */
    private static final String FORMAT_REMOVE_FAILED = "[!] Gagal menghapus transaksi dengan ID: %d.%n";

    /** Format judul hasil pencarian; %s diisi kata kunci. */
    private static final String FORMAT_SEARCH_HEADER = "Hasil Pencarian: \"%s\"";

    /** Format pesan kegagalan validasi dari domain. */
    private static final String FORMAT_VALIDATION_ERROR = "[!] %s!%n";

    /** Judul daftar seluruh data. */
    private static final String HEADER_LIST = "Daftar Transaksi:";

    /** Judul daftar yang sudah diurutkan. */
    private static final String HEADER_SORTED_LIST = "Daftar Transaksi (Terurut):";

    /** Label tampilan untuk transaksi pengeluaran. */
    private static final String LABEL_EXPENSE = "Pengeluaran";

    /** Label tampilan untuk transaksi pemasukan. */
    private static final String LABEL_INCOME = "Pemasukan";

    /** Pesan ketika daftar kosong. */
    private static final String MSG_EMPTY_LIST = "- Belum ada transaksi!";

    /** Pesan saat pilihan menu utama tidak dikenali. */
    private static final String MSG_INVALID_CHOICE = "[!] Pilihan tidak dimengerti.";

    /** Pesan saat ID yang dimasukkan bukan angka. */
    private static final String MSG_INVALID_ID = "[!] ID tidak valid!";

    /** Pesan saat pilihan pengurutan tidak valid. */
    private static final String MSG_INVALID_SORT_OPTION = "[!] Pilihan tidak valid!";

    /** Pesan ketika hasil pencarian kosong. */
    private static final String MSG_NOT_FOUND = "- Transaksi tidak ditemukan!";

    /** Pesan sukses setelah menghapus data. */
    private static final String MSG_REMOVE_SUCCESS = "Berhasil menghapus transaksi.";

    /** Label tampilan untuk jenis transaksi. */
    private String label(TransactionType type) {
        return type == TransactionType.INCOME ? LABEL_INCOME : LABEL_EXPENSE;
    }

    /** Memformat satu transaksi menjadi baris teks untuk ditampilkan. */
    private String format(Transaction transaction) {
        return String.format(FORMAT_ENTITY,
                transaction.getId(), transaction.getDescription(),
                transaction.getAmount(), label(transaction.getType()));
    }

    /**
     * Helper umum untuk menampilkan daftar transaksi.
     * Menampilkan pesan kosong jika list tidak berisi data.
     */
    private void printList(List<Transaction> transactions, String header, String emptyMessage) {
        System.out.println(header);

        if (transactions.isEmpty()) {
            System.out.println(emptyMessage);
            return;
        }

        for (Transaction transaction : transactions) {
            System.out.println(format(transaction));
        }
    }

    /** Menampilkan daftar semua transaksi beserta saldo terkini. */
    public void showTransactions(List<Transaction> transactions, long balance) {
        printList(transactions, HEADER_LIST, MSG_EMPTY_LIST);
        System.out.printf(FORMAT_BALANCE_SUMMARY, balance);
    }

    /** Menampilkan hasil pencarian berdasarkan kata kunci. */
    public void showSearchResults(List<Transaction> transactions, String keyword) {
        printList(transactions, String.format(FORMAT_SEARCH_HEADER, keyword), MSG_NOT_FOUND);
    }

    /** Menampilkan daftar transaksi yang sudah diurutkan. */
    public void showSortedTransactions(List<Transaction> transactions) {
        printList(transactions, HEADER_SORTED_LIST, MSG_EMPTY_LIST);
    }

    /** Menampilkan saldo saat ini. */
    public void showBalance(long balance) {
        System.out.printf(FORMAT_CURRENT_BALANCE, balance);
    }

    /** Menampilkan pesan sukses setelah menambah transaksi. */
    public void showAddSuccess(Transaction transaction) {
        System.out.printf(FORMAT_ADD_SUCCESS, format(transaction));
    }

    /** Menampilkan pesan sukses setelah menghapus transaksi. */
    public void showRemoveSuccess() {
        System.out.println(MSG_REMOVE_SUCCESS);
    }

    /** Menampilkan pesan gagal saat menghapus transaksi. */
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

    /** Menampilkan pesan kegagalan validasi dari domain (mis. jumlah tidak valid). */
    public void showValidationError(String message) {
        System.out.printf(FORMAT_VALIDATION_ERROR, message);
    }
}
