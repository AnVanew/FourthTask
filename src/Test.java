import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;


public class Test {

    private void moveFilesFromDir()  {
        try {
            List<Path> listOfFiles = Files.walk(PropertyReader.INPUT_DIRECTORY).filter(Files::isRegularFile).collect(Collectors.toList());
            if (listOfFiles.size() != 0)
                for (Path file : listOfFiles){
                    copyAndDeleteFile(file);
                }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void copyAndDeleteFile (Path file) throws IOException {
        copyFile(file);
        Files.delete(file);
    }

    private void copyFile(Path file) throws IOException{
        Path fileName = file.getFileName();
        try(FileInputStream fileInputStream = new FileInputStream(PropertyReader.INPUT_DIRECTORY + "/" + fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(PropertyReader.OUTPUT_DIRECTORY + "/" +fileName))
        {
            copy(fileInputStream, fileOutputStream);
        }
    }

    private void copy(InputStream source, OutputStream target) throws IOException {
        byte[] buf = new byte[8192];
        int length;
        while ((length = source.read(buf)) > 0) {
            target.write(buf, 0, length);
        }
    }
    
    private void start() {
        ScheduledExecutorService s = Executors.newSingleThreadScheduledExecutor();
        s.scheduleAtFixedRate(this::moveFilesFromDir, PropertyReader.INITIAL_DELAY, PropertyReader.PERIOD, TimeUnit.SECONDS);
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