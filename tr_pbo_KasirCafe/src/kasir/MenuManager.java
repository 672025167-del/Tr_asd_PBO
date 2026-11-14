package kasir;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/** CRUD menu + kategori, harga, stok, dan penyimpanan ke file. */
public class MenuManager {
    private static final String FILE = "data/menu.csv";
    private final List<Menu> daftar = new ArrayList<>();

    public MenuManager() {
        muat();
        if (daftar.isEmpty()) isiAwal();
    }

    private void isiAwal() {
        daftar.add(new Menu("M01", "Nasi Goreng", "Makanan", 18000, 20));
        daftar.add(new Menu("M02", "Mie Goreng", "Makanan", 16000, 20));
        daftar.add(new Menu("M03", "Kentang Goreng", "Snack", 12000, 15));
        daftar.add(new Menu("M04", "Kopi Susu", "Minuman", 15000, 30));
        daftar.add(new Menu("M05", "Es Teh", "Minuman", 5000, 40));
        simpan();
    }

    public List<Menu> getDaftar() { return daftar; }

    public Menu cari(String id) {
        for (Menu m : daftar) if (m.getId().equalsIgnoreCase(id)) return m;
        return null;
    }

    private String idBaru() {
        int max = 0;
        for (Menu m : daftar) {
            try { max = Math.max(max, Integer.parseInt(m.getId().substring(1))); }
            catch (Exception ignored) { }
        }
        return String.format("M%02d", max + 1);
    }

    // ---------- CREATE ----------
    public void tambah() {
        System.out.println("\n--- Tambah Menu ---");
        String nama = Util.inputString("Nama menu     : ");
        if (nama.isEmpty()) { System.out.println("Nama tidak boleh kosong."); return; }
        String kat = Util.inputString("Kategori      : ");
        int harga = Util.inputInt("Harga         : ", 1);
        int stok = Util.inputInt("Stok          : ", 0);
        Menu m = new Menu(idBaru(), nama, kat.isEmpty() ? "Lainnya" : kat, harga, stok);
        daftar.add(m);
        simpan();
        System.out.println("Menu berhasil ditambahkan dengan ID " + m.getId());
    }

    // ---------- READ ----------
    public void tampil() {
        System.out.println("\n=========================== DAFTAR MENU ===========================");
        System.out.printf("%-5s %-20s %-12s %12s %6s  %-10s%n", "ID", "Nama", "Kategori", "Harga", "Stok", "Status");
        System.out.println("-------------------------------------------------------------------");
        if (daftar.isEmpty()) System.out.println("(belum ada menu)");
        for (Menu m : daftar) {
            System.out.printf("%-5s %-20s %-12s %12s %6d  %-10s%n", m.getId(), m.getNama(),
                    m.getKategori(), Util.rp(m.getHarga()), m.getStok(),
                    m.tersedia() ? "Tersedia" : "HABIS");
        }
        System.out.println("===================================================================");
    }

    public void tampilPerKategori() {
        String kat = Util.inputString("Kategori yang dicari: ");
        boolean ada = false;
        for (Menu m : daftar) {
            if (m.getKategori().equalsIgnoreCase(kat)) {
                System.out.printf("%-5s %-20s %12s stok %d%n", m.getId(), m.getNama(), Util.rp(m.getHarga()), m.getStok());
                ada = true;
            }
        }
        if (!ada) System.out.println("Tidak ada menu pada kategori tersebut.");
    }

    // ---------- UPDATE ----------
    public void ubah() {
        tampil();
        Menu m = cari(Util.inputString("ID menu yang diubah: "));
        if (m == null) { System.out.println("Menu tidak ditemukan."); return; }
        System.out.println("(Kosongkan input untuk tidak mengubah)");
        String nama = Util.inputString("Nama baru     [" + m.getNama() + "]: ");
        String kat = Util.inputString("Kategori baru [" + m.getKategori() + "]: ");
        String harga = Util.inputString("Harga baru    [" + m.getHarga() + "]: ");
        String stok = Util.inputString("Stok baru     [" + m.getStok() + "]: ");
        try {
            if (!nama.isEmpty()) m.setNama(nama);
            if (!kat.isEmpty()) m.setKategori(kat);
            if (!harga.isEmpty()) {
                int h = Integer.parseInt(harga);
                if (h < 1) throw new NumberFormatException();
                m.setHarga(h);
            }
            if (!stok.isEmpty()) {
                int s = Integer.parseInt(stok);
                if (s < 0) throw new NumberFormatException();
                m.setStok(s);
            }
            simpan();
            System.out.println("Menu berhasil diubah.");
        } catch (NumberFormatException e) {
            System.out.println("Harga/stok tidak valid, sebagian perubahan mungkin belum tersimpan.");
            simpan();
        }
    }

    // ---------- DELETE ----------
    public void hapus() {
        tampil();
        Menu m = cari(Util.inputString("ID menu yang dihapus: "));
        if (m == null) { System.out.println("Menu tidak ditemukan."); return; }
        String y = Util.inputString("Yakin hapus '" + m.getNama() + "'? (y/n): ");
        if (y.equalsIgnoreCase("y")) {
            daftar.remove(m);
            simpan();
            System.out.println("Menu dihapus.");
        } else System.out.println("Dibatalkan.");
    }

    // ---------- FILE ----------
    public void simpan() {
        try {
            Files.createDirectories(Paths.get("data"));
            try (PrintWriter pw = new PrintWriter(new FileWriter(FILE))) {
                for (Menu m : daftar) pw.println(m.toCsv());
            }
        } catch (IOException e) {
            System.out.println("Gagal menyimpan menu: " + e.getMessage());
        }
    }

    private void muat() {
        File f = new File(FILE);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) daftar.add(Menu.fromCsv(line));
            }
        } catch (Exception e) {
            System.out.println("Gagal memuat menu: " + e.getMessage());
        }
    }
}
