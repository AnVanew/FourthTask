import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.*;
import java.util.stream.Stream;


public class Test {

    private void moveFilesFromDir()  {
        System.out.println("Begin scan directory");
        try (Stream <Path> pathStream = Files.walk(PropertyReader.INPUT_DIRECTORY)){
            if (PropertyReader.ACK_INPUT == 1){
               Stream<Path> pStream;
               pStream = pathStream
                        .filter(Files::isRegularFile)
                        .filter(p -> p.toString().endsWith(".ack"))
                        .map(Path::toAbsolutePath);
               pStream = pStream.map(p -> p = (Paths.get(p.toString().replace(".ack", ""))));
               pStream
                        .forEach(this::moveFile);
            }
            else {
                pathStream
                        .filter(Files::isRegularFile)
                        .map(Path::toAbsolutePath)
                        .forEach(this::moveFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("End scan directory");
    }

    private void moveFile(Path file){
        Path fileName = file.getFileName();
        Path destFile = PropertyReader.OUTPUT_DIRECTORY.resolve(file.getFileName()).toAbsolutePath();
        System.out.println("File: " + fileName + " move from " + file.getParent());
        try(FileInputStream fileInputStream = new FileInputStream(file.toFile());
            FileOutputStream fileOutputStream = new FileOutputStream(destFile.toFile()))
        {
            copy(fileInputStream, fileOutputStream);
            fileInputStream.close();
            Files.delete(file);
        } catch (IOException e){
            e.printStackTrace();
            throw new RuntimeException();
        }
        System.out.println("File: " + fileName + " move to " + destFile.getParent());
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