package executor;

public class Env {
    private final String id;
    private Value v;
    private Env next;

    public Env(String id, Value v, Env next) {
        this.id = id;
        this.v = v;
        this.next = next;
    }

    public Value getV() {
        return v;
    }

    public Env getNext() {
        return next;
    }

    public String getId() {
        return id;
    }

    public void setNext(Env next) {
        this.next = next;
    }

    public void setV(Value v) {
        this.v = v;
    }
}
