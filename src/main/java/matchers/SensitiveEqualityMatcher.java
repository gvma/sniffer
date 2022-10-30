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

public class SensitiveEqualityMatcher extends SmellMatcher {
  @Override
  protected void match(TestClass testClass) {
    for (TestMethod testMethod : testClass.getTestMethods()) {
      Node root = testMethod.getMethodDeclaration();
      List<Boolean> hasSensitiveEqualitySmell = new LinkedList<>();
      matchSensitiveEquality(root, hasSensitiveEqualitySmell);
      if (hasSensitiveEqualitySmell.size() > 0) {
        for (Boolean ignored : hasSensitiveEqualitySmell) {
          write(testMethod.getTestFilePath(), "Sensitive Equality", testMethod.getMethodName(), "[]");
        }
      }
    }
  }

  @Override
  public void write(String filePath, String testSmell, String name, String lines) {
    OutputWriter.getInstance().write(filePath, testSmell, name, lines);
    Logger.getLogger(SensitiveEqualityMatcher.class.getName()).info("Found sensitive equality in method \"" + name + "\" in lines " + lines);
  }

  private void matchSensitiveEquality(Node root, List<Boolean> hasSensitiveEqualitySmell) {
    DocumentTraversal traversal = (DocumentTraversal) root.getOwnerDocument();
    TreeWalker iterator = traversal.createTreeWalker(root, NodeFilter.SHOW_ALL, null, false);
    Node node = null;
    while ((node = iterator.nextNode()) != null) {
      String textContent = node.getTextContent().trim();
      if (node.getNodeName().equals("expr") && textContent.toLowerCase().startsWith("assert")) {
        if (node.getTextContent().contains(".toString()")) {
          hasSensitiveEqualitySmell.add(true);
        }
      }
    }
  }
}
