package matchers;

import org.w3c.dom.Node;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.TreeWalker;
import utils.OutputWriter;
import utils.TestClass;
import utils.TestMethod;
import java.util.LinkedList;
import java.util.logging.Logger;

public class AssertionRouletteMatcher extends SmellMatcher {
    @Override
    protected void match(TestClass testClass) {
        for (TestMethod testMethod : testClass.getTestMethods()) {
            Node root = testMethod.getMethodDeclaration();
            boolean hasAssertionRouletteSmell = matchAssertionRoulette(root);
            if (hasAssertionRouletteSmell) {
                write(testMethod.getTestFilePath(), "Assertion Roulette", testMethod.getMethodName(), new LinkedList<>().toString());
            }
        }
    }
    @Override
    public void write(String filePath, String testSmell, String name, String lines) {
        OutputWriter.getInstance().write(filePath, testSmell, name, lines);
        Logger.getLogger(AssertionRouletteMatcher.class.getName()).info("Found assertion roulette in method \"" + name + "\" in lines " + lines);
    }
    private boolean matchAssertionRoulette(Node root) {
        int assertionCount = 0;
        DocumentTraversal traversal = (DocumentTraversal) root.getOwnerDocument();
        TreeWalker iterator = traversal.createTreeWalker(root, NodeFilter.SHOW_ALL, null, false);
        Node node = null;
        while ((node = iterator.nextNode())!=null){
            String textContent = node.getTextContent().trim();
            if (node.getNodeName().equals("expr") && (textContent.startsWith("EXPECT_") || textContent.startsWith("FAIL_") || textContent.startsWith("ASSERT_"))) {
                String[] getCommentsFromExpect = textContent.split("<<");
                boolean hasComments = getCommentsFromExpect.length == 2;
                if(!hasComments) {
                    assertionCount++;
                }
            }
        }
        return assertionCount >= 2;
    }
}