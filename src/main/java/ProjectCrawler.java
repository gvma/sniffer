import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.AnnotationDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.FileNotFoundException;

public class ProjectCrawler {
    private final String rootDirectory;

    public ProjectCrawler(String rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    public void findAllJavaFiles(File rootFile) throws FileNotFoundException {
        File[] listedFiles = rootFile.listFiles();
        if (listedFiles != null) {
            for (File file : listedFiles) {
                if (file.isDirectory()) {
                    findAllJavaFiles(file);
                } else if (file.isFile()) {
                    if (file.getName().endsWith(".java")) {
                        new AssertionRouletteMatcher(file);
                    }
                }
            }
        }
    }

    public String getRootDirectory() {
        return rootDirectory;
    }
}
