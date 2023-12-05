import MuFormula.GenericMuFormula;

import java.io.File;
import java.io.FileNotFoundException;
import java.rmi.UnexpectedException;
import java.text.ParseException;
import java.util.Scanner;

/* TODO:
 *      Naive checker
 *      EL Checker
 *      CMD Interface
 */

public class ModelChecker {



    public static void main(String[] args) {

        String FilePath = "C:\\Users\\tijnt\\OneDrive\\Documenten\\TUE\\YM2\\Q2 - 2IMF35 Algorithms for Model Checking\\A1\\testcases\\combined\\";
        String stateFileName = "test.aut";
        //String formulaFileName = "form9.mcf";


        try {
            File inputFile = new File(FilePath+stateFileName);
            Scanner input = new Scanner(inputFile);
            StateSpace states = StateSpaceParser.parseStates(input);
            input.close();


            for (int i = 1; i <= 8; i++) {
                inputFile = new File(FilePath + "form" + i + ".mcf");
                input = new Scanner(inputFile);
                GenericMuFormula formula = FormulaParser.parseFormula(input);
                input.close();

                //System.out.println(formula);

                NaiveChecker nc = new NaiveChecker(states);
                ELChecker elc = new ELChecker(states);
                System.out.println(nc.eval(formula));
                System.out.println(elc.eval(formula));
                System.out.println();
            }

        } catch (FileNotFoundException | ParseException | UnexpectedException e) {
            e.printStackTrace();
        }

    }
}