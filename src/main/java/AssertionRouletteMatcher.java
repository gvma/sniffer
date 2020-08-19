import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;

public class AssertionRouletteMatcher extends VoidVisitorAdapter<Node> {
    private final File javaFile;

    public AssertionRouletteMatcher(File javaFile) throws FileNotFoundException {
        this.javaFile = javaFile;
        this.findAssertionRoulette();
    }

    // TODO still need to implement if the @Test belongs to JUnit
    public void findAssertionRoulette() throws FileNotFoundException {
        System.out.println("####");
        System.out.println("Analyzing file " + javaFile);
        new VoidVisitorAdapter<>() {
            @Override
            public void visit(MethodDeclaration n, Object arg) {
                super.visit(n, arg);
                if (n.getAnnotations().size() != 0) {
                    for (AnnotationExpr annotationExpr : n.getAnnotations()) {
                        if (annotationExpr.getNameAsString().equals("Test")) {
                            for (Node node : n.getChildNodes()) {
                                List<Integer> lines = new LinkedList<>();
                                if (countAssertions(node.getChildNodes(), 0, lines) >= 2) {
                                    System.out.println("Found assertion roulette in method " + n.getName() + " in lines " + lines);
                                }
                            }
                        }
                    }
                }
            }
        }.visit(StaticJavaParser.parse(javaFile), null);
    }

    public int countAssertions(List<Node> nodeList, Integer assertionCount, List<Integer> lines) {
        for (Node node : nodeList) {
            if (node.getMetaModel().getTypeName().equals("ExpressionStmt")) {
                if (node.toString().startsWith("Assert")
                    || node.toString().startsWith("Assertions")
                    || node.toString().startsWith("assert")) {
                    assertionCount++;
                    lines.add(node.getRange().get().begin.line);
                }
            }
            if (node.getChildNodes().size() == 0) {
                return assertionCount;
            } else {
                countAssertions(node.getChildNodes(), assertionCount, lines);
            }
        }
        return assertionCount;
    }
}
