import MuFormula.*;

import java.rmi.UnexpectedException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

public class FormulaParser {

    private final static HashSet<Character> formulaFirstSet = new HashSet<>(Arrays.asList('t', 'f', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '(', 'm', 'n', '<', '[', '!'));

    private static int  i;
    private static int rid;
    private static char[] input;

    private static HashMap<Character, RecursionVariable> recVariables = new HashMap<>();

    public static GenericMuFormula parseFormula(Scanner scanner) throws ParseException, UnexpectedException {
        return parseFormula(scanner, false);
    }

    public static GenericMuFormula parseFormula(Scanner scanner, boolean suppressLog) throws ParseException, UnexpectedException {

        String in;

        do {
            in = scanner.nextLine();
            i = 0;
            while (i < in.length() && in.charAt(i) == ' ') i++;
        } while ( (in.isEmpty() || !formulaFirstSet.contains( in.charAt(i) )) && scanner.hasNext() );

        if (scanner.hasNext()) {
            StringBuilder sb = new StringBuilder( in );

            do  {
                sb.append(scanner.nextLine());
            } while (scanner.hasNext());

            input = sb.toString().toCharArray();

        } else {
            input = in.toCharArray();
        }

        recVariables = new HashMap<>();
        rid = 0;
        int fid = 0;


        GenericMuFormula f = parse(fid);
        f = toPNF(f);
        if (!suppressLog) printStatistics(f);

        return f;
    }

    //region
    private static void printStatistics(GenericMuFormula f) {
        SimpleLogger.writeln("Formula: " + f);
        SimpleLogger.writeln("");
        SimpleLogger.writeln("Nested Depth: " + computeND(f));
        SimpleLogger.writeln("Alternating Depth: " + computeAD(f, BoundBy.NONE));

        HashMap<String, HashSet<RecursionVariable>> vars = new HashMap<>();
        vars.put("MU", new HashSet<>());
        vars.put("NU", new HashSet<>());
        SimpleLogger.writeln("Dependent Alternating Depth: " + computeDAD(f, BoundBy.NONE, vars));
        SimpleLogger.writeln("");
    }

    /**
     * Computes the nested Depth of formula f
     * @param f formula
     * @return the nested depth of f
     */
    private static int computeND(GenericMuFormula f) {
        if (f instanceof MuDiamond || f instanceof MuBox) {
            return computeND( ((SingleChildOperator) f).getChild() );
        }
        else if (f instanceof TwoChildrenOperator g) {
            return Integer.max( computeND(g.getLeftChild()), computeND(g.getRightChild()) );
        }
        else if (f instanceof FixedPoint g) {
            return 1 + computeND( g.getChild() );
        } else {
            return 0;
        }
    }

    private static int computeAD(GenericMuFormula f, BoundBy lastSeen) {
        if (f instanceof MuDiamond || f instanceof MuBox) {
            return computeAD( ((SingleChildOperator) f).getChild(), lastSeen );
        }
        else if (f instanceof TwoChildrenOperator g) {
            return Integer.max( computeAD(g.getLeftChild(), lastSeen), computeAD(g.getRightChild(), lastSeen) );
        }
        else if (f instanceof MuLFP g) {
            if (lastSeen != BoundBy.MU) {
                return 1 + computeAD(g.getChild(), BoundBy.MU);
            } else {
                return computeAD(g.getChild(), BoundBy.MU);
            }
        }
        else if (f instanceof MuGFP g) {
            if (lastSeen != BoundBy.NU) {
                return 1 + computeAD(g.getChild(), BoundBy.NU);
            } else {
                return computeAD(g.getChild(), BoundBy.NU);
            }
        } else {
            return 0;
        }
    }

    private static int computeDAD(GenericMuFormula f, BoundBy lastSeen, HashMap<String, HashSet<RecursionVariable>> vars) {
        if (f instanceof MuDiamond || f instanceof MuBox) {
            return computeDAD( ((SingleChildOperator) f).getChild(), lastSeen, vars );
        }
        else if (f instanceof TwoChildrenOperator g) {
            return Integer.max( computeDAD(g.getLeftChild(), lastSeen, vars), computeDAD(g.getRightChild(), lastSeen, vars) );
        }
        else if (f instanceof MuLFP g) {
            vars.get("MU").add(g.getRecVar());
            if ( (lastSeen == BoundBy.NONE) || (lastSeen == BoundBy.NU && occursIn(vars.get("NU"), g)) ) {
                return 1 + computeDAD(g.getChild(), BoundBy.MU, vars);
            } else {
                return computeDAD(g.getChild(), BoundBy.MU, vars);
            }
        }
        else if (f instanceof MuGFP g) {
            vars.get("NU").add(g.getRecVar());
            if ( (lastSeen == BoundBy.NONE) || (lastSeen == BoundBy.MU && occursIn(vars.get("MU"), g)) ) {
                return 1 + computeDAD(g.getChild(), BoundBy.NU, vars);
            } else {
                return computeDAD(g.getChild(), BoundBy.NU, vars);
            }
        } else {
            return 0;
        }
    }

    private static boolean occursIn(HashSet<RecursionVariable> vars, FixedPoint f) {

        for (RecursionVariable r: vars) {
            if (f.contains(r)) {
                return true;
            }
        }
        return false;
    }


    private static GenericMuFormula parse(int fid) throws ParseException, UnexpectedException {

        skipWhiteSpaces();

        if (formulaFirstSet.contains(input[i])) {

            return parseFormula(fid);

        } else {
            throw new ParseException( notValidMessage() , i);
        }
    }

    private static GenericMuFormula parseFormula(int fid) throws ParseException, UnexpectedException {
        char c = input[i];

        if (c == 't') return parseTrueLiteral();
        else if (c == 'f') return parseFalseLiteral();
        else if (Character.isUpperCase(c)) return parseRecursionVariable(BoundBy.NONE, Integer.MIN_VALUE);
        else if (c == '(') return parseLogicFormula(fid);
        else if (c == 'm') return parseMuFormula(fid);
        else if (c == 'n') return parseNuFormula(fid);
        else if (c == '[') return parseBoxFormula(fid);
        else if (c == '<') return parseDiamondFormula(fid);
        else if (c == '!') return parseNegation(fid);

        throw new ParseException( notValidMessage(), i );
    }

    //region Sub-parsers

    private static TrueLiteral parseTrueLiteral() throws ParseException {
        expect("true");
        skipWhiteSpaces();
        return new TrueLiteral();
    }

    private static FalseLiteral parseFalseLiteral() throws ParseException {
        expect("false");
        skipWhiteSpaces();
        return new FalseLiteral();
    }

    private static MuNeg parseNegation(int fid) throws ParseException, UnexpectedException {
        expect('!');
        skipWhiteSpaces();
        return new MuNeg(parseFormula(fid));
    }

    private static RecursionVariable parseRecursionVariable(BoundBy bd, int fid) {
        char n = input[i];
        i++;
        skipWhiteSpaces();
        if (recVariables.containsKey(n)) {
            return recVariables.get(n);
        } else {
            RecursionVariable rVar = new RecursionVariable(n, rid++, bd, fid);
            recVariables.put(n, rVar);
            return rVar;
        }
    }

    private static TwoChildrenOperator parseLogicFormula(int fid) throws ParseException, UnexpectedException {
        expect('(');
        skipWhiteSpaces();

        // parse left formula
        GenericMuFormula f;
        if (formulaFirstSet.contains(input[i])) {
            f = parseFormula(fid);
        } else {
            throw new ParseException( notValidMessage() , i);
        }

        // parse operator
        TwoChildrenOperator o;
        if (input[i] == '&' || input[i] == '|') {
            o = parseOperator();
        } else {
            throw new ParseException( notValidMessage() , i);
        }

        // parse right formula
        GenericMuFormula g;
        if (formulaFirstSet.contains(input[i])) {
            g = parseFormula(fid);
        } else {
            throw new ParseException( notValidMessage() , i);
        }

        expect(')');
        skipWhiteSpaces();

        // set children
        o.setLeftChild(f);
        o.setRightChild(g);

        return o;
    }


    private static TwoChildrenOperator parseOperator() throws ParseException {
        char c = input[i];

        if (c == '&') return parseLogicAndOperator();
        if (c == '|') return parseLogicOrOperator();
        throw new ParseException(notValidMessage(), i);
    }

    private static MuAnd parseLogicAndOperator() throws ParseException {
        expect("&&");
        skipWhiteSpaces();
        return new MuAnd();
    }

    private static MuOr parseLogicOrOperator() throws ParseException {
        expect("||");
        skipWhiteSpaces();
        return new MuOr();
    }

    private static MuLFP parseMuFormula(int fid) throws ParseException, UnexpectedException {
        RecursionVariable r;
        GenericMuFormula f;

        expect("mu");
        requiredWhiteSpace();
        if (Character.isUpperCase(input[i])) {
            r = parseRecursionVariable(BoundBy.MU, fid);
        } else {
            throw new ParseException( notValidMessage(), i);
        }
        expect('.');
        skipWhiteSpaces();
        if (formulaFirstSet.contains(input[i])) {
            f = parseFormula(fid+1);
        } else {
            throw new ParseException( notValidMessage(), i);
        }
        return new MuLFP(r, f, fid);
    }

    private static MuGFP parseNuFormula(int fid) throws ParseException, UnexpectedException {
        RecursionVariable r;
        GenericMuFormula f;

        expect("nu");
        requiredWhiteSpace();
        if (Character.isUpperCase(input[i])) {
            r = parseRecursionVariable(BoundBy.NU, fid);
        } else {
            throw new ParseException( notValidMessage(), i);
        }
        expect('.');
        skipWhiteSpaces();
        if (formulaFirstSet.contains(input[i])) {
            f = parseFormula(fid+1);
        } else {
            throw new ParseException( notValidMessage(), i);
        }
        return new MuGFP(r, f, fid);
    }


    private static MuDiamond parseDiamondFormula(int fid) throws ParseException, UnexpectedException {
        expect('<');
        skipWhiteSpaces();

        // parse action name
        String a;
        if ( Character.isLowerCase(input[i]) ) {
            a = parseActionName();
        } else {
            throw new ParseException( notValidMessage(), i);
        }
        expect('>');
        skipWhiteSpaces();

        // parse sub formula
        GenericMuFormula f;
        if (formulaFirstSet.contains(input[i])) {
            f = parseFormula(fid);
        } else {
            throw new ParseException( notValidMessage(), i);
        }
        return new MuDiamond(a, f);
    }

    private static MuBox parseBoxFormula(int fid) throws ParseException, UnexpectedException {
        expect('[');
        skipWhiteSpaces();

        // parse action name
        String a;
        if ( Character.isLowerCase(input[i]) ) {
            a = parseActionName();
        } else {
            throw new ParseException( notValidMessage(), i);
        }
        expect(']');
        skipWhiteSpaces();

        // parse sub formula
        GenericMuFormula f;
        if (formulaFirstSet.contains(input[i])) {
            f = parseFormula(fid);
        } else {
            throw new ParseException( notValidMessage(), i);
        }
        return new MuBox(a, f);
    }

    private static String parseActionName() {
        StringBuilder n = new StringBuilder();
        while ( Character.isLowerCase(input[i]) || Character.isDigit(input[i]) || input[i] == '_' ) {
            n.append(input[i]);
            i++;
        }
        return n.toString();
    }

    //endregion

    /**
     * Simplifies formula f by removing double negations
     */
    private static void simplify(GenericMuFormula f) {

        if (f instanceof SingleChildOperator fs) {
            GenericMuFormula cf = fs.getChild();
            if (cf instanceof MuNeg n1 && n1.getChild() instanceof MuNeg n2) {
                fs.setChild(n2.getChild());
            }

            simplify(fs.getChild());
        }

        else if (f instanceof TwoChildrenOperator fs) {
            GenericMuFormula lcf = fs.getLeftChild();
            GenericMuFormula rcf = fs.getRightChild();
            if (lcf instanceof MuNeg n1 && n1.getChild() instanceof MuNeg n2) {
                fs.setLeftChild(n2.getChild());
            }

            if (rcf instanceof MuNeg n1 && n1.getChild() instanceof MuNeg n2) {
                fs.setRightChild(n2.getChild());
            }

            simplify(fs.getLeftChild());
            simplify(fs.getRightChild());
        }

    }

    private static GenericMuFormula toPNF(GenericMuFormula f) {

        if (f instanceof TwoChildrenOperator parent) {

            GenericMuFormula leftChild = parent.getLeftChild();
            GenericMuFormula rightChild = parent.getRightChild();

            if (leftChild instanceof MuNeg neg) {
                parent.setLeftChild( invert(neg.getChild()) );
            } else {
                parent.setLeftChild( toPNF(leftChild) );
            }

            if (rightChild instanceof MuNeg neg) {
                parent.setRightChild( invert(neg.getChild()) );
            } else {
                parent.setRightChild( toPNF(rightChild) );
            }

        } else if (f instanceof MuNeg parent) {

            return invert( parent.getChild() );

        } else if (f instanceof SingleChildOperator parent) {

            GenericMuFormula child = parent.getChild();

            if (child instanceof MuNeg neg) {
                parent.setChild( invert(neg.getChild()) );
            } else {
                parent.setChild( toPNF(child) );
            }

        }

        return f;

    }

    private static GenericMuFormula invert(GenericMuFormula f) {
        if (f instanceof MuNeg g) {
            return g.getChild();
        }
        else if (f instanceof TrueLiteral) {
            return new FalseLiteral();
        }
        else if (f instanceof FalseLiteral) {
            return new TrueLiteral();
        }
        else if (f instanceof MuAnd g) {
            return new MuOr( invert(g.getLeftChild()), invert(g.getRightChild()) );
        }
        else if (f instanceof MuOr g) {
            return new MuAnd( invert(g.getLeftChild()), invert(g.getRightChild()) );
        }
        else if (f instanceof MuBox g) {
            return new MuDiamond( g.getAction(), invert(g.getChild()) );
        }
        else if (f instanceof MuDiamond g) {
            return new MuBox( g.getAction(), invert(g.getChild()) );
        }
        else if (f instanceof MuLFP g) {
            negateVar(g, g.getRecVar());
            return new MuGFP( g.getRecVar(), invert(g.getChild()), g.getLevel() );
        }
        else if (f instanceof MuGFP g) {
            negateVar(g, g.getRecVar());
            return new MuLFP( g.getRecVar(), invert(g.getChild()), g.getLevel() );
        } else {
            return new MuNeg(f);
        }
    }

    private static void negateVar(GenericMuFormula f, RecursionVariable X) {
        if (f instanceof SingleChildOperator g) {
            if (g.getChild() == X) {
                g.setChild(new MuNeg(X));
            } else {
                negateVar(g.getChild(), X);
            }
        }
        else if (f instanceof TwoChildrenOperator g) {
            if (g.getLeftChild() == X) {
                g.setLeftChild(new MuNeg(X));
            } else if (g.getRightChild() == X) {
                g.setRightChild(new MuNeg(X));
            } else {
                negateVar(g.getLeftChild(), X);
                negateVar(g.getRightChild(), X);
            }
        }
    }

    //region Helpers

    private static void skipWhiteSpaces() {
        while (i < input.length && input[i] == ' ') i++;
    }

    private static void requiredWhiteSpace() throws ParseException {
        if (input[i] != ' ') {
            throw new ParseException("Unexpected character '" + input[i] + "', required whitespace!", i);
        } else {
            skipWhiteSpaces();
        }
    }


    private static void expect(String e) throws ParseException {
        char[] eca = e.toCharArray();
        for (char c: eca) {
            expect(c);
        }
    }

    private static void expect(char e) throws ParseException {
        if (input[i] != e) {
            throw new ParseException("Unexpected character '" + input[i] +"', expected '" + e + "'!", i);
        }
        i++;
    }

    private static String notValidMessage() {
        return "Character " + input[i] + " is not a valid character.";
    }

    //endregion
}
