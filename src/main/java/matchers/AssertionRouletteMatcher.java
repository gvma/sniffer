package matchers;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.visitor.TreeVisitor;
import utils.OutputWriter;
import utils.TestClass;
import utils.TestMethod;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public final class AssertionRouletteMatcher extends SmellMatcher {

    private Integer assertionCount = 0;

    @Override
    protected void match(TestClass testClass) {
        for (TestMethod testMethod : testClass.getTestMethods()) {
            for (Node node : testMethod.getMethodDeclaration().getChildNodes()) {
                List<Integer> lines = new LinkedList<>();
                matchAssertionRoulette(node.getChildNodes(), lines);
                if (assertionCount >= 2) {
                    write(testMethod.getTestFilePath(), "Assertion Roulette", testMethod.getMethodDeclaration().getNameAsString(), lines.toString());
                }
                assertionCount = 0;
            }
        }
    }

    @Override
    public void write(String filePath, String testSmell, String methodName, String lines) {
        OutputWriter.getInstance().write(filePath, testSmell, methodName, lines);
        Logger.getLogger(AssertionRouletteMatcher.class.getName()).info("Found assertion roulette in method \"" + methodName + "\" in lines " + lines);
    }

    private void matchAssertionRoulette(List<Node> nodeList, List<Integer> lines) {
        for (Node node : nodeList) {
            new TreeVisitor() {
                @Override
                public void process(Node node) {
                    if (node.getRange().isPresent()) {
                        if (!lines.contains(node.getRange().get().begin.line) &&
                                (node.toString().trim().startsWith("assert") || node.toString().trim().startsWith("Assert"))) {
                            assertionCount++;
                            lines.add(node.getRange().get().begin.line);
                        }
                    }
                }
            }.visitPreOrder(node);
        }
    }

}