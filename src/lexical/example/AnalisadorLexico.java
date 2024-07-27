package lexical.example;
import java.io.FileInputStream;
import java.io.PushbackInputStream;


public class AnalisadorLexico {
        private int line;
    private TabelaDeSimbolos st;
    private PushbackInputStream input;

    public AnalisadorLexico(String filename) {//testar
        try {
            input = new PushbackInputStream(new FileInputStream(filename), 2);
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

        while (state != 12 && state != 13 && state != 14 && state != 15) {
            int c = getc();


            switch (state) {
                case 1:
                    if(c == ' ' || c == '\t'){
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
                        state = 3;
                    }
                    else if(c=='!'||c=='<'||c=='>'){
                        lex.token += (char) c;
                        state = 4;
                    }
                    else if(c=='+'||c=='-'||c=='+'||c=='*'||c=='/'||c=='('||c==')'||c==';'||c==','){
                        lex.token += (char) c;
                        state = 12;
                    }
                    else if(c =='|'){
                        lex.token += (char) c;
                        state = 5;
                    }
                    else if(c=='&'){
                        lex.token += (char) c;
                        state = 6;
                    }

                    else if(Character.isDigit(c)){
                        lex.token += (char) c;
                        state = 8;
                    }
                    else if(Character.isLetter(c)||c=='_'){
                        lex.token += (char) c;
                        state = 7;
                    }
                    else if(c=='{'){
                        //lex.token += (char) c;
                        state = 9;
                    }
                    else if (c == -1) {
                        lex.type = TipoToken.END_OF_FILE;
                        state = 15;
                    } else {
                        lex.token += (char) c;
                        lex.type = TipoToken.INVALID_TOKEN;
                        state = 15;
                    }
                    break;
                case 2:
                    if(c=='\n'){
                        line++;
                        state = 1;
                    }
                    else if (c == -1) {
                        lex.type = TipoToken.END_OF_FILE;
                        state = 15;
                    }
                    else{
                        state = 2;
                    }
                    //analisar possibilidades de erro no c =! '/'
                    break;
                case 3:
                    if(c=='='){
                        lex.token += (char) c;
                        state = 12;
                    }
                    else if (c == -1) {
                        lex.type = TipoToken.END_OF_FILE;
                        state = 15;
                    }
                    else{
                        lex.type = TipoToken.INVALID_TOKEN;
                        state = 15;
                    }

                    break;
                case 4:
                    if(c=='='){
                        lex.token += (char) c;
                        state = 12;
                    }
                    else{
                        ungetc(c);
                        state = 12;
                    }
                    break;
                case 5:
                    if(c=='|'){
                        lex.token += (char) c;
                        state = 12;
                    }
                    else{
                        ungetc(c);
                        lex.type = TipoToken.INVALID_TOKEN;
                        state = 15;
                    }

                    break;
                case 6:
                    if(c=='&'){
                        lex.token += (char) c;
                        state = 12;
                    }
                    else{
                        ungetc(c);
                        lex.type = TipoToken.INVALID_TOKEN;
                        state = 15;
                    }
                    break;
                case 7:
                    if(c=='_'|| Character.isLetter(c)||Character.isDigit(c)){
                        lex.token += (char) c;
                        state = 7;
                    }
                    else{
                        ungetc(c);
                        state = 12;
                    }
                    break;
                case 8:
                    //adicionar tres pontos na tabela de simbolo
                    if(Character.isDigit(c)){
                        lex.token += (char) c;
                        state = 8;
                    }
                    else if(c=='.'){
                        lex.token += (char) c;
                        state = 9;
                    }
                    else{
                        ungetc(c);
                        lex.type = TipoToken.NUMBER;
                        state = 13;
                    }

                    break;
                case 9:
                    if(c=='}'){
                        ungetc(c);
                        lex.type = TipoToken.TEXT;
                        state = 14;
                    }
                    else{
                        lex.token += (char) c;
                        state = 9;
                    }
                    break;
                case 10:
                    if(Character.isDigit(c)){
                        lex.token += (char) c;
                        lex.type = TipoToken.FLOAT;
                        state = 11;
                    }
                    else{
                        lex.type = TipoToken.INVALID_TOKEN;
                        state = 16;
                    }
                    break;
                case 11:
                    if(Character.isDigit(c)){
                        lex.token += (char) c;
                        state = 11;
                    }
                    else{
                        ungetc(c);
                        state = 13;
                    }
                    break;
                // case 12:
                //     if(c=='_'||c=='$'||Character.isLetter(c)||Character.isDigit(c)){
                //         lex.token += (char) c;
                //         state = 12;
                //     }
                //     else{
                //         ungetc(c);
                //         state = 15;
                //     }
                //     break;
                // case 13:
                //     if(Character.isDigit(c)){
                //         lex.token += (char) c;
                //         state = 13;
                //     }
                //     else{
                //         ungetc(c);
                //         lex.type = TipoToken.NUMBER;
                //         state = 16;
                //     }
                //     break;
                // case 14:
                //     if(c=='\''){
                //         //lex.token += (char) c;
                //         lex.type = TipoToken.TEXT;
                //         state = 16;
                //     }
                //     else{
                //         lex.token += (char) c;
                //         state = 14;
                //     }
                //     break;
                default:
                    throw new LexicalException("Unreachable");
            }
        }

        if (state == 12)
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
