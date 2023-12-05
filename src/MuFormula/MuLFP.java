package MuFormula;

public class MuLFP extends FixedPoint {

    public MuLFP(RecursionVariable r, GenericMuFormula child, int lvl) {
        super(r, child, lvl);
        boundByOpposite = false;
        for (RecursionVariable var: unbndVars) {
            if (var.getBoundBy() == BoundBy.NU && var.getBoundAt() == this.level-1) {
                boundByOpposite = true;
                break;
            }
        }
    }

    public String toString() {
        return "mu " + r + ". " + child;
    }
}
