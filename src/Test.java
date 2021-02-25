import propertyReader.PropertyReader;

import java.io.*;
import java.nio.file.Path;
import java.util.concurrent.*;

import static propertyReader.PropertyReader.*;


public class Test {

    private void exec() {
        Test test = new Test();
        File inputFolder = new File(INPUT_DIRECTORY);
        File[] listOfFiles = inputFolder.listFiles();
        if (listOfFiles != null)
            for (File file : listOfFiles){
                String fileName = file.getName();
                try(FileInputStream fileInputStream = new FileInputStream(file);
                    FileOutputStream fileOutputStream = new FileOutputStream(new File(OUTPUT_DIRECTORY + "/" +fileName))){
                    test.copy(fileInputStream, fileOutputStream);
                } catch (IOException e){
                    System.out.println(e);
                }
                file.delete();
            }
    }

    private void start() {
        ScheduledExecutorService s = Executors.newSingleThreadScheduledExecutor();
        s.scheduleAtFixedRate(this::exec, INITIAL_DELAY, PERIOD, TimeUnit.SECONDS);
    }

    private void copy(InputStream source, OutputStream target) throws IOException {
        byte[] buf = new byte[8192];
        int length;
        while ((length = source.read(buf)) > 0) {
            target.write(buf, 0, length);
        }
    }

    public static void main(String[] args) {
        new PropertyReader();
        new Test().start();
    }
}