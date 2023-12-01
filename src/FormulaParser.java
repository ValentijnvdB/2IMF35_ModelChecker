import MuFormula.*;

import java.rmi.UnexpectedException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;

public class FormulaParser {

    private final static HashSet<Character> formulaFirstSet = new HashSet<>(Arrays.asList('t', 'f', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '(', 'm', 'n', '<', '['));

    private static int  i;
    private static int r;
    private static char[] input;

    public static GenericMuFormula parseFormula(Scanner scanner) throws ParseException, UnexpectedException {

        i = 0;
        input = scanner.nextLine().toCharArray();

        return parse();
    }

    private static GenericMuFormula parse() throws ParseException, UnexpectedException {

        skipWhiteSpaces();

        if (formulaFirstSet.contains(input[i])) {

            return parseFormula();

        } else {
            throw new ParseException( notValidMessage() , i);
        }
    }

    private static GenericMuFormula parseFormula() throws ParseException, UnexpectedException {
        char c = input[i];

        if (c == 't') return parseTrueLiteral();
        else if (c == 'f') return parseFalseLiteral();
        else if (Character.isUpperCase(c)) return parseRecursionVariable();
        else if (c == '(') return parseLogicFormula();
        else if (c == 'm') return parseMuFormula();
        else if (c == 'n') return parseNuFormula();
        else if (c == '[') return parseBoxFormula();
        else if (c == '<') return parseDiamondFormula();

        throw new ParseException( notValidMessage(), i );
    }

    //region Sub-parsers

    private static TrueLiteral parseTrueLiteral() throws ParseException {
        expect("true");
        skipWhiteSpaces();
        return new TrueLiteral();
    }

    private static MuNeg parseFalseLiteral() throws ParseException {
        expect("false");
        skipWhiteSpaces();
        return new MuNeg(new TrueLiteral());
    }

    private static RecursionVariable parseRecursionVariable() {
        char n = input[i];
        i++;
        skipWhiteSpaces();
        return new RecursionVariable(n, r++);
    }

    private static GenericMuFormula parseLogicFormula() throws ParseException, UnexpectedException {
        expect('(');
        skipWhiteSpaces();

        // parse left formula
        GenericMuFormula f;
        if (formulaFirstSet.contains(input[i])) {
            f = parseFormula();
        } else {
            throw new ParseException( notValidMessage() , i);
        }

        // parse operator
        GenericMuFormula o;
        if (input[i] == '&' || input[i] == '|') {
            o = parseOperator();
        } else {
            throw new ParseException( notValidMessage() , i);
        }

        // parse right formula
        GenericMuFormula g;
        if (formulaFirstSet.contains(input[i])) {
            g = parseFormula();
        } else {
            throw new ParseException( notValidMessage() , i);
        }

        expect(')');
        skipWhiteSpaces();

        // set children
        if (o instanceof MuNeg neg) {
            GenericMuFormula a = neg.getChild();
            if (a instanceof MuAnd and) {
                and.setLeftChild( new MuNeg(f) );
                and.setRightChild( new MuNeg(g) );
            } else {
                throw new UnexpectedException("'MuAnd' object expected, instead got '" + a.getClass().toString() + "'!");
            }

        } else if (o instanceof MuAnd and)  {
            and.setLeftChild(f);
            and.setRightChild(g);

        } else {
            throw new UnexpectedException("'MuAnd' or 'MuNeg' object expected, instead got '" + o.getClass().toString() + "'!");
        }

        return o;
    }


    private static GenericMuFormula parseOperator() throws ParseException {
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

    private static MuNeg parseLogicOrOperator() throws ParseException {
        expect("||");
        skipWhiteSpaces();
        return new MuNeg(new MuAnd());
    }

    private static MuLFP parseMuFormula() throws ParseException, UnexpectedException {
        RecursionVariable r;
        GenericMuFormula f;

        expect("mu");
        requiredWhiteSpace();
        if (Character.isUpperCase(input[i])) {
            r = parseRecursionVariable();
        } else {
            throw new ParseException( notValidMessage(), i);
        }
        expect('.');
        skipWhiteSpaces();
        if (formulaFirstSet.contains(input[i])) {
            f = parseFormula();
        } else {
            throw new ParseException( notValidMessage(), i);
        }
        return new MuLFP(r, f);
    }

    private static MuGFP parseNuFormula() throws ParseException, UnexpectedException {
        RecursionVariable r;
        GenericMuFormula f;

        expect("nu");
        requiredWhiteSpace();
        if (Character.isUpperCase(input[i])) {
            r = parseRecursionVariable();
        } else {
            throw new ParseException( notValidMessage(), i);
        }
        expect('.');
        skipWhiteSpaces();
        if (formulaFirstSet.contains(input[i])) {
            f = parseFormula();
        } else {
            throw new ParseException( notValidMessage(), i);
        }
        return new MuGFP(r, f);
    }


    private static MuNeg parseDiamondFormula() throws ParseException, UnexpectedException {
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
            f = parseFormula();
        } else {
            throw new ParseException( notValidMessage(), i);
        }
        return new MuNeg(new MuBox(a, new MuNeg(f)));
    }

    private static MuBox parseBoxFormula() throws ParseException, UnexpectedException {
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
            f = parseFormula();
        } else {
            throw new ParseException( notValidMessage(), i);
        }
        return new MuBox(a, f);
    }

    private static String parseActionName() {
        StringBuilder n = new StringBuilder();
        while ( Character.isLowerCase(input[i]) ) {
            n.append(input[i]);
            i++;
        }
        return n.toString();
    }

    //endregion

    //region Helpers

    private static void skipWhiteSpaces() {
        while (i < input.length && input[i] == ' ') i++;
    }

    private static void requiredWhiteSpace() throws ParseException {
        if (input[i] != ' ') {
            throw new ParseException("Unexpected character " + input[i] + ", required whitespace!", i);
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
            throw new ParseException("Unexpected character " + input[i] +", expected " + e + "!", i);
        }
        i++;
    }

    private static String notValidMessage() {
        return "Character " + input[i] + " is not a valid character.";
    }

    //endregion
}
