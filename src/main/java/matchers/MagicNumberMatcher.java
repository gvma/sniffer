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
import java.util.List;
import java.util.logging.Logger;

public class MagicNumberMatcher extends SmellMatcher {

  @Override
  protected void match(TestClass testClass) {
    for (TestMethod testMethod : testClass.getTestMethods()) {
      NodeList methodChildren = testMethod.getMethodDeclaration().getChildNodes();
      for (int i = 0; i < methodChildren.getLength(); ++i) {
        Node node = methodChildren.item(i);
        List<Boolean> lines = new LinkedList<>();
        matchMagicNumber(node.getChildNodes(), lines);
        if (lines.size() > 0) {
          for (Boolean ignored : lines) {
            write(testMethod.getTestFilePath(), "Magic Number", testMethod.getMethodName(), "[]");
          }
        }
      }
    }
  }

  @Override
  public void write(String filePath, String testSmell, String name, String lines) {
    OutputWriter.getInstance().write(filePath, testSmell, name, lines);
    Logger.getLogger(MagicNumberMatcher.class.getName()).info("Found magic number in method \"" + name + "\" in lines " + lines);
  }

  private void matchMagicNumber(NodeList nodeList, List<Boolean> lines) {
    for (int i = 0; i < nodeList.getLength(); ++i) {
      Node root = nodeList.item(i);
      DocumentTraversal traversal = (DocumentTraversal) nodeList.item(i).getOwnerDocument();
      TreeWalker iterator = traversal.createTreeWalker(root, NodeFilter.SHOW_ALL, null, false);
      Node node = null;
      while ((node = iterator.nextNode()) != null) {
        boolean isSameExpect = false;
        String textContent = node.getTextContent();
        if (node.getNodeName().equals("expr") && textContent.toLowerCase().startsWith("assert")) {
          DocumentTraversal expectTraversal = (DocumentTraversal) nodeList.item(i).getOwnerDocument();
          TreeWalker expectIterator = expectTraversal.createTreeWalker(node, NodeFilter.SHOW_ALL, null, false);
          Node itNode = null;
          while ((itNode = expectIterator.nextNode()) != null) {
            if (itNode.getNodeName().equals("literal")) {
              if (this.isNumber(itNode) && !isSameExpect) {
                isSameExpect = true;
                lines.add(true);
              }
            }
          }
        }
      }
    }
  }

  private boolean isNumber(Node node) {
    String textContent = node.getTextContent();
    return !(textContent.startsWith("\"") || textContent.startsWith("'") || textContent.equals("true")
            || textContent.equals("false") || textContent.equals("null"));
  }
}
