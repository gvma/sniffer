package matchers;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.TreeWalker;
import utils.OutputWriter;
import utils.TestClass;

import java.util.LinkedList;
import java.util.logging.Logger;

public class ConstructorInitializationMatcher extends SmellMatcher {
    private String constructorFunctionName;
    @Override
    protected void match(TestClass testClass) {
      NodeList root = testClass.getFileContentXml();
      for (int i = 0; i < root.getLength(); ++i) {
        Node node = root.item(i);
        if (node.getNodeName().equals("class")) {
          NodeList classNodeList = node.getChildNodes();
          boolean hasConstructorInitializationSmell = this.matchConstructorInitialization(classNodeList);
          if(hasConstructorInitializationSmell) {
            write(testClass.getAbsolutePath(), "Constructor Initialization", this.constructorFunctionName, new LinkedList<>().toString());
          }
        }
      }
    }

    @Override
    public void write(String filePath, String testSmell, String name, String lines) {
        OutputWriter.getInstance().write(filePath, testSmell, name, lines);
        Logger.getLogger(AssertionRouletteMatcher.class.getName()).info("Found constructor initialization in class \"" + name + "\" in lines " + lines);
    }

    private boolean matchConstructorInitialization(NodeList nodeList) {
      for (int j = 0; j < nodeList.getLength(); ++j) {
        Node classNode = nodeList.item(j);
        if (classNode.getNodeName().equals("block")) {
          DocumentTraversal traversal = (DocumentTraversal) classNode.getOwnerDocument();
          TreeWalker iterator = traversal.createTreeWalker(classNode, NodeFilter.SHOW_ALL, null, false);
          Node nodeBlock = null;
          while ((nodeBlock = iterator.nextNode()) != null) {
            if (nodeBlock.getNodeName().equals("constructor")) {
              getConstructorFunctionName(iterator);
              return true;
            }
          }
        }
      }
      return false;
    }

    private void getConstructorFunctionName(TreeWalker iterator) {
      Node node = null;
      while ((node = iterator.nextNode()) != null) {
        if(node.getNodeName().equals("name")) {
          String functionName = node.getFirstChild().getNodeValue();
          this.constructorFunctionName = functionName;
          return;
        }
      }
    }
}
