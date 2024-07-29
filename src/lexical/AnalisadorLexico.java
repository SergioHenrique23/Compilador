package lexical;
import java.io.FileInputStream;
import java.io.PushbackInputStream;


public class AnalisadorLexico  implements AutoCloseable {
    private int line;
    private TabelaDeSimbolos st;
    private PushbackInputStream input;

    public AnalisadorLexico(String filename) {
        try {
            input = new PushbackInputStream(new FileInputStream(filename));
        } catch (Exception e) {
            throw new LexicalException("Unable to open file");
        }

        st = new TabelaDeSimbolos();
        line = 1;
    }

    public void close() {
        try {
            input.close();
        } catch (Exception e) {
            throw new LexicalException("Unable to close file");
        }
    }

    public int getLine() {
        return this.line;
    }

    public Token nextToken() {
        Token lex = new Token("", TipoToken.END_OF_FILE);

        int state = 1;

        while (state != 12 && state != 11  && state != 14 && state != 10) {
            int c = getc();

            switch (state) {
                case 1:
                    if(c == ' ' || c == '\t'|| c == '\r'){
                    state = 1;
                    }
                    else if(c =='\n'){
                        line++;
                        state = 1;
                    }
                    else if(c=='%'){
                        state = 2;
                    }
                    else if(c==':'){
                        lex.token += (char) c;
                        state = 3;
                    }
                    else if(c=='&'){
                        lex.token += (char) c;
                        state = 15;
                    }
                    else if(c=='!'||c=='<'||c=='>'){
                        lex.token += (char) c;
                        state = 4;
                    }
                    else if(c=='='||c=='+'||c=='-'||c=='+'||c=='*'||c=='/'||c=='('||c==')'||c==';'||c==','){
                        lex.token += (char) c;
                        state = 10;
                    }
                    else if(c =='|'){
                        lex.token += (char) c;
                        state = 5;
                    }

                    else if(Character.isDigit(c)){
                        lex.token += (char) c;
                        state = 7;
                    }
                    else if(Character.isLetter(c)||c=='_'){
                        lex.token += (char) c;
                        state = 6;
                    }
                    else if(c=='{'){
                        state = 8;
                    }
                    else if (c == -1) {
                        lex.type = TipoToken.END_OF_FILE;
                        state = 14;
                    } else {
                        lex.token += (char) c;
                        lex.type = TipoToken.INVALID_TOKEN;
                        state = 14;
                    }

                    break;
                case 2:
                    if(c=='\n'){
                        line++;
                        state = 1;
                    }
                    else if (c == -1) {
                        lex.type = TipoToken.END_OF_FILE;
                        state = 14;
                    }
                    else{
                        state = 2;
                    }
                    break;
                case 15:
                    if(c=='&'){
                        lex.token += (char) c;
                        state = 10;
                    }
                    else if (c == -1) {
                        lex.type = TipoToken.UNEXPECTED_EOF;
                        state = 14;
                    }
                    else{
                        lex.type = TipoToken.INVALID_TOKEN;
                        state = 14;
                    }
                    break;
                case 3:
                    if(c=='='){
                        lex.token += (char) c;
                        state = 10;
                    }
                    else if (c == -1) {
                        lex.type = TipoToken.END_OF_FILE;
                        state = 14;
                    }
                    else{
                        lex.type = TipoToken.INVALID_TOKEN;
                        state = 14;
                    }

                    break;
                case 4:
                    if(c=='='){
                        lex.token += (char) c;
                        state = 10;
                    }
                    else{
                        ungetc(c);
                        state = 10;
                    }
                    break;
                case 5:
                    if(c=='|'){
                        lex.token += (char) c;
                        state = 10;
                    }
                    else if (c == -1) {
                        lex.type = TipoToken.END_OF_FILE;
                        state = 14;
                    }
                    else{
                        lex.type = TipoToken.INVALID_TOKEN;
                        state = 14;
                    }

                    break;
                case 6:
                    if (Character.isLetter(c) ||
                            Character.isDigit(c) ||
                            c == '_') {
                        lex.token += (char) c;
                        state = 6;
                    }
                    else{
                        ungetc(c);
                        state = 10;
                    }
                    break;
                case 7:
                    if(Character.isDigit(c)){
                        lex.token += (char) c;
                        state = 7;
                    }
                    else if (c=='.'){
                        lex.token += (char) c;
                        state = 9;
                    }
                    else{
                        lex.type = TipoToken.NUMBER;
                        ungetc(c);
                        state = 11;
                    }
                    break;
                case 8:
                    if(c=='}'){
                        lex.type = TipoToken.TEXT;
                        state = 12;
                    }
                    else if(c==-1){
                        lex.type = TipoToken.UNEXPECTED_EOF;
                        state = 14;
                    }
                    else{
                        lex.token += (char) c;
                        state = 8;
                    }

                    break;
                case 9:
                    if(Character.isDigit(c)){
                        lex.token += (char) c;
                        state = 16;
                    }
                    else if(c==-1){
                        lex.type = TipoToken.UNEXPECTED_EOF;
                        state = 14;
                    }
                    else {

                        lex.type = TipoToken.INVALID_TOKEN;
                        state = 14;
                    }
                    break;
                case 16:
                    if(Character.isDigit(c)){
                        lex.token += (char) c;
                        state = 16;
                    }
                    else {
                        ungetc(c);
                        lex.type = TipoToken.FLOAT;
                        state = 11;
                    }
                    break;

                default:
                    throw new LexicalException("Unreachable");
            }
        }

        if (state == 10)
            lex.type = st.get(lex.token);

        return lex;
    }

    private int getc() {
        try {
            return input.read();
        } catch (Exception e) {
            throw new LexicalException("Unable to read file");
        }
    }

    private void ungetc(int c) {
        if (c != -1) {
            try {
                input.unread(c);
            } catch (Exception e) {
                throw new LexicalException("Unable to ungetc");
            }
        }
    }
}
