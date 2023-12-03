package MuFormula;

public class RecursionVariable extends GenericMuFormula {

    private char name;
    private int index;

    public RecursionVariable(char name, int index) {
        this.name = name;
        this.index = index;
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
