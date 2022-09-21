package matchers;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.TreeWalker;
import utils.OutputWriter;
import utils.TestClass;
import utils.TestMethod;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.logging.Logger;
public class ConditionalTestMatcher extends SmellMatcher {

    @Override
    protected void match(TestClass testClass) {
      for (TestMethod testMethod : testClass.getTestMethods()) {
        NodeList childrenMethods = testMethod.getMethodDeclaration().getChildNodes();
        for (int i = 0; i < childrenMethods.getLength(); ++i) {
          Node node = childrenMethods.item(i);
          if(node.getNodeName().equals("block")) {
            Set<Integer> lines = new HashSet<>();
            matchConditionalTest(node.getChildNodes(), lines);
            if (lines.size() != 0) {
              write(testMethod.getTestFilePath(), "Conditional Test", testMethod.getMethodName(), new LinkedList<>().toString());
            }
          }
        }
      }
    }
    @Override
    public void write(String filePath, String testSmell, String name, String lines) {
        OutputWriter.getInstance().write(filePath, testSmell, name, lines);
        Logger.getLogger(AssertionRouletteMatcher.class.getName()).info("Found conditional test in method \"" + name + "\" in lines " + lines);
    }

  private void matchConditionalTest(NodeList nodeList, Set<Integer> lines) {
    for (int i = 0; i < nodeList.getLength(); ++i) {
      Node root = nodeList.item(i);
      if(root.getNodeName().equals("block_content")) {
        NodeList childrenBlockContent = root.getChildNodes();
        for(int k = 0; k < childrenBlockContent.getLength(); ++k) {
          Node childBlockContent = childrenBlockContent.item(k);
          if (childBlockContent.getNodeName().equals("if_stmt") || childBlockContent.getNodeName().equals("for") || childBlockContent.getNodeName().equals("while") || childBlockContent.getNodeName().equals("do")) {
            NodeList childrenConditional = childBlockContent.getChildNodes();
            for (int j = 0; j < childrenConditional.getLength(); ++j) {
              Node childConditional = childrenConditional.item(j);
              DocumentTraversal traversal = (DocumentTraversal) childConditional.getOwnerDocument();
              TreeWalker iterator = traversal.createTreeWalker(childConditional, NodeFilter.SHOW_ALL, null, false);
              Node node = null;
              while ((node = iterator.nextNode()) != null) {
                String textContent = node.getTextContent().trim();
                if (node.getNodeName().equals("expr") && (textContent.startsWith("assert") || textContent.startsWith("Assert"))) {
                  lines.add(0);
                }
              }
            }
          }
        }
      }
    }
  }
}
