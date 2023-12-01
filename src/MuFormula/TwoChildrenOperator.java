package MuFormula;

public class TwoChildrenOperator extends GenericMuFormula {

    public GenericMuFormula leftChild;
    public GenericMuFormula rightChild;

    public void setLeftChild(GenericMuFormula lc) {
        this.leftChild = lc;
    }

    public void setRightChild(GenericMuFormula rc) {
        this.rightChild = rc;
    }
}
