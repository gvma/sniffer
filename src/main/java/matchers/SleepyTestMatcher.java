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

public class SleepyTestMatcher extends SmellMatcher {
  @Override
  protected void match(TestClass testClass) {
    for (TestMethod testMethod : testClass.getTestMethods()) {
      Node root = testMethod.getMethodDeclaration();
      List<Boolean> hasSleepTestSmell = new LinkedList<>();
      matchSleepyTest(root, hasSleepTestSmell);
      if (hasSleepTestSmell.size() > 0) {
        for (Boolean ignored : hasSleepTestSmell) {
          write(testMethod.getTestFilePath(), "Sleep Test", testMethod.getMethodName(), "[]");
        }
      }
    }
  }

  @Override
  public void write(String filePath, String testSmell, String name, String lines) {
    OutputWriter.getInstance().write(filePath, testSmell, name, lines);
    Logger.getLogger(SleepyTestMatcher.class.getName()).info("Found sleepy test in method \"" + name + "\" in lines " + lines);
  }

  private void matchSleepyTest(Node root, List<Boolean> hasSleepTestSmell) {
    DocumentTraversal traversal = (DocumentTraversal) root.getOwnerDocument();
    TreeWalker iterator = traversal.createTreeWalker(root, NodeFilter.SHOW_ALL, null, false);
    Node node = null;
    while ((node = iterator.nextNode()) != null) {
      String textContent = node.getTextContent().trim();
      if (node.getNodeName().equals("expr") && (textContent.toLowerCase().startsWith("sleep(") || textContent.startsWith("Thread.sleep("))) {
        hasSleepTestSmell.add(true);
      }
    }
  }
}
