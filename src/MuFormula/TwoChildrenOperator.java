package MuFormula;

public class TwoChildrenOperator extends GenericMuFormula {

    protected GenericMuFormula leftChild;
    protected GenericMuFormula rightChild;

    public GenericMuFormula getLeftChild() {
        return leftChild;
    }

    public GenericMuFormula getRightChild() {
        return rightChild;
    }

    public void setLeftChild(GenericMuFormula lc) {
        this.leftChild = lc;
    }

    public void setRightChild(GenericMuFormula rc) {
        this.rightChild = rc;
    }
}
