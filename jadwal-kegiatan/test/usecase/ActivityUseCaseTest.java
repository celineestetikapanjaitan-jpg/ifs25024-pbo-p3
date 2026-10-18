package usecase;

import adapter.repository.ActivityRepository;
import domain.entity.Activity;
import domain.entity.SortOption;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;

/**
 * Pengujian sederhana (tanpa library eksternal) untuk {@link ActivityUseCase}.
 * Logika bisnis diuji langsung tanpa I/O konsol - bukti bahwa use case terpisah dari layer tampilan.
 *
 * <p>Cara menjalankan dari folder proyek (Linux/macOS: ganti {@code ;} dengan {@code :}):
 * <pre>
 * javac -encoding UTF-8 -d output -sourcepath "src;test" test/usecase/ActivityUseCaseTest.java
 * java -cp output usecase.ActivityUseCaseTest
 * </pre>
 */
public class ActivityUseCaseTest {
    /** Satu skenario pengujian yang boleh melempar exception. */
    @FunctionalInterface
    private interface Scenario {
        void run() throws Exception;
    }

    /** Jumlah skenario yang gagal. */
    private int failures = 0;

    public static void main(String[] args) {
        ActivityUseCaseTest test = new ActivityUseCaseTest();
        test.runAll();
        if (test.failures > 0) {
            System.out.println(test.failures + " skenario GAGAL");
            System.exit(1);
        }
        System.out.println("Semua skenario LULUS");
    }

    /** Daftar seluruh skenario pengujian. */
    private void runAll() {
        scenario("addActivity menyimpan kegiatan dengan ID auto-increment", () -> {
            ActivityUseCase useCase = newUseCase();
            Activity first = useCase.addActivity("Kuliah OOP", "Senin", "08:00");
            Activity second = useCase.addActivity("Olahraga", "Jumat", "06:00");
            assertEquals(1, first.getId());
            assertEquals(2, second.getId());
            assertEquals("06:00", second.getTime());
        });

        scenario("addActivity menolak judul, hari, atau waktu kosong", () -> {
            ActivityUseCase useCase = newUseCase();
            assertThrows(ValidationException.class, () -> useCase.addActivity(" ", "Senin", "08:00"));
            assertThrows(ValidationException.class, () -> useCase.addActivity("Kuliah", "", "08:00"));
            assertThrows(ValidationException.class, () -> useCase.addActivity("Kuliah", "Senin", " "));
            assertEquals(0, useCase.getAllActivities().size());
        });

        scenario("updateActivity parsial: field null tidak diubah", () -> {
            ActivityUseCase useCase = newUseCase();
            Activity activity = useCase.addActivity("Rapat", "Senin", "09:00");
            useCase.updateActivity(activity.getId(), null, "Selasa", null);
            assertEquals("Rapat", activity.getTitle());
            assertEquals("Selasa", activity.getDay());
            assertEquals("09:00", activity.getTime());
        });

        scenario("updateActivity dapat mengubah judul, hari, dan waktu sekaligus", () -> {
            ActivityUseCase useCase = newUseCase();
            Activity activity = useCase.addActivity("Rapat", "Senin", "09:00");
            useCase.updateActivity(activity.getId(), "Rapat Besar", "Rabu", "10:30");
            assertEquals("Rapat Besar", activity.getTitle());
            assertEquals("Rabu", activity.getDay());
            assertEquals("10:30", activity.getTime());
        });

        scenario("updateActivity menolak ID tidak ada dan isian kosong tanpa mengubah data", () -> {
            ActivityUseCase useCase = newUseCase();
            Activity activity = useCase.addActivity("Rapat", "Senin", "09:00");
            assertThrows(EntityNotFoundException.class, () -> useCase.updateActivity(99, "A", null, null));
            assertThrows(ValidationException.class, () -> useCase.updateActivity(activity.getId(), "Baru", " ", null));
            assertEquals("Rapat", activity.getTitle());
        });

        scenario("removeActivity menghapus data, ID tidak ada melempar EntityNotFoundException", () -> {
            ActivityUseCase useCase = newUseCase();
            Activity activity = useCase.addActivity("Rapat", "Senin", "09:00");
            useCase.removeActivity(activity.getId());
            assertEquals(0, useCase.getAllActivities().size());
            assertThrows(EntityNotFoundException.class, () -> useCase.removeActivity(99));
        });

        scenario("searchActivities tidak peka huruf besar/kecil", () -> {
            ActivityUseCase useCase = newUseCase();
            useCase.addActivity("Rapat Koordinasi", "Senin", "09:00");
            useCase.addActivity("Olahraga", "Jumat", "06:00");
            assertEquals(1, useCase.searchActivities("RAPAT").size());
            assertEquals(0, useCase.searchActivities("zzz").size());
        });

        scenario("sortActivities: hari Senin-Minggu, pada hari yang sama urut waktu", () -> {
            ActivityUseCase useCase = newUseCase();
            useCase.addActivity("Olahraga", "Jumat", "06:00");
            useCase.addActivity("Kuliah", "Senin", "13:00");
            useCase.addActivity("Rapat", "Senin", "09:00");
            assertEquals("Rapat", useCase.sortActivities(SortOption.DAY).get(0).getTitle());
            assertEquals("Kuliah", useCase.sortActivities(SortOption.DAY).get(1).getTitle());
            assertEquals("Olahraga", useCase.sortActivities(SortOption.DAY).get(2).getTitle());
            assertEquals("Olahraga", useCase.sortActivities(SortOption.TIME).get(0).getTitle());
            assertEquals("Kuliah", useCase.sortActivities(SortOption.TITLE_ASC).get(0).getTitle());
            assertEquals("Rapat", useCase.sortActivities(SortOption.TITLE_DESC).get(0).getTitle());
        });
    }

    /** Membuat use case baru dengan repository kosong agar setiap skenario saling independen. */
    private ActivityUseCase newUseCase() {
        return new ActivityUseCase(new ActivityRepository());
    }

    /** Menjalankan satu skenario dan mencatat hasilnya. */
    private void scenario(String name, Scenario scenario) {
        try {
            scenario.run();
            System.out.println("[LULUS] " + name);
        } catch (AssertionError | Exception e) {
            failures++;
            System.out.println("[GAGAL] " + name + " -> " + e);
        }
    }

    private void assertEquals(Object expected, Object actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("diharapkan <" + expected + "> tetapi <" + actual + ">");
        }
    }

    /** Memastikan skenario melempar exception bertipe tertentu. */
    private void assertThrows(Class<? extends Exception> expected, Scenario scenario) {
        try {
            scenario.run();
        } catch (Exception e) {
            if (expected.isInstance(e)) {
                return;
            }
            throw new AssertionError("exception tidak sesuai: " + e);
        }
        throw new AssertionError("seharusnya melempar " + expected.getSimpleName());
    }
}
