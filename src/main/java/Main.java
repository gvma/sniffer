import github.GithubRepositoryClone;
import matchers.Sniffer;
import projectCrawler.ProjectCrawler;
import utils.TestClass;
import utils.TestMethod;

import java.io.FileWriter;

public class Main {
    public synchronized static void main(String[] args) throws Exception {
        Sniffer sniffer = new Sniffer(args[0]);
        int count = 0;
        String classTemplate = "public class %s {\n%s\n}";
        String outputPath = "C:\\Users\\guiga\\Documents\\UFAL\\IC\\EASY\\methodPerFile";
        ProjectCrawler projectCrawler = sniffer.getProjectCrawler();
        for (TestClass testClass : projectCrawler.getTestClasses()) {
            for (TestMethod testMethod : testClass.getTestMethods()) {
                FileWriter fileWriter = new FileWriter(outputPath + "\\" + "java" + count + ".java");
                fileWriter.write(String.format(classTemplate, "java" + count, testMethod.getMethodDeclaration()));
                fileWriter.flush();
                fileWriter.close();
                count++;
            }
        }
    }
}
