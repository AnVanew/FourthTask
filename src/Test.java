import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Test {

    private void moveFilesFromDir()  {
        try (Stream <Path> pathStream = Files.walk(PropertyReader.INPUT_DIRECTORY)) {
            pathStream.filter(Files::isRegularFile).map(Path::toAbsolutePath).forEach(path -> {
                try {
                    moveFile(path);
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void moveFile(Path file) throws IOException{
        Path fileName = file.getFileName();
        try(FileInputStream fileInputStream = new FileInputStream(file.toFile());
            FileOutputStream fileOutputStream = new FileOutputStream(PropertyReader.OUTPUT_DIRECTORY + "/" +fileName))
        {
            copy(fileInputStream, fileOutputStream);
        }
        Files.delete(file);
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

    public static void main (String[] args) throws IOException {
        PropertyReader.loadProperties();
        new Test().start();
    }
}