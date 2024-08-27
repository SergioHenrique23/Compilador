import lexical.AnalisadorLexico;
import sintatic.AnaliseSintatica;

public class Main {
    public static void main(String[] args) {
            if (args.length != 1) {
                System.out.println("Usage: java Comp [source file]");
                return;
            }
    
            try (AnalisadorLexico l = new AnalisadorLexico(args[0])) {
                // O código a seguir é usado apenas para testar o analisador Sintatico
                AnaliseSintatica analisador = new AnaliseSintatica(l);
                analisador.start();
            } catch (Exception e) {
                System.err.println("Internal error: " + e.getMessage());
            }
        }
}