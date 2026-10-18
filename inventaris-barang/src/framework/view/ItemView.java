package framework.view;

import adapter.presenter.ItemPresenter;
import domain.entity.Item;
import domain.entity.SortOption;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;
import framework.util.InputUtil;
import java.util.Map;
import java.util.Optional;
import usecase.ItemUseCase;

/**
 * Tampilan konsol aplikasi inventaris barang.
 * Menerima input user, memanggil use case, dan menangani exception domain secara terstruktur.
 */
public class ItemView {
    /** Input yang membatalkan operasi pada sebuah form. */
    private static final String CANCEL_INPUT = "x";

    /** Input pada menu utama untuk keluar dari aplikasi. */
    private static final String MENU_EXIT = "x";

    /** Kode pilihan menu utama: tambah. */
    private static final String MENU_ADD = "1";

    /** Kode pilihan menu utama: ubah stok. */
    private static final String MENU_UPDATE_STOCK = "2";

    /** Kode pilihan menu utama: cari. */
    private static final String MENU_SEARCH = "3";

    /** Kode pilihan menu utama: urutkan. */
    private static final String MENU_SORT = "4";

    /** Kode pilihan menu utama: hapus. */
    private static final String MENU_REMOVE = "5";

    /** Pilihan menu pengurutan beserta opsi pengurutan domain yang dipetakan. */
    private static final Map<String, SortOption> SORT_OPTIONS = Map.of(
            "1", SortOption.NAME_ASC,
            "2", SortOption.NAME_DESC,
            "3", SortOption.QUANTITY_ASC,
            "4", SortOption.QUANTITY_DESC);

    /** Use case yang menjalankan operasi bisnis. */
    private final ItemUseCase itemUseCase;
    /** Presenter yang memformat hasil ke output layar. */
    private final ItemPresenter presenter;

    public ItemView(ItemUseCase itemUseCase, ItemPresenter presenter) {
        this.itemUseCase = itemUseCase;
        this.presenter = presenter;
    }

    /** Menampilkan menu utama dan loop interaksi user. */
    public void show() {
        boolean running = true;
        while (running) {
            presenter.showItems(itemUseCase.getAllItems());
            printMenu();

            String input = InputUtil.input("Pilih");
            System.out.println();
            switch (input) {
                case MENU_ADD -> addItem();
                case MENU_UPDATE_STOCK -> updateStock();
                case MENU_SEARCH -> searchItem();
                case MENU_SORT -> sortItem();
                case MENU_REMOVE -> removeItem();
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
        System.out.println("1. Tambah");
        System.out.println("2. Ubah Stok");
        System.out.println("3. Cari");
        System.out.println("4. Urutkan");
        System.out.println("5. Hapus");
        System.out.println("x. Keluar");
    }

    /** Form tambah barang baru. */
    private void addItem() {
        System.out.println("[Menambah Barang]");
        String name = InputUtil.input("Nama (x Jika Batal)");
        if (name.equals(CANCEL_INPUT)) {
            return;
        }

        String strQuantity = InputUtil.input("Jumlah");
        if (strQuantity.equals(CANCEL_INPUT)) {
            return;
        }

        int quantity;
        try {
            quantity = parseQuantity(strQuantity);
        } catch (ValidationException e) {
            presenter.showInvalidQuantity();
            return;
        }

        String category = InputUtil.input("Kategori (x Jika Batal)");
        if (category.equals(CANCEL_INPUT)) {
            return;
        }

        try {
            Item item = itemUseCase.addItem(name, quantity, category);
            presenter.showAddSuccess(item);
        } catch (ValidationException e) {
            presenter.showValidationError(e.getMessage());
        }
    }

    /** Form ubah stok barang berdasarkan ID; stok dikosongkan berarti tidak diubah. */
    private void updateStock() {
        System.out.println("[Mengubah Stok]");
        String strId = InputUtil.input("ID Barang yang diubah (x Jika Batal)");
        if (strId.equals(CANCEL_INPUT)) {
            return;
        }

        int id;
        try {
            id = parseId(strId);
        } catch (ValidationException e) {
            presenter.showInvalidId();
            return;
        }

        String strQuantity = InputUtil.input("Jumlah Baru (Kosongkan jika tidak ingin mengubah)");

        Integer quantity = null;
        if (!strQuantity.isBlank()) {
            try {
                quantity = parseQuantity(strQuantity);
            } catch (ValidationException e) {
                presenter.showInvalidQuantity();
                return;
            }
        }

        try {
            itemUseCase.updateStock(id, quantity);
            presenter.showUpdateSuccess();
        } catch (EntityNotFoundException e) {
            presenter.showUpdateFailed(e.getId());
        } catch (ValidationException e) {
            presenter.showValidationError(e.getMessage());
        }
    }

    /** Form cari barang berdasarkan kata kunci nama. */
    private void searchItem() {
        System.out.println("[Mencari Barang]");
        String keyword = InputUtil.input("Kata Kunci (x Jika Batal)");
        if (!keyword.equals(CANCEL_INPUT)) {
            presenter.showSearchResults(itemUseCase.searchItems(keyword), keyword);
        }
    }

    /** Form urutkan barang berdasarkan pilihan user. */
    private void sortItem() {
        System.out.println("[Mengurutkan Barang]");
        System.out.println("Pilihan Pengurutan:");
        System.out.println("1. Nama (A-Z)");
        System.out.println("2. Nama (Z-A)");
        System.out.println("3. Jumlah (Terkecil -> Terbesar)");
        System.out.println("4. Jumlah (Terbesar -> Terkecil)");
        System.out.println("x. Batal");

        String input = InputUtil.input("Pilih");
        System.out.println();
        if (input.equals(CANCEL_INPUT)) {
            return;
        }

        // Konversi input angka ke enum domain; pilihan tidak dikenal ditolak
        mapSortOption(input).ifPresentOrElse(
                option -> presenter.showSortedItems(itemUseCase.sortItems(option)),
                presenter::showInvalidSortOption);
    }

    /** Form hapus barang berdasarkan ID. */
    private void removeItem() {
        System.out.println("[Menghapus Barang]");
        String strId = InputUtil.input("[ID Barang] yang dihapus (x Jika Batal)");
        if (strId.equals(CANCEL_INPUT)) {
            return;
        }

        try {
            int id = parseId(strId);
            itemUseCase.removeItem(id);
            presenter.showRemoveSuccess();
        } catch (ValidationException e) {
            presenter.showInvalidId();
        } catch (EntityNotFoundException e) {
            presenter.showRemoveFailed(e.getId());
        }
    }

    /**
     * Mengonversi input menjadi jumlah stok berupa angka bulat lebih dari 0.
     *
     * @throws ValidationException jika input bukan angka atau tidak lebih dari 0
     */
    private int parseQuantity(String value) throws ValidationException {
        try {
            int quantity = Integer.parseInt(value.trim());
            if (quantity <= 0) {
                throw new ValidationException("Jumlah stok tidak valid");
            }
            return quantity;
        } catch (NumberFormatException e) {
            throw new ValidationException("Jumlah stok tidak valid");
        }
    }

    /**
     * Mengonversi input string menjadi ID.
     *
     * @throws ValidationException jika string bukan format angka
     */
    private int parseId(String value) throws ValidationException {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new ValidationException("ID harus berupa angka numerik");
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
