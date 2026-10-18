package usecase;

import domain.entity.SortOption;
import domain.entity.Transaction;
import domain.entity.TransactionType;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;
import domain.repository.ITransactionRepository;
import java.util.List;
import java.util.Locale;

/**
 * Use case yang menangani logika bisnis catatan keuangan.
 * Tidak melakukan I/O - hanya memproses data dan melempar checked exception
 * domain jika operasi tidak valid atau data tidak ditemukan.
 */
public class FinanceUseCase {
    /** Pesan validasi: keterangan tidak boleh kosong. */
    private static final String ERR_DESCRIPTION_EMPTY = "Keterangan tidak boleh kosong";

    /** Pesan saat jumlah uang tidak berupa angka lebih dari 0. */
    private static final String ERR_INVALID_AMOUNT = "Jumlah tidak valid";

    /** Port repository yang di-inject dari luar (Dependency Inversion). */
    private final ITransactionRepository transactionRepository;

    public FinanceUseCase(ITransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /** Mengambil semua transaksi yang tersedia. */
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    /** Menambahkan transaksi baru setelah validasi keterangan dan jumlah. */
    public Transaction addTransaction(String description, long amount, TransactionType type)
            throws ValidationException {
        requireFilled(description, ERR_DESCRIPTION_EMPTY);
        if (amount <= 0) {
            throw new ValidationException(ERR_INVALID_AMOUNT);
        }
        return transactionRepository.save(description, amount, type);
    }

    /** Menghapus transaksi berdasarkan ID; melempar exception jika tidak ditemukan. */
    public void removeTransaction(int id) throws EntityNotFoundException {
        boolean deleted = transactionRepository.deleteById(id);
        if (!deleted) {
            throw new EntityNotFoundException(id);
        }
    }

    /** Mencari transaksi yang keterangannya mengandung kata kunci (case-insensitive). */
    public List<Transaction> searchTransactions(String keyword) {
        String lowerKeyword = keyword.toLowerCase(Locale.ROOT);
        return transactionRepository.findBy(
                transaction -> transaction.getDescription().toLowerCase(Locale.ROOT).contains(lowerKeyword));
    }

    /** Mengurutkan transaksi sesuai kriteria {@link SortOption} yang dipilih. */
    public List<Transaction> sortTransactions(SortOption option) {
        return transactionRepository.findAll().stream()
                .sorted(option.comparator())
                .toList();
    }

    /** Menghitung saldo: total pemasukan dikurangi total pengeluaran. */
    public long getBalance() {
        return transactionRepository.findAll().stream()
                .mapToLong(Transaction::signedAmount)
                .sum();
    }

    /** Memastikan nilai tidak null/kosong; melempar {@link ValidationException} jika tidak. */
    private void requireFilled(String value, String message) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(message);
        }
    }
}
