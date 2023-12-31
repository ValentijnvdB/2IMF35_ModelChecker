import MuFormula.*;

import java.util.HashMap;
import java.util.HashSet;

public class NaiveChecker extends Checker {

    private StateSpace states;

    // RecVar -> Set of states
    private HashMap<Integer, HashSet<Integer>> A;

    public NaiveChecker(StateSpace states) {
        this.states = states;
    }

    public HashSet<Integer> eval(GenericMuFormula f) {
        iterations = 0;
        A = new HashMap<>();
        return evaluate(f);
    }

    private HashSet<Integer> evaluate(GenericMuFormula f) {

        if (f instanceof RecursionVariable g) return A.get( g.getIndex() );

        else if (f instanceof TrueLiteral) return states.getS();

        else if (f instanceof FalseLiteral) return new HashSet<>();

        else if (f instanceof MuNeg g) {
            HashSet<Integer> st = states.getS();
            st.removeAll( evaluate(g.getChild()) );

            return st;
        }

        else if (f instanceof MuAnd g) {
            HashSet<Integer> ls = evaluate( g.getLeftChild() );
            HashSet<Integer> rs = evaluate( g.getRightChild() );
            ls.retainAll( rs );

            return ls;
        }

        else if (f instanceof MuOr g) {
            HashSet<Integer> ls = evaluate( g.getLeftChild() );
            HashSet<Integer> rs = evaluate( g.getRightChild() );
            ls.addAll( rs );

            return ls;
        }

        else if (f instanceof MuBox g) {
            HashSet<Integer> ts = evaluate( g.getChild() );
            HashSet<Integer> out = states.getS();

            HashSet<Integer> nts = states.getS();
            nts.removeAll( ts );

            HashSet<Integer>[] revAdj = states.revAdj.get(g.getAction());
            if (revAdj != null) {
                for (Integer t : nts) {
                    out.removeAll(revAdj[t]);
                }
            }
            return out;
        }

        else if (f instanceof MuDiamond g) {
            HashSet<Integer> ts = evaluate( g.getChild() );
            HashSet<Integer> out = new HashSet<>();

            HashSet<Integer>[] revAdj = states.revAdj.get(g.getAction());
            if (revAdj != null) {
                for (Integer t : ts) {
                    out.addAll(revAdj[t]);
                }
            }
            return out;
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
            A.put(i, evaluate(subFormula) );
            iterations++;
        } while (! A.get(i).equals(X) );

        return A.get(i);
    }


}
