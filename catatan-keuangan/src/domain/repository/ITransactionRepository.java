package domain.repository;

import domain.entity.Transaction;
import domain.entity.TransactionType;

/**
 * Port spesifik Transaksi yang memperluas generic {@link IRepository}.
 * Menambahkan method pembuatan data baru dengan penomoran ID otomatis.
 */
public interface ITransactionRepository extends IRepository<Transaction, Integer> {
    /**
     * Menyimpan data baru; ID dibuat oleh repository.
     *
     * @return data yang tersimpan (lengkap dengan ID)
     */
    Transaction save(String description, long amount, TransactionType type);
}
