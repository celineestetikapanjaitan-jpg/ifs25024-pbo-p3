package usecase;

import adapter.repository.TransactionRepository;
import domain.entity.Transaction;
import domain.entity.SortOption;
import domain.entity.TransactionType;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;

/**
 * Pengujian sederhana (tanpa library eksternal) untuk {@link FinanceUseCase}.
 * Logika bisnis diuji langsung tanpa I/O konsol - bukti bahwa use case terpisah dari layer tampilan.
 *
 * <p>Cara menjalankan dari folder proyek (Linux/macOS: ganti {@code ;} dengan {@code :}):
 * <pre>
 * javac -encoding UTF-8 -d output -sourcepath "src;test" test/usecase/FinanceUseCaseTest.java
 * java -cp output usecase.FinanceUseCaseTest
 * </pre>
 */
public class FinanceUseCaseTest {
    /** Satu skenario pengujian yang boleh melempar exception. */
    @FunctionalInterface
    private interface Scenario {
        void run() throws Exception;
    }

    /** Jumlah skenario yang gagal. */
    private int failures = 0;

    public static void main(String[] args) {
        FinanceUseCaseTest test = new FinanceUseCaseTest();
        test.runAll();
        if (test.failures > 0) {
            System.out.println(test.failures + " skenario GAGAL");
            System.exit(1);
        }
        System.out.println("Semua skenario LULUS");
    }

    /** Daftar seluruh skenario pengujian. */
    private void runAll() {
        scenario("addTransaction menyimpan data dengan ID auto-increment", () -> {
            FinanceUseCase useCase = newUseCase();
            Transaction first = useCase.addTransaction("Gaji", 1_000_000, TransactionType.INCOME);
            Transaction second = useCase.addTransaction("Makan", 25_000, TransactionType.EXPENSE);
            assertEquals(1, first.getId());
            assertEquals(2, second.getId());
            assertEquals(2, useCase.getAllTransactions().size());
        });

        scenario("addTransaction menolak keterangan kosong", () ->
                assertThrows(ValidationException.class,
                        () -> newUseCase().addTransaction("   ", 1000, TransactionType.INCOME)));

        scenario("addTransaction menolak jumlah nol atau negatif", () -> {
            FinanceUseCase useCase = newUseCase();
            assertThrows(ValidationException.class, () -> useCase.addTransaction("Kopi", 0, TransactionType.EXPENSE));
            assertThrows(ValidationException.class, () -> useCase.addTransaction("Kopi", -5, TransactionType.EXPENSE));
        });

        scenario("getBalance = total pemasukan - total pengeluaran", () -> {
            FinanceUseCase useCase = newUseCase();
            useCase.addTransaction("Gaji", 1_000_000, TransactionType.INCOME);
            useCase.addTransaction("Sewa", 250_000, TransactionType.EXPENSE);
            assertEquals(750_000L, useCase.getBalance());
        });

        scenario("removeTransaction menghapus data, ID tidak ada melempar EntityNotFoundException", () -> {
            FinanceUseCase useCase = newUseCase();
            Transaction saved = useCase.addTransaction("Gaji", 1000, TransactionType.INCOME);
            useCase.removeTransaction(saved.getId());
            assertEquals(0, useCase.getAllTransactions().size());
            try {
                useCase.removeTransaction(99);
                throw new AssertionError("seharusnya melempar EntityNotFoundException");
            } catch (EntityNotFoundException e) {
                assertEquals(99, e.getId());
            }
        });

        scenario("searchTransactions tidak peka huruf besar/kecil", () -> {
            FinanceUseCase useCase = newUseCase();
            useCase.addTransaction("Gaji Bulanan", 1000, TransactionType.INCOME);
            useCase.addTransaction("Makan", 10, TransactionType.EXPENSE);
            assertEquals(1, useCase.searchTransactions("GAJI").size());
            assertEquals(0, useCase.searchTransactions("zzz").size());
        });

        scenario("sortTransactions mengikuti SortOption", () -> {
            FinanceUseCase useCase = newUseCase();
            useCase.addTransaction("Kecil", 10, TransactionType.EXPENSE);
            useCase.addTransaction("Besar", 500, TransactionType.INCOME);
            assertEquals("Besar", useCase.sortTransactions(SortOption.AMOUNT_DESC).get(0).getDescription());
            assertEquals("Kecil", useCase.sortTransactions(SortOption.AMOUNT_ASC).get(0).getDescription());
            assertEquals(TransactionType.INCOME, useCase.sortTransactions(SortOption.INCOME_FIRST).get(0).getType());
            assertEquals(TransactionType.EXPENSE, useCase.sortTransactions(SortOption.EXPENSE_FIRST).get(0).getType());
        });
    }

    /** Membuat use case baru dengan repository kosong agar setiap skenario saling independen. */
    private FinanceUseCase newUseCase() {
        return new FinanceUseCase(new TransactionRepository());
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
