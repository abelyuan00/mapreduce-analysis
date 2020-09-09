import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

    public static List<String> readFileString(String file) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line = null;
            List<String> lines = new ArrayList<>();
            while ((line = bufferedReader.readLine()) != null) {
                lines.add(line);
            }
            return lines;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void writeFile(String file, List<String> lines) {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            for (String line : lines) {
                bufferedWriter.write(line + System.lineSeparator());
            }
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
