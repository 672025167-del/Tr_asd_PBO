package kasir;

import java.util.Scanner;

/** Fungsi bantu: format rupiah & input aman. */
public class Util {
    public static final Scanner SC = new Scanner(System.in);

    public static String rp(int n) {
        return "Rp" + String.format("%,d", n).replace(',', '.');
    }

    public static String inputString(String label) {
        System.out.print(label);
        return SC.nextLine().trim();
    }

    public static int inputInt(String label, int min) {
        while (true) {
            System.out.print(label);
            try {
                int n = Integer.parseInt(SC.nextLine().trim());
                if (n < min) {
                    System.out.println("  ! Nilai minimal " + min + ".");
                    continue;
                }
                return n;
            } catch (NumberFormatException e) {
                System.out.println("  ! Masukkan angka yang valid.");
            }
        }
    }
}
