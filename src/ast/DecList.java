package ast;

public class DecList extends Expression {
    private final Boolean recursive;
    private final Expression hd;
    private final Expression tl;

    public DecList(Boolean recursive, Expression hd, Expression tl) {
        this.recursive = recursive;
        this.hd = hd;
        this.tl = tl;
    }

    public Boolean getRecursive() {
        return recursive;
    }

    public Expression getHd() {
        return hd;
    }

    public Expression getTl() {
        return tl;
    }
}
