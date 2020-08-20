import com.github.javaparser.ast.*;
import com.sun.tools.javac.util.Log;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class AssertionRouletteMatcher {

    public void findAssertionRoulette(TestMethod testMethod) {
        Logger logger = Logger.getLogger(AssertionRouletteMatcher.class.getName());
        logger.info("Analyzing file " + testMethod.getTestFilePath());
        for (Node node : testMethod.getMethodDeclaration().getChildNodes()) {
            List<Integer> lines = new LinkedList<>();
            if (countAssertions(node.getChildNodes(), 0, lines) >= 2) {
                List<String> toWrite = new LinkedList<>();
                toWrite.add(OutputWriter.projectName);
                toWrite.add(testMethod.getTestFilePath());
                toWrite.add("Assertion Roulette");
                toWrite.add(testMethod.getMethodDeclaration().getNameAsString());
                toWrite.add(lines.toString());
                System.out.println("Found assertion roulette in method \"" + testMethod.getMethodDeclaration().getName() + "\" in lines " + lines);
                String[] itemsArray = new String[toWrite.size()];
                itemsArray = toWrite.toArray(itemsArray);
                OutputWriter.csvWriter.writeNext(itemsArray);
            }
        }
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
