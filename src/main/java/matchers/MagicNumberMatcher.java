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

public class MagicNumberMatcher extends SmellMatcher {

    @Override
    protected void match(TestClass testClass) {
        for (TestMethod testMethod : testClass.getTestMethods()) {
            Set<Integer> lines = new HashSet<>();
            for (Node node : testMethod.getMethodDeclaration().getChildNodes()) {
                matchMagicNumber(node.getChildNodes(), lines);
                if (lines.size() > 0) {
                    write(testMethod.getTestFilePath(), "Magic Number", testMethod.getMethodDeclaration().getNameAsString(), lines.toString());
                }
            }
        }
    }

    @Override
    public void write(String filePath, String testSmell, String name, String lines) {
        OutputWriter.getInstance().write(filePath, testSmell, name, lines);
        Logger.getLogger(AssertionRouletteMatcher.class.getName()).info("Found magic number in method \"" + name + "\" in lines " + lines);
    }

    private void matchMagicNumber(List<Node> nodeList, Set<Integer> lines) {
        for (Node node : nodeList) {
            new TreeVisitor() {
                @Override
                public void process(Node node) {
                    if (node.toString().trim().startsWith("assert") || node.toString().trim().startsWith("Assert")) {
                        for (Node n1 : node.getChildNodes()) {
                            if (n1.getMetaModel().getTypeName().equals("ArrayAccessExpr")) {
                                continue;
                            }
                            for (Node n2 : n1.getChildNodes()) {
                                if (!n2.getMetaModel().getTypeName().equals("MethodCallExpr")) {
                                    if (n2.getMetaModel().getTypeName().endsWith("LiteralExpr")) {
                                        if (!n2.toString().startsWith("\"")
                                                && !n2.toString().startsWith("'")
                                                && !n2.toString().equals("true")
                                                && !n2.toString().equals("false")
                                                && !n2.toString().equals("null")) {
                                            if (node.getRange().isPresent()) {
                                                lines.add(node.getRange().get().begin.line);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }.visitPreOrder(node);
        }
    }
}
