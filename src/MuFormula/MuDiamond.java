package MuFormula;

public class MuDiamond extends SingleChildOperator {

    private final String action;

    public MuDiamond(String action, GenericMuFormula child) {
        super(child);
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    public String toString() {
        return "<" + action + ">" + child;
    }
}
