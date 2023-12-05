package MuFormula;

import java.util.HashSet;

public class FixedPoint extends SingleChildOperator {

    protected RecursionVariable r;

    protected boolean boundByOpposite;

    public FixedPoint(RecursionVariable r, GenericMuFormula child) {
        super(child);
        this.r = r;
        this.unbndVars = ((HashSet<RecursionVariable>) child.getUnbndVars().clone());
        unbndVars.remove( r );
    }

    public boolean isBoundByOpposite() {
        return boundByOpposite;
    }

    public RecursionVariable getRecVar() {
        return r;
    }

    public int getRecVarIndex() {
        return r.getIndex();
    }

}
