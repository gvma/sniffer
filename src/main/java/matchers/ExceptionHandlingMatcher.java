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

public class ExceptionHandlingMatcher extends SmellMatcher {

  boolean hasExceptionHandlingSmell = false;

  @Override
  protected void match(TestClass testClass) {
    for (TestMethod testMethod : testClass.getTestMethods()) {
      Node root = testMethod.getMethodDeclaration();
      System.out.println(testMethod.getMethodDeclaration().getTextContent());
      matchExceptionHandlingRecursive(root, false);
      if (hasExceptionHandlingSmell) {
        write(testMethod.getTestFilePath(), "Exception Handling", testMethod.getMethodName(), new LinkedList<>().toString());
      }
      this.hasExceptionHandlingSmell = false;
    }
  }

  @Override
  public void write(String filePath, String testSmell, String name, String lines) {
    OutputWriter.getInstance().write(filePath, testSmell, name, lines);
    Logger.getLogger(AssertionRouletteMatcher.class.getName()).info("Found Exception Handling in method \"" + name + "\" in line " + lines);
  }

  // Abordagem recursiva
  private void matchExceptionHandlingRecursive(Node root, boolean isInsideTryCatch) {
    NodeList rootChildren = root.getChildNodes();
    Node rootChild = null;
    for(int i = 0; i < rootChildren.getLength(); ++i) {
      rootChild = rootChildren.item(i);
//      System.out.println("Node: " + rootChild.getNodeName() + " content: " + rootChild.getTextContent());
      if(rootChild.getNodeName().equals("try") || rootChild.getNodeName().equals("catch")) {
        matchExceptionHandlingRecursive(rootChild, true);
      }
      String textContent = rootChild.getTextContent().trim();
      if(isInsideTryCatch && rootChild.getNodeName().equals("expr") && (textContent.startsWith("EXPECT_") || textContent.startsWith("FAIL") || textContent.startsWith("ASSERT_"))) {
        this.hasExceptionHandlingSmell = true;
      }
      matchExceptionHandlingRecursive(rootChild, isInsideTryCatch);
    }
  }
  // Segunda abordagem
  private boolean matchExceptionHandling(NodeList nodeList) {
    for(int j = 0; j < nodeList.getLength(); ++j) {
      Node childBlockContent = nodeList.item(j);
      System.out.println("Node: " + childBlockContent.getNodeName() + " content: " + childBlockContent.getTextContent());
      if (childBlockContent.getNodeName().equals("try") || childBlockContent.getNodeName().equals("catch")) {
        NodeList childrenTryContent = childBlockContent.getChildNodes();
        for (int k = 0; k < childrenTryContent.getLength(); ++k) {
          Node childTryContent = childrenTryContent.item(k);
          DocumentTraversal traversal = (DocumentTraversal) childTryContent.getOwnerDocument();
          TreeWalker iterator = traversal.createTreeWalker(childTryContent, NodeFilter.SHOW_ALL, null, false);
          Node node = null;
          while ((node = iterator.nextNode()) != null) {
            String textContent = node.getTextContent().trim();
            if (node.getNodeName().equals("expr") && (textContent.startsWith("EXPECT_") || textContent.startsWith("FAIL_") || textContent.startsWith("ASSERT_"))) {
              return true;
            }
          }
        }
      }
    }
    return false;
  }
}