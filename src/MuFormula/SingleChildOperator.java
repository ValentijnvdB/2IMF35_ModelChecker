package MuFormula;

public class SingleChildOperator extends GenericMuFormula {

    public GenericMuFormula child;

    public SingleChildOperator() {

    }

    public SingleChildOperator(GenericMuFormula child) {
        this.child = child;
    }

    public void setChild(GenericMuFormula child) {
        this.child = child;
    }

    public GenericMuFormula getChild() {
        return this.child;
    }
}
