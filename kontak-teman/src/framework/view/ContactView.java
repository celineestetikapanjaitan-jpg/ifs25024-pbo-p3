package framework.view;

import adapter.presenter.ContactPresenter;
import domain.entity.SortOption;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;
import framework.util.InputUtil;
import java.util.Map;
import java.util.Optional;
import usecase.ContactUseCase;

/**
 * Tampilan konsol aplikasi kontak teman.
 * Menerima input user, memanggil use case, dan menangani exception domain secara terstruktur.
 * Layer ini tidak mengandung logika bisnis - hanya menangani interaksi user.
 */
public class ContactView {
    /** Input yang membatalkan operasi pada sebuah form. */
    private static final String CANCEL_INPUT = "x";

    /** Input pada menu utama untuk keluar dari aplikasi. */
    private static final String MENU_EXIT = "x";

    /** Kode pilihan menu utama: tambah. */
    private static final String MENU_ADD = "1";

    /** Kode pilihan menu utama: ubah. */
    private static final String MENU_UPDATE = "2";

    /** Kode pilihan menu utama: cari. */
    private static final String MENU_SEARCH = "3";

    /** Kode pilihan menu utama: urutkan. */
    private static final String MENU_SORT = "4";

    /** Kode pilihan menu utama: hapus. */
    private static final String MENU_REMOVE = "5";

    /** Pilihan menu pengurutan beserta opsi pengurutan domain yang dipetakan. */
    private static final Map<String, SortOption> SORT_OPTIONS = Map.of(
            "1", SortOption.NAME_ASC,
            "2", SortOption.NAME_DESC);

    /** Use case yang menjalankan operasi bisnis. */
    private final ContactUseCase contactUseCase;

    /** Presenter yang memformat hasil ke output layar. */
    private final ContactPresenter presenter;

    public ContactView(ContactUseCase contactUseCase, ContactPresenter presenter) {
        this.contactUseCase = contactUseCase;
        this.presenter = presenter;
    }

    /** Menampilkan menu utama dan loop interaksi user. */
    public void show() {
        boolean running = true;
        while (running) {
            // Tampilkan daftar kontak terkini sebelum menu
            presenter.showContacts(contactUseCase.getAllContacts());
            printMenu();

            String input = InputUtil.input("Pilih");
            System.out.println();
            switch (input) {
                case MENU_ADD -> addContact();
                case MENU_UPDATE -> updateContact();
                case MENU_SEARCH -> searchContact();
                case MENU_SORT -> sortContact();
                case MENU_REMOVE -> removeContact();
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
        System.out.println("2. Ubah");
        System.out.println("3. Cari");
        System.out.println("4. Urutkan");
        System.out.println("5. Hapus");
        System.out.println("x. Keluar");
    }

    /** Form tambah kontak baru. */
    private void addContact() {
        System.out.println("[Menambah Kontak]");
        String name = InputUtil.input("Nama (x Jika Batal)");
        // Isian kosong ditolak diam-diam supaya prompt berikutnya tidak menelan input baris selanjutnya
        if (name.equals(CANCEL_INPUT) || name.isBlank()) {
            return;
        }

        String phone = InputUtil.input("Telepon");
        if (phone.isBlank()) {
            return;
        }

        String email = InputUtil.input("Email");
        if (email.isBlank()) {
            return;
        }

        try {
            presenter.showAddSuccess(contactUseCase.addContact(name, phone, email));
        } catch (ValidationException e) {
            presenter.showValidationError(e.getMessage());
        }
    }

    /** Form ubah nama, telepon, dan/atau email kontak. */
    private void updateContact() {
        System.out.println("[Mengubah Kontak]");
        String strId = InputUtil.input("ID Kontak yang diubah (x Jika Batal)");

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

        String newName = InputUtil.input("Nama Baru (Kosongkan jika tidak ingin mengubah)");
        String newPhone = InputUtil.input("Telepon Baru (Kosongkan jika tidak ingin mengubah)");
        String newEmail = InputUtil.input("Email Baru (Kosongkan jika tidak ingin mengubah)");

        try {
            // null berarti field tersebut tidak diubah
            contactUseCase.updateContact(
                    id, blankToNull(newName), blankToNull(newPhone), blankToNull(newEmail));
            presenter.showUpdateSuccess();
        } catch (EntityNotFoundException e) {
            presenter.showUpdateFailed(e.getId());
        } catch (ValidationException e) {
            presenter.showValidationError(e.getMessage());
        }
    }

    /** Form hapus kontak berdasarkan ID. */
    private void removeContact() {
        System.out.println("[Menghapus Kontak]");
        String strId = InputUtil.input("[ID Kontak] yang dihapus (x Jika Batal)");

        if (strId.equals(CANCEL_INPUT)) {
            return;
        }

        try {
            int id = parseId(strId);
            contactUseCase.removeContact(id);
            presenter.showRemoveSuccess();
        } catch (ValidationException e) {
            presenter.showInvalidId();
        } catch (EntityNotFoundException e) {
            presenter.showRemoveFailed(e.getId());
        }
    }

    /** Form cari kontak berdasarkan kata kunci nama. */
    private void searchContact() {
        System.out.println("[Mencari Kontak]");
        String keyword = InputUtil.input("Kata Kunci (x Jika Batal)");

        if (!keyword.equals(CANCEL_INPUT)) {
            presenter.showSearchResults(contactUseCase.searchContacts(keyword), keyword);
        }
    }

    /** Form urutkan kontak berdasarkan pilihan user. */
    private void sortContact() {
        System.out.println("[Mengurutkan Kontak]");
        System.out.println("Pilihan Pengurutan:");
        System.out.println("1. Nama (A-Z)");
        System.out.println("2. Nama (Z-A)");
        System.out.println("x. Batal");

        String input = InputUtil.input("Pilih");
        // Akhiri baris prompt agar hasil pengurutan tampil di baris berikutnya
        System.out.println();
        if (input.equals(CANCEL_INPUT)) {
            return;
        }

        // Konversi input angka ke enum domain; pilihan tidak dikenal ditolak
        mapSortOption(input).ifPresentOrElse(
                option -> presenter.showSortedContacts(contactUseCase.sortContacts(option)),
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

    /** Mengubah input kosong menjadi null (artinya: field tidak diubah). */
    private String blankToNull(String value) {
        return value.isBlank() ? null : value;
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
