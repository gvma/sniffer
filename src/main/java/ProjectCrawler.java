import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;

public class ProjectCrawler {
    private final String rootDirectory;
    private final List<TestMethod> testMethods;

    public ProjectCrawler(String rootDirectory) {
        this.rootDirectory = rootDirectory;
        this.testMethods = new LinkedList<>();
    }

    public void run(File rootFile) throws FileNotFoundException {
        File[] listedFiles = rootFile.listFiles();
        if (listedFiles != null) {
            for (File file : listedFiles) {
                if (file.isDirectory()) {
                    run(file);
                } else if (file.isFile()) {
                    if (file.getName().endsWith(".java")) {
                        gatherAllTestMethodsFromFile(file);
                    }
                }
            }
        }
    }

    public void gatherAllTestMethodsFromFile(File javaFile) throws FileNotFoundException {
        new VoidVisitorAdapter<Object>() {
            @Override
            public void visit(MethodDeclaration n, Object arg) {
                super.visit(n, arg);
                if (n.getAnnotations().size() != 0) {
                    for (AnnotationExpr annotationExpr : n.getAnnotations()) {
                        if (annotationExpr.getNameAsString().equals("Test")) {
                            testMethods.add(new TestMethod(n.getRange().get().begin.line,
                                    n.getRange().get().end.line,
                                    n.getNameAsString(),
                                    n.asMethodDeclaration(),
                                    javaFile.getAbsolutePath()));
                            break;
                        }
                    }
                }
            }
        }.visit(StaticJavaParser.parse(javaFile), null);
    }

    public String getRootDirectory() {
        return rootDirectory;
    }

    public List<TestMethod> getTestMethods() {
        return testMethods;
    }
}
