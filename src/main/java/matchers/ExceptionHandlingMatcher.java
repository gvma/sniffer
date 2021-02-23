package matchers;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.visitor.TreeVisitor;
import utils.JUnit5Utilization;
import utils.OutputWriter;
import utils.TestClass;
import utils.TestMethod;

import java.util.List;
import java.util.logging.Logger;

public class ExceptionHandlingMatcher extends SmellMatcher {

    @Override
    protected void match(TestClass testClass) {
        for (TestMethod testMethod : testClass.getTestMethods()) {
            for (Node node : testMethod.getMethodDeclaration().getChildNodes()) {
                if (matchExceptionHandling(node.getChildNodes()) && JUnit5Utilization.isSmellyAndJUnit5(testClass.getAbsolutePath())) {
                    write(testMethod.getTestFilePath(), "Exception Handling", testMethod.getMethodDeclaration().getNameAsString(), Integer.toString(testMethod.getBeginLine()));
                }
            }
        }
    }

    @Override
    public void write(String filePath, String testSmell, String name, String lines) {
        OutputWriter.getInstance().write(filePath, testSmell, name, lines);
        Logger.getLogger(AssertionRouletteMatcher.class.getName()).info("Found Exception Handling in method \"" + name + "\" in line " + lines);
    }

    private boolean matchExceptionHandling(List<Node> nodeList) {
        boolean[] isExceptionHandling = {false};
        for (Node node : nodeList) {
            if (isExceptionHandling[0]) {
                return true;
            }
            new TreeVisitor() {
                @Override
                public void process(Node node) {
                    if (node.getMetaModel().getTypeName().equals("TryStmt")) {
                        for (Node tryBlockChild : node.getChildNodes()) {
                            if (tryBlockChild.getMetaModel().getTypeName().equals("BlockStmt")) {
                                for (Node blockStmtChild : tryBlockChild.getChildNodes()) {
                                    if (blockStmtChild.getMetaModel().getTypeName().equals("ExpressionStmt")) {
                                        if (blockStmtChild.toString().trim().startsWith("Assert")
                                                || blockStmtChild.toString().trim().startsWith("assert")
                                                || blockStmtChild.toString().trim().endsWith("Assert")
                                                || blockStmtChild.toString().trim().endsWith("assert")
                                                || blockStmtChild.toString().trim().startsWith("fail")
                                                || blockStmtChild.toString().trim().endsWith("fail")) {
                                            isExceptionHandling[0] = true;

                                        }
                                    }
                                }
                            } else if (tryBlockChild.getMetaModel().getTypeName().equals("CatchClause")) {
                                for (Node catchClauseChild : tryBlockChild.getChildNodes()) {
                                    if (catchClauseChild.getMetaModel().getTypeName().equals("BlockStmt")) {
                                        for (Node blockStmtChild : catchClauseChild.getChildNodes()) {
                                            if (blockStmtChild.getMetaModel().getTypeName().equals("ExpressionStmt")) {
                                                if (blockStmtChild.toString().trim().startsWith("Assert")
                                                        || blockStmtChild.toString().trim().startsWith("assert")
                                                        || blockStmtChild.toString().trim().startsWith("fail")) {
                                                    isExceptionHandling[0] = true;
                                                }
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
        return isExceptionHandling[0];
    }
}