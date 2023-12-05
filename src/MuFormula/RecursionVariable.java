package MuFormula;

public class RecursionVariable extends GenericMuFormula {

    private final char name;
    private final int index;
    private BoundBy boundBy;

    public RecursionVariable(char name, int index, BoundBy bd) {
        super();
        this.name = name;
        this.index = index;
        this.unbndVars.add(this);
        boundBy = bd;
    }

    public char getName() {
        return this.name;
    }

    public int getIndex() {
        return this.index;
    }

    public BoundBy getBoundBy() {
        return boundBy;
    }


    public String toString() {
        return String.valueOf(name);
    }
}
