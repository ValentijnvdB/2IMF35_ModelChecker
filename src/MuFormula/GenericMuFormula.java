package MuFormula;

import java.util.HashSet;

public abstract class GenericMuFormula {

    protected HashSet<Integer> unbndVars;

    public GenericMuFormula() {
        unbndVars = new HashSet<>();
    }

    public boolean isOpen() {
        return unbndVars.isEmpty();
    }

    protected HashSet<Integer> getUnbndVars() {
        return unbndVars;
    }

    protected void removeUnbndVar(int v) {
        unbndVars.remove(v);
    }
}
