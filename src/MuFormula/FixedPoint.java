package MuFormula;

import java.util.HashSet;

public class FixedPoint extends SingleChildOperator {

    protected RecursionVariable r;

    protected boolean boundByOpposite;

    protected int level;

    public FixedPoint(RecursionVariable r, GenericMuFormula child, int lvl) {
        super(child);
        this.r = r;
        this.unbndVars = ((HashSet<RecursionVariable>) child.getUnbndVars().clone());
        unbndVars.remove( r );
        level = lvl;
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
