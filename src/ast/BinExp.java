package ast;

import lexer.Token;

public class BinExp extends Expression{
    private final Token binOpr;
    private final Expression left;
    private final Expression right;

    public BinExp(Token binOpr, Expression left, Expression right) {
        this.binOpr = binOpr;
        this.left = left;
        this.right = right;
    }

    public Expression getLeft() {
        return left;
    }

    public Expression getRight() {
        return right;
    }

    public Token getBinOpr() {
        return binOpr;
    }
}
