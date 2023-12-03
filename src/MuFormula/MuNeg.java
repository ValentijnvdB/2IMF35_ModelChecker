package MuFormula;

public class MuNeg extends SingleChildOperator {


    public MuNeg(GenericMuFormula child) {
        super(child);
    }

    public String toString() {
        return "!" + child;
    }

}
