import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ModelChecker {



    public static void main(String[] args) {

        String filePath = "C:\\Users\\tijnt\\OneDrive\\Documenten\\TUE\\YM2\\Q2 - 2IMF35 Algorithms for Model Checking\\A1\\testcases\\boolean\\";
        String fileName = "test.aut";

        File inputFile = new File(filePath+fileName);
        try {
            Scanner input = new Scanner(inputFile);
            StateSpaceParser.parseStates(input);
            input.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}