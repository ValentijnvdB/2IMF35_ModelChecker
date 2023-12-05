import MuFormula.GenericMuFormula;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* TODO:
 *      Naive checker
 *      EL Checker
 *      CMD Interface
 */

public class ModelChecker {

    private static final String[] EL_STRINGS = new String[]{"el", "emerson-lei", "e"};
    private static final String[] NV_STRINGS = new String[]{"n", "naive", "nv"};

    private static final String[] QUIT_STRINGS = new String[]{"quit", "q"};

    public static void main(String[] args) {

        Scanner sysInScanner = new Scanner(System.in);
        System.out.println("MuFormula Model Checker");

        boolean quit = false;
        boolean useNaive = false;

        StateSpace states = null;
        GenericMuFormula formula;

        NaiveChecker nvc = null;
        ELChecker elc = null;

        while(!quit) {

            try {
                String input = sysInScanner.nextLine();


                List<String> list = new ArrayList<>();
                Matcher m = Pattern.compile("(\"[^\"]*\"|[^\\s]+)").matcher(input);
                while (m.find()) {
                    list.add(m.group(1).replace("\"", ""));
                }

                if ( isCommand(list.get(0), "load") ) {

                    // load state space
                    Scanner file = new Scanner(new File(list.get(1)));
                    states = StateSpaceParser.parseStates(file);
                    file.close();

                    nvc = new NaiveChecker(states);
                    elc = new ELChecker(states);
                    System.out.println("StateSpace loaded");

                } else if ( isCommand(list.get(0), "check") ) {

                    if (states == null || nvc == null || elc == null) {
                        System.out.println("Please load a state space first.");
                    } else {
                        Scanner file = new Scanner(new File(list.get(1)));
                        formula = FormulaParser.parseFormula(file);
                        file.close();
                        if (useNaive) {
                            System.out.println("States: " + nvc.eval(formula));
                        } else {
                            System.out.println("States: " + elc.eval(formula));
                        }
                    }

                } else if ( isCommand(list.get(0), "toggle") ) {

                    useNaive = !useNaive;
                    System.out.println("Toggled algorithm. Now using: " + (useNaive ? "Naive Algorithm." : "Emerson-Lei Algorithm."));

                } else if ( isCommand(list.get(0), "use") ) {

                    if (isCommand(list.get(1), EL_STRINGS)) {
                        useNaive = false;
                        System.out.println("Now using Emerson-Lei Algorithm.");

                    } else if (isCommand(list.get(1), NV_STRINGS)) {
                        useNaive = true;
                        System.out.println("Now using Naive Algorithm.");

                    } else {
                        System.out.println("Please use 'el' for the Emerson-Lei Algorithm or 'nv' for Naive Algorithm");
                    }

                } else if (isCommand(list.get(0), QUIT_STRINGS)) {
                    quit = true;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        sysInScanner.close();
    }

    private static boolean isCommand(String input, String[] expected) {
        for (String e: expected) {
            if (isCommand(input, e)) return true;
        }
        return false;
    }

    private static boolean isCommand(String input, String expected) {
        return input.toLowerCase().equals(expected);
    }

    private static void printHelp() {
        System.out.println("How to use:");
    }



}