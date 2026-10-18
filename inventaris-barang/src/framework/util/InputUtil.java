package framework.util;

import java.util.Scanner;

/**
 * Utility untuk membaca input teks dari keyboard.
 * Berada di layer framework karena bergantung langsung pada {@code System.in}.
 */
public final class InputUtil {
    /** Nilai yang dikembalikan saat data input habis; sama dengan perintah batal/keluar. */
    private static final String END_OF_INPUT = "x";

    /** Karakter carriage return sisa dari berkas input bergaya Windows (CRLF). */
    private static final String CARRIAGE_RETURN = "\r";

    /** Scanner bersama untuk seluruh aplikasi (satu instance cukup). */
    private static final Scanner SCANNER = new Scanner(System.in);

    /** Kelas utilitas: tidak perlu dibuat instance-nya. */
    private InputUtil() {
    }

    /**
     * Menampilkan prompt dan membaca satu baris input dari user.
     * Jika data input sudah habis, mengembalikan {@value #END_OF_INPUT} agar aplikasi berhenti dengan
     * wajar, bukan melempar exception.
     *
     * @param info label yang ditampilkan sebelum input
     * @return teks yang diketik user, tanpa karakter carriage return
     */
    public static String input(String info) {
        System.out.print(info + " : ");
        if (!SCANNER.hasNextLine()) {
            return END_OF_INPUT;
        }
        // Buang \r agar perbandingan seperti equals("x") tidak gagal karena karakter tak terlihat
        return SCANNER.nextLine().replace(CARRIAGE_RETURN, "");
    }
}
