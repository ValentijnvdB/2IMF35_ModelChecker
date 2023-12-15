import MuFormula.GenericMuFormula;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.exit;

/* TODO:
 *      Naive checker
 *      EL Checker
 *      CMD Interface
 */

public class ModelChecker {

    private static final String[] EL_STRINGS = new String[]{"el", "emerson-lei", "e"};
    private static final String[] NV_STRINGS = new String[]{"n", "naive", "nv"};

    private static final String[] QUIT_STRINGS = new String[]{"quit", "q", "exit"};

    private static final String[] BASE_STRINGS = new String[]{"cd", "path", "base"};

    private static final String[] HELP_STRINGS = new String[]{"help", "h"};

    private static StateSpace states = null;
    private static NaiveChecker nvc = null;
    private static ELChecker elc = null;

    public static void main(String[] args) {

        if (args.length > 0) {

            String[] values;

            Path basePath = Paths.get(System.getProperty("user.dir"));
            Path[] stateSpaces = new Path[]{};
            Path[] formulas = new Path[]{};
            boolean all = false;
            boolean useNaive = false;
            int repeat = 1;


            for (String arg : args) {

                if (arg.startsWith("-states")) {
                    values = arg.split("=")[1].split(",");

                    stateSpaces = new Path[values.length];

                    for (int i = 0; i < values.length; i++) {
                        stateSpaces[i] = Paths.get(values[i]);
                        if ( !Files.exists(stateSpaces[i]) ) {
                            System.out.println(values[i] + " was not found!");
                            exit(1);
                        }
                    }

                }

                else if (arg.startsWith("-eval")) {
                    values = arg.split("=")[1].split(",");

                    formulas = new Path[values.length];

                    for (int i = 0; i < values.length; i++) {
                        formulas[i] = Paths.get(values[i]);
                        if ( !Files.exists(formulas[i]) ) {
                            System.out.println(values[i] + " was not found!");
                            exit(1);
                        }
                    }
                }

                else if (arg.startsWith("-out")) {
                    try {
                        String file = arg.split("=")[1];
                        SimpleLogger.outputToFile(new File(file));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                else if (arg.startsWith("-all")) {
                    all = true;
                }

                else if (arg.startsWith("-use")) {
                    String alg = arg.split("=")[1];

                    if (contains(alg, NV_STRINGS)) useNaive = true;
                    else if (contains(alg, EL_STRINGS)) useNaive = false;
                    else System.out.println("Algorithm unclear, using Emerson-Lei! To specify : use 'nv' for naive or 'el' for Emerson-Lei");
                }

                else if (arg.startsWith("-repeat")) {
                    repeat = Integer.parseInt( arg.split("=")[1] );
                }

            }

            try {
                evaluateAll(stateSpaces, formulas, useNaive, all, repeat);
            } catch (Exception e) {
                e.printStackTrace();
            }

            SimpleLogger.close();

        } else {
            openCLI();
        }

    }

    private static void evaluateAll(Path[] stateSpaces, Path[] formulas, boolean useNaive, boolean all, int repeat) throws IOException, ParseException {

        Scanner scanner;
        GenericMuFormula formula;
        long startTime, endTime;
        double[] runTimes;
        HashSet<Integer> out = null;

        Checker checker;

        SimpleLogger.writeln("Using " + (useNaive ? "Naive Algorithm." : "Emerson-Lei Algorithm."));

        if (repeat > 1) warmUp(stateSpaces[0], formulas[0], useNaive);

        for (Path stateSpace : stateSpaces) {

            SimpleLogger.writeln("");
            SimpleLogger.writeln("LOADING IN NEW STATESPACE: " + stateSpace.getFileName());
            scanner = new Scanner(stateSpace);
            states = StateSpaceParser.parseStates(scanner);
            scanner.close();
            SimpleLogger.writeln("StateSpace loaded");
            SimpleLogger.writeln("Starting evaluation phase...");
            SimpleLogger.writeln("");
            SimpleLogger.writeln("");

            if (useNaive) checker = new NaiveChecker(states);
            else checker = new ELChecker(states);

            runTimes = new double[repeat];

            for (Path form : formulas) {

                SimpleLogger.writeln("Evaluating " + form.getFileName());

                scanner = new Scanner(form);
                formula = FormulaParser.parseFormula(scanner);
                scanner.close();

                for (int i = 0; i < repeat; i++) {

                    startTime = System.nanoTime();
                    out = checker.eval(formula);
                    endTime = System.nanoTime();

                    runTimes[i] = (endTime - startTime) / 1000000.0;
                }

                SimpleLogger.writeln("Number of fixed point iterations: " + checker.iterations);
                SimpleLogger.writeln("");

                if (all) {
                    SimpleLogger.writeln("States that satisfy the formula: " + out);
                } else {
                    String sat = out.contains(states.initial) ? "satisfies " : "does NOT satisfy ";
                    SimpleLogger.writeln("The initial state " + states.initial + " " + sat + "the formula.");
                }

                SimpleLogger.writeln("");

                if (repeat == 1) {
                    SimpleLogger.writeln("Evaluation time: " + runTimes[0] + " milliseconds");
                    SimpleLogger.writeln("");
                } else {
                    SimpleLogger.writeln("Evaluation times:");
                    for (int i = 0; i < repeat; i++) {
                        SimpleLogger.writeln("Run " + i + ": " + runTimes[i] + " milliseconds");
                    }
                    SimpleLogger.writeln("");
                }
                if (SimpleLogger.toFile()) System.out.println("    Completed " + form.getFileName());
            }
            if (SimpleLogger.toFile()) System.out.println("COMPLETED: " + stateSpace.getFileName());
        }
    }

    private static void warmUp(Path stateSpace, Path form, boolean useNaive) {
        try {
            Scanner scanner = new Scanner(stateSpace);
            StateSpace ss = StateSpaceParser.parseStates(scanner);
            scanner.close();

            Checker checker;

            if (useNaive) checker = new NaiveChecker(ss);
            else checker = new ELChecker(ss);

            scanner = new Scanner(form);
            GenericMuFormula formula = FormulaParser.parseFormula(scanner, true);
            scanner.close();

            checker.eval(formula);
        } catch (Exception e) {
            System.out.println("Failed during warm-up");
        }
    }

    private static void openCLI() {

        Scanner sysInScanner = new Scanner(System.in);
        printWelcome();

        boolean quit = false;
        boolean useNaive = false;


        Path basePath = Paths.get(System.getProperty("user.dir"));

        while(!quit) {

            try {
                System.out.print(basePath + " > ");
                String input = sysInScanner.nextLine();


                List<String> list = new ArrayList<>();
                Matcher m = Pattern.compile("(\"[^\"]*\"|[^\\s]+)").matcher(input);
                while (m.find()) {
                    list.add(m.group(1).replace("\"", ""));
                }

                if (list.isEmpty()) continue;

                String command = list.get(0);

                if (equals(command, "load")) load(basePath, list.get(1));

                else if (equals(command, "eval"))
                    eval(basePath, list.get(1), useNaive, (list.size() > 2 && list.get(2).equals("-all")) );

                else if (equals(command, "toggle")) {

                    useNaive = !useNaive;
                    System.out.println("Toggled algorithm. Now using: " + (useNaive ? "Naive Algorithm." : "Emerson-Lei Algorithm."));

                } else if (equals(command, "use")) {

                    if (contains(list.get(1), EL_STRINGS)) {
                        useNaive = false;
                        System.out.println("Now using Emerson-Lei Algorithm.");

                    } else if (contains(list.get(1), NV_STRINGS)) {
                        useNaive = true;
                        System.out.println("Now using Naive Algorithm.");

                    } else {
                        System.out.println("Please use 'el' for the Emerson-Lei Algorithm or 'nv' for Naive Algorithm");
                    }

                } else if (contains(list.get(0), BASE_STRINGS)) {

                    if (list.get(1).equals("..")) {
                        basePath = basePath.getParent();
                    } else {

                        Path path = Paths.get(list.get(1));

                        if (Files.exists(path)) {
                            basePath = path;
                        } else if (Files.exists(basePath.resolve(path))) {
                            basePath = basePath.resolve(path);
                        } else {
                            System.out.println("Directory does not exist!");
                        }
                    }


                } else if (contains(list.get(0), HELP_STRINGS)) {
                    printHelp();
                } else if (contains(list.get(0), QUIT_STRINGS)) {
                    quit = true;
                } else {
                    System.out.println("Command does not exist! Use 'help' for a list of commands.");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        sysInScanner.close();
    }

    private static void load(Path basePath, String fileLoc) {
        try {
            // load state space
            Scanner file = new Scanner(basePath.resolve(Paths.get(fileLoc)));
            states = StateSpaceParser.parseStates(file);
            file.close();

            nvc = new NaiveChecker(states);
            elc = new ELChecker(states);
            System.out.println("StateSpace loaded");

        } catch (NoSuchFileException e) {
            System.out.println("File: '" + basePath.resolve(Paths.get(fileLoc)) + "' not found!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void eval(Path basePath, String fileLoc, boolean useNaive, boolean all) {
        try {
            if (states == null || nvc == null || elc == null) {
                System.out.println("Please load a state space first.");
            } else {
                Scanner file = new Scanner(basePath.resolve(Paths.get(fileLoc)));
                GenericMuFormula formula = FormulaParser.parseFormula(file);
                file.close();

                HashSet<Integer> out;
                long startTime, endTime;

                if (useNaive) {
                    System.out.println("Using Naive Algorithm.");
                    startTime = System.nanoTime();
                    out = nvc.eval(formula);
                    endTime = System.nanoTime();


                } else {
                    System.out.println("Using Emerson-Lei Algorithm.");
                    startTime = System.nanoTime();
                    out = elc.eval(formula);
                    endTime = System.nanoTime();
                }

                System.out.println("Number of fixed point iterations: " + (useNaive ? nvc.iterations : elc.iterations));

                if (all) {
                    System.out.println("States that satisfy the formula: " + out);
                } else {
                    String sat = out.contains(states.initial) ? "satisfies " : "does not satisfy ";
                    System.out.println("The initial state " + states.initial + " " + sat + "the formula.");
                }

                double runtime = (endTime - startTime) / 1000000.0;
                System.out.println("Evaluation time: " + runtime + " milliseconds");

            }
        } catch (NoSuchFileException e) {
            System.out.println("File: '" + basePath.resolve(Paths.get(fileLoc)) + "' not found!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private static boolean contains(String input, String[] expected) {
        for (String e: expected) {
            if (equals(input, e)) return true;
        }
        return false;
    }

    private static boolean equals(String input, String expected) {
        return input.toLowerCase().equals(expected);
    }

    private static void printWelcome() {
        System.out.println("======================= Mu-Formula Model Checker =======================");
        System.out.println("                                By                                      ");
        System.out.println("   Valentijn van den Berg, Sander van Heesch and Raymen van Veldhoven   ");
        System.out.println("             For 2IMF35 Algorithms for Model Checking 23/24             ");
        System.out.println("========================================================================");
        printHelp();
    }

    private static void printHelp() {
        System.out.println();
        System.out.println("How to use:");
        System.out.println("Start by loading a state space using the 'load' command. Then check mu formulas on that state space using the 'eval' command.");
        System.out.println();
        System.out.println("Command:    Parameter:     Description:");
        System.out.println("cd          dir            Set base directory (alias base, path).");
        System.out.println("load        file           Load a state space from file.");
        System.out.println("eval        file           Loads a mu formula from file, then evaluates it for the loaded state space. Use '-all' flag (at the end) to output all states that satisfy the formula.");
        System.out.println("toggle                     Toggles between Naive and Emerson-Lei Algorithm.");
        System.out.println("use         alg            Set specific algorithm ('nv' for naive and 'el' for Emerson-Lei).");
        System.out.println("help                       Print this help screen (alias h).");
        System.out.println("quit                       Exit the program (alia q, exit).");
        System.out.println();
        System.out.println();
    }



}