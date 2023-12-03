import MuFormula.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class NaiveChecker {

    private StateSpace states;

    // RecVar -> Set of states
    private HashMap<Integer, HashSet<Integer>> A;

    public NaiveChecker(StateSpace states) {
        this.states = states;
        A = new HashMap<>();
    }

    public HashSet<Integer> eval(GenericMuFormula f) {

        if (f instanceof RecursionVariable g) return A.get( g.getIndex() );

        else if (f instanceof TrueLiteral) return states.getS();

        else if (f instanceof MuNeg g) {
            HashSet<Integer> st = states.getS();
            st.removeAll( eval(g.getChild()) );

            return st;
        }

        else if (f instanceof MuAnd g) {
            HashSet<Integer> ls = eval( g.getLeftChild() );
            HashSet<Integer> rs = eval( g.getRightChild() );
            ls.retainAll( rs );

            return ls;
        }

        else if (f instanceof MuBox g) {
            HashSet<Integer> ts = eval( g.getChild() );
            HashSet<Integer> ss = states.getS();

            HashSet<Integer> nts = states.getS();
            nts.removeAll( ts );

            HashSet<Integer>[] revAdj = states.revAdj.get(g.getAction());
            for (Integer t: nts) {
                ss.removeAll( revAdj[t] );
            }
            return ss;
        }

        else if (f instanceof MuGFP g) {

            int i = g.getRecVarIndex();
            A.put(i, states.getS());

            return evalFixed(i, g.getChild());
        }

        else if (f instanceof MuLFP g) {

            int i = g.getRecVarIndex();
            A.put(i, new HashSet<>());

            return evalFixed(i, g.getChild());

        }

        throw new RuntimeException("Something went wrong!");
    }

    private HashSet<Integer> evalFixed(int i, GenericMuFormula subFormula) {
        HashSet<Integer> X;

        do {
            X = A.get(i);
            A.put(i, eval(subFormula) );
        } while (! A.get(i).equals(X) );

        return A.get(i);
    }


}
