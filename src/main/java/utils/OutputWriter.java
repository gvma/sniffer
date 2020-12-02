package utils;

import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class OutputWriter {
    public static CSVWriter csvWriter;
    public static String projectName;
    private static volatile OutputWriter instance;

    private OutputWriter() {}

    public static OutputWriter getInstance() {
        OutputWriter result = instance;
        if (result != null) {
            return result;
        }
        synchronized (OutputWriter.class) {
            if (instance == null) {
                instance = new OutputWriter();
            }
            return instance;
        }
    }

    public void setOutputFile(String projectPath) {
        String regex;
        if (System.getProperty("file.separator").equals("/")) {
            regex = "/";
        } else {
            regex = "\\\\";
        }
        String[] splitted = projectPath.split(regex);
        OutputWriter.projectName = splitted[splitted.length - 1];
        writeFirstCSVRow();
    }

    private void writeFirstCSVRow() {
        try {
            csvWriter = new CSVWriter(new FileWriter(System.getProperty("user.dir")
                    + System.getProperty("file.separator")
                    + OutputWriter.projectName + "_output.csv"));
            csvWriter.writeNext(new String[]{"Project", "Absolute Path", "Test Smell", "Method Name", "Lines"}, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(String filePath, String testSmell, String methodName, String lines) {
        List<String> toWrite = new LinkedList<>();
        toWrite.add(projectName);
        toWrite.add(filePath);
        toWrite.add(testSmell);
        toWrite.add(methodName);
        toWrite.add(lines);
        String[] itemsArray = new String[toWrite.size()];
        itemsArray = toWrite.toArray(itemsArray);
        csvWriter.writeNext(itemsArray, true);
    }

    public void write(String filePath, String newFeature) {
        List<String> toWrite = new LinkedList<>();
        toWrite.add(projectName);
        toWrite.add(filePath);
        toWrite.add(newFeature);
        String[] itemsArray = new String[toWrite.size()];
        itemsArray = toWrite.toArray(itemsArray);
        csvWriter.writeNext(itemsArray, true);
    }
}
