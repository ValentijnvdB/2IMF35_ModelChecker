package MuFormula;

public class RecursionVariable extends GenericMuFormula {

    private final char name;
    private final int index;
    private final BoundBy boundBy;
    private final int boundAt;

    public RecursionVariable(char name, int index, BoundBy bb, int ba) {
        super();
        this.name = name;
        this.index = index;
        this.unbndVars.add(this);
        boundBy = bb;
        boundAt = ba;
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

    public int getBoundAt() {
        return boundAt;
    }


    public String toString() {
        return String.valueOf(name);
    }
}
