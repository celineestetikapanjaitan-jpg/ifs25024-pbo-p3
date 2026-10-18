package framework.view;

import adapter.presenter.ActivityPresenter;
import domain.entity.SortOption;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;
import framework.util.InputUtil;
import java.util.Map;
import java.util.Optional;
import usecase.ActivityUseCase;

/**
 * Tampilan konsol aplikasi jadwal kegiatan.
 * Menerima input user, memanggil use case, dan menangani exception domain secara terstruktur.
 * Layer ini tidak mengandung logika bisnis - hanya menangani interaksi user.
 */
public class ActivityView {
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
            "1", SortOption.DAY,
            "2", SortOption.TIME,
            "3", SortOption.TITLE_ASC,
            "4", SortOption.TITLE_DESC);

    /** Use case yang menjalankan operasi bisnis. */
    private final ActivityUseCase activityUseCase;

    /** Presenter yang memformat hasil ke output layar. */
    private final ActivityPresenter presenter;

    public ActivityView(ActivityUseCase activityUseCase, ActivityPresenter presenter) {
        this.activityUseCase = activityUseCase;
        this.presenter = presenter;
    }

    /** Menampilkan menu utama dan loop interaksi user. */
    public void show() {
        boolean running = true;
        while (running) {
            // Tampilkan daftar kegiatan terkini sebelum menu
            presenter.showActivities(activityUseCase.getAllActivities());
            printMenu();

            String input = InputUtil.input("Pilih");
            System.out.println();
            switch (input) {
                case MENU_ADD -> addActivity();
                case MENU_UPDATE -> updateActivity();
                case MENU_SEARCH -> searchActivity();
                case MENU_SORT -> sortActivity();
                case MENU_REMOVE -> removeActivity();
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

    /** Form tambah kegiatan baru. */
    private void addActivity() {
        System.out.println("[Menambah Kegiatan]");
        String title = InputUtil.input("Judul (x Jika Batal)");
        if (title.equals(CANCEL_INPUT) || title.isBlank()) {
            return;
        }

        String day = InputUtil.input("Hari (x Jika Batal)");
        if (day.equals(CANCEL_INPUT) || day.isBlank()) {
            return;
        }

        String time = InputUtil.input("Waktu (x Jika Batal)");
        if (time.equals(CANCEL_INPUT) || time.isBlank()) {
            return;
        }

        try {
            presenter.showAddSuccess(activityUseCase.addActivity(title, day, time));
        } catch (ValidationException e) {
            presenter.showValidationError(e.getMessage());
        }
    }

    /** Form ubah judul, hari, dan/atau waktu kegiatan. */
    private void updateActivity() {
        System.out.println("[Mengubah Kegiatan]");
        String strId = InputUtil.input("ID Kegiatan yang diubah (x Jika Batal)");

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

        String newTitle = InputUtil.input("Judul Baru (Kosongkan jika tidak ingin mengubah)");
        String newDay = InputUtil.input("Hari Baru (Kosongkan jika tidak ingin mengubah)");
        String newTime = InputUtil.input("Waktu Baru (Kosongkan jika tidak ingin mengubah)");

        try {
            // null berarti field tersebut tidak diubah
            activityUseCase.updateActivity(
                    id, blankToNull(newTitle), blankToNull(newDay), blankToNull(newTime));
            presenter.showUpdateSuccess();
        } catch (EntityNotFoundException e) {
            presenter.showUpdateFailed(e.getId());
        } catch (ValidationException e) {
            presenter.showValidationError(e.getMessage());
        }
    }

    /** Form hapus kegiatan berdasarkan ID. */
    private void removeActivity() {
        System.out.println("[Menghapus Kegiatan]");
        String strId = InputUtil.input("[ID Kegiatan] yang dihapus (x Jika Batal)");

        if (strId.equals(CANCEL_INPUT)) {
            return;
        }

        try {
            int id = parseId(strId);
            activityUseCase.removeActivity(id);
            presenter.showRemoveSuccess();
        } catch (ValidationException e) {
            presenter.showInvalidId();
        } catch (EntityNotFoundException e) {
            presenter.showRemoveFailed(e.getId());
        }
    }

    /** Form cari kegiatan berdasarkan kata kunci judul. */
    private void searchActivity() {
        System.out.println("[Mencari Kegiatan]");
        String keyword = InputUtil.input("Kata Kunci (x Jika Batal)");

        if (!keyword.equals(CANCEL_INPUT)) {
            presenter.showSearchResults(activityUseCase.searchActivities(keyword), keyword);
        }
    }

    /** Form urutkan kegiatan berdasarkan pilihan user. */
    private void sortActivity() {
        System.out.println("[Mengurutkan Kegiatan]");
        System.out.println("Pilihan Pengurutan:");
        System.out.println("1. Hari (Senin -> Minggu)");
        System.out.println("2. Waktu (Awal -> Akhir)");
        System.out.println("3. Judul (A-Z)");
        System.out.println("4. Judul (Z-A)");
        System.out.println("x. Batal");

        String input = InputUtil.input("Pilih");
        // Akhiri baris prompt agar hasil pengurutan tampil di baris berikutnya
        System.out.println();
        if (input.equals(CANCEL_INPUT)) {
            return;
        }

        // Konversi input angka ke enum domain; pilihan tidak dikenal ditolak
        mapSortOption(input).ifPresentOrElse(
                option -> presenter.showSortedActivities(activityUseCase.sortActivities(option)),
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
