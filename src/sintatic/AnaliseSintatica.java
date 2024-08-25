package sintatic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import interpreter.command.AssignCommand;
import interpreter.command.BlocksCommand;
import interpreter.command.Command;
import interpreter.command.PrintCommand;
import interpreter.command.WhileCommand;
import interpreter.command.AssertCommand;
import interpreter.command.ForCommand;
import interpreter.command.DoWhileCommand;

import interpreter.command.IfCommand;
import interpreter.expr.BinaryExpr;
import interpreter.expr.BinaryOp;
import interpreter.expr.ConstExpr;
import interpreter.expr.Expr;
import interpreter.expr.FunctionExpr;
import interpreter.expr.FunctionOp;
import interpreter.expr.SafeVariable;
import interpreter.expr.SetExpr;
import interpreter.expr.SingleListItem;
import interpreter.expr.UnaryExpr;
import interpreter.expr.UnaryOp;
import interpreter.expr.UnsafeVariable;
import interpreter.expr.Variable;
import interpreter.expr.ListExpr;
import interpreter.expr.IfListItem;
import interpreter.expr.SingleListItem;
import interpreter.expr.SpreadListItem;
import interpreter.expr.ForListItem;
import interpreter.expr.ListItem;
import interpreter.expr.AccessExpr;
import interpreter.expr.MapItem;
import interpreter.expr.MapExpr;


import interpreter.util.Utils;
import interpreter.value.BoolValue;
import interpreter.value.NumberValue;
import interpreter.value.TextValue;
import interpreter.value.MapValue;
import interpreter.value.ListValue;
import interpreter.value.Value;

//import org.jcp.xml.dsig.internal.dom.Utils;

import lexical.AnalisadorLexico;
import lexical.TipoToken;
import lexical.Token;

public class AnaliseSintatica {
    private AnalisadorLexico lex;
    private Token current;
    private Map<String,Variable> memory;

    public AnaliseSintatica(AnalisadorLexico lex) {
        this.lex = lex;
        this.current = lex.nextToken();
        memory = new HashMap<String,Variable>();
    }

    public void start() {
        procProgram();
        eat(TipoToken.END_OF_FILE);
    }

    private void advance() {
         System.out.println("Advanced (\"" + current.token + "\", " +
             current.type + ")");
        current = lex.nextToken();
    }

    private void eat(TipoToken type) {
         System.out.println("Expected (..., " + type + "), found (\"" +
            current.token + "\", " + current.type + ")");
        if (type == current.type) {
            current = lex.nextToken();
        } else {
            showError();
        }
    }

    private void showError() {
        System.out.printf("%02d: ", lex.getLine());

        switch (current.type) {
            case INVALID_TOKEN:
                System.out.printf("Lexema inválido [%s]\n", current.token);
                break;
            case UNEXPECTED_EOF:
            case END_OF_FILE:
                System.out.printf("Fim de arquivo inesperado\n");
                break;
            default:
                System.out.printf("Lexema não esperado [%s]\n", current.token);
                break;
        }

        System.exit(1);
    }

    // program ::= app identifier body

    private void procProgram() {
        eat(TipoToken.APP);
        procIdentifier();//eat(TipoToken.NAME);
        procBody();
    }

    //body ::= [ var decl-list] init stmt-list return

    private void procBody() {
        if (current.type == TipoToken.VAR) {
            advance();
            proDeclList();
        }
        eat(TipoToken.INIT);
        procStmtList();
        eat(TipoToken.RETURN);

    }

    //decl-list ::= decl {";" decl}

    private void proDeclList() {
        proDecl();
        while(current.type == TipoToken.SEMICOLON){
            proDecl();
        }
    }


    // decl ::= type ident-list
    private void procDecl() {
        procType();
        procIdentList();
    }

    // ident-list ::= identifier {"," identifier}
    private void procIdentList() {
        procIdentifier();
        while(current.type == TipoToken.COMMA){
            procIdentifier();
        }

    }

    // type ::= integer
    //             | real
    private void procType() {
        
        if(current.type == TipoToken.INTEGER || current.type == TipoToken.REAL ){
            advance();
        }
        else{
            showError();
        }
    }

    // <stmt-list ::= stmt {";" stmt}
    private void procStmtList() {
       procStmt();
       while (current.type == TipoToken.SEMICOLON){
           procStmt();
       }
    }

    // stmt := assign-stmt
    //             | if-stmt
    //             | repeat-stmt
    //             | read-stmt
    //             | write-stmt
    private void procStmt() {
        if(current.type == TipoToken.NAME){
            procAssignStmt();
        }
        else if(current.type == TipoToken.IF){
            procIfStmt();
        }
        else if(current.type == TipoToken.REPEAT){
            procRepeatStmt();
        }
        else if(current.type == TipoToken.WRITE){
            procWriteStmt();
        }
        else if(current.type == TipoToken.READ){
            procReadStmt();
        }
        else{
            showError();
        }

    }

    // assign-stmt ::= identifier ":=" simple_expr
    private void procAssignStmt() {
        procIdentifier();
        eat(TipoToken.ASSIGN);
        procSimpleExpr();
    }

    // if-stmt ::= if condition then stmt-list if-stmt2 end
    private void procIfStmt() {
        eat(TipoToken.IF);
        procCondition();
        eat(TipoToken.Then);
        procStmtList();
        procIfStmt2();
        eat(TipoToken.END);
    }

    // if-stmt2 ::= else stmt-list
    //                | λ
    private void procIfStmt2() {
        if(current.type == TipoToken.ELSE){
            advance();
            procStmtList();
        }
        else if(current.type == TipoToken.ELSE){
            return;
        }
        else{
          showError();
        }
    }

    // <repeat-stmt ::= repeat stmt-list stmt-suffix
    private void procRepeatStmt() {
        eat(TipoToken.REPEAT);
        procStmtList();
        procStmtSuffix();
    }

    // stmt-suffix ::= until condition
    private void procStmtSuffix() {
        eat(TipoToken.UNTIL);
        procCondition();
    }

    // read-stmt ::= read "(" identifier ")"
    private void procReadStmt() {
        eat(TipoToken.READ);
        eat(TipoToken.PARENTESES_L);
        procIdentifier();
        eat(TipoToken.PARENTESES_R);
    }

    // write-stmt ::= write "(" writable ")"
    private void procWriteStmt() {
        eat(TipoToken.WRITE);
        eat(TipoToken.PARENTESES_L);
        procWritable();
        eat(TipoToken.PARENTESES_R);
    }

    // writable ::= simple-expr
    //                   | litera
    private void procWritable() {

    }

    // condition ::= expression
    private void procCondition() {
        procExpression();
    }

    // expression ::= simple-expr expression2
    private void procExpression() {
        procSimpleExpression();
        procExpression2();
    }

    // expression2 ::=  relop simple-expr expression2
    //          		     | λ
    private void procExpression2() {//pode estar faltando o follow
        if(current.type == TipoToken.EQUAL||
                current.type == TipoToken.GREATER||
                current.type == TipoToken.LOWER||
                current.type == TipoToken.NOT_EQUAL||
                current.type == TipoToken.GREATER_EQUAL||
                current.type == TipoToken.LOWER_EQUAL){
            advance();
            procSimpleExpression();
            procExpression2();
        }
    }

    // simple-expr ::= term simple-expr2
    private void procSimpleExpression() {
        procTerm();
        procSimpleExpression2();
    }

    // simple-expr2 ::= addop term simple-expr2
    //          		     | λ
    private void procSimpleExpression2() {
        if(current.type == TipoToken.ADD||
                current.type == TipoToken.SUB||
                current.type == TipoToken.OR){
            procTerm();
            procExpression2();
        }
    }

    // term ::= factor-a term2
    private void procTerm() {
        procFactorA();
        procTerm2();
    }

    // <list> ::= '[' [ <l-elem> { ',' <l-elem> } ] ']'
    private ListExpr procList() {
        ListItem lElem = null;

        eat(TipoToken.SQUARE_BRACKETS_L);
        int line = lex.getLine();
        ListExpr le = new ListExpr(line);

        if (current.type == TipoToken.NOT ||
                current.type == TipoToken.SUB ||
                current.type == TipoToken.INC ||
                current.type == TipoToken.DEC ||
                current.type == TipoToken.PARENTESES_L ||
                current.type == TipoToken.NULL ||
                current.type == TipoToken.FALSE ||
                current.type == TipoToken.TRUE ||
                current.type == TipoToken.NUMBER ||
                current.type == TipoToken.TEXT ||
                current.type == TipoToken.READ ||
                current.type == TipoToken.RANDOM ||
                current.type == TipoToken.LENGTH ||
                current.type == TipoToken.KEYS ||
                current.type == TipoToken.VALUES ||
                current.type == TipoToken.TOBOOL ||
                current.type == TipoToken.TOINT ||
                current.type == TipoToken.TOSTR ||
                current.type == TipoToken.NAME ||
                current.type == TipoToken.SQUARE_BRACKETS_L ||
                current.type == TipoToken.CURLY_BRACKETS_L)  {
            lElem = procLElem();
            le.addItem(lElem);

            while(current.type == TipoToken.COMMA) {
                advance();
                lElem = procLElem();
                le.addItem(lElem);
            }
        }
        
        eat(TipoToken.SQUARE_BRACKETS_R);

        return le;
    } 

    // <l-elem> ::= <l-single> | <l-spread> | <l-if> | <l-for>
    private ListItem procLElem() {
        ListItem li = null;

        switch (current.type) {
            case FOR:
                li = procLFor();
                break;
            case IF:
                li = procLIf();
                break;
            case SPREAD:
                li = procLSpread();
                break;
            default:
                li = procLSingle();
                break;
        }
        return li;
    }

    // <l-single> ::= <expr>
    private SingleListItem procLSingle() {
        Expr expr = procExpr();
        int line = lex.getLine();
        SingleListItem sli = new SingleListItem(line, expr);
        return sli;
    }

    // <l-spread> ::= '...' <expr>
    private SpreadListItem procLSpread() {
        Expr expr = null;
        eat(TipoToken.SPREAD);
        int line = lex.getLine();
        expr = procExpr();
        SpreadListItem sli = new SpreadListItem(line, expr);

        return sli;
    }

    // <l-if> ::= if '(' <expr> ')' <l-elem> [ else <l-elem> ]
    private IfListItem procLIf() {
        eat(TipoToken.IF);
        int line = lex.getLine();
        eat(TipoToken.PARENTESES_L);
        Expr expr = procExpr();
        eat(TipoToken.PARENTESES_R);
        ListItem lIfElem = procLElem();
        ListItem lElseElem = null;

        if(current.type == TipoToken.ELSE) {
            advance();
            lElseElem = procLElem();
        }

        IfListItem ili = new IfListItem(line, expr, lIfElem, lElseElem);
        return ili;
    }

    // <l-for> ::= for '(' <name> in <expr> ')' <l-elem>
    private ForListItem procLFor() {
        eat(TipoToken.FOR);
        int line = lex.getLine();
        eat(TipoToken.PARENTESES_L);
        Variable name = procName();
        eat(TipoToken.IN);
        Expr lista = procExpr();
        eat(TipoToken.PARENTESES_R);
        ListItem li = procLElem();

        ForListItem fli = new ForListItem(line, name, lista, li);

        return fli;
    }

    // <map> ::= '{' [ <m-elem> { ',' <m-elem> } ] '}'
    private MapExpr procMap() {
        eat(TipoToken.CURLY_BRACKETS_L);
        int line = lex.getLine();

        MapExpr mexpr = new MapExpr(line);

        if (current.type == TipoToken.NOT ||
                current.type == TipoToken.SUB ||
                current.type == TipoToken.INC ||
                current.type == TipoToken.DEC ||
                current.type == TipoToken.PARENTESES_L ||
                current.type == TipoToken.NULL ||
                current.type == TipoToken.FALSE ||
                current.type == TipoToken.TRUE ||
                current.type == TipoToken.NUMBER ||
                current.type == TipoToken.TEXT ||
                current.type == TipoToken.READ ||
                current.type == TipoToken.RANDOM ||
                current.type == TipoToken.LENGTH ||
                current.type == TipoToken.KEYS ||
                current.type == TipoToken.VALUES ||
                current.type == TipoToken.TOBOOL ||
                current.type == TipoToken.TOINT ||
                current.type == TipoToken.TOSTR ||
                current.type == TipoToken.NAME ||
                current.type == TipoToken.SQUARE_BRACKETS_L ||
                current.type == TipoToken.CURLY_BRACKETS_L) {
            MapItem item = procMElem();
            mexpr.addItem(item);

            while (current.type == TipoToken.COMMA) {
                advance();
                item = procMElem();
                mexpr.addItem(item);
            }
        }
        eat(TipoToken.CURLY_BRACKETS_R);

        return mexpr;

    }

    // <m-elem> ::= <expr> ':' <expr>
    private MapItem procMElem() {
        Expr key = procExpr();
        eat(TipoToken.COLON);
        Expr value = procExpr();

        MapItem item = new MapItem(key, value);
        return item;
    }

    private Variable procDeclarationName(boolean constant, boolean nullable) {
        String name = current.token;
        eat(TipoToken.NAME);
        int line = lex.getLine();

        if (memory.containsKey(name))
            Utils.abort(line, "A variável já foi declarada");

        Variable var;
        if (nullable) {
            var = new UnsafeVariable(line, name, constant);
        } else {
            var = new SafeVariable(line, name, constant);
        }

        memory.put(name, var);

        return var;
    }

    private Variable procName() {
        String name = current.token;
        eat(TipoToken.NAME);
        int line = lex.getLine();


        if (!memory.containsKey(name))
            Utils.abort(line, "A variável ainda não foi declarada");

        Variable var = memory.get(name);
        return var;
    }

    private NumberValue procNumber() {
        String txt = current.token;
        eat(TipoToken.NUMBER);

        int n;
        try {
            n = Integer.parseInt(txt);
        } catch (Exception e) {
            n = 0;
        }

        NumberValue nv = new NumberValue(n);
        return nv;
    }

    private TextValue procText() {
        String txt = current.token;
        eat(TipoToken.TEXT);
        TextValue tv = new TextValue(txt);
        return tv;
    }
}
