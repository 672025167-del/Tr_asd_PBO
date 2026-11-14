package kasir;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/** Proses transaksi, riwayat, dan laporan penjualan. */
public class TransaksiManager {
    private static final String FILE = "data/transaksi.csv";
    private final MenuManager menuManager;
    private final List<Transaksi> riwayat = new ArrayList<>();

    public TransaksiManager(MenuManager menuManager) {
        this.menuManager = menuManager;
        muat();
    }

    public void transaksiBaru() {
        String waktu = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        Transaksi t = new Transaksi(String.format("TRX%04d", riwayat.size() + 1), waktu);
        Map<String, Integer> keranjang = new LinkedHashMap<>(); // id -> jumlah di keranjang

        while (true) {
            menuManager.tampil();
            System.out.println("Keranjang saat ini:");
            tampilKeranjang(keranjang);
            String id = Util.inputString("\nMasukkan ID menu (kosongkan untuk selesai): ");
            if (id.isEmpty()) break;

            Menu m = menuManager.cari(id);
            if (m == null) { System.out.println("  ! Menu tidak ditemukan."); continue; }
            if (!m.tersedia()) { System.out.println("  ! Stok " + m.getNama() + " habis."); continue; }

            int jml = Util.inputInt("Jumlah pesanan: ", 1);
            int sudah = keranjang.getOrDefault(m.getId(), 0);
            if (sudah + jml > m.getStok()) {
                System.out.println("  ! Stok tidak cukup. Sisa stok: " + m.getStok() + ", di keranjang: " + sudah);
                continue;
            }
            keranjang.put(m.getId(), sudah + jml);
            System.out.println("  + " + m.getNama() + " x" + jml + " ditambahkan.");
        }

        if (keranjang.isEmpty()) { System.out.println("Tidak ada pesanan. Transaksi dibatalkan."); return; }

        for (Map.Entry<String, Integer> e : keranjang.entrySet()) {
            Menu m = menuManager.cari(e.getKey());
            t.tambahItem(new ItemPesanan(m.getNama(), m.getHarga(), e.getValue()));
        }

        int total = t.getTotal();
        System.out.println("\nTOTAL PEMBAYARAN: " + Util.rp(total));
        System.out.println("Metode pembayaran:\n  1. CASH\n  2. QRIS");
        int pilih;
        do { pilih = Util.inputInt("Pilih (1/2): ", 1); } while (pilih > 2);

        if (pilih == 1) {
            int bayar;
            while (true) {
                bayar = Util.inputInt("Uang dibayar: ", 0);
                if (bayar >= total) break;
                System.out.println("  ! Uang kurang " + Util.rp(total - bayar) + ".");
            }
            t.setPembayaran("CASH", bayar, bayar - total);
        } else {
            System.out.println("Silakan scan QRIS... pembayaran " + Util.rp(total) + " diterima.");
            t.setPembayaran("QRIS", total, 0); // QRIS tidak menghitung kembalian
        }

        // Stok berkurang otomatis setelah transaksi berhasil
        for (Map.Entry<String, Integer> e : keranjang.entrySet()) {
            menuManager.cari(e.getKey()).kurangiStok(e.getValue());
        }
        menuManager.simpan();

        riwayat.add(t);
        simpan();
        t.cetakStruk();
    }

    private void tampilKeranjang(Map<String, Integer> k) {
        if (k.isEmpty()) { System.out.println("  (kosong)"); return; }
        int total = 0;
        for (Map.Entry<String, Integer> e : k.entrySet()) {
            Menu m = menuManager.cari(e.getKey());
            int sub = m.getHarga() * e.getValue();
            total += sub;
            System.out.printf("  %-20s x%-3d %12s%n", m.getNama(), e.getValue(), Util.rp(sub));
        }
        System.out.println("  Total sementara: " + Util.rp(total));
    }

    public void tampilRiwayat() {
        System.out.println("\n====================== RIWAYAT TRANSAKSI ======================");
        if (riwayat.isEmpty()) { System.out.println("(belum ada transaksi)"); return; }
        System.out.printf("%-8s %-20s %-6s %6s %12s%n", "ID", "Waktu", "Metode", "Item", "Total");
        for (Transaksi t : riwayat) {
            int qty = 0;
            for (ItemPesanan i : t.getItems()) qty += i.getJumlah();
            System.out.printf("%-8s %-20s %-6s %6d %12s%n", t.getId(), t.getWaktu(), t.getMetode(), qty, Util.rp(t.getTotal()));
        }
        String id = Util.inputString("\nKetik ID transaksi untuk lihat struk (kosongkan untuk kembali): ");
        if (id.isEmpty()) return;
        for (Transaksi t : riwayat) {
            if (t.getId().equalsIgnoreCase(id)) { t.cetakStruk(); return; }
        }
        System.out.println("Transaksi tidak ditemukan.");
    }

    public void laporan() {
        System.out.println("\n====================== LAPORAN PENJUALAN ======================");
        if (riwayat.isEmpty()) { System.out.println("(belum ada transaksi)"); return; }
        int pendapatan = 0, cash = 0, qris = 0;
        Map<String, int[]> perMenu = new TreeMap<>(); // nama -> {qty, omzet}
        for (Transaksi t : riwayat) {
            pendapatan += t.getTotal();
            if (t.getMetode().equals("CASH")) cash += t.getTotal(); else qris += t.getTotal();
            for (ItemPesanan i : t.getItems()) {
                int[] v = perMenu.computeIfAbsent(i.getNamaMenu(), k -> new int[2]);
                v[0] += i.getJumlah();
                v[1] += i.getSubtotal();
            }
        }
        System.out.println("Jumlah transaksi : " + riwayat.size());
        System.out.println("Total pendapatan : " + Util.rp(pendapatan));
        System.out.println("  - via CASH     : " + Util.rp(cash));
        System.out.println("  - via QRIS     : " + Util.rp(qris));
        System.out.println("\nPenjualan per menu:");
        System.out.printf("%-20s %8s %14s%n", "Menu", "Terjual", "Pendapatan");
        for (Map.Entry<String, int[]> e : perMenu.entrySet()) {
            System.out.printf("%-20s %8d %14s%n", e.getKey(), e.getValue()[0], Util.rp(e.getValue()[1]));
        }
        System.out.println("===============================================================");
    }

    private void simpan() {
        try {
            Files.createDirectories(Paths.get("data"));
            try (PrintWriter pw = new PrintWriter(new FileWriter(FILE))) {
                for (Transaksi t : riwayat) pw.println(t.toCsv());
            }
        } catch (IOException e) {
            System.out.println("Gagal menyimpan transaksi: " + e.getMessage());
        }
    }

    private void muat() {
        File f = new File(FILE);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) riwayat.add(Transaksi.fromCsv(line));
            }
        } catch (Exception e) {
            System.out.println("Gagal memuat transaksi: " + e.getMessage());
        }
    }
}
