package usecase;

import adapter.repository.ItemRepository;
import domain.entity.Item;
import domain.entity.SortOption;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;

/**
 * Pengujian sederhana (tanpa library eksternal) untuk {@link ItemUseCase}.
 * Logika bisnis diuji langsung tanpa I/O konsol - bukti bahwa use case terpisah dari layer tampilan.
 *
 * <p>Cara menjalankan dari folder proyek (Linux/macOS: ganti {@code ;} dengan {@code :}):
 * <pre>
 * javac -encoding UTF-8 -d output -sourcepath "src;test" test/usecase/ItemUseCaseTest.java
 * java -cp output usecase.ItemUseCaseTest
 * </pre>
 */
public class ItemUseCaseTest {
    /** Satu skenario pengujian yang boleh melempar exception. */
    @FunctionalInterface
    private interface Scenario {
        void run() throws Exception;
    }

    /** Jumlah skenario yang gagal. */
    private int failures = 0;

    public static void main(String[] args) {
        ItemUseCaseTest test = new ItemUseCaseTest();
        test.runAll();
        if (test.failures > 0) {
            System.out.println(test.failures + " skenario GAGAL");
            System.exit(1);
        }
        System.out.println("Semua skenario LULUS");
    }

    /** Daftar seluruh skenario pengujian. */
    private void runAll() {
        scenario("addItem menyimpan barang (nama dan kategori dipangkas) dengan ID auto-increment", () -> {
            ItemUseCase useCase = newUseCase();
            Item first = useCase.addItem("  Mouse ", 10, " Elektronik ");
            Item second = useCase.addItem("Pensil", 50, "Alat Tulis");
            assertEquals(1, first.getId());
            assertEquals("Mouse", first.getName());
            assertEquals("Elektronik", first.getCategory());
            assertEquals(2, second.getId());
        });

        scenario("addItem menolak nama kosong, stok tidak valid, dan kategori kosong", () -> {
            ItemUseCase useCase = newUseCase();
            assertThrows(ValidationException.class, () -> useCase.addItem(" ", 5, "Elektronik"));
            assertThrows(ValidationException.class, () -> useCase.addItem("Mouse", 0, "Elektronik"));
            assertThrows(ValidationException.class, () -> useCase.addItem("Mouse", -1, "Elektronik"));
            assertThrows(ValidationException.class, () -> useCase.addItem("Mouse", 5, ""));
        });

        scenario("updateStock mengubah stok; null berarti tidak diubah", () -> {
            ItemUseCase useCase = newUseCase();
            Item item = useCase.addItem("Mouse", 10, "Elektronik");
            useCase.updateStock(item.getId(), null);
            assertEquals(10, item.getQuantity());
            useCase.updateStock(item.getId(), 25);
            assertEquals(25, item.getQuantity());
        });

        scenario("updateStock menolak stok tidak valid dan ID yang tidak ada", () -> {
            ItemUseCase useCase = newUseCase();
            Item item = useCase.addItem("Mouse", 10, "Elektronik");
            assertThrows(ValidationException.class, () -> useCase.updateStock(item.getId(), 0));
            assertThrows(EntityNotFoundException.class, () -> useCase.updateStock(99, 5));
            assertEquals(10, item.getQuantity());
        });

        scenario("removeItem menghapus data, menolak ID <= 0, dan ID tidak ada melempar EntityNotFoundException", () -> {
            ItemUseCase useCase = newUseCase();
            Item item = useCase.addItem("Mouse", 10, "Elektronik");
            assertThrows(ValidationException.class, () -> useCase.removeItem(0));
            useCase.removeItem(item.getId());
            assertEquals(0, useCase.getAllItems().size());
            assertThrows(EntityNotFoundException.class, () -> useCase.removeItem(99));
        });

        scenario("searchItems tidak peka huruf besar/kecil", () -> {
            ItemUseCase useCase = newUseCase();
            useCase.addItem("Mouse", 10, "Elektronik");
            useCase.addItem("Keyboard", 5, "Elektronik");
            assertEquals(1, useCase.searchItems("MOU").size());
            assertEquals(0, useCase.searchItems("zzz").size());
        });

        scenario("sortItems mengikuti SortOption", () -> {
            ItemUseCase useCase = newUseCase();
            useCase.addItem("Pensil", 50, "Alat Tulis");
            useCase.addItem("Buku", 20, "Alat Tulis");
            useCase.addItem("Meja", 2, "Furnitur");
            assertEquals("Buku", useCase.sortItems(SortOption.NAME_ASC).get(0).getName());
            assertEquals("Pensil", useCase.sortItems(SortOption.NAME_DESC).get(0).getName());
            assertEquals("Meja", useCase.sortItems(SortOption.QUANTITY_ASC).get(0).getName());
            assertEquals("Pensil", useCase.sortItems(SortOption.QUANTITY_DESC).get(0).getName());
        });
    }

    /** Membuat use case baru dengan repository kosong agar setiap skenario saling independen. */
    private ItemUseCase newUseCase() {
        return new ItemUseCase(new ItemRepository());
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
