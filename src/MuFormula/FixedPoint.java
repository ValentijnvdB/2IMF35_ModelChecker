package MuFormula;

import java.util.HashSet;

public class FixedPoint extends SingleChildOperator {

    RecursionVariable r;

    public FixedPoint(RecursionVariable r, GenericMuFormula child) {
        super(child);
        this.r = r;
        this.unbndVars = ((HashSet<Integer>) child.getUnbndVars().clone());
        unbndVars.remove( getRecVarIndex() );
    }

    public RecursionVariable getRecVar() {
        return r;
    }

    public int getRecVarIndex() {
        return r.getIndex();
    }

}
