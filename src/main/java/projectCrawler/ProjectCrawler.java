package projectCrawler;

import matchers.Sniffer;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.TreeWalker;
import org.xml.sax.SAXException;
import projectCrawler.positionalXMLReader.PositionalXMLReader;
import utils.TestClass;
import utils.TestMethod;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ProjectCrawler {
  private final List<TestClass> testClasses;
  private final List<TestMethod> testMethods;
  private final File rootFile;

  public ProjectCrawler(String rootDirectory) {
    this.testClasses = new LinkedList<>();
    this.testMethods = new LinkedList<>();
    this.rootFile = new File(rootDirectory);
  }

  public void run() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
    run(rootFile);
  }

  private void run(File rootFile) throws IOException, SAXException, InterruptedException {
    File[] listedFiles = rootFile.listFiles();
    if (listedFiles != null) {
      for (File file : listedFiles) {
        if (file.isDirectory()) {
          run(file);
        } else if (file.isFile()) {
          if (file.getName().endsWith(".java")) {
            Logger logger = Logger.getLogger(Sniffer.class.getName());
            try {
              logger.info("Analyzing file " + file.getAbsolutePath());
              String filePath = file.getAbsolutePath();
              String xmlFilePath = filePath + ".xml";
              Runtime rt = Runtime.getRuntime();
              Process process = rt.exec(String.format("srcml %s -o %s", filePath, xmlFilePath));
              process.waitFor();
              File xmlFile = new File(xmlFilePath);
              FileReader fileReader = new FileReader(xmlFile);
              BufferedReader br = new BufferedReader(fileReader);
              StringBuilder xmlString = new StringBuilder();
              String line;
              while ((line = br.readLine()) != null) {
                xmlString.append(line).append("\n");
              }
              fileReader.close();
              InputStream inputStream = new ByteArrayInputStream(xmlString.toString().getBytes());
              Document doc = PositionalXMLReader.readXML(inputStream);
              inputStream.close();
              Node rootNode = doc.getFirstChild();
              findMethods(rootNode, filePath);
              if (this.testMethods.size() > 0) {
//                                List<TestMethod> testMethodsList = new LinkedList<>(this.checkIntegrityOfMethods());
                List<TestMethod> testMethodsList = this.testMethods.stream().collect(Collectors.toList());
                for (TestMethod index : this.testMethods) {
                  logger.info("Find method: " + index.getMethodName());
                }
                TestClass testClass = new TestClass(testMethodsList, rootNode.getTextContent(), file.getAbsolutePath(), rootNode.getChildNodes());
                this.testClasses.add(testClass);
                this.testMethods.clear();
              }
            } catch (Exception e) {
              logger.log(Level.SEVERE, "Unable to find test methods due to: " + e.getMessage(), e);
            }

          }
        }
      }
    }
  }
  // Ainda precisa adaptar para java.
  private List<TestMethod> checkIntegrityOfMethods() {
    List<TestMethod> testMethodsList = this.testMethods.stream().collect(Collectors.toList());
    List<Integer> removeIndex = new ArrayList<>();
    List<TestMethod> newNodesToAdd = new LinkedList<>();
    for (int i = 0; i < testMethodsList.size(); ++i) {
      // TEST.*\(.*\)\s*{\X*}
      String regexPattern = "TEST.*\\(.*\\)\\s*\\{";
      Pattern pattern = Pattern.compile(regexPattern);
      Matcher m = pattern.matcher(testMethodsList.get(i).getMethodDeclaration().getTextContent());
      boolean hasExtraTestMethods = m.find();
      if (hasExtraTestMethods) {
        TestMethod currentTestMethod = testMethodsList.get(i);
        removeIndex.add(i);
        Node newNode = removeExtraTestMethods(currentTestMethod.getMethodDeclaration());
        boolean isFreakMethod = removeFreakTestMethods(newNode);
        if (!isFreakMethod) {
          TestMethod newTestMethod = new TestMethod(currentTestMethod.getMethodName(), newNode, currentTestMethod.getTestFilePath());
          newNodesToAdd.add(newTestMethod);
          continue;
        }
      }
      boolean isFreakMethod = removeFreakTestMethods(testMethodsList.get(i).getMethodDeclaration());
      if (isFreakMethod) {
        removeIndex.add(i);
      }
    }
    removeIndex.sort(Collections.reverseOrder());
    for (Integer index : removeIndex) {
      TestMethod remove = testMethodsList.get(index);
      Logger.getLogger(Sniffer.class.getName()).info("Removing freak method: " + remove.getMethodName());
      testMethodsList.remove(remove);
    }
    testMethodsList.addAll(newNodesToAdd);
    return testMethodsList;
  }

  // Ainda precisa adaptar para java.
  private Node removeExtraTestMethods(Node root) {
    DocumentTraversal traversal = (DocumentTraversal) root.getFirstChild().getOwnerDocument();
    TreeWalker iterator = traversal.createTreeWalker(root, NodeFilter.SHOW_ALL, null, false);
    Node node = null;
    boolean isMacro = false;
    Node macroNode = null;
    while ((node = iterator.nextNode()) != null) {
      if (node.getNodeName().equals("macro") && node.getTextContent().startsWith("TEST")) {
        macroNode = node;
        isMacro = true;
      }
      if (isMacro && node.getNodeName().equals("block")) {
        isMacro = false;
        Node parent = node.getParentNode();
        parent.removeChild(node);
      }
    }
    if (macroNode != null) {
      Node parent = macroNode.getParentNode();
      parent.removeChild(macroNode);
    }
    return root;
  }

  private boolean removeFreakTestMethods(Node root) {
    String rootString = root.getTextContent();
    Stack<Boolean> stack = new Stack<>();
    for (int i = 0; i < rootString.length(); ++i) {
      if (rootString.charAt(i) == '{') {
        stack.push(true);
      }
      if (!stack.isEmpty() && rootString.charAt(i) == '}') {
        stack.pop();
      }
    }
    return !stack.isEmpty();
  }

  private void findMethods(Node rootNode, String filePath) {
    NodeList childNodes = rootNode.getChildNodes();
    for (int i = 0; i < childNodes.getLength(); ++i) {
      Node node = childNodes.item(i);
      if (node != null && node.getNodeName().equals("class")) {
        NodeList classNodeList = node.getChildNodes();
        for (int j = 0; j < classNodeList.getLength(); ++j) {
          Node classNode = classNodeList.item(j);
          if (classNode != null && classNode.getNodeName().equals("block")) {
            NodeList blockNodeList = classNode.getChildNodes();
            if (blockNodeList.getLength() > 0) {
              getTestMethods(blockNodeList, filePath);
            }
          }
        }
      }
    }
  }

  private boolean hasTestAnnotation(Node functionNode) {
    NodeList annotationNodeList = functionNode.getChildNodes();
    for (int m = 0; m < annotationNodeList.getLength(); ++m) {
      Node nameNode = annotationNodeList.item(m);
      if (nameNode != null && nameNode.getNodeName().equals("name")) {
        Node testNode = nameNode.getFirstChild();
        if (testNode != null && testNode.getTextContent().trim().equalsIgnoreCase("test")) {
          return true;
        }
      }
    }
    return false;
  }

  private void getTestMethods(NodeList blockNodeList, String filePath) {
    String methodName = "";
    for (int k = 0; k < blockNodeList.getLength(); ++k) {
      Node blockNode = blockNodeList.item(k);
      if (blockNode != null && blockNode.getNodeName().equals("function")) {
        NodeList functionNodeList = blockNode.getChildNodes();
        boolean isTestMethod = false;
        for (int l = 0; l < functionNodeList.getLength(); ++l) {
          Node functionNode = functionNodeList.item(l);
          if (functionNode != null && functionNode.getNodeName().equals("annotation")) {
            if (hasTestAnnotation(functionNode)) {
              isTestMethod = true;
            }
          }
          if (functionNode != null && isTestMethod && functionNode.getNodeName().equals("name")) {
            methodName = functionNode.getFirstChild().getTextContent();
          }

          if (functionNode != null && isTestMethod && functionNode.getNodeName().equals("block")) {
            this.testMethods.add(new TestMethod(methodName, functionNode, filePath));
            methodName = "";
          }
        }
      }
    }
  }

  public List<TestClass> getTestClasses() {
    return testClasses;
  }
}
