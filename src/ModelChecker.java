import MuFormula.GenericMuFormula;

import java.io.File;
import java.io.FileNotFoundException;
import java.rmi.UnexpectedException;
import java.text.ParseException;
import java.util.Scanner;

public class ModelChecker {



    public static void main(String[] args) {

        String FilePath = "C:\\Users\\tijnt\\OneDrive\\Documenten\\TUE\\YM2\\Q2 - 2IMF35 Algorithms for Model Checking\\A1\\testcases\\combined\\";
        String stateFileName = "test.aut";
        String formulaFileName = "form3.mcf";


        try {
            File inputFile = new File(FilePath+stateFileName);
            Scanner input = new Scanner(inputFile);
            StateSpaceParser.parseStates(input);
            input.close();


            inputFile = new File(FilePath+formulaFileName);
            input = new Scanner(inputFile);
            GenericMuFormula formula = FormulaParser.parseFormula(input);

            System.out.println(formula);

            input.close();

        } catch (FileNotFoundException | ParseException | UnexpectedException e) {
            e.printStackTrace();
        }

    }
}