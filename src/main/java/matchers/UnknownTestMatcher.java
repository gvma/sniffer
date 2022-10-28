package matchers;

import org.w3c.dom.Node;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.TreeWalker;
import utils.OutputWriter;
import utils.TestClass;
import utils.TestMethod;
import java.util.logging.Logger;

public class UnknownTestMatcher extends SmellMatcher {
    @Override
    protected void match(TestClass testClass) {
        for (TestMethod testMethod : testClass.getTestMethods()) {
            Node root = testMethod.getMethodDeclaration();
            boolean hasUnknownTestSmell = matchUnknownTest(root);
            if (hasUnknownTestSmell) {
                write(testMethod.getTestFilePath(), "Unknown Test", testMethod.getMethodName(), "[]");
            }
        }
    }

    @Override
    public void write(String filePath, String testSmell, String name, String lines) {
        OutputWriter.getInstance().write(filePath, testSmell, name, lines);
        Logger.getLogger(UnknownTestMatcher.class.getName()).info("Found unknown test in method \"" + name + "\" in lines " + lines);
    }

    private boolean matchUnknownTest(Node root) {
        int assertionCount = 0;
        DocumentTraversal traversal = (DocumentTraversal) root.getOwnerDocument();
        TreeWalker iterator = traversal.createTreeWalker(root, NodeFilter.SHOW_ALL, null, false);
        Node node = null;
        while ((node = iterator.nextNode()) != null) {
            String textContent = node.getTextContent().trim();
            if (node.getNodeName().equals("expr") && (textContent.startsWith("EXPECT_") || textContent.startsWith("FAIL") || textContent.startsWith("ASSERT_"))) {
                assertionCount += 1;
            }
        }
        return assertionCount == 0;
    }
}