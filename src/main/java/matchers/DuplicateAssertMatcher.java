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

  @Override
  protected void match(TestClass testClass) {
    for (TestMethod testMethod : testClass.getTestMethods()) {
      NodeList childrenMethods = testMethod.getMethodDeclaration().getChildNodes();
      Map<String, Integer> allAsserts = new HashMap<>();
      getAllAsserts(childrenMethods, allAsserts);
      for (Map.Entry<String, Integer> entry : allAsserts.entrySet()) {
        if (entry.getValue() > 1) {
          write(testMethod.getTestFilePath(), "Duplicate Assert", testMethod.getMethodName(), new LinkedList<>().toString());
        }
      }
    }
  }

  @Override
  public void write(String filePath, String testSmell, String name, String lines) {
    OutputWriter.getInstance().write(filePath, testSmell, name, lines);
    Logger.getLogger(AssertionRouletteMatcher.class.getName()).info("Found duplicate assert in method \"" + name + "\" in lines " + lines);
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
//          System.out.println("Node: " + node.getNodeName() + " content: " + textContent);
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