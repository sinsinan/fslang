package ast;

public class BoolCon  extends Expression{
    private final Boolean b;

    public BoolCon(Boolean b) {
        this.b = b;
    }

    public Boolean getB() {
        return b;
    }
}
