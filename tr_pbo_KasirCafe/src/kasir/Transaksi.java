package kasir;

import java.util.ArrayList;
import java.util.List;

/** Satu transaksi lengkap beserta struknya. */
public class Transaksi {
    private final String id;
    private final String waktu;
    private final List<ItemPesanan> items = new ArrayList<>();
    private String metode = "";
    private int bayar;
    private int kembalian;

    public Transaksi(String id, String waktu) {
        this.id = id;
        this.waktu = waktu;
    }

    public void tambahItem(ItemPesanan item) { items.add(item); }

    public int getTotal() {
        int total = 0;
        for (ItemPesanan i : items) total += i.getSubtotal();
        return total;
    }

    public void setPembayaran(String metode, int bayar, int kembalian) {
        this.metode = metode;
        this.bayar = bayar;
        this.kembalian = kembalian;
    }

    public String getId() { return id; }
    public String getWaktu() { return waktu; }
    public List<ItemPesanan> getItems() { return items; }
    public String getMetode() { return metode; }
    public int getBayar() { return bayar; }
    public int getKembalian() { return kembalian; }

    /** Cetak struk ke layar. */
    public void cetakStruk() {
        System.out.println("\n==========================================");
        System.out.println("               STRUK CAFE");
        System.out.println("==========================================");
        System.out.println("No. Transaksi : " + id);
        System.out.println("Waktu         : " + waktu);
        System.out.println("------------------------------------------");
        System.out.printf("%-16s %3s %9s %10s%n", "Menu", "Qty", "Harga", "Subtotal");
        for (ItemPesanan i : items) {
            System.out.printf("%-16s %3d %9s %10s%n", potong(i.getNamaMenu(), 16), i.getJumlah(),
                    Util.rp(i.getHarga()), Util.rp(i.getSubtotal()));
        }
        System.out.println("------------------------------------------");
        System.out.printf("%-30s %10s%n", "TOTAL", Util.rp(getTotal()));
        System.out.printf("%-30s %10s%n", "Metode Bayar", metode);
        if (metode.equals("CASH")) {
            System.out.printf("%-30s %10s%n", "Dibayar", Util.rp(bayar));
            System.out.printf("%-30s %10s%n", "Kembalian", Util.rp(kembalian));
        }
        System.out.println("==========================================");
        System.out.println("      Terima kasih atas kunjungannya!");
        System.out.println("==========================================\n");
    }

    private String potong(String s, int n) { return s.length() <= n ? s : s.substring(0, n); }

    // Format penyimpanan: id|waktu|metode|bayar|kembalian|nama,harga,qty;nama,harga,qty
    public String toCsv() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            ItemPesanan it = items.get(i);
            if (i > 0) sb.append(";");
            sb.append(it.getNamaMenu()).append(",").append(it.getHarga()).append(",").append(it.getJumlah());
        }
        return id + "|" + waktu + "|" + metode + "|" + bayar + "|" + kembalian + "|" + sb;
    }

    public static Transaksi fromCsv(String line) {
        String[] p = line.split("\\|", -1);
        Transaksi t = new Transaksi(p[0], p[1]);
        t.setPembayaran(p[2], Integer.parseInt(p[3]), Integer.parseInt(p[4]));
        for (String part : p[5].split(";")) {
            if (part.isEmpty()) continue;
            String[] f = part.split(",");
            t.tambahItem(new ItemPesanan(f[0], Integer.parseInt(f[1]), Integer.parseInt(f[2])));
        }
        return t;
    }
}
