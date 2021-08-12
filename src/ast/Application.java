package ast;

public class Application extends Expression{
    private final Expression fun;
    private final Expression aParam;

    public Application(Expression fun, Expression aParam) {
        this.fun = fun;
        this.aParam = aParam;
    }

    public Expression getaParam() {
        return aParam;
    }

    public Expression getFun() {
        return fun;
    }
}
