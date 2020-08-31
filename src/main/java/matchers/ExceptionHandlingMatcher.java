package matchers;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.visitor.TreeVisitor;
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
                if (matchExceptionHandling(node.getChildNodes())) {
                    write(testMethod.getTestFilePath(), "Exception Handling", testMethod.getMethodDeclaration().getNameAsString(), Integer.toString(testMethod.getBeginLine()));
                }
            }
        }
    }

    @Override
    public void write(String filePath, String testSmell, String methodName, String lines) {
        OutputWriter.getInstance().write(filePath, testSmell, methodName, lines);
        Logger.getLogger(AssertionRouletteMatcher.class.getName()).info("Found Exception Handling in method \"" + methodName + "\" in line " + lines);
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
                                    System.out.println("Node: " + blockStmtChild + " and type: " + blockStmtChild.getMetaModel().getTypeName());
                                    if (blockStmtChild.getMetaModel().getTypeName().equals("ExpressionStmt")) {
                                        System.out.println(blockStmtChild.toString().trim().startsWith("fail"));
                                        if (blockStmtChild.toString().trim().startsWith("Assert")
                                                || blockStmtChild.toString().trim().startsWith("assert")
                                                || blockStmtChild.toString().trim().startsWith("fail")) {
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
                        System.out.println("\n\n\n\n");
                    }
                }
            }.visitPreOrder(node);
        }
        return isExceptionHandling[0];
    }
}