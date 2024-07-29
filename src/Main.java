import lexical.AnalisadorLexico;
import lexical.Token;
import lexical.TipoToken;

public class Main {
    public static void main(String[] args) {
            if (args.length != 1) {
                System.out.println("Usage: java Comp [source file]");
                return;
            }
    
            try (AnalisadorLexico l = new AnalisadorLexico(args[0])) {
                // O código a seguir é usado apenas para testar o analisador léxico.
                System.out.println("Tokens:");
                Token lex = l.nextToken();
                while (checkType(lex.type)) {
                    System.out.printf("(\"%s\", %s)\n", lex.token, lex.type);
                    lex = l.nextToken();
                }
    
                switch (lex.type) {
                    case INVALID_TOKEN:
                        System.out.printf("%02d: Lexema inválido [%s]\n", l.getLine(), lex.token);
                        break;
                    case UNEXPECTED_EOF:
                        System.out.printf("%02d: Fim de arquivo inesperado\n", l.getLine());
                        break;
                    default:
                        System.out.printf("(\"%s\", %s)\n", lex.token, lex.type);
                        break;
                }
            } catch (Exception e) {
                System.err.println("Internal error: " + e.getMessage());
            }
        }
    
        private static boolean checkType(TipoToken type) {
            return !(type == TipoToken.END_OF_FILE ||
                    type == TipoToken.INVALID_TOKEN ||
                    type == TipoToken.UNEXPECTED_EOF);
        }
}