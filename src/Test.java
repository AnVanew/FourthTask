import java.io.*;
import java.util.concurrent.*;



public class Test {

    private void exec() {
        File inputFolder = new File(PropertyReader.INPUT_DIRECTORY);
        File[] listOfFiles = inputFolder.listFiles();
        if (listOfFiles != null)
            for (File file : listOfFiles){
                String fileName = file.getName();
                try(FileInputStream fileInputStream = new FileInputStream(file);
                    FileOutputStream fileOutputStream = new FileOutputStream(PropertyReader.OUTPUT_DIRECTORY + "/" +fileName)){
                    copy(fileInputStream, fileOutputStream);
                } catch (IOException e){
                    System.out.println(e);
                }
                file.delete();
            }
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