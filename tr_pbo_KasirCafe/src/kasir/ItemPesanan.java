package kasir;

/** Satu baris pesanan dalam transaksi (menu + jumlah). */
public class ItemPesanan {
    private final String namaMenu;
    private final int harga;
    private final int jumlah;

    public ItemPesanan(String namaMenu, int harga, int jumlah) {
        this.namaMenu = namaMenu;
        this.harga = harga;
        this.jumlah = jumlah;
    }

    public String getNamaMenu() { return namaMenu; }
    public int getHarga() { return harga; }
    public int getJumlah() { return jumlah; }

    /** Subtotal otomatis = harga x jumlah. */
    public int getSubtotal() { return harga * jumlah; }
}
