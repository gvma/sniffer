package projectCrawler;

//import com.sun.xml.internal.bind.annotation.XmlLocation;
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
import java.util.logging.Logger;

public class ProjectCrawler {
    private final List<TestClass> testClasses;
    private final List<TestMethod> testMethods;
    private final File rootFile;

    public ProjectCrawler(String rootDirectory) {
        this.testClasses = new LinkedList<>();
        this.testMethods = new LinkedList<>();
        this.rootFile = new File(rootDirectory);
    }

    public void run() throws IOException, ParserConfigurationException, SAXException {
        run(rootFile);
    }

    private void run(File rootFile) throws IOException, ParserConfigurationException, SAXException {
        File[] listedFiles = rootFile.listFiles();
        if (listedFiles != null) {
            for (File file : listedFiles) {
                if (file.isDirectory()) {
                    run(file);
                } else if (file.isFile()) {
                    if (file.getName().endsWith(".java")) {
                        Logger logger = Logger.getLogger(Sniffer.class.getName());
                        logger.info("Analyzing file " + file.getAbsolutePath());

                        String filePath = file.getAbsolutePath();
                        String xmlFilePath = filePath + ".xml";
                        Runtime rt = Runtime.getRuntime();
                        String executeSRCML = String.format("srcml %s -o %s", filePath, xmlFilePath);
                        rt.exec(executeSRCML);

                        File xmlFile = new File(xmlFilePath);
                        FileReader fileReader = new FileReader(xmlFile);   //reads the file
                        BufferedReader br=new BufferedReader(fileReader);  //creates a buffering character input stream
                        StringBuilder xmlString = new StringBuilder();    //constructs a string buffer with no characters
                        String line;
                        while ((line = br.readLine()) != null) {
                            xmlString.append(line).append("\n");
                        }
                        fileReader.close();

                        InputStream inputStream = new ByteArrayInputStream(xmlString.toString().getBytes());

                        Document doc = PositionalXMLReader.readXML(inputStream);
                        inputStream.close();
                        NodeList childNodes = doc.getFirstChild().getChildNodes();

                        findMethods(childNodes, filePath);

                        for(int i = 0; i < this.testMethods.size(); ++i) {
                            testClasses.add(new TestClass(testMethods, this.testMethods.get(i).getMethodDeclaration().getTextContent(), file.getAbsolutePath(), childNodes));
                        }
                    }
                }
            }
        }
    }

    private void findMethods(NodeList childNodes, String filePath) {
        for (int i = 0; i < childNodes.getLength(); ++i) {
            Node node = childNodes.item(i);
            if (node.getNodeName().equals("class")) {
                NodeList classNodeList = node.getChildNodes();
                for (int j = 0; j < classNodeList.getLength(); ++j) {
                    Node classNode = classNodeList.item(j);
                    if (classNode.getNodeName().equals("block")) {
                        NodeList blockNodeList = classNode.getChildNodes();
                        getTestMethods(blockNodeList, filePath);
                    }
                }
            }
        }
    }

    private void getTestMethods(NodeList blockNodeList, String filePath) {
        for (int k = 0; k < blockNodeList.getLength(); ++k) {
            Node blockNode = blockNodeList.item(k);
            if (blockNode.getNodeName().equals("function")) {
                NodeList functionNodeList = blockNode.getChildNodes();
                boolean isTestMethod = false;
                Node functionContent = blockNode;
                for (int l = 0; l < functionNodeList.getLength(); ++l) {
                    Node functionNode = functionNodeList.item(l);
                    if (functionNode.getNodeName().equals("annotation")) {
                        NodeList annotationNodeList = functionNode.getChildNodes();
                        for (int m = 0; m < annotationNodeList.getLength(); ++m) {
                            Node nameNode = annotationNodeList.item(m);
                            if (nameNode.getNodeName().equals("name")) {
                                Node testNode = nameNode.getFirstChild();
                                if (testNode.getNodeValue().equals("Test")) {
                                    isTestMethod = true;
                                }
                            }
                        }
                    }

                    if (isTestMethod && functionNode.getNodeName().equals("name")) {
                        Node functionNameNode = functionNode.getFirstChild();
                        TestMethod testMethod = new TestMethod(functionNameNode.getNodeValue(), functionContent, filePath);
                        this.testMethods.add(testMethod);
                    }
                }
            }
        }
    }

    public List<TestClass> getTestClasses() {
        return testClasses;
    }
}
