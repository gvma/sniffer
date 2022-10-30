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

public class RedundantPrintMatcher extends SmellMatcher {

  @Override
  protected void match(TestClass testClass) {
    for (TestMethod testMethod : testClass.getTestMethods()) {
      Node root = testMethod.getMethodDeclaration();
      List<Boolean> hasRedundantPrintSmell = new LinkedList<>();
      matchRedundantPrint(root, hasRedundantPrintSmell);
      if (hasRedundantPrintSmell.size() > 0) {
        for (Boolean ignored : hasRedundantPrintSmell) {
          write(testMethod.getTestFilePath(), "Redundant Print", testMethod.getMethodName(), "[]");
        }
      }
    }
  }

  @Override
  public void write(String filePath, String testSmell, String name, String lines) {
    OutputWriter.getInstance().write(filePath, testSmell, name, lines);
    Logger.getLogger(RedundantPrintMatcher.class.getName()).info("Found redundant print in method \"" + name + "\" in lines " + lines);
  }

  private void matchRedundantPrint(Node root, List<Boolean> hasRedundantPrintSmell) {
    DocumentTraversal traversal = (DocumentTraversal) root.getOwnerDocument();
    TreeWalker iterator = traversal.createTreeWalker(root, NodeFilter.SHOW_ALL, null, false);
    Node node = null;
    while ((node = iterator.nextNode()) != null) {
      String textContent = node.getTextContent().trim();
      if (node.getNodeName().equals("expr") && textContent.startsWith("System.out")) {
        hasRedundantPrintSmell.add(true);
      }
    }
  }
}
