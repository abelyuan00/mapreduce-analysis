import java.io.File;
import java.net.URLDecoder;

public class ApplicationMain {
    private static String INPUT_FILE;
    private static String OUTPUT_FILE;
    public static final String OUTPUT_TARGET_FILE = "file/output/part-r-00000";
    static {
        INPUT_FILE = URLDecoder.decode(new File("file/input/data.json").getAbsolutePath());
        OUTPUT_FILE = URLDecoder.decode(new File("file/output/").getAbsolutePath());
    }

    public static void main(String[] args) throws Exception {
        //data cleaning
        twitterClean.startJob(INPUT_FILE, OUTPUT_FILE);
        // data analysing
        TwitterAnalysis.analysis();

    }
}
