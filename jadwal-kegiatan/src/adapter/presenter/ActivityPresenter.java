package adapter.presenter;

import domain.entity.Activity;
import java.util.List;

/**
 * Presenter yang memformat data dari use case menjadi output layar.
 * Memisahkan logika tampilan dari entity, use case, dan view sehingga
 * format output bisa diubah tanpa menyentuh domain.
 */
public class ActivityPresenter {
    /** Format pesan sukses setelah menambah data. */
    private static final String FORMAT_ADD_SUCCESS = "Berhasil menambah kegiatan: %s%n";

    /** Format satu baris data: [id] ... | ... sesuai ketentuan modul. */
    private static final String FORMAT_ENTITY = "[%d] %s | Hari: %s | Pukul: %s";

    /** Format pesan gagal menghapus data berdasarkan ID. */
    private static final String FORMAT_REMOVE_FAILED = "[!] Gagal menghapus kegiatan dengan ID: %d.%n";

    /** Format judul hasil pencarian; %s diisi kata kunci. */
    private static final String FORMAT_SEARCH_HEADER = "Hasil Pencarian: \"%s\"";

    /** Format pesan gagal mengubah data berdasarkan ID. */
    private static final String FORMAT_UPDATE_FAILED = "[!] Gagal mengubah kegiatan dengan ID: %d.%n";

    /** Format pesan kegagalan validasi dari domain. */
    private static final String FORMAT_VALIDATION_ERROR = "[!] %s!%n";

    /** Judul daftar seluruh data. */
    private static final String HEADER_LIST = "Daftar Kegiatan:";

    /** Judul daftar yang sudah diurutkan. */
    private static final String HEADER_SORTED_LIST = "Daftar Kegiatan (Terurut):";

    /** Pesan ketika daftar kosong. */
    private static final String MSG_EMPTY_LIST = "- Data kegiatan belum tersedia!";

    /** Pesan saat pilihan menu utama tidak dikenali. */
    private static final String MSG_INVALID_CHOICE = "[!] Pilihan tidak dimengerti.";

    /** Pesan saat ID yang dimasukkan bukan angka. */
    private static final String MSG_INVALID_ID = "[!] ID tidak valid!";

    /** Pesan saat pilihan pengurutan tidak valid. */
    private static final String MSG_INVALID_SORT_OPTION = "[!] Pilihan tidak valid!";

    /** Pesan ketika hasil pencarian kosong. */
    private static final String MSG_NOT_FOUND = "- Kegiatan tidak ditemukan!";

    /** Pesan sukses setelah menghapus data. */
    private static final String MSG_REMOVE_SUCCESS = "Berhasil menghapus kegiatan.";

    /** Pesan sukses setelah mengubah data. */
    private static final String MSG_UPDATE_SUCCESS = "Berhasil mengubah kegiatan.";

    /** Memformat satu kegiatan menjadi baris teks untuk ditampilkan. */
    private String format(Activity activity) {
        return String.format(FORMAT_ENTITY,
                activity.getId(), activity.getTitle(), activity.getDay(), activity.getTime());
    }

    /**
     * Helper umum untuk menampilkan daftar kegiatan.
     * Menampilkan pesan kosong jika list tidak berisi data.
     */
    private void printList(List<Activity> activities, String header, String emptyMessage) {
        System.out.println(header);

        if (activities.isEmpty()) {
            System.out.println(emptyMessage);
            return;
        }

        for (Activity activity : activities) {
            System.out.println(format(activity));
        }
    }

    /** Menampilkan daftar semua kegiatan. */
    public void showActivities(List<Activity> activities) {
        printList(activities, HEADER_LIST, MSG_EMPTY_LIST);
    }

    /** Menampilkan hasil pencarian berdasarkan kata kunci. */
    public void showSearchResults(List<Activity> activities, String keyword) {
        printList(activities, String.format(FORMAT_SEARCH_HEADER, keyword), MSG_NOT_FOUND);
    }

    /** Menampilkan daftar kegiatan yang sudah diurutkan. */
    public void showSortedActivities(List<Activity> activities) {
        printList(activities, HEADER_SORTED_LIST, MSG_EMPTY_LIST);
    }

    /** Menampilkan pesan sukses setelah menambah kegiatan. */
    public void showAddSuccess(Activity activity) {
        System.out.printf(FORMAT_ADD_SUCCESS, format(activity));
    }

    /** Menampilkan pesan sukses setelah mengubah kegiatan. */
    public void showUpdateSuccess() {
        System.out.println(MSG_UPDATE_SUCCESS);
    }

    /** Menampilkan pesan gagal saat mengubah kegiatan. */
    public void showUpdateFailed(int id) {
        System.out.printf(FORMAT_UPDATE_FAILED, id);
    }

    /** Menampilkan pesan sukses setelah menghapus kegiatan. */
    public void showRemoveSuccess() {
        System.out.println(MSG_REMOVE_SUCCESS);
    }

    /** Menampilkan pesan gagal saat menghapus kegiatan. */
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

    /** Menampilkan pesan kegagalan validasi dari domain (mis. judul kosong). */
    public void showValidationError(String message) {
        System.out.printf(FORMAT_VALIDATION_ERROR, message);
    }
}
