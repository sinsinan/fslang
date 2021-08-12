package ast;

public class NumCon extends Expression{
    private final double n;

    public NumCon(double n) {
        this.n = n;
    }

    public double getN() {
        return n;
    }
}
