package MuFormula;

public class FixedPoint extends SingleChildOperator {

    RecursionVariable r;

    public FixedPoint(RecursionVariable r, GenericMuFormula child) {
        this.r = r;
        this.child = child;
    }

    public RecursionVariable getRecVar() {
        return r;
    }

    public int getRecVarIndex() {
        return r.getIndex();
    }

}
