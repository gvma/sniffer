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
import java.util.logging.Logger;

public class AssertionRouletteMatcher extends SmellMatcher {

  private Integer assertionCount = 0;

  @Override
  protected void match(TestClass testClass) {
      for (TestMethod testMethod : testClass.getTestMethods()) {
        Node root = testMethod.getMethodDeclaration();
        boolean hasAssertionRouletteSmell = matchAssertionRoulette(root);
        if (hasAssertionRouletteSmell) {
          write(testMethod.getTestFilePath(), "Assertion Roulette", testMethod.getMethodName(), "[]");
        }
      }
  }

  @Override
  public void write(String filePath, String testSmell, String name, String lines) {
    OutputWriter.getInstance().write(filePath, testSmell, name, lines);
    Logger.getLogger(AssertionRouletteMatcher.class.getName()).info("Found assertion roulette in method \"" + name + "\" in lines " + lines);
  }

  private void matchAssertionRoulette(NodeList nodeList) {
    for (int i = 0; i < nodeList.getLength(); ++i) {
      Node root = nodeList.item(i);
      if(root.getNodeName().equals("block_content")) {
        NodeList childrenBlockContent = root.getChildNodes();
        for(int k = 0; k < childrenBlockContent.getLength(); ++k) {
          Node childBlockContent = childrenBlockContent.item(k);
          DocumentTraversal traversal = (DocumentTraversal) childBlockContent.getOwnerDocument();
          TreeWalker iterator = traversal.createTreeWalker(childBlockContent, NodeFilter.SHOW_ALL, null, false);
          Node node = null;
          while ((node = iterator.nextNode()) != null) {
            String textContent = node.getTextContent().trim();
            if (node.getNodeName().equals("expr") && (textContent.startsWith("assert") || textContent.startsWith("Assert"))) {
              this.assertionCount++;
            }
          }
        }
      }
    }
  }

  private boolean matchAssertionRoulette(Node root) {
    int assertionCount = 0;
    DocumentTraversal traversal = (DocumentTraversal) root.getOwnerDocument();
    TreeWalker iterator = traversal.createTreeWalker(root, NodeFilter.SHOW_ALL, null, false);
    Node node = null;
    while ((node = iterator.nextNode())!=null){
      String textContent = node.getTextContent().trim();
      if (node.getNodeName().equals("expr") && (textContent.toLowerCase().startsWith("assert") || textContent.toLowerCase().startsWith("fail"))) {
//        String[] getCommentsFromExpect = textContent.split("<<");
//        boolean hasComments = getCommentsFromExpect.length == 2;
//        if(!hasComments) {
//          assertionCount++;
//        }
      }
    }
    return assertionCount >= 2;
  }
}