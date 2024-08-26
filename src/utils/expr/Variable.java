package utils.expr;

import utils.util.Utils;
import utils.value.Value;

public class Variable extends SetExpr {

    private String name;
    private boolean constant;
    private Value<?> value;
    private boolean initialized;

    public Variable(int line, String name, boolean constant) {
        super(line);
        this.name = name;
        this.constant = constant;
        this.value = null;
        this.initialized = false;
    }

    public String getName() {
        return name;
    }

    public boolean isConstant() {
        return constant;
    }

     public Value<?> expr() {
        if (!initialized)
            Utils.abort(super.getLine());

        return value;
    }

    public void setValue(Value<?> value) {
        if (initialized && this.isConstant())
            Utils.abort(super.getLine());

        this.value = value;
        this.initialized = true;
    }
    
}

