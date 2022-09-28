package matchers;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.TreeWalker;
import utils.OutputWriter;
import utils.TestClass;
import utils.TestMethod;

import java.util.*;
import java.util.logging.Logger;

public class DuplicateAssertMatcher extends SmellMatcher {
  private Map<String, Integer> allAsserts = new HashMap<>();

  @Override
  protected void match(TestClass testClass) {
    for (TestMethod testMethod : testClass.getTestMethods()) {
      Node root = testMethod.getMethodDeclaration();
      getAllAssertsRecursive(root);
      for (Map.Entry<String, Integer> entry : this.allAsserts.entrySet()) {
        boolean hasDuplicateAssertSmell = entry.getValue() > 1;
        if (hasDuplicateAssertSmell) {
          write(testMethod.getTestFilePath(), "Duplicate Assert", testMethod.getMethodName(), new LinkedList<>().toString());
        }
      }
      this.allAsserts.clear();
    }
  }

  @Override
  public void write(String filePath, String testSmell, String name, String lines) {
    OutputWriter.getInstance().write(filePath, testSmell, name, lines);
    Logger.getLogger(AssertionRouletteMatcher.class.getName()).info("Found duplicate assert in method \"" + name + "\" in lines " + lines);
  }

  private void getAllAssertsRecursive(Node root) {
    NodeList rootChildren = root.getChildNodes();
    Node rootChild;
    for(int i = 0; i < rootChildren.getLength(); ++i) {
      rootChild = rootChildren.item(i);
      String textContent = rootChild.getTextContent().trim();
      if (rootChild.getNodeName().equals("expr") && (textContent.startsWith("EXPECT_") || textContent.startsWith("ASSERT_"))) {
        if (this.allAsserts.containsKey(rootChild.getTextContent())) {
          this.allAsserts.put(rootChild.getTextContent(), allAsserts.get(rootChild.getTextContent()) + 1);
        } else {
          this.allAsserts.put(rootChild.getTextContent(), 1);
        }
      }
      getAllAssertsRecursive(rootChild);
    }
  }

  private void getAllAsserts(NodeList nodeList, Map<String, Integer> allAsserts) {
    for (int i = 0; i < nodeList.getLength(); ++i) {
      Node root = nodeList.item(i);
      DocumentTraversal traversal = (DocumentTraversal) nodeList.item(i).getOwnerDocument();
      TreeWalker iterator = traversal.createTreeWalker(root, NodeFilter.SHOW_ALL, null, false);
      Node node = null;
      while ((node = iterator.nextNode())!=null){
        String textContent = node.getTextContent().trim();
        if (node.getNodeName().equals("expr") && (textContent.startsWith("EXPECT_") || textContent.startsWith("ASSERT_"))) {
          if (allAsserts.containsKey(node.getTextContent())) {
            allAsserts.put(node.getTextContent(), allAsserts.get(node.getTextContent()) + 1);
          } else {
            allAsserts.put(node.getTextContent(), 1);
          }
        }
      }
    }
  }
}