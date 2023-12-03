package MuFormula;

public class MuBox extends SingleChildOperator {

    private final String action;

    public MuBox(String action, GenericMuFormula child) {
        super(child);
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    public String toString() {
        return "[" + action + "]" + child;
    }
}
