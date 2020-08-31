package matchers;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.visitor.TreeVisitor;
import utils.OutputWriter;
import utils.TestClass;
import utils.TestMethod;

import java.util.List;
import java.util.logging.Logger;

public class UnknownTestMatcher extends SmellMatcher {

    private Integer assertionCount = 0;

    @Override
    protected void match(TestClass testClass) {
        for (TestMethod testMethod : testClass.getTestMethods()) {
            matchUnknownTest(testMethod.getMethodDeclaration().getChildNodes());
            System.out.println(assertionCount);
            if (assertionCount == 0) {
                write(testMethod.getTestFilePath(), "Unknown Test", testMethod.getMethodDeclaration().getNameAsString(), Integer.toString(testMethod.getBeginLine()));
            }
            assertionCount = 0;
        }
    }

    @Override
    public void write(String filePath, String testSmell, String methodName, String lines) {
        OutputWriter.getInstance().write(filePath, testSmell, methodName, lines);
        Logger.getLogger(AssertionRouletteMatcher.class.getName()).info("Found assertion roulette in method \"" + methodName + "\" in lines " + lines);
    }

    private void matchUnknownTest(List<Node> nodeList) {
        for (Node node : nodeList) {
            new TreeVisitor() {
                @Override
                public void process(Node node) {
                    if (node.toString().trim().startsWith("assert") || node.toString().trim().startsWith("Assert")) {
                        assertionCount++;
                    } else if (node.toString().trim().startsWith("fail")) {
                        assertionCount++;
                    }
                }
            }.visitPreOrder(node);
        }
    }
}
