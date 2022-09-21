package matchers;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import utils.OutputWriter;
import utils.TestClass;
import utils.TestMethod;

import java.util.LinkedList;
import java.util.logging.Logger;

public class EmptyTestMatcher extends SmellMatcher {
    @Override
    protected void match(TestClass testClass) {
      for (TestMethod testMethod : testClass.getTestMethods()) {
        NodeList childrenMethods = testMethod.getMethodDeclaration().getChildNodes();
        for (int i = 0; i < childrenMethods.getLength(); ++i) {
          Node node = childrenMethods.item(i);
          if(node.getNodeName().equals("block")) {
            boolean hasEmptyTestSmell = matchEmptyTest(node.getChildNodes());
            if(hasEmptyTestSmell) {
              write(testMethod.getTestFilePath(), "Empty Test", testMethod.getMethodName(), new LinkedList<>().toString());
            }
          }
        }
      }
    }

    @Override
    public void write(String filePath, String testSmell, String name, String lines) {
        OutputWriter.getInstance().write(filePath, testSmell, name, lines);
        Logger.getLogger(AssertionRouletteMatcher.class.getName()).info("Found empty test in method \"" + name + "\" in lines " + lines);
    }

    private boolean matchEmptyTest(NodeList nodeList) {
      for (int i = 0; i < nodeList.getLength(); ++i) {
        Node root = nodeList.item(i);
        if(root.getNodeName().equals("block_content")) {
          NodeList childrenBlockContent = root.getChildNodes();
          String blockContentText = childrenBlockContent.item(0).getTextContent().trim();
          boolean isBlockContentEmpty = blockContentText.isEmpty();
          if(childrenBlockContent.getLength() == 1 && isBlockContentEmpty) {
            return true;
          }
        }
      }
      return false;
    }
}
