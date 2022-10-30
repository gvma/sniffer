package matchers;

import org.w3c.dom.Node;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.TreeWalker;
import utils.OutputWriter;
import utils.TestClass;
import utils.TestMethod;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class TestRunWarMatcher extends SmellMatcher {
  @Override
  protected void match(TestClass testClass) {
    for (TestMethod testMethod : testClass.getTestMethods()) {
      Node root = testMethod.getMethodDeclaration();
      List<Boolean> hasTestRunWarSmell = new LinkedList<>();
      matchTestRunWar(root, hasTestRunWarSmell);
      if (hasTestRunWarSmell.size() > 0) {
        for (Boolean ignored : hasTestRunWarSmell) {
          write(testMethod.getTestFilePath(), "Test Run War", testMethod.getMethodName(), "[]");
        }
      }
    }
  }

  @Override
  public void write(String filePath, String testSmell, String name, String lines) {
    OutputWriter.getInstance().write(filePath, testSmell, name, lines);
    Logger.getLogger(TestRunWarMatcher.class.getName()).info("Found test run war in method \"" + name + "\" in lines " + lines);
  }

  private void matchTestRunWar(Node root, List<Boolean> hasTestRunWarSmell) {
    DocumentTraversal traversal = (DocumentTraversal) root.getOwnerDocument();
    TreeWalker iterator = traversal.createTreeWalker(root, NodeFilter.SHOW_ALL, null, false);
    Node node = null;
    while ((node = iterator.nextNode()) != null) {
      String textContent = node.getTextContent().trim();
      if (node.getNodeName().equals("expr") && (textContent.toLowerCase().startsWith("system.get") || textContent.toLowerCase().startsWith("system.set"))) {
        hasTestRunWarSmell.add(true);
      }
    }
  }
}
