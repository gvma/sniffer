package projectCrawler;

import matchers.Sniffer;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import projectCrawler.positionalXMLReader.PositionalXMLReader;
import utils.TestClass;
import utils.TestMethod;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
              String className = file.getName().split(".java")[0];
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
                List<TestMethod> testMethodsList = this.testMethods.stream().collect(Collectors.toList());
                for (TestMethod index : this.testMethods) {
                  logger.info("Find method: " + index.getMethodName());
                }
                TestClass testClass = new TestClass(testMethodsList, rootNode.getTextContent(), file.getAbsolutePath(), rootNode.getChildNodes(), className);
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
