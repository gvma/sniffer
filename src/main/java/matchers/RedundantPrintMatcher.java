package matchers;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import utils.OutputWriter;
import utils.TestClass;
import utils.TestMethod;

import java.util.LinkedList;
import java.util.logging.Logger;

public class RedundantPrintMatcher extends SmellMatcher {

    @Override
    protected void match(TestClass testClass) {
      for (TestMethod testMethod : testClass.getTestMethods()) {
        NodeList childrenMethods = testMethod.getMethodDeclaration().getChildNodes();
        for (int i = 0; i < childrenMethods.getLength(); ++i) {
          Node node = childrenMethods.item(i);
          if(node.getNodeName().equals("block")) {
            boolean hasRedundantPrintSmell = matchRedundantPrint(node.getChildNodes());
            if (hasRedundantPrintSmell) {
              write(testMethod.getTestFilePath(), "Redundant Print", testMethod.getMethodName(), new LinkedList<>().toString());
            }
          }
        }
      }
    }

    @Override
    public void write(String filePath, String testSmell, String name, String lines) {
        OutputWriter.getInstance().write(filePath, testSmell, name, lines);
        Logger.getLogger(AssertionRouletteMatcher.class.getName()).info("Found redundant print in method \"" + name + "\" in lines " + lines);
    }

    private Boolean matchRedundantPrint(NodeList nodeList) {
      for (int i = 0; i < nodeList.getLength(); ++i) {
        Node root = nodeList.item(i);
        if(root.getNodeName().equals("block_content")) {
          NodeList childrenBlockContent = root.getChildNodes();
          for(int k = 0; k < childrenBlockContent.getLength(); ++k) {
            Node childBlockContent = childrenBlockContent.item(k);
            if(childBlockContent.getTextContent().contains("System.out")) {
              return true;
            }
          }
        }
      }
      return false;
    }
}
