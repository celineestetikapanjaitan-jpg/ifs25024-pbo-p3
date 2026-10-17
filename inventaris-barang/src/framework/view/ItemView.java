package framework.view;

import adapter.presenter.ItemPresenter;
import domain.entity.Item;
import domain.entity.SortOption;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;
import framework.util.InputUtil;
import usecase.ItemUseCase;

/**
 * Tampilan konsol aplikasi inventaris barang.
 * Menerima input user, memanggil use case, dan menangani exception domain secara terstruktur.
 */
public class ItemView {
    private final ItemUseCase itemUseCase;
    private final ItemPresenter presenter;

    public ItemView(ItemUseCase itemUseCase, ItemPresenter presenter) {
        this.itemUseCase = itemUseCase;
        this.presenter = presenter;
    }

    public void show() {
        boolean running = true;
        while (running) {
            presenter.showItems(itemUseCase.getAllItems());
            printMenu();

            String input = InputUtil.input("Pilih");
            System.out.println();
            switch (input) {
                case "1" -> addItem();
                case "2" -> updateStock();
                case "3" -> searchItem();
                case "4" -> sortItem();
                case "5" -> removeItem();
                case "x" -> running = false;
                default -> presenter.showInvalidChoice();
            }

            if (running) {
                System.out.println();
            }
        }
    }

    private void printMenu() {
        System.out.println("Menu:");
        System.out.println("1. Tambah");
        System.out.println("2. Ubah Stok");
        System.out.println("3. Cari");
        System.out.println("4. Urutkan");
        System.out.println("5. Hapus");
        System.out.println("x. Keluar");
    }

    private void addItem() {
        System.out.println("[Menambah Barang]");
        String name = InputUtil.input("Nama (x Jika Batal)");
        if (name.equals("x")) {
            return;
        }

        String strQuantity = InputUtil.input("Jumlah");
        if (strQuantity.equals("x")) {
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
        if (category.equals("x")) {
            return;
        }

        try {
            Item item = itemUseCase.addItem(name, quantity, category);
            presenter.showAddSuccess(item);
        } catch (ValidationException e) {
            presenter.showError(e.getMessage());
        }
    }

    private void updateStock() {
        System.out.println("[Mengubah Stok]");
        String strId = InputUtil.input("ID Barang yang diubah (x Jika Batal)");
        if (strId.equals("x")) {
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
            presenter.showError(e.getMessage());
        }
    }

    private void searchItem() {
        System.out.println("[Mencari Barang]");
        String keyword = InputUtil.input("Kata Kunci (x Jika Batal)");
        if (!keyword.equals("x")) {
            presenter.showSearchResults(itemUseCase.searchItems(keyword), keyword);
        }
    }

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
        if (input.equals("x")) {
            return;
        }

        SortOption option = mapSortOption(input);
        if (option == null) {
            presenter.showInvalidSortOption();
            return;
        }

        presenter.showSortedItems(itemUseCase.sortItems(option));
    }

    private void removeItem() {
        System.out.println("[Menghapus Barang]");
        String strId = InputUtil.input("[ID Barang] yang dihapus (x Jika Batal)");
        if (strId.equals("x")) {
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

    private int parseId(String value) throws ValidationException {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new ValidationException("ID harus berupa angka numerik");
        }
    }

    private SortOption mapSortOption(String input) {
        return switch (input) {
            case "1" -> SortOption.NAME_ASC;
            case "2" -> SortOption.NAME_DESC;
            case "3" -> SortOption.QUANTITY_ASC;
            case "4" -> SortOption.QUANTITY_DESC;
            default -> null;
        };
    }
}
