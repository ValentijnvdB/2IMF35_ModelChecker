package MuFormula;

public class MuAnd extends TwoChildrenOperator {

    public MuAnd() {
        super();
    }
    public String toString() {
        return "(" + leftChild + " && " + rightChild + ") ";
    }
}
