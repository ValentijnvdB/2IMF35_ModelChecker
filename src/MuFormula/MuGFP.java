package MuFormula;

public class MuGFP extends SingleChildOperator {

    RecursionVariable r;

    public MuGFP(RecursionVariable r, GenericMuFormula child) {
        this.r = r;
        this.child = child;
    }

    public String toString() {
        return "nu " + r + ". " + child;
    }
}
