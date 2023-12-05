package MuFormula;

public class MuOr extends TwoChildrenOperator {

    public MuOr() {
        super();
    }

    public MuOr(GenericMuFormula lc, GenericMuFormula rc) {
        super(lc, rc);
    }
    public String toString() {
        return "(" + leftChild + " || " + rightChild + ") ";
    }
}
