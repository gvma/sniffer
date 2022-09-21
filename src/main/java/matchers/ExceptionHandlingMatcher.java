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

  @Override
  protected void match(TestClass testClass) {
    for (TestMethod testMethod : testClass.getTestMethods()) {
      NodeList childrenMethods = testMethod.getMethodDeclaration().getChildNodes();
      for (int i = 0; i < childrenMethods.getLength(); ++i) {
        Node node = childrenMethods.item(i);
        if(node.getNodeName().equals("block")) {
          boolean hasExceptionHandlingSmell = matchExceptionHandling(node.getChildNodes());
          if (hasExceptionHandlingSmell) {
            write(testMethod.getTestFilePath(), "Exception Handling", testMethod.getMethodName(), new LinkedList<>().toString());
          }
        }
      }
    }
  }

  @Override
  public void write(String filePath, String testSmell, String name, String lines) {
    OutputWriter.getInstance().write(filePath, testSmell, name, lines);
    Logger.getLogger(AssertionRouletteMatcher.class.getName()).info("Found Exception Handling in method \"" + name + "\" in line " + lines);
  }

  private boolean matchExceptionHandling(NodeList nodeList) {
    for (int i = 0; i < nodeList.getLength(); ++i) {
      Node root = nodeList.item(i);
      if(root.getNodeName().equals("block_content")) {
        NodeList childrenBlockContent = root.getChildNodes();
        for(int j = 0; j < childrenBlockContent.getLength(); ++j) {
          Node childBlockContent = childrenBlockContent.item(j);
          if (childBlockContent.getNodeName().equals("try") || childBlockContent.getNodeName().equals("catch")) {
            NodeList childrenTryContent = childBlockContent.getChildNodes();
            for(int k = 0; k < childrenTryContent.getLength(); ++k) {
              Node childTryContent = childrenTryContent.item(k);
              DocumentTraversal traversal = (DocumentTraversal) childTryContent.getOwnerDocument();
              TreeWalker iterator = traversal.createTreeWalker(childTryContent, NodeFilter.SHOW_ALL, null, false);
              Node node = null;
              while ((node = iterator.nextNode()) != null) {
                String textContent = node.getTextContent().trim();
                if (node.getNodeName().equals("expr") && (textContent.startsWith("assert") || textContent.startsWith("Assert") || textContent.startsWith("fail"))) {
                  return true;
                }
              }
            }
          }
        }
      }
    }
    return false;
  }
}