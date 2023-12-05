package MuFormula;

public class MuAnd extends TwoChildrenOperator {

    public MuAnd() {
        super();
    }

    public MuAnd(GenericMuFormula lc, GenericMuFormula rc) {
        super(lc, rc);
    }

    public String toString() {
        return "(" + leftChild + " && " + rightChild + ") ";
    }
}
