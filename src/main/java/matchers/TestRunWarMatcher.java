package matchers;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.visitor.TreeVisitor;
import utils.JUnit5Utilization;
import utils.OutputWriter;
import utils.TestClass;
import utils.TestMethod;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class TestRunWarMatcher extends SmellMatcher {
    @Override
    protected void match(TestClass testClass) {
        for (TestMethod testMethod : testClass.getTestMethods()) {
            for (Node node : testMethod.getMethodDeclaration().getChildNodes()) {
                List<Integer> lines = new LinkedList<>();
                matchTestRunWar(node.getChildNodes(), lines);
                if (!lines.isEmpty() && JUnit5Utilization.isSmellyAndJUnit5(testClass.getAbsolutePath())) {
                    write(testMethod.getTestFilePath(), "Test Run War", testMethod.getMethodDeclaration().getNameAsString(), lines.toString());

                }
            }
        }
    }

    @Override
    public void write(String filePath, String testSmell, String name, String lines) {
        OutputWriter.getInstance().write(filePath, testSmell, name, lines);
        Logger.getLogger(AssertionRouletteMatcher.class.getName()).info("Found test run war in method \"" + name + "\" in lines " + lines);
    }

    private void matchTestRunWar(List<Node> nodeList, List<Integer> lines) {
        for (Node n : nodeList) {
            new TreeVisitor() {
                @Override
                public void process(Node node) {
                    if (node.toString().contains("System.get") || node.toString().contains("System.set")) {
                        if (node.getRange().isPresent()) {
                            if (!lines.contains(node.getRange().get().begin.line)) {
                                lines.add(node.getRange().get().begin.line);
                            }
                        }
                    }
                }
            }.visitPreOrder(n);
        }
    }
}
