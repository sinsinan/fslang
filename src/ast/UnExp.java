package ast;

import lexer.Token;

public class UnExp extends Expression{
    private final Token unOpr;
    private final Expression unArg;

    public UnExp (Token unOpr, Expression unArg) {
        this.unOpr = unOpr;
        this.unArg = unArg;
    }

    public Expression getUnArg() {
        return unArg;
    }

    public Token getUnOpr() {
        return unOpr;
    }
}
