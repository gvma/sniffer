package matchers;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import utils.OutputWriter;
import utils.TestClass;
import utils.TestMethod;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class ConditionalTestMatcher extends SmellMatcher {

  @Override
  protected void match(TestClass testClass) {
    for (TestMethod testMethod : testClass.getTestMethods()) {
      NodeList methodChildren = testMethod.getMethodDeclaration().getChildNodes();
      for (int i = 0; i < methodChildren.getLength(); ++i) {
        Node node = methodChildren.item(i);
        List<Boolean> hasConditionalSmell = new LinkedList<>();
        matchConditionalTestRecursive(node, false, hasConditionalSmell);
        if (hasConditionalSmell.size() > 0) {
          for (Boolean ignored : hasConditionalSmell) {
            write(testMethod.getTestFilePath(), "Conditional Test", testMethod.getMethodName(), "[]");
          }
        }
      }
    }
  }

  @Override
  public void write(String filePath, String testSmell, String name, String lines) {
    OutputWriter.getInstance().write(filePath, testSmell, name, lines);
    Logger.getLogger(ConditionalTestMatcher.class.getName()).info("Found conditional test in method \"" + name + "\" in lines " + lines);
  }

  private void matchConditionalTestRecursive(Node root, boolean isInsideConditional, List<Boolean> hasConditionalSmell) {
    NodeList rootChildren = root.getChildNodes();
    Node rootChild;
    for (int i = 0; i < rootChildren.getLength(); ++i) {
      rootChild = rootChildren.item(i);
      String textContent = rootChild.getTextContent().trim();
      if (root.getNodeName().equals("if_stmt") || root.getNodeName().equals("for") || root.getNodeName().equals("while") || root.getNodeName().equals("do")) {
        matchConditionalTestRecursive(rootChild, true, hasConditionalSmell);
      } else {
        if (isInsideConditional && rootChild.getNodeName().equals("expr") && textContent.toLowerCase().startsWith("assert")) {
          hasConditionalSmell.add(true);
        }
        matchConditionalTestRecursive(rootChild, isInsideConditional, hasConditionalSmell);
      }
    }
  }
}
