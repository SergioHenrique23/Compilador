package utils.util;

public class Utils {

    private Utils() {
    }

    public static void abort(int line, String message) {
        System.out.printf("%02d: %s\n", line, message);
        throw new RuntimeException("erro grave");
        //System.exit(1);
    }

    public static void abort(int line) {
        System.out.printf("%02d: Operação Inválida\n", line);
        throw new RuntimeException("erro grave");
        //System.exit(1);

    }
}
