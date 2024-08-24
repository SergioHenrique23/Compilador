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

import org.jcp.xml.dsig.internal.dom.Utils;

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

    public Command start() {
        Command cmd = procCode();
        eat(TipoToken.END_OF_FILE);
        return cmd;
    }

    private void advance() {
         //System.out.println("Advanced (\"" + current.token + "\", " +
             //current.type + ")");
        current = lex.nextToken();
    }

    private void eat(TipoToken type) {
         //System.out.println("Expected (..., " + type + "), found (\"" + 
            //current.token + "\", " + current.type + ")");
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

    // <code> ::= { <cmd> }
    private BlocksCommand procCode() {
        int line = lex.getLine();
        List<Command> cmds = new ArrayList<Command>();
        while (current.type == TipoToken.FINAL ||
                current.type == TipoToken.VAR ||
                current.type == TipoToken.PRINT ||
                current.type == TipoToken.ASSERT ||
                current.type == TipoToken.IF ||
                current.type == TipoToken.WHILE ||
                current.type == TipoToken.DO ||
                current.type == TipoToken.FOR ||
                current.type == TipoToken.NOT ||
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
            Command c = procCmd();
            cmds.add(c);
        }

        BlocksCommand bc = new BlocksCommand(line, cmds);
        return bc;
    }

    // <cmd> ::= <decl> | <print> | <assert> | <if> | <while> | <dowhile> | <for> | <assign>
    private Command procCmd() {
        Command cmd = null;
        switch (current.type) {
            case FINAL:
            case VAR:
                cmd = procDecl();
                break;
            case PRINT:
                cmd = procPrint();
                break;
            case ASSERT:
                cmd = procAssert();
                break;
            case IF:
                cmd = procIf();
                break;
            case WHILE:
                cmd = procWhile();
                break;
            case DO:
                cmd = procDoWhile();
                break;
            case FOR:
                cmd = procFor();
                break;
            case NOT:
            case SUB:
            case INC:
            case DEC:
            case PARENTESES_L:
            case NULL:
            case FALSE:
            case TRUE:
            case NUMBER:
            case TEXT:
            case READ:
            case RANDOM:
            case LENGTH:
            case KEYS:
            case VALUES:
            case TOBOOL:
            case TOINT:
            case TOSTR:
            case NAME:
            case SQUARE_BRACKETS_L:
            case CURLY_BRACKETS_L:
                cmd = procAssign();
                break;
            default:
                showError();
                break;
        }

        return cmd;
    }

    // <decl> ::= [ final ] var [ '?' ] <name> [ '=' <expr> ] { ',' <name> [ '=' <expr> ] } ';'
    private BlocksCommand procDecl() {
        int line = lex.getLine();
        List<Command> cmds = new ArrayList<Command>();

        boolean constant = false;
        if (current.type == TipoToken.FINAL) {
            advance();
            constant = true;
        }

        eat(TipoToken.VAR);

        boolean nullable = false;
        if (current.type == TipoToken.NULLABLE) {
            advance();
            nullable = true;
        }
        Variable var = procDeclarationName(constant, nullable);

        if (current.type == TipoToken.ASSIGN) {
            line = lex.getLine(); 
            advance();

            Expr rhs = procExpr();

            AssignCommand acmd = new AssignCommand(line, rhs, var);
            cmds.add(acmd);
        }

        while (current.type == TipoToken.COMMA) {
            advance();

            var = procDeclarationName(constant, nullable);

            if (current.type == TipoToken.ASSIGN) {
                advance();

                Expr rhs = procExpr();

                AssignCommand acmd = new AssignCommand(line, rhs, var);
                cmds.add(acmd);
            }
        }

        eat(TipoToken.SEMICOLON);

        BlocksCommand bcmd = new BlocksCommand(line, cmds);
        return bcmd;
    }

    // <print> ::= print '(' [ <expr> ] ')' ';'
    private PrintCommand procPrint() {
        eat(TipoToken.PRINT);
        int line = lex.getLine();

        eat(TipoToken.PARENTESES_L);

        Expr expr = null;
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
            expr = procExpr();
        }
        eat(TipoToken.PARENTESES_R);
        eat(TipoToken.SEMICOLON);

        PrintCommand pc = new PrintCommand(line, expr);
        return pc;
    }

    // <assert> ::= assert '(' <expr> [ ',' <expr> ] ')' ';'
    private AssertCommand procAssert() {
        
        Expr msg = null;
        eat(TipoToken.ASSERT);
        int line = lex.getLine();
        eat(TipoToken.PARENTESES_L);
        Expr condition = procExpr();

        if(current.type == TipoToken.COMMA) {
            eat(TipoToken.COMMA);
            msg = procExpr();
        }
        
        eat(TipoToken.PARENTESES_R);
        eat(TipoToken.SEMICOLON);

        AssertCommand ac = new AssertCommand(line, condition, msg);
        return ac;
    }

    // <if> ::= if '(' <expr> ')' <body> [ else <body> ]
    private IfCommand procIf() {
        
        Command cmdsElse = null;
        eat(TipoToken.IF);
        int line = lex.getLine();

        eat(TipoToken.PARENTESES_L);
        Expr expr = procExpr();
        eat(TipoToken.PARENTESES_R);
        Command cmdsIf = procBody();

        if (current.type == TipoToken.ELSE) {
            advance();
            cmdsElse = procBody();
        }

        IfCommand icmd = new IfCommand(line, expr, cmdsIf, cmdsElse);
        
        return icmd;
    }

    // <while> ::= while '(' <expr> ')' <body>
    private WhileCommand procWhile() {
        eat(TipoToken.WHILE);
        int line = lex.getLine();

        eat(TipoToken.PARENTESES_L);
        Expr expr = procExpr();
        eat(TipoToken.PARENTESES_R);
        Command cmds = procBody();

        WhileCommand wcmd = new WhileCommand(line, expr, cmds);
        return wcmd; 
    }

    // <dowhile> ::= do <body> while '(' <expr> ')' ';'
    private DoWhileCommand procDoWhile() {
        eat(TipoToken.DO);
        int line = lex.getLine();

        Command cmd = procBody();
        eat(TipoToken.WHILE);
        eat(TipoToken.PARENTESES_L);
        Expr expr = procExpr();
        eat(TipoToken.PARENTESES_R);
        eat(TipoToken.SEMICOLON);

        DoWhileCommand dwcmd = new DoWhileCommand(line, cmd, expr);

        return dwcmd;
    }

    // <for> ::= for '(' <name> in <expr> ')' <body>
    private BlocksCommand procFor() {
        List<Command> cmds = new ArrayList<Command>();

        eat(TipoToken.FOR);
        int line = lex.getLine();
        eat(TipoToken.PARENTESES_L);
        if(!(current.type == TipoToken.NAME))
            Utils.abort(line);
        
        Variable v = procDeclarationName(false, false);  
        
        eat(TipoToken.IN);
        Expr expr = procExpr();

        AssignCommand ascmd = new AssignCommand(line, expr, v);
        
        cmds.add(ascmd);

        eat(TipoToken.PARENTESES_R);
        Command c = procBody();

        ForCommand fc = new ForCommand(line, v, expr, c);
        cmds.add(fc);
        return new BlocksCommand(line, cmds);
    }

    // <body> ::= <cmd> | '{' <code> '}'
    private Command procBody() {
        Command cmds = null;
        if (current.type == TipoToken.CURLY_BRACKETS_L) {
            advance();
            cmds = procCode();
            eat(TipoToken.CURLY_BRACKETS_R);
        } else {
            cmds = procCmd();
        }

        return cmds;
    }

    // <assign> ::= [ <expr> '=' ] <expr> ';'
    private AssignCommand procAssign() {
        int line = lex.getLine();
        Expr rhs = procExpr();
        SetExpr lhs = null;

        
        if (current.type == TipoToken.ASSIGN) {
            advance();

            if (!(rhs instanceof SetExpr))
                Utils.abort(line);

            lhs = (SetExpr) rhs;
            rhs = procExpr();
        }
        eat(TipoToken.SEMICOLON);

        AssignCommand acmd = new AssignCommand(line, rhs, lhs);
        return acmd;
    }

    // <expr> ::= <cond> [ '??' <cond> ]
    private Expr procExpr() {
        Expr expr = procCond();
        if (current.type == TipoToken.IF_NULL) {
            advance();
            int line = lex.getLine();
            BinaryOp op = BinaryOp.IF_NULL;
            Expr expr2 = procCond();

            BinaryExpr be = new BinaryExpr(line, expr, op, expr2);
            
            return be;
        }
        return expr;
    }

    // <cond> ::= <rel> { ( '&&' | '||' ) <rel> }
    private Expr procCond() {
        Expr left = procRel();
        while (current.type == TipoToken.AND ||
                current.type == TipoToken.OR) {
            BinaryOp op = null;
            if (current.type == TipoToken.AND) {
                op = BinaryOp.AND;
                advance();
            } else {
                op = BinaryOp.OR;
                advance();
            }

            int line = lex.getLine();
            Expr right = procRel();

            left = new BinaryExpr(line, left, op, right);
        }

        return left;
    }

    // <rel> ::= <arith> [ ( '<' | '>' | '<=' | '>=' | '==' | '!=' ) <arith> ]
    private Expr procRel() {
        Expr left = procArith();
        if (current.type == TipoToken.LOWER ||
                current.type == TipoToken.GREATER ||
                current.type == TipoToken.LOWER_EQUAL ||
                current.type == TipoToken.GREATER_EQUAL ||
                current.type == TipoToken.EQUAL ||
                current.type == TipoToken.NOT_EQUAL) {
            BinaryOp op = null;
            switch (current.type) {
                case LOWER:
                    op = BinaryOp.LOWER;
                    advance();
                    break;
                case GREATER:
                    op = BinaryOp.GREATER;
                    advance();
                    break;
                case LOWER_EQUAL:
                    op = BinaryOp.LOWER_EQUAL;
                    advance();
                    break;
                case GREATER_EQUAL:
                    op = BinaryOp.GREATER_EQUAL;
                    advance();
                    break;
                case EQUAL:
                    op = BinaryOp.EQUAL;
                    advance();
                    break;
                default:
                    op = BinaryOp.NOT_EQUAL;
                    advance();
                    break;
            }

            int line = lex.getLine();
            Expr right = procArith();

            left = new BinaryExpr(line, left, op, right);
        }

        return left;
    }

    // <arith> ::= <term> { ( '+' | '-' ) <term> }
    private Expr procArith() {
        Expr left = procTerm();

        while (current.type == TipoToken.ADD ||
                current.type == TipoToken.SUB) {
            BinaryOp op = null;
            if (current.type == TipoToken.ADD) {
                op = BinaryOp.ADD;
                advance();
            } else {
                op = BinaryOp.SUB;
                advance();
            }
            int line = lex.getLine();

            Expr right = procTerm();

            left = new BinaryExpr(line, left, op, right);
        }

        return left;
    }

    // <term> ::= <prefix> { ( '*' | '/' | '%' ) <prefix> }
    private Expr procTerm() {
        Expr left = procPrefix();

        while(current.type == TipoToken.MUL ||
            current.type == TipoToken.DIV ||
            current.type == TipoToken.MOD) {
            BinaryOp op = null;
            if(current.type == TipoToken.MUL) {
                op = BinaryOp.MUL;
                advance();
            }
            else if(current.type == TipoToken.DIV) {
                op = BinaryOp.DIV;
                advance();
            }
            else {
                op = BinaryOp.MOD;
                advance();
            }

            int line = lex.getLine();
            Expr right = procPrefix();
            left = new BinaryExpr(line, left, op, right);
        }
        return left;
    }

    // <prefix> ::= [ '!' | '-' | '++' | '--' ] <factor>
    private Expr procPrefix() {
        UnaryOp op = null;
        if (current.type == TipoToken.NOT ||
                current.type == TipoToken.SUB ||
                current.type == TipoToken.INC ||
                current.type == TipoToken.DEC) {
            switch (current.type) {
                case NOT:
                    op = UnaryOp.NOT;
                    advance();
                    break;
                case SUB:
                    op = UnaryOp.NEG;
                    advance();
                    break;
                case INC:
                    op = UnaryOp.PRE_INC;
                    advance();
                    break;
                default:
                    op = UnaryOp.PRE_DEC;
                    advance();
                    break;
            }
        }
        int line = lex.getLine();
        Expr expr = procFactor();
        
        if (op != null) {
            UnaryExpr ue = new UnaryExpr(line, expr, op);
            return ue;
        }

        return expr;
    }

    // <factor> ::= ( '(' <expr> ')' | <rvalue> ) [ '++' | '--' ]
    private Expr procFactor() {
        Expr expr = null;
        UnaryOp op = null;
        if (current.type == TipoToken.PARENTESES_L) {
            advance();
            expr = procExpr();
            eat(TipoToken.PARENTESES_R);
        } else {
            expr = procRValue();
        }
        if (current.type == TipoToken.INC ||
                current.type == TipoToken.DEC) {
            if (current.type == TipoToken.INC) {
                op = UnaryOp.POS_INC;
                advance();
            } else {
                op = UnaryOp.POS_DEC;
                advance();
            }
        }

        int line = lex.getLine();

        if (op != null) {
            UnaryExpr ue = new UnaryExpr(line, expr, op);
            return ue;
        }


        return expr;
    }

    // <rvalue> ::= <const> | <function> | <lvalue> | <list> | <map>
    private Expr procRValue() {
        Expr expr = null;
        switch (current.type) {
            case NULL:
            case FALSE:
            case TRUE:
            case NUMBER:
            case TEXT:
                expr = procConst();
                break;
            case READ:
            case RANDOM:
            case LENGTH:
            case KEYS:
            case VALUES:
            case TOBOOL:
            case TOINT:
            case TOSTR:
                expr = procFunction();
                break;
            case NAME:
                expr = procLValue();
                break;
            case SQUARE_BRACKETS_L:
                expr = procList();
                break;
            case CURLY_BRACKETS_L:
                expr = procMap();
                break;
            default:
                showError();
                break;
        }

        return expr;
    }

    // <const> ::= null | false | true | <number> | <text>
    private ConstExpr procConst() {
        Value<?> v = null;
        switch (current.type) {
            case NULL:
                advance();
                v = null;
                break;
            case FALSE:
                advance();
                v = new BoolValue(false);
                break;
            case TRUE:
                advance();
                v = new BoolValue(true);
                break;
            case NUMBER:
                v = procNumber();
                break;
            case TEXT:
                v = procText();
                break;
            default:
                showError();
                break;
        }

        int line = lex.getLine();
        ConstExpr ce = new ConstExpr(line, v);
        return ce;
    }

    // <function> ::= ( read | random | length | keys | values | tobool | toint | tostr ) '(' <expr> ')'
    private FunctionExpr procFunction() {
        FunctionOp op = null;
        switch (current.type) {
            case READ:
                advance();
                op = FunctionOp.READ;
                break;
            case RANDOM:
                advance();
                op = FunctionOp.RANDOM;
                break;
            case LENGTH:
                advance();
                op = FunctionOp.LENGTH;
                break;
            case KEYS:
                advance();
                op = FunctionOp.KEYS;
                break;
            case VALUES:
                advance();
                op = FunctionOp.VALUES;
                break;
            case TOBOOL:
                advance();
                op = FunctionOp.TOBOOL;
                break;
            case TOINT:
                advance();
                op = FunctionOp.TOINT;
                break;
            case TOSTR:
                advance();
                op = FunctionOp.TOSTR;
                break;
            default:
                showError();
                break;
        }
        int line = lex.getLine();

        eat(TipoToken.PARENTESES_L);
        Expr expr = procExpr();
        eat(TipoToken.PARENTESES_R);

        FunctionExpr fexpr = new FunctionExpr(line, op, expr);
        return fexpr;
    }

    // <lvalue> ::= <name> { '[' <expr> ']' }
    private SetExpr procLValue() {
        SetExpr base = procName();
        while (current.type == TipoToken.SQUARE_BRACKETS_L) {
            advance();
            int line = lex.getLine();

            Expr index = procExpr();

            base = new AccessExpr(line, base, index);

            eat(TipoToken.SQUARE_BRACKETS_R);
        }



        return base;
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
