package domain.entity;

/**
 * Jenis transaksi keuangan.
 * Urutan konstanta dipakai sebagai urutan natural (pemasukan lebih dulu).
 */
public enum TransactionType {
    /** Uang masuk. */
    INCOME,

    /** Uang keluar. */
    EXPENSE
}
