package matchers;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.visitor.TreeVisitor;
import utils.OutputWriter;
import utils.TestClass;
import utils.TestMethod;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class DuplicateAssertMatcher extends SmellMatcher {
    @Override
    protected void match(TestClass testClass) {
        for (TestMethod testMethod : testClass.getTestMethods()) {
            for (Node node : testMethod.getMethodDeclaration().getChildNodes()) {
                List<Integer> lines = new LinkedList<>();
                Map<String, List<Integer>> allAsserts = new HashMap<>();
                getAllAsserts(node.getChildNodes(), allAsserts);
                for (Map.Entry<String, List<Integer>> entry : allAsserts.entrySet()) {
                    if (entry.getValue().size() > 1) {
                        for (Integer line : entry.getValue()) {
                            if (!lines.contains(line)) {
                                lines.add(line);
                            }
                        }
                    }
                }
                if (!lines.isEmpty()) {
                    write(testMethod.getTestFilePath(), "Duplicate Assert", testMethod.getMethodDeclaration().getNameAsString(), lines.toString());
                }
            }
        }
    }

    @Override
    public void write(String filePath, String testSmell, String methodName, String lines) {
        OutputWriter.getInstance().write(filePath, testSmell, methodName, lines);
        Logger.getLogger(AssertionRouletteMatcher.class.getName()).info("Found duplicate assert in method \"" + methodName + "\" in lines " + lines);
    }

    private void getAllAsserts(List<Node> nodeList, Map<String, List<Integer>> allAsserts) {
        List<Node> nodes = new LinkedList<>();
        for (Node node : nodeList) {
            new TreeVisitor() {
                @Override
                public void process(Node node) {
                    if (node.getRange().isPresent()) {
                        if (node.getMetaModel().getTypeName().equals("ExpressionStmt")) {
                            if (node.toString().trim().startsWith("assert") || node.toString().trim().startsWith("Assert")) {
                                if (node.getRange().isPresent()) {
                                    allAsserts.computeIfAbsent(node.toString().trim(), k -> new LinkedList<>());
                                    List<Integer> lines = allAsserts.get(node.toString());
                                    if (!lines.contains(node.getRange().get().begin.line)) {
                                        nodes.add(node);
                                        lines.add(node.getRange().get().begin.line);
                                    }
                                    allAsserts.put(node.toString().trim(), lines);
                                }
                            }
                        }
                    }
                }
            }.visitPreOrder(node);
        }
    }
}
