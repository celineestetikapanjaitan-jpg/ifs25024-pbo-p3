package usecase;

import adapter.repository.GuestRepository;
import domain.entity.Guest;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;

/**
 * Pengujian sederhana (tanpa library eksternal) untuk {@link GuestUseCase}.
 * Logika bisnis diuji langsung tanpa I/O konsol - bukti bahwa use case terpisah dari layer tampilan.
 *
 * <p>Cara menjalankan dari folder proyek (Linux/macOS: ganti {@code ;} dengan {@code :}):
 * <pre>
 * javac -encoding UTF-8 -d output -sourcepath "src;test" test/usecase/GuestUseCaseTest.java
 * java -cp output usecase.GuestUseCaseTest
 * </pre>
 */
public class GuestUseCaseTest {
    /** Satu skenario pengujian yang boleh melempar exception. */
    @FunctionalInterface
    private interface Scenario {
        void run() throws Exception;
    }

    /** Jumlah skenario yang gagal. */
    private int failures = 0;

    public static void main(String[] args) {
        GuestUseCaseTest test = new GuestUseCaseTest();
        test.runAll();
        if (test.failures > 0) {
            System.out.println(test.failures + " skenario GAGAL");
            System.exit(1);
        }
        System.out.println("Semua skenario LULUS");
    }

    /** Daftar seluruh skenario pengujian. */
    private void runAll() {
        scenario("registerGuest menyimpan tamu dengan ID auto-increment", () -> {
            GuestUseCase useCase = newUseCase();
            Guest first = useCase.registerGuest("Budi Santoso", "Rapat Koordinasi");
            Guest second = useCase.registerGuest("Ani", "Konsultasi");
            assertEquals(1, first.getId());
            assertEquals(2, second.getId());
            assertEquals("Konsultasi", second.getPurpose());
        });

        scenario("registerGuest menolak nama atau tujuan kosong", () -> {
            GuestUseCase useCase = newUseCase();
            assertThrows(ValidationException.class, () -> useCase.registerGuest("  ", "Rapat"));
            assertThrows(ValidationException.class, () -> useCase.registerGuest("Budi", ""));
            assertEquals(0, useCase.getAllGuests().size());
        });

        scenario("removeGuest menghapus data, ID tidak ada melempar EntityNotFoundException", () -> {
            GuestUseCase useCase = newUseCase();
            Guest saved = useCase.registerGuest("Budi", "Rapat");
            useCase.removeGuest(saved.getId());
            assertEquals(0, useCase.getAllGuests().size());
            try {
                useCase.removeGuest(99);
                throw new AssertionError("seharusnya melempar EntityNotFoundException");
            } catch (EntityNotFoundException e) {
                assertEquals(99, e.getId());
            }
        });

        scenario("searchGuests mencari nama tanpa peka huruf besar/kecil", () -> {
            GuestUseCase useCase = newUseCase();
            useCase.registerGuest("Budi Santoso", "Rapat");
            useCase.registerGuest("Ani Wijaya", "Konsultasi");
            assertEquals(1, useCase.searchGuests("BUDI").size());
            assertEquals(2, useCase.searchGuests("i").size());
            assertEquals(0, useCase.searchGuests("zzz").size());
        });
    }

    /** Membuat use case baru dengan repository kosong agar setiap skenario saling independen. */
    private GuestUseCase newUseCase() {
        return new GuestUseCase(new GuestRepository());
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
