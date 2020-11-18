package matchers;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.visitor.TreeVisitor;
import utils.OutputWriter;
import utils.TestClass;
import utils.TestMethod;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class ConditionalTestMatcher extends SmellMatcher {

    @Override
    protected void match(TestClass testClass) {
        for (TestMethod testMethod : testClass.getTestMethods()) {
            for (Node node : testMethod.getMethodDeclaration().getChildNodes()) {
                Set<Integer> lines = new HashSet<>();
                matchConditionalTest(node.getChildNodes(), lines);
                if (lines.size() != 0) {
                    write(testMethod.getTestFilePath(), "Conditional Test", testMethod.getMethodDeclaration().getNameAsString(), lines.toString());
                }
            }
        }
    }

    @Override
    public void write(String filePath, String testSmell, String name, String lines) {
        OutputWriter.getInstance().write(filePath, testSmell, name, lines);
        Logger.getLogger(AssertionRouletteMatcher.class.getName()).info("Found conditional test in method \"" + name + "\" in lines " + lines);
    }

    private void matchConditionalTest(List<Node> nodeList, Set<Integer> lines) {
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
