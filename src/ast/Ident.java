package ast;

public class Ident extends Expression{
    private final String id;

    public Ident (String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
