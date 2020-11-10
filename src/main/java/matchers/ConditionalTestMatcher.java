package matchers;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.visitor.TreeVisitor;
import com.sun.source.tree.Tree;
import utils.OutputWriter;
import utils.TestClass;
import utils.TestMethod;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class ConditionalTestMatcher extends SmellMatcher {

    @Override
    protected void match(TestClass testClass) {
        for (TestMethod testMethod : testClass.getTestMethods()) {
            for (Node node : testMethod.getMethodDeclaration().getChildNodes()) {
                List<Integer> lines = new LinkedList<>();
                matchConditionalTest(node.getChildNodes(), lines);
                if (!lines.isEmpty()) {
                    write(testMethod.getTestFilePath(), "Conditional Test", testMethod.getMethodDeclaration().getNameAsString(), Integer.toString(testMethod.getBeginLine()));
                }
            }
        }
    }

    @Override
    public void write(String filePath, String testSmell, String methodName, String lines) {
        OutputWriter.getInstance().write(filePath, testSmell, methodName, lines);
        Logger.getLogger(AssertionRouletteMatcher.class.getName()).info("Found conditional test in method \"" + methodName + "\" in lines " + lines);
    }

    private void matchConditionalTest(List<Node> nodeList, List<Integer> lines) {
        for (Node node : nodeList) {
            new TreeVisitor() {
                @Override
                public void process(Node node) {
                    if (node.getMetaModel().getTypeName().equals("IfStmt")
                            || node.getMetaModel().getTypeName().equals("WhileStmt")
                            || node.getMetaModel().getTypeName().equals("ForStmt")
                            || node.getMetaModel().getTypeName().equals("DoStmt")) {
                        for (Node child : node.getChildNodes()) {
                            new TreeVisitor() {
                                @Override
                                public void process(Node node) {
                                    if (node.toString().trim().startsWith("assert") || node.toString().trim().startsWith("Assert")) {
                                        if (node.getRange().isPresent()) {
                                            lines.add(node.getRange().get().begin.line);
                                        }
                                    }
                                }
                            }.visitPreOrder(child);
                        }
                    }
                }
            }.visitPreOrder(node);
        }
    }
}
