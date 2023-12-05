package MuFormula;

public class MuGFP extends FixedPoint {


    public MuGFP(RecursionVariable r, GenericMuFormula child) {
        super(r, child);
        boundByOpposite = false;
        for (RecursionVariable var: unbndVars) {
            if (var.getBoundBy() == BoundBy.MU) {
                boundByOpposite = true;
                break;
            }
        }
    }



    public String toString() {
        return "nu " + r + ". " + child;
    }
}
