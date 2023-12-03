package MuFormula;

public class MuLFP extends FixedPoint {

    public MuLFP(RecursionVariable r, GenericMuFormula child) {
        super(r, child);
    }

    public String toString() {
        return "mu " + r + ". " + child;
    }
}
