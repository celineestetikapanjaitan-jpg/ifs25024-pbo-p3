# Praktikum 3 - Java Exception & Generic

Lima studi kasus aplikasi konsol berbasis **Clean Architecture** dengan **Java Exception** (checked exception
domain) dan **Java Generic** (`IRepository<T, ID>` dan `InMemoryRepository<T, ID>`). Setiap folder adalah proyek
mandiri dengan struktur layer yang seragam.

| Folder | Studi kasus | Entitas | Fitur |
|--------|-------------|---------|-------|
| `catatan-keuangan/` | Catatan Keuangan | `Transaction`, `TransactionType` | tambah pemasukan/pengeluaran, cari, urutkan, lihat saldo, hapus |
| `buku-tamu/` | Buku Tamu | `Guest` | daftarkan tamu, cari nama, hapus |
| `inventaris-barang/` | Inventaris Barang | `Item` | tambah, ubah stok (parsial), cari, urutkan, hapus |
| `jadwal-kegiatan/` | Jadwal Kegiatan | `Activity` | tambah, ubah (parsial), cari, urutkan, hapus |
| `kontak-teman/` | Kontak Teman | `Contact` | tambah, ubah (parsial), cari, urutkan, hapus |

## Struktur setiap proyek

```
<studi-kasus>/
├── src/
│   ├── App.java                      Composition Root (merakit seluruh dependency)
│   ├── domain/                       aturan inti, tidak bergantung pada layer lain
│   │   ├── entity/                   entitas (field id final) dan enum SortOption / tipe
│   │   ├── exception/                DomainException, ValidationException, EntityNotFoundException
│   │   └── repository/               IRepository<T, ID> (port generik) dan port spesifik entitas
│   ├── usecase/                      logika bisnis, bebas I/O, melempar checked exception domain
│   ├── adapter/
│   │   ├── repository/               InMemoryRepository<T, ID> (abstract generic) dan repository konkret
│   │   └── presenter/                memformat keluaran use case menjadi teks layar
│   └── framework/
│       ├── util/                     InputUtil (pembaca System.in berbasis Scanner)
│       └── view/                     tampilan konsol: menu, try-catch exception, memanggil presenter
├── test/usecase/                     pengujian use case tanpa library eksternal
├── test-cases/                       TC-01..TC-04 (.tc) berisi input untuk uji manual
└── structure.txt                     pohon direktori proyek
```

## Aturan dependensi (Dependency Rule)

```
framework / adapter  ->  usecase  ->  domain
```

Panah selalu mengarah ke dalam. Layer `domain` tidak mengimpor layer lain, dan `usecase` tidak menyentuh
`System.out` atau `System.in`. Entitas tidak memiliki `toString()` untuk tampilan; pemformatan baris
`[<id>] ... | ... | ...` dilakukan oleh presenter.

## Perilaku input yang konsisten

- Mengetik `x` pada prompt yang berlabel *(x Jika Batal)* membatalkan operasi dan kembali ke menu utama.
- Pada menu ubah, mengosongkan sebuah isian berarti data lama tidak diubah (update parsial, `null`).
- ID harus berupa angka bulat; jumlah nominal/stok harus angka bulat lebih dari 0.
- Semua kegagalan ditangani lewat checked exception domain (`try-catch` di view), bukan `null` atau kode error.

## Kompilasi dan menjalankan

Jalankan dari dalam folder studi kasus (contoh `catatan-keuangan/`). Hasil kompilasi (`.class`) disimpan di folder
`output/` yang terpisah dari `src/` dan diabaikan oleh Git (`.gitignore`).

```
javac -encoding UTF-8 -d output -sourcepath src src/App.java
java -cp output App
```

Menjalankan salah satu test case sebagai input:

```
java -cp output App < test-cases/TC-01.tc
```

## Pengujian use case

Pengujian tidak memerlukan library tambahan. Di Windows pemisah `-sourcepath` adalah `;`, di Linux/macOS `:`.

```
javac -encoding UTF-8 -d output -sourcepath "src;test" test/usecase/FinanceUseCaseTest.java
java -cp output usecase.FinanceUseCaseTest
```

Ganti `Finance` dengan `Guest`, `Item`, `Activity`, atau `Contact` sesuai studi kasus.
