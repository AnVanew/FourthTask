package propertyReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertyReader {

    public static String INPUT_DIRECTORY;
    public static String OUTPUT_DIRECTORY;
    public static int INITIAL_DELAY;
    public static int PERIOD;

    public PropertyReader()  {
        Properties properties = new Properties();
        String pathPropFile = "resources.properties";
        try ( FileInputStream fileInputStream = new FileInputStream(new File(pathPropFile)) ) {
            properties.load(fileInputStream);
            INPUT_DIRECTORY = properties.getProperty("INPUT_DIRECTORY");
            OUTPUT_DIRECTORY = properties.getProperty("OUTPUT_DIRECTORY");
            INITIAL_DELAY = Integer.valueOf(properties.getProperty("INITIAL_DELAY"));
            PERIOD = Integer.valueOf(properties.getProperty("PERIOD"));
        }catch (IOException e){
            System.out.println(e);
        }
    }

}