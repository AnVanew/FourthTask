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
                getAbsPaths(pathStream)
                        .filter(p -> p.toString().endsWith(".ack"))
                        .forEach(this::moveFileDeleteACK);
            }
            else {
                getAbsPaths(pathStream)
                        .forEach(this::moveFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("End scan directory");
    }

    private Stream <Path> getAbsPaths(Stream<Path> pathStream){
        return pathStream
                .filter(Files::isRegularFile)
                .map(Path::toAbsolutePath);
    }

    private void moveFileDeleteACK(Path file){
        try {
            Files.delete(file);
            file = Paths.get(file.toString().replace(".ack", ""));
            moveFile(file);
        }catch (IOException e){
            System.out.println(e);
            throw new RuntimeException();
        }

    }

    private void moveFile(Path file){
        Path fileName = file.getFileName();
        Path destFile = PropertyReader.OUTPUT_DIRECTORY.resolve(file.getFileName()).toAbsolutePath();
        System.out.println("Trying to move file: " + fileName + " from " + file.getParent());
        try(FileInputStream fileInputStream = new FileInputStream(file.toFile());
            FileOutputStream fileOutputStream = new FileOutputStream(destFile.toFile()))
        {
            copy(fileInputStream, fileOutputStream);
            if (PropertyReader.ACK_OUTPUT == 1) {
                Files.createFile(PropertyReader.OUTPUT_DIRECTORY.resolve(file.getFileName() + ".ack").toAbsolutePath());
            }
            fileInputStream.close();
            Files.delete(file);
        } catch (FileNotFoundException e ){
            System.out.println("File " + fileName + " not found");
            return;
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