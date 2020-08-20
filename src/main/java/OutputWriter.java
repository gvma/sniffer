import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class OutputWriter {
    public static CSVWriter csvWriter;
    public static String projectName;

    public OutputWriter(String projectPath) {
        String regex = "";
        if (System.getProperty("file.separator").equals("/")) {
            regex = "/";
        } else {
            regex = "\\\\";
        }
        String[] splitted = projectPath.split(regex);
        OutputWriter.projectName = splitted[splitted.length - 1];

        try {
            csvWriter = new CSVWriter(new FileWriter(System.getProperty("user.dir")
                    + System.getProperty("file.separator")
                    + OutputWriter.projectName + "_output.csv"));
            csvWriter.writeNext(new String[]{"Project", "Absolute Path", "Test Smell", "Method Name", "Lines"});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void write(String filePath, String testSmell, String methodName, String lines) {
        List<String> toWrite = new LinkedList<>();
        toWrite.add(projectName);
        toWrite.add(filePath);
        toWrite.add(testSmell);
        toWrite.add(methodName);
        toWrite.add(lines);
        String[] itemsArray = new String[toWrite.size()];
        itemsArray = toWrite.toArray(itemsArray);
        csvWriter.writeNext(itemsArray);
    }
}
