package MuFormula;

public class MuGFP extends FixedPoint {


    public MuGFP(RecursionVariable r, GenericMuFormula child, int lvl) {
        super(r, child, lvl);
        boundByOpposite = false;
        for (RecursionVariable var: unbndVars) {
            if (var.getBoundBy() == BoundBy.MU && var.getBoundAt() == this.level-1) {
                boundByOpposite = true;
                break;
            }
        }
    }



    public String toString() {
        return "nu " + r + ". " + child;
    }
}
