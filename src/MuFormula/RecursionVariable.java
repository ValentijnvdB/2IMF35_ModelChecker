package MuFormula;

public class RecursionVariable extends GenericMuFormula {

    char name;
    int index;

    public RecursionVariable(char name, int index) {
        this.name = name;
        this.index = index;
    }

    public String toString() {
        return String.valueOf(name);
    }
}
