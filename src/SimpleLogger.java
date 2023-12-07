import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SimpleLogger {

    private static FileWriter writer = null;
    private static boolean toFile = false;


    public static void outputToFile(File file) throws IOException {
        writer = new FileWriter(file);
        toFile = true;
    }

    public static void outputToSystemOut() {
        close();
        writer = null;
        toFile = false;
    }

    public static void close() {
        if (writer!=null) {
            try {
                writer.close();
            } catch (IOException e) {
                System.out.println("Failed to close writer!");
            }
        }
    }

    public static void write(String string) {
        if (toFile) {
            try {
                writer.write(string);
            } catch (IOException e) {
                System.out.println("Failed to write: " + string + "to File!");
            }
        } else {
            System.out.print(string);
        }
    }

    public static void writeln(String string) {
        write(string + "\n");
    }

}
