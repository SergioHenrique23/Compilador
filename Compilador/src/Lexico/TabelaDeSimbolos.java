package Lexico;

import java.util.HashMap;
import java.util.Map;

public class TabelaDeSimbolos {
    private Map<String, TipoToken> ts;
    protected TabelaDeSimbolos anterior;

    public TabelaDeSimbolos() {
        ts = new HashMap<String, TipoToken>();
        //criar tabaela de simoblos com o put
        ts.put("?", TipoToken.NULLABLE);
        ts.put(";", TipoToken.SEMICOLON);
        ts.put("=", TipoToken.ASSIGN);
        ts.put("==", TipoToken.EQUAL);
        ts.put("!=", TipoToken.NOT_EQUAL);
        ts.put("<", TipoToken.LOWER);
        ts.put("<=", TipoToken.LOWER_EQUAL);
        ts.put(">", TipoToken.GREATER);
        ts.put(">=", TipoToken.GREATER_EQUAL);
        ts.put("+", TipoToken.ADD);
        ts.put("-", TipoToken.SUB);
        ts.put("*", TipoToken.MUL);
        ts.put("/", TipoToken.DIV);
        ts.put("%", TipoToken.MOD);
        ts.put("[", TipoToken.SQUARE_BRACKETS_L);
        ts.put("]", TipoToken.SQUARE_BRACKETS_R);
        ts.put(",", TipoToken.COMMA);
        ts.put("{", TipoToken.CURLY_BRACKETS_L);
        ts.put("}", TipoToken.CURLY_BRACKETS_R);
        ts.put("&&", TipoToken.AND);
        ts.put("||", TipoToken.OR);
        ts.put("??", TipoToken.IF_NULL);
        ts.put("!", TipoToken.DENIAL);
        ts.put("--", TipoToken.DECREMENT);
        ts.put("++", TipoToken.INCREMENT);
        ts.put("var", TipoToken.VAR);
        ts.put("var?", TipoToken.VARN);
        ts.put("final", TipoToken.FINAL);
        ts.put("assert", TipoToken.ASSERT);
        ts.put("print", TipoToken.PRINT);
        ts.put("while", TipoToken.WHILE);
        ts.put("do?", TipoToken.DO);
        ts.put("for", TipoToken.FOR);
        ts.put("read", TipoToken.READ);
        ts.put("random", TipoToken.RANDOM);
        ts.put("length", TipoToken.LENGTH);
        ts.put("keys", TipoToken.KEYS);
        ts.put("values", TipoToken.VALUES);
        ts.put("in", TipoToken.IN);
        ts.put("tobool", TipoToken.TOBOOL);
        ts.put("toint", TipoToken.TOINT);
        ts.put("tostr", TipoToken.TOSTR);
        ts.put("null", TipoToken.NULL);
        ts.put("false", TipoToken.FALSE);
        ts.put("true", TipoToken.TRUE);
        ts.put(":", TipoToken.TWO_POINTS);
        ts.put("...", TipoToken.THREE_POINT);
        ts.put("(", TipoToken.PARENTESES_L);
        ts.put(")", TipoToken.PARENTESES_R);
        ts.put("if", TipoToken.IF);
        ts.put("else", TipoToken.ELSE);
        
        anterior = null;
    }

    public TabelaDeSimbolos(TabelaDeSimbolos anterior) {
        ts = new HashMap<String, TipoToken>();
        this.anterior = anterior;
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
        for (TabelaDeSimbolos t = this; t!=null; t = t.anterior){
            TipoToken encontrado = (TipoToken) t.ts.get(token);
            if(encontrado != null)
                return encontrado;
        }
        return null;
    }
}
