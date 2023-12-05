import MuFormula.*;

import java.util.HashMap;
import java.util.HashSet;

public class ELChecker {

    private StateSpace states;
    private int iterations;

    // RecVar -> Set of states
    private HashMap<Integer, HashSet<Integer>> A;

    public ELChecker(StateSpace states) {
        this.states = states;
        A = new HashMap<>();
    }

    public HashSet<Integer> eval(GenericMuFormula f) {
        init(f);
        iterations = 0;
        HashSet<Integer> out = evaluate(f);
        System.out.println(iterations);
        return out;
    }

    private void init(GenericMuFormula f) {
        if (f instanceof SingleChildOperator g) {
            if (f instanceof MuGFP h) {
                A.put(h.getRecVarIndex(), states.getS());
            } else if (f instanceof MuLFP h) {
                A.put(h.getRecVarIndex(), new HashSet<>());
            }
            init(g.getChild());
        }

        else if (f instanceof TwoChildrenOperator g) {
            init(g.getLeftChild());
            init(g.getRightChild());
        }
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
            HashSet<Integer> ss = states.getS();

            HashSet<Integer> nts = states.getS();
            nts.removeAll( ts );

            HashSet<Integer>[] revAdj = states.revAdj.get(g.getAction());
            for (Integer t: nts) {
                ss.removeAll( revAdj[t] );
            }
            return ss;
        }

        else if (f instanceof MuDiamond g) {
            HashSet<Integer> ts = evaluate( g.getChild() );
            HashSet<Integer> out = new HashSet<>();

            HashSet<Integer>[] revAdj = states.revAdj.get(g.getAction());
            for (Integer t: ts) {
                out.addAll( revAdj[t] );
            }
            return out;
        }

        else if (f instanceof MuGFP g) {

            if (g.isBoundByOpposite()) {
                resetA(g, false);
            }
            int i = g.getRecVarIndex();

            return evalFixed(i, g.getChild());
        }

        else if (f instanceof MuLFP g) {

            if (g.isBoundByOpposite()) {
                resetA(g, true);
            }
            int i = g.getRecVarIndex();

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

    private void resetA(GenericMuFormula f, boolean lfp) {

        if (lfp && f instanceof MuLFP g && g.isOpen()) {
            A.put(g.getRecVarIndex(), new HashSet<>());
        } else if ((!lfp) && f instanceof MuGFP g && g.isOpen()) {
            A.put(g.getRecVarIndex(), states.getS());
        }

        if (f instanceof SingleChildOperator g) {
            resetA(g.getChild(), lfp);
        } else if (f instanceof TwoChildrenOperator g) {
            resetA(g.getLeftChild(), lfp);
            resetA(g.getRightChild(), lfp);
        }
    }


}
