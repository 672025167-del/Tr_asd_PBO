package kasir;

/** Data satu menu makanan/minuman. */
public class Menu {
    private String id;
    private String nama;
    private String kategori; // Makanan / Minuman / Snack, dll
    private int harga;
    private int stok;

    public Menu(String id, String nama, String kategori, int harga, int stok) {
        this.id = id;
        this.nama = nama;
        this.kategori = kategori;
        this.harga = harga;
        this.stok = stok;
    }

    public String getId() { return id; }
    public String getNama() { return nama; }
    public String getKategori() { return kategori; }
    public int getHarga() { return harga; }
    public int getStok() { return stok; }

    public void setNama(String nama) { this.nama = nama; }
    public void setKategori(String kategori) { this.kategori = kategori; }
    public void setHarga(int harga) { this.harga = harga; }
    public void setStok(int stok) { this.stok = stok; }

    public boolean tersedia() { return stok > 0; }

    public void kurangiStok(int jumlah) { this.stok -= jumlah; }

    public String toCsv() {
        return id + ";" + nama + ";" + kategori + ";" + harga + ";" + stok;
    }

    public static Menu fromCsv(String line) {
        String[] p = line.split(";");
        return new Menu(p[0], p[1], p[2], Integer.parseInt(p[3]), Integer.parseInt(p[4]));
    }
}
