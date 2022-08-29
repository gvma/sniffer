package matchers;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.TreeWalker;
import utils.OutputWriter;
import utils.TestClass;
import utils.TestMethod;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class AssertionRouletteMatcher extends SmellMatcher {

    private Integer assertionCount = 0;

    @Override
    protected void match(TestClass testClass) {
        for (TestMethod testMethod : testClass.getTestMethods()) {
            NodeList methodChilds = testMethod.getMethodDeclaration().getChildNodes();
            for (int i = 0; i < methodChilds.getLength(); ++i) {
                Node node = methodChilds.item(i);
                matchAssertionRoulette(node.getChildNodes(), new LinkedList<>());
                if (assertionCount >= 2) {
                    write(testMethod.getTestFilePath(), "Assertion Roulette", testMethod.getMethodName(), new LinkedList<>().toString());
                }
                assertionCount = 0;
            }
        }
    }

    @Override
    public void write(String filePath, String testSmell, String name, String lines) {
        OutputWriter.getInstance().write(filePath, testSmell, name, lines);
        Logger.getLogger(AssertionRouletteMatcher.class.getName()).info("Found assertion roulette in method \"" + name + "\" in lines " + lines);
    }

    private void matchAssertionRoulette(NodeList nodeList, List<Integer> lines) {
        for (int i = 0; i < nodeList.getLength(); ++i) {
            Node root = nodeList.item(i);
            DocumentTraversal traversal = (DocumentTraversal) nodeList.item(i).getOwnerDocument();
            TreeWalker iterator = traversal.createTreeWalker(root, NodeFilter.SHOW_ALL, null, false);
            Node node = null;
            while ((node = iterator.nextNode())!=null){
                if (node.getNodeName().equals("expr") && node.getTextContent().contains("EXPECT_")) {
                    assertionCount++;
                }
            }
        }
    }

}