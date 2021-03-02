import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class PropertyReader {

    public static Path INPUT_DIRECTORY;
    public static Path OUTPUT_DIRECTORY;
    public static int INITIAL_DELAY;
    public static int PERIOD;
    public static String ACK_INPUT;
    public static String ACK_OUTPUT;

    public static void loadProperties () throws IOException {
        Properties properties = new Properties();
        String pathPropFile = "resources.properties";
        try ( FileInputStream fileInputStream = new FileInputStream(new File(pathPropFile)) ) {
            properties.load(fileInputStream);
            INPUT_DIRECTORY = Paths.get(properties.getProperty("INPUT_DIRECTORY"));
            OUTPUT_DIRECTORY = Paths.get(properties.getProperty("OUTPUT_DIRECTORY"));
            INITIAL_DELAY = Integer.parseInt(properties.getProperty("INITIAL_DELAY"));
            PERIOD = Integer.parseInt(properties.getProperty("PERIOD"));
            ACK_INPUT = properties.getProperty("ACK_INPUT");
            ACK_OUTPUT = properties.getProperty("ACK_OUTPUT");
        }
    }

}