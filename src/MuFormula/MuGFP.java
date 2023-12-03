package MuFormula;

public class MuGFP extends FixedPoint {


    public MuGFP(RecursionVariable r, GenericMuFormula child) {
        super(r, child);
    }

    public String toString() {
        return "nu " + r + ". " + child;
    }
}
