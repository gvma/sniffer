package matchers;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import utils.OutputWriter;
import utils.TestClass;
import utils.TestMethod;

import java.util.logging.Logger;

public class EmptyTestMatcher extends SmellMatcher {

  @Override
  protected void match(TestClass testClass) {
    for (TestMethod testMethod : testClass.getTestMethods()) {
      Node root = testMethod.getMethodDeclaration();
      boolean hasEmptyTestSmell = matchEmptyTest(root);
      if (hasEmptyTestSmell) {
        write(testMethod.getTestFilePath(), "Empty Test", testMethod.getMethodName(), "[]");
      }
    }
  }

  @Override
  public void write(String filePath, String testSmell, String name, String lines) {
    OutputWriter.getInstance().write(filePath, testSmell, name, lines);
    Logger.getLogger(EmptyTestMatcher.class.getName()).info("Found empty test in method \"" + name + "\" in lines " + lines);
  }

  private boolean checkIfItsJustComments(NodeList blockContentChildren) {
    for (int i = 1; i < blockContentChildren.getLength(); ++i) {
      Node node = blockContentChildren.item(i);
      boolean ignoreEmpty = node.getNodeName().equals("#text") && node.getTextContent().trim().isEmpty();
      boolean ignoreStartEndFunctions = node.getNodeName().equals("#text") && (node.getTextContent().trim().equals("}") || node.getTextContent().trim().equals("{"));
      if (node.getNodeName().equals("block_content") || node.getNodeName().equals("block")) {
        return checkIfItsJustComments(node.getChildNodes());
      } else {
        if (!node.getNodeName().equals("comment") && !ignoreEmpty && !ignoreStartEndFunctions) {
          return false;
        }
      }
    }
    return true;
  }

  private boolean matchEmptyTest(Node root) {
    NodeList nodeList = root.getChildNodes();
    return checkIfItsJustComments(nodeList);
  }
}
