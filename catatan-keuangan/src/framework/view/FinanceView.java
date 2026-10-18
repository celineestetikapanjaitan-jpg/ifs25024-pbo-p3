package framework.view;

import adapter.presenter.FinancePresenter;
import domain.entity.SortOption;
import domain.entity.TransactionType;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;
import framework.util.InputUtil;
import java.util.Map;
import java.util.Optional;
import usecase.FinanceUseCase;

/**
 * Tampilan konsol aplikasi catatan keuangan.
 * Menerima input user, memanggil use case, dan menangani exception domain secara terstruktur.
 * Layer ini tidak mengandung logika bisnis - hanya menangani interaksi user.
 */
public class FinanceView {
    /** Input yang membatalkan operasi pada sebuah form. */
    private static final String CANCEL_INPUT = "x";

    /** Input pada menu utama untuk keluar dari aplikasi. */
    private static final String MENU_EXIT = "x";

    /** Kode pilihan menu utama: tambah pemasukan. */
    private static final String MENU_ADD_INCOME = "1";

    /** Kode pilihan menu utama: tambah pengeluaran. */
    private static final String MENU_ADD_EXPENSE = "2";

    /** Kode pilihan menu utama: cari. */
    private static final String MENU_SEARCH = "3";

    /** Kode pilihan menu utama: urutkan. */
    private static final String MENU_SORT = "4";

    /** Kode pilihan menu utama: lihat saldo. */
    private static final String MENU_SHOW_BALANCE = "5";

    /** Kode pilihan menu utama: hapus. */
    private static final String MENU_REMOVE = "6";

    /** Pilihan menu pengurutan beserta opsi pengurutan domain yang dipetakan. */
    private static final Map<String, SortOption> SORT_OPTIONS = Map.of(
            "1", SortOption.AMOUNT_ASC,
            "2", SortOption.AMOUNT_DESC,
            "3", SortOption.INCOME_FIRST,
            "4", SortOption.EXPENSE_FIRST);

    /** Use case yang menjalankan operasi bisnis. */
    private final FinanceUseCase financeUseCase;

    /** Presenter yang memformat hasil ke output layar. */
    private final FinancePresenter presenter;

    public FinanceView(FinanceUseCase financeUseCase, FinancePresenter presenter) {
        this.financeUseCase = financeUseCase;
        this.presenter = presenter;
    }

    /** Menampilkan menu utama dan loop interaksi user. */
    public void show() {
        boolean running = true;
        while (running) {
            // Tampilkan daftar transaksi terkini (beserta saldo) sebelum menu
            presenter.showTransactions(financeUseCase.getAllTransactions(), financeUseCase.getBalance());
            printMenu();

            String input = InputUtil.input("Pilih");
            System.out.println();
            switch (input) {
                case MENU_ADD_INCOME -> addTransaction(TransactionType.INCOME);
                case MENU_ADD_EXPENSE -> addTransaction(TransactionType.EXPENSE);
                case MENU_SEARCH -> searchTransaction();
                case MENU_SORT -> sortTransaction();
                case MENU_SHOW_BALANCE -> presenter.showBalance(financeUseCase.getBalance());
                case MENU_REMOVE -> removeTransaction();
                case MENU_EXIT -> running = false;
                default -> presenter.showInvalidChoice();
            }

            if (running) {
                System.out.println();
            }
        }
    }

    /** Mencetak opsi menu ke layar. */
    private void printMenu() {
        System.out.println("Menu:");
        System.out.println("1. Tambah Pemasukan");
        System.out.println("2. Tambah Pengeluaran");
        System.out.println("3. Cari");
        System.out.println("4. Urutkan");
        System.out.println("5. Lihat Saldo");
        System.out.println("6. Hapus");
        System.out.println("x. Keluar");
    }

    /** Form tambah transaksi (pemasukan atau pengeluaran). */
    private void addTransaction(TransactionType type) {
        if (type == TransactionType.INCOME) {
            System.out.println("[Tambah Pemasukan]");
        } else {
            System.out.println("[Tambah Pengeluaran]");
        }

        String description = InputUtil.input("Keterangan (x Jika Batal)");
        if (description.equals(CANCEL_INPUT)) {
            return;
        }

        String strAmount = InputUtil.input("Jumlah");

        try {
            long amount = parseAmount(strAmount);
            presenter.showAddSuccess(financeUseCase.addTransaction(description, amount, type));
        } catch (ValidationException e) {
            presenter.showValidationError(e.getMessage());
        }
    }

    /** Form hapus transaksi berdasarkan ID. */
    private void removeTransaction() {
        System.out.println("[Hapus Transaksi]");
        String strId = InputUtil.input("ID Transaksi (x Jika Batal)");

        if (strId.equals(CANCEL_INPUT)) {
            return;
        }

        try {
            int id = parseId(strId);
            financeUseCase.removeTransaction(id);
            presenter.showRemoveSuccess();
        } catch (ValidationException e) {
            presenter.showInvalidId();
        } catch (EntityNotFoundException e) {
            presenter.showRemoveFailed(e.getId());
        }
    }

    /** Form cari transaksi berdasarkan kata kunci keterangan. */
    private void searchTransaction() {
        System.out.println("[Cari Transaksi]");
        String keyword = InputUtil.input("Kata Kunci (x Jika Batal)");

        if (!keyword.equals(CANCEL_INPUT)) {
            presenter.showSearchResults(financeUseCase.searchTransactions(keyword), keyword);
        }
    }

    /** Form urutkan transaksi berdasarkan pilihan user. */
    private void sortTransaction() {
        System.out.println("[Urutkan Transaksi]");
        System.out.println("1. Jumlah (Terkecil)");
        System.out.println("2. Jumlah (Terbesar)");
        System.out.println("3. Pemasukan Dulu");
        System.out.println("4. Pengeluaran Dulu");
        System.out.println("x. Batal");

        String input = InputUtil.input("Pilih");
        // Akhiri baris prompt agar hasil pengurutan tampil di baris berikutnya
        System.out.println();
        if (input.equals(CANCEL_INPUT)) {
            return;
        }

        // Konversi input angka ke enum domain; pilihan tidak dikenal ditolak
        mapSortOption(input).ifPresentOrElse(
                option -> presenter.showSortedTransactions(financeUseCase.sortTransactions(option)),
                presenter::showInvalidSortOption);
    }

    /**
     * Mengonversi input string menjadi ID.
     *
     * @throws ValidationException jika string bukan format angka
     */
    private int parseId(String value) throws ValidationException {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new ValidationException("ID harus berupa angka numerik");
        }
    }

    /**
     * Mengonversi input string menjadi jumlah uang.
     *
     * @throws ValidationException jika string bukan format angka
     */
    private long parseAmount(String value) throws ValidationException {
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            throw new ValidationException("Jumlah tidak valid");
        }
    }

    /**
     * Memetakan pilihan menu pengurutan ke {@link SortOption} domain.
     *
     * @return opsi pengurutan, atau kosong jika pilihan tidak dikenali
     */
    private Optional<SortOption> mapSortOption(String input) {
        return Optional.ofNullable(SORT_OPTIONS.get(input));
    }
}
