package value;

import ast.Expression;
import executor.Env;

public class Value {

    private Value hd;
    private Value tl;
    private Env env;
    private Expression exp;
    private Character ch;
    private Double n;
    private Boolean b;
    private ValueType type;

    public void setValue(Value that) {
        this.hd = that.hd;
        this.tl = that.tl;
        this.env = that.env;
        this.exp = that.exp;
        this.ch = that.ch;
        this.n = that.n;
        this.b = that.b;
        this.type = that.type;
    }

    public Value(Double n) {
        this.type = ValueType.Number;
        this.n = n;
    }

    public Value(Boolean b) {
        this.type = ValueType.Boolean;
        this.b = b;
    }

    public Value(Character ch) {
        this.type = ValueType.Character;
        this.ch = ch;
    }

    public Value(ValueType type) {
        this.type = type;
    }

    public Value(ValueType type, Expression exp, Env env) {
        this.type = type;
        this.exp = exp;
        this.env = env;
    }

    public Value(Value hd, Value tl) {
        this.type = ValueType.List;
        this.hd = hd;
        this.tl = tl;
    }

    public ValueType getType() {
        return type;
    }

    public Expression getExp() {
        return exp;
    }

    public Value getTl() {
        return tl;
    }

    public Boolean getB() {
        return b;
    }

    public Character getCh() {
        return ch;
    }

    public Double getN() {
        return n;
    }

    public Env getEnv() {
        return env;
    }

    public Value getHd() {
        return hd;
    }
}
