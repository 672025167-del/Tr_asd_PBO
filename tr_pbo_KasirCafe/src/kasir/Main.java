package kasir;

/** Program utama Sistem Kasir Cafe. */
public class Main {
    public static void main(String[] args) {
        MenuManager menu = new MenuManager();
        TransaksiManager trx = new TransaksiManager(menu);

        while (true) {
            System.out.println("\n########## SISTEM KASIR CAFE ##########");
            System.out.println("1. Transaksi Baru");
            System.out.println("2. Lihat Daftar Menu");
            System.out.println("3. Tambah Menu");
            System.out.println("4. Ubah Menu (nama/kategori/harga/stok)");
            System.out.println("5. Hapus Menu");
            System.out.println("6. Lihat Menu per Kategori");
            System.out.println("7. Riwayat Transaksi");
            System.out.println("8. Laporan Penjualan");
            System.out.println("0. Keluar");
            int pilih = Util.inputInt("Pilih menu: ", 0);
            switch (pilih) {
                case 1: trx.transaksiBaru(); break;
                case 2: menu.tampil(); break;
                case 3: menu.tambah(); break;
                case 4: menu.ubah(); break;
                case 5: menu.hapus(); break;
                case 6: menu.tampilPerKategori(); break;
                case 7: trx.tampilRiwayat(); break;
                case 8: trx.laporan(); break;
                case 0: System.out.println("Terima kasih. Sampai jumpa!"); return;
                default: System.out.println("Pilihan tidak valid.");
            }
        }
    }
}
