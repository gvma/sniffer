package matchers;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.LiteralExpr;
import utils.OutputWriter;
import utils.TestClass;
import utils.TestMethod;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class MagicNumberMatcher extends SmellMatcher {

    @Override
    protected void match(TestClass testClass) {
        for (TestMethod testMethod : testClass.getTestMethods()) {
            Set<Integer> lines = new HashSet<>();
            for (Node node : testMethod.getMethodDeclaration().getChildNodes()) {
                matchMagicNumber(node.getChildNodes(), lines);
                if (lines.size() > 0) {
                    OutputWriter.getInstance().write(testMethod.getTestFilePath(),
                            "Magic Number",
                            testMethod.getMethodDeclaration().getNameAsString(),
                            lines.toString());
                    Logger.getLogger(AssertionRouletteMatcher.class.getName()).info("Found magic number in method \"" + testMethod.getMethodDeclaration().getName() + "\" in lines " + lines);
                }
            }
        }
    }

    private void matchMagicNumber(List<Node> nodeList, Set<Integer> lines) {
        for (Node node : nodeList) {
            if (node.getMetaModel().getTypeName().equals("ExpressionStmt")) {
                if (node.toString().startsWith("Assert")
                    || node.toString().startsWith("assert")) {
                    for (Node n1 : node.getChildNodes()) {
                        for (Node n2 : n1.getChildNodes()) {
                            if (n2.getMetaModel().getTypeName().endsWith("LiteralExpr")) {
                                lines.add(node.getRange().get().begin.line);
                            }
                        }
                    }
                }
            }
            if (node.getChildNodes().size() == 0) {
                return;
            } else {
                matchMagicNumber(node.getChildNodes(), lines);
            }
        }
    }
}
