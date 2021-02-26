import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Test {

    private void exec()  {
        try {
            List<File> listOfFiles = null;
            listOfFiles = Files.walk(PropertyReader.INPUT_DIRECTORY).filter(Files::isRegularFile).map(Path::toFile).collect(Collectors.toList());
            if (listOfFiles.size() != 0)
                for (File file : listOfFiles){
                   copyAndDeleteFile(file);
                }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void copyAndDeleteFile (File file) throws IOException {
        String fileName = file.getName();
        try(FileInputStream fileInputStream = new FileInputStream(PropertyReader.INPUT_DIRECTORY + "/" + fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(PropertyReader.OUTPUT_DIRECTORY + "/" +fileName)){
            copy(fileInputStream, fileOutputStream);
        }
        file.delete();
    }

    private void start() {
        ScheduledExecutorService s = Executors.newSingleThreadScheduledExecutor();
        s.scheduleAtFixedRate(this::exec, PropertyReader.INITIAL_DELAY, PropertyReader.PERIOD, TimeUnit.SECONDS);
    }

    private void copy(InputStream source, OutputStream target) throws IOException {
        byte[] buf = new byte[8192];
        int length;
        while ((length = source.read(buf)) > 0) {
            target.write(buf, 0, length);
        }
    }

    public static void main(String[] args) {
        try {
            PropertyReader.loadProperties();
        } catch (IOException e) {
            e.printStackTrace();
        }
        new Test().start();
    }
}