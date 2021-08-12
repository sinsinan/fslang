package ast;

public class Block extends Expression{
    private final Expression decs;
    private final Expression exp;

    public Block (Expression decs, Expression exp) {
        this.decs = decs;
        this.exp = exp;
    }

    public Expression getDecs() {
        return decs;
    }

    public Expression getExp() {
        return exp;
    }
}
