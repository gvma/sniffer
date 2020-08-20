import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

// Args[0] is the project root folder
public class Main {
    public static void main(String[] args) throws IllegalArgumentException, IOException {
        if (args.length > 0) {
            ProjectCrawler projectCrawler = new ProjectCrawler(args[0]);
            projectCrawler.run(new File(projectCrawler.getRootDirectory()));
            OutputWriter outputWriter = new OutputWriter(args[0]);
            Matcher matcher = new Matcher();
            matcher.match(projectCrawler.getTestMethods());
            OutputWriter.csvWriter.close();
        } else {
            throw new IllegalArgumentException("You must provide a correct file path with a java project!");
        }
    }
}
