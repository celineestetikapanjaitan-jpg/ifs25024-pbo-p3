package framework.util;

import java.util.Scanner;

/**
 * Utility untuk membaca input string dari keyboard.
 */
public class InputUtil {
    private static final Scanner scanner = new Scanner(System.in);

    public static String input(String info) {
        System.out.print(info + " : ");
        if (!scanner.hasNextLine()) {
            return "x";
        }
        // Buang karakter carriage return (\r) sisa dari file input bergaya Windows (CRLF)
        // agar perbandingan seperti .equals("x") tidak gagal karena karakter tak terlihat.
        return scanner.nextLine().replace("\r", "");
    }
}
