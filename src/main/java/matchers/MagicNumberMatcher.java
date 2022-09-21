package matchers;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import utils.OutputWriter;
import utils.TestClass;
import utils.TestMethod;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.logging.Logger;

public class MagicNumberMatcher extends SmellMatcher {

  @Override
  protected void match(TestClass testClass) {
    for (TestMethod testMethod : testClass.getTestMethods()) {
      NodeList childrenMethods = testMethod.getMethodDeclaration().getChildNodes();
      Set<Integer> lines = new HashSet<>();
      for (int i = 0; i < childrenMethods.getLength(); ++i) {
        Node node = childrenMethods.item(i);
        if(node.getNodeName().equals("block")) {
          boolean hasMagicNumberSmell = matchMagicNumber(node.getChildNodes(), lines);
          if (hasMagicNumberSmell) {
            write(testMethod.getTestFilePath(), "Magic Number", testMethod.getMethodName(), new LinkedList<>().toString());
          }
        }
      }
    }
  }

  @Override
  public void write(String filePath, String testSmell, String name, String lines) {
      OutputWriter.getInstance().write(filePath, testSmell, name, lines);
      Logger.getLogger(AssertionRouletteMatcher.class.getName()).info("Found magic number in method \"" + name + "\" in lines " + lines);
  }

  private boolean matchMagicNumber(NodeList nodeList, Set<Integer> lines) {
    for (int i = 0; i < nodeList.getLength(); ++i) {
      Node root = nodeList.item(i);
      if(root.getNodeName().equals("block_content")) {
        NodeList childrenBlockContent = root.getChildNodes();
        for(int j = 0; j < childrenBlockContent.getLength(); ++j) {
          Node childBlockContent = childrenBlockContent.item(j);
          String textContent = childBlockContent.getTextContent().trim();
          if(childBlockContent.getNodeName().equals("expr_stmt")) {
            NodeList childrenExprStmt = childBlockContent.getChildNodes();
            for(int k = 0; k < childrenExprStmt.getLength(); ++k) {
              Node childExprStmt = childrenExprStmt.item(k);
              if(childExprStmt.getNodeName().equals("expr") && (textContent.startsWith("assert") || textContent.startsWith("Assert"))) {
                NodeList childrenExpr = childExprStmt.getChildNodes();
                for(int l = 0; l < childrenExpr.getLength(); ++l) {
                  Node childExpr = childrenExpr.item(l);
                  if(childExpr.getNodeName().equals("call")) {
                    NodeList childrenCall = childExpr.getChildNodes();
                    for(int m = 0; m < childrenCall.getLength(); ++m) {
                      Node callChild = childrenCall.item(m);
                      if(callChild.getNodeName().equals("argument_list")) {
                        NodeList childrenArgumentList = callChild.getChildNodes();
                        for(int n = 0; n < childrenArgumentList.getLength(); ++ n) {
                          Node argumentListChild = childrenArgumentList.item(n);
                          if(argumentListChild.getNodeName().equals("argument")) {
                            NodeList argumentChildren = argumentListChild.getChildNodes();
                            for(int o = 0; o < argumentChildren.getLength(); ++o) {
                              Node argumentChild = argumentChildren.item(o);
                              if(argumentChild.getNodeName().equals("expr")) {
                                NodeList exprChildren = argumentChild.getChildNodes();
                                for(int p = 0; p < exprChildren.getLength(); ++p) {
                                  Node exprChild = exprChildren.item(p);
                                  if(exprChild.getNodeName().equals("literal")) {
                                    NamedNodeMap literalAttributes = exprChild.getAttributes();
                                    boolean hasAttributes = literalAttributes.getLength() > 0;
                                    boolean isAttributeNumber = hasAttributes && literalAttributes.item(0).getNodeValue().equals("number");
                                    if(isAttributeNumber) {
                                      return true;
                                    }
                                  }
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
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
