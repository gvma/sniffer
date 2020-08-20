import com.github.javaparser.ast.*;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;


public class AssertionRouletteMatcher {

    public void match(TestMethod testMethod) {
        for (Node node : testMethod.getMethodDeclaration().getChildNodes()) {
            List<Integer> lines = new LinkedList<>();
            if (matchAssertionRoulette(node.getChildNodes(), 0, lines) >= 2) {
                OutputWriter.write(testMethod.getTestFilePath(),
                        "Assertion Roulette",
                        testMethod.getMethodDeclaration().getNameAsString(),
                        lines.toString());
                Logger.getLogger(AssertionRouletteMatcher.class.getName()).info("Found assertion roulette in method \"" + testMethod.getMethodDeclaration().getName() + "\" in lines " + lines);
            }
        }
    }

    private int matchAssertionRoulette(List<Node> nodeList, Integer assertionCount, List<Integer> lines) {
        for (Node node : nodeList) {
            if (node.getMetaModel().getTypeName().equals("ExpressionStmt")) {
                if (node.toString().startsWith("Assert")
                        || node.toString().startsWith("assert")) {
                    assertionCount++;
                    lines.add(node.getRange().get().begin.line);
                }
            }
            if (node.getChildNodes().size() == 0) {
                return assertionCount;
            } else {
                matchAssertionRoulette(node.getChildNodes(), assertionCount, lines);
            }
        }
        return assertionCount;
    }
}
