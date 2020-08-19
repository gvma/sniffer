import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;

import java.io.File;
import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) throws IllegalArgumentException, FileNotFoundException {
        if (args.length > 0) {
            ProjectCrawler projectCrawler = new ProjectCrawler(args[0]);
            projectCrawler.findAllJavaFiles(new File(projectCrawler.getRootDirectory()));
        } else {
            throw new IllegalArgumentException("You must provide a correct file path with a java project!");
        }
    }
}
