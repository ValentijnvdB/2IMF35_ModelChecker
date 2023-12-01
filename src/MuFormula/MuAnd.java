package MuFormula;

public class MuAnd extends TwoChildrenOperator {


    public String toString() {
        return "(" + leftChild + " && " + rightChild + ") ";
    }
}
