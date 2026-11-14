# Sistem Kasir Cafe (TR PBO)

Aplikasi kasir cafe berbasis **Java (console)** dengan konsep OOP.

## Fitur
- CRUD menu makanan & minuman
- Kelola kategori, harga, dan stok menu
- Transaksi: pilih menu + jumlah, subtotal & total dihitung otomatis
- Stok berkurang otomatis setelah transaksi berhasil (dengan validasi stok)
- Pembayaran CASH (hitung kembalian) atau QRIS (tanpa kembalian)
- Struk transaksi (daftar pesanan, jumlah, harga, total, metode bayar)
- Riwayat transaksi & laporan penjualan
- Data tersimpan otomatis ke folder `data/` (CSV)

## Struktur
```
src/kasir/
  Main.java              -> menu utama
  Menu.java              -> model menu
  ItemPesanan.java       -> baris pesanan
  Transaksi.java         -> transaksi + struk
  MenuManager.java       -> CRUD menu
  TransaksiManager.java  -> transaksi, riwayat, laporan
  Util.java              -> format rupiah & input
```

## Cara Menjalankan
```bash
mkdir out
javac -d out src/kasir/*.java
java -cp out kasir.Main
```
Butuh JDK 8 atau lebih baru.
