package MuFormula;

public class RecursionVariable extends GenericMuFormula {

    private final char name;
    private final int index;

    public RecursionVariable(char name, int index) {
        super();
        this.name = name;
        this.index = index;
        this.unbndVars.add(index);
    }

    public char getName() {
        return this.name;
    }

    public int getIndex() {
        return this.index;
    }


    public String toString() {
        return String.valueOf(name);
    }
}
