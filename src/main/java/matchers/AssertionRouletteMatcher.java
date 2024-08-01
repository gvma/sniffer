package matchers;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.TreeWalker;
import utils.OutputWriter;
import utils.TestClass;
import utils.TestMethod;
import utils.Utils;

import java.util.logging.Logger;

public class AssertionRouletteMatcher extends SmellMatcher {

  private Integer assertionCount = 0;

  @Override
  protected void match(TestClass testClass) {
    for (TestMethod testMethod : testClass.getTestMethods()) {
      Node root = testMethod.getMethodDeclaration();
      boolean hasAssertionRouletteSmell = matchAssertionRoulette(root);
      if (hasAssertionRouletteSmell) {
        write(testMethod.getTestFilePath(), "Assertion Roulette", testMethod.getMethodName(), "["+assertionCount+"]");
      }
    }
  }

  @Override
  public void write(String filePath, String testSmell, String name, String lines) {
    OutputWriter.getInstance().write(filePath, testSmell, name, lines);
    Logger.getLogger(AssertionRouletteMatcher.class.getName()).info("Found assertion roulette in method \"" + name + "\" in lines " + lines);
  }

  private boolean matchAssertionRoulette(Node root) {
    assertionCount = 0;
    int assertsWithNoMessage = 0;
    DocumentTraversal traversal = (DocumentTraversal) root.getOwnerDocument();
    TreeWalker iterator = traversal.createTreeWalker(root, NodeFilter.SHOW_ALL, null, false);
    Node node = null;
    while ((node = iterator.nextNode()) != null) {
      String textContent = node.getTextContent().trim();
      if ((node.getNodeName().equals("expr")|| node.getNodeName().equals("macro")) && (textContent.toLowerCase().trim().startsWith("assert") || textContent.toLowerCase().startsWith("fail"))) {
        assertionCount += 1;
        int numbersOfArguments = Utils.getNumbersOfArguments(node);
        if (numbersOfArguments == 2) {
          assertsWithNoMessage += 1;
        }
      }
    }
    return assertsWithNoMessage > 0 && assertionCount > 1;
  }
}