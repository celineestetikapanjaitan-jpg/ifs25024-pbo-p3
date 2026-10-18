package framework.view;

import adapter.presenter.GuestPresenter;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;
import framework.util.InputUtil;
import usecase.GuestUseCase;

/**
 * Tampilan konsol aplikasi buku tamu.
 * Menerima input user, memanggil use case, dan menangani exception domain secara terstruktur.
 * Layer ini tidak mengandung logika bisnis - hanya menangani interaksi user.
 */
public class GuestView {
    /** Input yang membatalkan operasi pada sebuah form. */
    private static final String CANCEL_INPUT = "x";

    /** Input pada menu utama untuk keluar dari aplikasi. */
    private static final String MENU_EXIT = "x";

    /** Kode pilihan menu utama: daftarkan tamu. */
    private static final String MENU_REGISTER = "1";

    /** Kode pilihan menu utama: cari. */
    private static final String MENU_SEARCH = "2";

    /** Kode pilihan menu utama: hapus. */
    private static final String MENU_REMOVE = "3";

    /** Use case yang menjalankan operasi bisnis. */
    private final GuestUseCase guestUseCase;

    /** Presenter yang memformat hasil ke output layar. */
    private final GuestPresenter presenter;

    public GuestView(GuestUseCase guestUseCase, GuestPresenter presenter) {
        this.guestUseCase = guestUseCase;
        this.presenter = presenter;
    }

    /** Menampilkan menu utama dan loop interaksi user. */
    public void show() {
        boolean running = true;
        while (running) {
            // Tampilkan daftar tamu terkini sebelum menu
            presenter.showGuests(guestUseCase.getAllGuests());
            printMenu();

            String input = InputUtil.input("Pilih");
            System.out.println();
            switch (input) {
                case MENU_REGISTER -> registerGuest();
                case MENU_SEARCH -> searchGuest();
                case MENU_REMOVE -> removeGuest();
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
        System.out.println("1. Daftarkan");
        System.out.println("2. Cari");
        System.out.println("3. Hapus");
        System.out.println("x. Keluar");
    }

    /** Form pendaftaran tamu baru. */
    private void registerGuest() {
        System.out.println("[Mendaftarkan Tamu]");
        String name = InputUtil.input("Nama (x Jika Batal)");
        if (name.equals(CANCEL_INPUT)) {
            return;
        }

        String purpose = InputUtil.input("Tujuan Kunjungan (x Jika Batal)");
        if (purpose.equals(CANCEL_INPUT)) {
            return;
        }

        try {
            presenter.showRegisterSuccess(guestUseCase.registerGuest(name, purpose));
        } catch (ValidationException e) {
            presenter.showValidationError(e.getMessage());
        }
    }

    /** Form hapus tamu berdasarkan ID. */
    private void removeGuest() {
        System.out.println("[Menghapus Tamu]");
        String strId = InputUtil.input("[ID Tamu] yang dihapus (x Jika Batal)");

        if (strId.equals(CANCEL_INPUT)) {
            return;
        }

        try {
            int id = parseId(strId);
            guestUseCase.removeGuest(id);
            presenter.showRemoveSuccess();
        } catch (ValidationException e) {
            presenter.showInvalidId();
        } catch (EntityNotFoundException e) {
            presenter.showRemoveFailed(e.getId());
        }
    }

    /** Form cari tamu berdasarkan kata kunci nama. */
    private void searchGuest() {
        System.out.println("[Mencari Tamu]");
        String keyword = InputUtil.input("Nama (x Jika Batal)");

        if (!keyword.equals(CANCEL_INPUT)) {
            presenter.showSearchResults(guestUseCase.searchGuests(keyword), keyword);
        }
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
}
