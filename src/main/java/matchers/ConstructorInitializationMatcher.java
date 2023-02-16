package matchers;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.TreeWalker;
import utils.OutputWriter;
import utils.TestClass;

import java.util.logging.Logger;

public class ConstructorInitializationMatcher extends SmellMatcher {
    @Override
    protected void match(TestClass testClass) {
      NodeList root = testClass.getFileContentXml();
      for (int i = 0; i < root.getLength(); ++i) {
        Node node = root.item(i);
        if (node.getNodeName().equals("class")) {
          NodeList classNodeList = node.getChildNodes();
            boolean hasConstructorInitializationSmell = this.matchConstructorInitialization(classNodeList, testClass.getClassName());
            if(hasConstructorInitializationSmell) {
              write(testClass.getAbsolutePath(), "Constructor Initialization", testClass.getClassName(), "[]");
            }
        }
      }
    }

    @Override
    public void write(String filePath, String testSmell, String name, String lines) {
        OutputWriter.getInstance().write(filePath, testSmell, name, lines);
        Logger.getLogger(ConstructorInitializationMatcher.class.getName()).info("Found constructor initialization in class \"" + name + "\" in lines " + lines);
    }


    private boolean matchConstructorInitialization(NodeList nodeList, String className) {
      for (int j = 0; j < nodeList.getLength(); ++j) {
        Node classNode = nodeList.item(j);
        if (classNode.getNodeName().equals("block")) {
          DocumentTraversal traversal = (DocumentTraversal) classNode.getOwnerDocument();
          TreeWalker iterator = traversal.createTreeWalker(classNode, NodeFilter.SHOW_ALL, null, false);
          Node nodeBlock = null;
          while ((nodeBlock = iterator.nextNode()) != null) {
            if (nodeBlock.getNodeName().equals("constructor")) {
              String constructorFunctionName = getConstructorFunctionName(iterator);
              if(constructorFunctionName.equals(className)) {
                return true;
              }
            }
          }
        }
      }
      return false;
    }

    private String getConstructorFunctionName(TreeWalker iterator) {
      Node node = null;
      while ((node = iterator.nextNode()) != null) {
        if(node.getNodeName().equals("name")) {
          return node.getFirstChild().getTextContent().trim();
        }
      }
      return "";
    }
}
