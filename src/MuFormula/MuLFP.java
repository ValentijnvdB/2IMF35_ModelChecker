package MuFormula;

public class MuLFP extends FixedPoint {

    public MuLFP(RecursionVariable r, GenericMuFormula child) {
        super(r, child);
        boundByOpposite = false;
        for (RecursionVariable var: unbndVars) {
            if (var.getBoundBy() == BoundBy.NU) {
                boundByOpposite = true;
                break;
            }
        }
    }

    public String toString() {
        return "mu " + r + ". " + child;
    }
}
