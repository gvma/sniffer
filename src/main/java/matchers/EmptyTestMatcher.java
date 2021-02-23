package matchers;

import com.github.javaparser.ast.Node;
import utils.JUnit5Utilization;
import utils.OutputWriter;
import utils.TestClass;
import utils.TestMethod;

import java.util.List;
import java.util.logging.Logger;

public class EmptyTestMatcher extends SmellMatcher {
    @Override
    protected void match(TestClass testClass) {
        for (TestMethod testMethod : testClass.getTestMethods()) {
            List<Node> nodeList = testMethod.getMethodDeclaration().getChildNodes();
            if (nodeList.get(nodeList.size() - 1).getChildNodes().size() == 0) {
                if (nodeList.get(nodeList.size() - 1).getRange().isPresent() && JUnit5Utilization.isSmellyAndJUnit5(testClass.getAbsolutePath())) {
                    write(testClass.getAbsolutePath(),
                            "Empty Test",
                            testClass.getClassName(),
                            String.valueOf(nodeList.get(nodeList.size() - 1).getRange().get().begin.line));
                }
            }
        }
    }

    @Override
    public void write(String filePath, String testSmell, String name, String lines) {
        OutputWriter.getInstance().write(filePath, testSmell, name, lines);
        Logger.getLogger(AssertionRouletteMatcher.class.getName()).info("Found empty test in method \"" + name + "\" in lines " + lines);
    }
}
