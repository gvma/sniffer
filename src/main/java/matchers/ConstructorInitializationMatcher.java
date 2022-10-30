//package matchers;
//
//import org.w3c.dom.Node;
//import org.w3c.dom.NodeList;
//import org.w3c.dom.traversal.DocumentTraversal;
//import org.w3c.dom.traversal.NodeFilter;
//import org.w3c.dom.traversal.TreeWalker;
//import utils.OutputWriter;
//import utils.TestClass;
//
//import java.util.LinkedList;
//import java.util.logging.Logger;
//
//public class ConstructorInitializationMatcher extends SmellMatcher {
//    private String constructorFunctionName;
//    @Override
//    protected void match(TestClass testClass) {
//      NodeList root = testClass.getFileContentXml();
//      for (int i = 0; i < root.getLength(); ++i) {
//        Node node = root.item(i);
////        System.out.println("Node: " + node.getNodeName() + " CONTENT: " + node.getTextContent());
//        if (node.getNodeName().equals("class")) {
////          System.out.println("Class: " + node.getTextContent());
//          NodeList classNodeList = node.getChildNodes();
//          if(checkIfItsClassTest(node)) {
////            System.out.println("AQUIIII");
//            boolean hasConstructorInitializationSmell = this.matchConstructorInitialization(classNodeList);
//            if(hasConstructorInitializationSmell) {
//              write(testClass.getAbsolutePath(), "Constructor Initialization", this.constructorFunctionName, "[]");
//            }
//          }
//        }
//      }
//    }
//
//    @Override
//    public void write(String filePath, String testSmell, String name, String lines) {
//        OutputWriter.getInstance().write(filePath, testSmell, name, lines);
//        Logger.getLogger(ConstructorInitializationMatcher.class.getName()).info("Found constructor initialization in class \"" + name + "\" in lines " + lines);
//    }
//
//    private boolean hasTestAnnotation(Node functionNode) {
//      NodeList annotationNodeList = functionNode.getChildNodes();
//      for (int m = 0; m < annotationNodeList.getLength(); ++m) {
//        Node nameNode = annotationNodeList.item(m);
//                System.out.println("Node: " + nameNode.getNodeName() + " CONTENT: " + nameNode.getTextContent());
//        if (nameNode != null && nameNode.getNodeName().equals("name")) {
//          Node testNode = nameNode.getFirstChild();
//          if (testNode != null && testNode.getTextContent().trim().equalsIgnoreCase("test")) {
//            System.out.println("Contem Test");
//            return true;
//          }
//        }
//      }
//      return false;
//    }
//
//    private boolean checkIfItsClassTest(Node root) {
//      DocumentTraversal traversal = (DocumentTraversal) root.getOwnerDocument();
//      TreeWalker iterator = traversal.createTreeWalker(root, NodeFilter.SHOW_ALL, null, false);
//      Node node = null;
//      while ((node = iterator.nextNode()) != null) {
//        if (node.getNodeName().equals("annotation")) {
//          System.out.println("ANNOTATION");
//          if(hasTestAnnotation(node)) {
//            return true;
//          }
//        }
//      }
//      return false;
//    }
//
//    private boolean matchConstructorInitialization(NodeList nodeList) {
//      for (int j = 0; j < nodeList.getLength(); ++j) {
//        Node classNode = nodeList.item(j);
//        if (classNode.getNodeName().equals("block")) {
//          DocumentTraversal traversal = (DocumentTraversal) classNode.getOwnerDocument();
//          TreeWalker iterator = traversal.createTreeWalker(classNode, NodeFilter.SHOW_ALL, null, false);
//          Node nodeBlock = null;
//          while ((nodeBlock = iterator.nextNode()) != null) {
//            if (nodeBlock.getNodeName().equals("constructor")) {
//              getConstructorFunctionName(iterator);
//              return true;
//            }
//          }
//        }
//      }
//      return false;
//    }
//
//    private void getConstructorFunctionName(TreeWalker iterator) {
//      Node node = null;
//      while ((node = iterator.nextNode()) != null) {
//        if(node.getNodeName().equals("name")) {
//          String functionName = node.getFirstChild().getTextContent();
//          this.constructorFunctionName = functionName;
//          return;
//        }
//      }
//    }
//}
