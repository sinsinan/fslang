package ast;

public class Decln extends Expression {
    private final String name;
    private final Expression val;

    public Decln(String name, Expression val) {
        this.name = name;
        this.val = val;
    }

    public Expression getVal() {
        return val;
    }

    public String getName() {
        return name;
    }
}
