package MuFormula;

import java.util.HashSet;

public class TwoChildrenOperator extends GenericMuFormula {

    protected GenericMuFormula leftChild;
    protected GenericMuFormula rightChild;

    public TwoChildrenOperator() {
        super();
    }

    public TwoChildrenOperator(GenericMuFormula lc, GenericMuFormula rc) {
        this.setLeftChild(lc);
        this.setRightChild(rc);
    }

    public GenericMuFormula getLeftChild() {
        return leftChild;
    }

    public GenericMuFormula getRightChild() {
        return rightChild;
    }

    public void setLeftChild(GenericMuFormula lc) {
        this.leftChild = lc;

        unbndVars = (HashSet<RecursionVariable>) leftChild.getUnbndVars().clone();
        if (rightChild != null) unbndVars.addAll(rightChild.unbndVars);
    }

    public void setRightChild(GenericMuFormula rc) {
        this.rightChild = rc;

        unbndVars = (HashSet<RecursionVariable>) rightChild.getUnbndVars().clone();
        if (leftChild != null) unbndVars.addAll(leftChild.unbndVars);
    }
}
