package matchers;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.TreeWalker;
import utils.OutputWriter;
import utils.TestClass;
import utils.TestMethod;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.logging.Logger;

public class ConditionalTestMatcher extends SmellMatcher {

    @Override
    protected void match(TestClass testClass) {
        for (TestMethod testMethod : testClass.getTestMethods()) {
            NodeList methodChilds = testMethod.getMethodDeclaration().getChildNodes();
            for (int i = 0; i < methodChilds.getLength(); ++i) {
                Node node = methodChilds.item(i);
                Set<Integer> lines = new HashSet<>();
                matchConditionalTest(node.getChildNodes(), lines);
                if (lines.size() != 0) {
                    write(testMethod.getTestFilePath(), "Conditional Test", testMethod.getMethodName(), new LinkedList<>().toString());
                }
            }
        }
    }

    @Override
    public void write(String filePath, String testSmell, String name, String lines) {
        OutputWriter.getInstance().write(filePath, testSmell, name, lines);
        Logger.getLogger(AssertionRouletteMatcher.class.getName()).info("Found conditional test in method \"" + name + "\" in lines " + lines);
    }

    private void matchConditionalTest(NodeList nodeList, Set<Integer> lines) {
        for (int i = 0; i < nodeList.getLength(); ++i) {
            Node root = nodeList.item(i);
            if (root.getNodeName().equals("if_stmt") || root.getNodeName().equals("for") || root.getNodeName().equals("while") || root.getNodeName().equals("do")) {
                NodeList conditionalChilds = root.getChildNodes();
                for (int j = 0; j < conditionalChilds.getLength(); ++j) {
                    Node conditional = conditionalChilds.item(j);
                    DocumentTraversal traversal = (DocumentTraversal) conditional.getOwnerDocument();
                    TreeWalker iterator = traversal.createTreeWalker(conditional, NodeFilter.SHOW_ALL, null, false);
                    Node c = null;
                    while ((c = iterator.nextNode()) != null) {
                        if (c.getNodeName().equals("expr") && c.getTextContent().contains("EXPECT_")) {
                            lines.add(0);
                        }
                    }
                }
            }
        }
    }
}
