package ast;

public class IfExp extends Expression {
    private final Expression e1;
    private final Expression e2;
    private final Expression e3;

    public IfExp(Expression e1, Expression e2, Expression e3) {
        this.e1 = e1;
        this.e2 = e2;
        this.e3 = e3;
    }

    public Expression getE1() {
        return e1;
    }

    public Expression getE2() {
        return e2;
    }

    public Expression getE3() {
        return e3;
    }
}
