package MuFormula;

public class SingleChildOperator extends GenericMuFormula {

    protected GenericMuFormula child;

    public SingleChildOperator(GenericMuFormula child) {
        super();
        this.setChild(child);
        this.unbndVars = child.getUnbndVars();
    }

    public void setChild(GenericMuFormula child) {
        this.child = child;
    }

    public GenericMuFormula getChild() {
        return this.child;
    }
}
