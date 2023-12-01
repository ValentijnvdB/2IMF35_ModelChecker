package MuFormula;

public class MuBox extends SingleChildOperator {

    private final String action;

    public MuBox(String action, GenericMuFormula child) {
        this.action = action;
        this.child = child;
    }

    public String toString() {
        return "[" + action + "]" + child;
    }
}
