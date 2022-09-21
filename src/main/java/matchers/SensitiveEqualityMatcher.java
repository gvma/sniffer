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

public class SensitiveEqualityMatcher extends SmellMatcher{
    @Override
    protected void match(TestClass testClass) {
      for (TestMethod testMethod : testClass.getTestMethods()) {
        NodeList childrenMethods = testMethod.getMethodDeclaration().getChildNodes();
        for (int i = 0; i < childrenMethods.getLength(); ++i) {
          Node node = childrenMethods.item(i);
          if(node.getNodeName().equals("block")) {
            boolean hasSensitiveEqualitySmell = matchSensitiveEquality(node.getChildNodes());
            if (hasSensitiveEqualitySmell) {
              write(testMethod.getTestFilePath(), "Sensitive Equality", testMethod.getMethodName(), new LinkedList<>().toString());
            }
          }
        }
      }
    }

    @Override
    public void write(String filePath, String testSmell, String name, String lines) {
        OutputWriter.getInstance().write(filePath, testSmell, name, lines);
        Logger.getLogger(AssertionRouletteMatcher.class.getName()).info("Found sensitive equality in method \"" + name + "\" in lines " + lines);
    }

    private boolean matchSensitiveEquality(NodeList nodeList) {
      for (int i = 0; i < nodeList.getLength(); ++i) {
        Node root = nodeList.item(i);
        if(root.getNodeName().equals("block_content")) {
          NodeList childrenBlockContent = root.getChildNodes();
          for(int k = 0; k < childrenBlockContent.getLength(); ++k) {
            Node childBlockContent = childrenBlockContent.item(k);
            DocumentTraversal traversal = (DocumentTraversal) childBlockContent.getOwnerDocument();
            TreeWalker iterator = traversal.createTreeWalker(childBlockContent, NodeFilter.SHOW_ALL, null, false);
            Node node = null;
            while ((node = iterator.nextNode()) != null) {
              String textContent = node.getTextContent().trim();
              if (node.getNodeName().equals("expr") && (textContent.startsWith("assert") || textContent.startsWith("Assert"))) {
                if(node.getTextContent().contains("toString")) {
                  return true;
                }
              }
            }
          }
        }
      }
      return false;
    }
}
