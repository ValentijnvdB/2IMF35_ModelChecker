package MuFormula;

public class MuLFP extends SingleChildOperator {

    RecursionVariable r;

    public MuLFP(RecursionVariable r, GenericMuFormula child) {
        this.r = r;
        this.child = child;
    }

    public String toString() {
        return "mu " + r + ". " + child;
    }
}
