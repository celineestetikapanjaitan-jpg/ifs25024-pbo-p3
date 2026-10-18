package usecase;

import adapter.repository.ContactRepository;
import domain.entity.Contact;
import domain.entity.SortOption;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;

/**
 * Pengujian sederhana (tanpa library eksternal) untuk {@link ContactUseCase}.
 * Logika bisnis diuji langsung tanpa I/O konsol - bukti bahwa use case terpisah dari layer tampilan.
 *
 * <p>Cara menjalankan dari folder proyek (Linux/macOS: ganti {@code ;} dengan {@code :}):
 * <pre>
 * javac -encoding UTF-8 -d output -sourcepath "src;test" test/usecase/ContactUseCaseTest.java
 * java -cp output usecase.ContactUseCaseTest
 * </pre>
 */
public class ContactUseCaseTest {
    /** Satu skenario pengujian yang boleh melempar exception. */
    @FunctionalInterface
    private interface Scenario {
        void run() throws Exception;
    }

    /** Jumlah skenario yang gagal. */
    private int failures = 0;

    public static void main(String[] args) {
        ContactUseCaseTest test = new ContactUseCaseTest();
        test.runAll();
        if (test.failures > 0) {
            System.out.println(test.failures + " skenario GAGAL");
            System.exit(1);
        }
        System.out.println("Semua skenario LULUS");
    }

    /** Daftar seluruh skenario pengujian. */
    private void runAll() {
        scenario("addContact menyimpan kontak dengan ID auto-increment", () -> {
            ContactUseCase useCase = newUseCase();
            Contact first = useCase.addContact("Budi Santoso", "081234567890", "budi@email.com");
            Contact second = useCase.addContact("Ani Wijaya", "082345678901", "ani@email.com");
            assertEquals(1, first.getId());
            assertEquals(2, second.getId());
            assertEquals("ani@email.com", second.getEmail());
        });

        scenario("addContact menolak nama, telepon, atau email kosong", () -> {
            ContactUseCase useCase = newUseCase();
            assertThrows(ValidationException.class, () -> useCase.addContact(" ", "0812", "a@b.com"));
            assertThrows(ValidationException.class, () -> useCase.addContact("Budi", "", "a@b.com"));
            assertThrows(ValidationException.class, () -> useCase.addContact("Budi", "0812", " "));
            assertEquals(0, useCase.getAllContacts().size());
        });

        scenario("updateContact parsial: field null tidak diubah", () -> {
            ContactUseCase useCase = newUseCase();
            Contact contact = useCase.addContact("Ani Wijaya", "0823", "ani@email.com");
            useCase.updateContact(contact.getId(), "Ani Updated", null, null);
            assertEquals("Ani Updated", contact.getName());
            assertEquals("0823", contact.getPhone());
            assertEquals("ani@email.com", contact.getEmail());
        });

        scenario("updateContact dapat mengubah nama, telepon, dan email sekaligus", () -> {
            ContactUseCase useCase = newUseCase();
            Contact contact = useCase.addContact("Budi", "0812", "budi@email.com");
            useCase.updateContact(contact.getId(), "Budi Baru", "0899", "baru@email.com");
            assertEquals("Budi Baru", contact.getName());
            assertEquals("0899", contact.getPhone());
            assertEquals("baru@email.com", contact.getEmail());
        });

        scenario("updateContact menolak ID tidak ada dan isian kosong tanpa mengubah data", () -> {
            ContactUseCase useCase = newUseCase();
            Contact contact = useCase.addContact("Ani", "0823", "ani@email.com");
            assertThrows(EntityNotFoundException.class, () -> useCase.updateContact(99, "X", null, null));
            assertThrows(ValidationException.class, () -> useCase.updateContact(contact.getId(), "Baru", " ", null));
            assertEquals("Ani", contact.getName());
        });

        scenario("removeContact menghapus data, ID tidak ada melempar EntityNotFoundException", () -> {
            ContactUseCase useCase = newUseCase();
            Contact contact = useCase.addContact("Ani", "0823", "ani@email.com");
            useCase.removeContact(contact.getId());
            assertEquals(0, useCase.getAllContacts().size());
            assertThrows(EntityNotFoundException.class, () -> useCase.removeContact(99));
        });

        scenario("searchContacts tidak peka huruf besar/kecil", () -> {
            ContactUseCase useCase = newUseCase();
            useCase.addContact("Budi Santoso", "0812", "b@e.com");
            useCase.addContact("Ani Wijaya", "0823", "a@e.com");
            assertEquals(1, useCase.searchContacts("BUDI").size());
            assertEquals(0, useCase.searchContacts("zzz").size());
        });

        scenario("sortContacts mengurutkan nama A-Z dan Z-A", () -> {
            ContactUseCase useCase = newUseCase();
            useCase.addContact("Budi", "0812", "b@e.com");
            useCase.addContact("Citra", "0834", "c@e.com");
            useCase.addContact("Ani", "0823", "a@e.com");
            assertEquals("Ani", useCase.sortContacts(SortOption.NAME_ASC).get(0).getName());
            assertEquals("Citra", useCase.sortContacts(SortOption.NAME_DESC).get(0).getName());
        });
    }

    /** Membuat use case baru dengan repository kosong agar setiap skenario saling independen. */
    private ContactUseCase newUseCase() {
        return new ContactUseCase(new ContactRepository());
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
