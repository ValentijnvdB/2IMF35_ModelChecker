import MuFormula.GenericMuFormula;

import java.util.HashSet;

public abstract class Checker {

    public int iterations;

    public abstract HashSet<Integer> eval(GenericMuFormula f);
}
