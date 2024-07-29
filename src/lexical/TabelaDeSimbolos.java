package lexical;
import java.util.HashMap;
import java.util.Map;

public class TabelaDeSimbolos {
    private Map<String, TipoToken> ts;

    public TabelaDeSimbolos() {
        ts = new HashMap<String, TipoToken>();
        //criar tabaela de simoblos com o put
        ts.put("app",TipoToken.APP);
        ts.put("init",TipoToken.INIT);
        ts.put("real",TipoToken.REAL);
        ts.put("integer",TipoToken.INTEGER);
        ts.put("return",TipoToken.RETURN);
        ts.put("?", TipoToken.NULLABLE);
        ts.put(";", TipoToken.SEMICOLON);
        ts.put(":=", TipoToken.ASSIGN);
        ts.put("=", TipoToken.EQUAL);
        ts.put("!=", TipoToken.NOT_EQUAL);
        ts.put("<", TipoToken.LOWER);
        ts.put("<=", TipoToken.LOWER_EQUAL);
        ts.put(">", TipoToken.GREATER);
        ts.put(">=", TipoToken.GREATER_EQUAL);
        ts.put("+", TipoToken.ADD);
        ts.put("-", TipoToken.SUB);
        ts.put("*", TipoToken.MUL);
        ts.put("/", TipoToken.DIV);
        ts.put(",", TipoToken.COMMA);
        ts.put("{", TipoToken.CURLY_BRACKETS_L);
        ts.put("}", TipoToken.CURLY_BRACKETS_R);
        ts.put("&&", TipoToken.AND);
        ts.put("||", TipoToken.OR);
        ts.put("??", TipoToken.IF_NULL);
        ts.put("!", TipoToken.DENIAL);
        ts.put("var", TipoToken.VAR);
        ts.put("read", TipoToken.READ);
        ts.put("false", TipoToken.FALSE);
        ts.put("true", TipoToken.TRUE);
        ts.put(":", TipoToken.TWO_POINTS);
        ts.put("(", TipoToken.PARENTESES_L);
        ts.put(")", TipoToken.PARENTESES_R);
        ts.put("if", TipoToken.IF);
        ts.put("else", TipoToken.ELSE);
        ts.put("then",TipoToken.THEN);
        ts.put("end",TipoToken.END);
        ts.put("repeat",TipoToken.REPEAT);
        ts.put("until",TipoToken.UNTIL);
        ts.put("read",TipoToken.READ);
        ts.put("write",TipoToken.WRITE);
        
    }

    public TabelaDeSimbolos(TabelaDeSimbolos anterior) {
        ts = new HashMap<String, TipoToken>();
    }

    public boolean contains(String TipoToken) {
        return ts.containsKey(TipoToken);
    }

    public void put(String token, TipoToken tipo){
        ts.put(token, tipo);
    }

    public TipoToken getTipoToken(String token) {
        return this.contains(token) ? ts.get(token) : TipoToken.NAME;
    }

    public TipoToken get(String token){
        TabelaDeSimbolos t = this;
            TipoToken encontrado = (TipoToken) t.getTipoToken(token);
            if(encontrado != null)
                return encontrado;
        return null;
    }
}
