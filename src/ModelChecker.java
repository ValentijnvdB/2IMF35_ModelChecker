import MuFormula.GenericMuFormula;

import java.io.File;
import java.io.FileNotFoundException;
import java.rmi.UnexpectedException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
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



    public static void main(String[] args) {

        Scanner sysInScanner = new Scanner(System.in);
        System.out.println("MuFormula Model Checker");

        boolean quit = false;
        boolean useNaive = false;

        StateSpace states = null;
        GenericMuFormula formula = null;

        NaiveChecker nvc = null;
        ELChecker elc = null;

        while(!quit) {

            try {
                String input = sysInScanner.nextLine();


                List<String> list = new ArrayList<String>();
                Matcher m = Pattern.compile("(\"[^\"]*\"|[^\\s]+)").matcher(input);
                while (m.find()) {
                    list.add(m.group(1).replace("\"", ""));
                }

                if ( isCommand(list.get(0), "load") ) {
                    Scanner file = new Scanner(new File(list.get(1)));
                    states = StateSpaceParser.parseStates(file);
                    file.close();
                    nvc = new NaiveChecker(states);
                    elc = new ELChecker(states);
                    System.out.println("StateSpace loaded");
                } else if ( isCommand(list.get(0), "check") ) {

                    if (states == null || nvc == null || elc == null) {
                        System.out.println("Please load a state space to test.");
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
                }



            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        sysInScanner.close();
    }

    private static boolean isCommand(String input, String expected) {
        return input.toLowerCase().equals(expected);
    }

    private static void printHelp() {
        System.out.println("How to use:");
    }


//        String FilePath = "C:\\Users\\tijnt\\OneDrive\\Documenten\\TUE\\YM2\\Q2 - 2IMF35 Algorithms for Model Checking\\A1\\testcases\\combined\\";
//        String stateFileName = "test.aut";
//        //String formulaFileName = "form9.mcf";
//
//
//        try {
//            File inputFile = new File(FilePath+stateFileName);
//            Scanner input = new Scanner(inputFile);
//            StateSpace states = StateSpaceParser.parseStates(input);
//            input.close();
//
//
//            for (int i = 1; i <= 8; i++) {
//                inputFile = new File(FilePath + "form" + i + ".mcf");
//                input = new Scanner(inputFile);
//                GenericMuFormula formula = FormulaParser.parseFormula(input);
//                input.close();
//
//                //System.out.println(formula);
//
//                NaiveChecker nc = new NaiveChecker(states);
//                ELChecker elc = new ELChecker(states);
//                System.out.println(nc.eval(formula));
//                System.out.println(elc.eval(formula));
//                System.out.println();
//            }
//
//        } catch (FileNotFoundException | ParseException | UnexpectedException e) {
//            e.printStackTrace();
//        }
//
//    }


}