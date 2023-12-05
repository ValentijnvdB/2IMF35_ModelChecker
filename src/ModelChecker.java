import MuFormula.GenericMuFormula;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    private static final String[] QUIT_STRINGS = new String[]{"quit", "q", "exit"};

    private static final String[] BASE_STRINGS = new String[]{"path", "base"};

    private static final String[] HELP_STRINGS = new String[]{"help", "h"};

    public static void main(String[] args) {

        Scanner sysInScanner = new Scanner(System.in);
        System.out.println("======================= Mu-Formula Model Checker =======================");
        System.out.println("                                By                                      ");
        System.out.println("   Valentijn van den Berg, Sander van Heesch and Raymen van Veldhoven   ");
        System.out.println("             For 2IMF35 Algorithms for Model Checking 23/24             ");
        System.out.println("========================================================================");
        printHelp();

        boolean quit = false;
        boolean useNaive = false;

        StateSpace states = null;
        GenericMuFormula formula;

        NaiveChecker nvc = null;
        ELChecker elc = null;

        Path basePath = Paths.get("");

        while(!quit) {

            try {
                String input = sysInScanner.nextLine();


                List<String> list = new ArrayList<>();
                Matcher m = Pattern.compile("(\"[^\"]*\"|[^\\s]+)").matcher(input);
                while (m.find()) {
                    list.add(m.group(1).replace("\"", ""));
                }

                if (isCommand(list.get(0), "load")) {

                    // load state space
                    Scanner file = new Scanner(basePath.resolve(Paths.get(list.get(1))));
                    states = StateSpaceParser.parseStates(file);
                    file.close();

                    nvc = new NaiveChecker(states);
                    elc = new ELChecker(states);
                    System.out.println("StateSpace loaded");

                } else if (isCommand(list.get(0), "eval")) {

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

                } else if (isCommand(list.get(0), "toggle")) {

                    useNaive = !useNaive;
                    System.out.println("Toggled algorithm. Now using: " + (useNaive ? "Naive Algorithm." : "Emerson-Lei Algorithm."));

                } else if (isCommand(list.get(0), "use")) {

                    if (isCommand(list.get(1), EL_STRINGS)) {
                        useNaive = false;
                        System.out.println("Now using Emerson-Lei Algorithm.");

                    } else if (isCommand(list.get(1), NV_STRINGS)) {
                        useNaive = true;
                        System.out.println("Now using Naive Algorithm.");

                    } else {
                        System.out.println("Please use 'el' for the Emerson-Lei Algorithm or 'nv' for Naive Algorithm");
                    }

                } else if (isCommand(list.get(0), BASE_STRINGS)) {
                    Path path = Paths.get(list.get(1));

                    if (!Files.exists(path)) System.out.println("Directory does not exist!");
                    else {
                        basePath = path;
                        System.out.println("Now using base directory: '" + basePath + "'.");
                    }

                } else if (isCommand(list.get(0), HELP_STRINGS)) {
                    printHelp();
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
        System.out.println();
        System.out.println("How to use:");
        System.out.println("Start by loading a state space using the 'load' command. Then check mu formulas on that state space using the 'eval' command.");
        System.out.println();
        System.out.println("Command:    Parameter:     Description:");
        System.out.println("base        dir            Set base directory (alias path).");
        System.out.println("                           After setting all files/paths are relative the the base directory.");
        System.out.println("load        file           Load a state space from file.");
        System.out.println("eval        file           Loads a mu formula from file, then evaluates it for the loaded state space.");
        System.out.println("toggle                     Toggles between Naive and Emerson-Lei Algorithm.");
        System.out.println("use         alg            Set specific algorithm ('nv' for naive and 'el' for Emerson-Lei).");
        System.out.println("help                       Print this help screen (alias h).");
        System.out.println("quit                       Exit the program (alia q, exit).");
        System.out.println();
        System.out.println();
    }



}