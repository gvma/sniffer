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
import java.util.logging.Logger;

public class ProjectCrawler {
    private final List<TestClass> testClasses;
    private final File rootFile;

    public ProjectCrawler(String rootDirectory) {
        this.testClasses = new LinkedList<>();
        this.rootFile = new File(rootDirectory);
    }

    public void run() throws IOException, ParserConfigurationException, SAXException {
        run(rootFile);
    }

    private void run(File rootFile) throws IOException, SAXException {
        File[] listedFiles = rootFile.listFiles();
        if (listedFiles != null) {
            for (File file : listedFiles) {
                if (file.isDirectory()) {
                    run(file);
                } else if (file.isFile()) {
                    if (file.getName().endsWith(".cpp")) {
                        Logger logger = Logger.getLogger(Sniffer.class.getName());
                        logger.info("Analyzing file " + file.getAbsolutePath());

                        String filePath = file.getAbsolutePath();
                        String xmlFilePath = filePath + ".xml";
                        Runtime rt = Runtime.getRuntime();
                        rt.exec(String.format("C:\\Program Files\\srcML\\srcml \"%s\" -o \"%s\"", filePath, xmlFilePath));

                        File xmlFile = new File(xmlFilePath);
                        FileReader fileReader = new FileReader(xmlFile);
                        BufferedReader br=new BufferedReader(fileReader);
                        StringBuilder xmlString = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            xmlString.append(line).append("\n");
                        }
                        fileReader.close();

                        InputStream inputStream = new ByteArrayInputStream(xmlString.toString().getBytes());

                        Document doc = PositionalXMLReader.readXML(inputStream);
                        inputStream.close();
                        NodeList childNodes = doc.getFirstChild().getChildNodes();

                        findMethods(childNodes, file, false);
                        findMethods(childNodes, file, true);
                    }
                }
            }
        }
    }

    public void findMethods(NodeList childNodes, File file, boolean withNamespace) throws FileNotFoundException {
        Node includeNode = null;
        Node namespaceNode = null;
        Node macroNode = null;
        for (int i = 0; i < childNodes.getLength(); ++i) {
            Node node = childNodes.item(i);

            if (withNamespace) {
                if (node.getNodeName().equals("namespace")) {
                    namespaceNode = node;
                }

                if (node.getTextContent().contains("gtest/gtest.h") || node.getTextContent().contains("gmock/gmock.h")) {
                    includeNode = node;
                }
            } else {
                if (node.getTextContent().contains("TEST")) {
                    macroNode = node;
                }
            }

            if (withNamespace) {
                if (includeNode != null && namespaceNode != null) {
                    List<TestMethod> testMethods = gatherAllTestMethodsFromFileWithNamespace(new LinkedList<>(), file, namespaceNode);
                    if (testMethods.size() > 0) {
                        testClasses.add(new TestClass(testMethods, namespaceNode.getTextContent(), file.getAbsolutePath()));
                    }
                    includeNode = null;
                    namespaceNode = null;
                    break;
                }
            }
            else {
                if (node.getNodeName().equals("block")) {
                    List<TestMethod> testMethods = gatherAllTestMethodsFromFileWithoutNamespace(new LinkedList<>(), file, node, macroNode);
                    if (testMethods.size() > 0) {
                        testClasses.add(new TestClass(testMethods, node.getTextContent(), file.getAbsolutePath()));
                    }
                    macroNode = null;
                }
            }
        }
    }

    public List<TestMethod> gatherAllTestMethodsFromFileWithoutNamespace(List<TestMethod> testMethods, File javaFile, Node rootNode, Node macroNode) throws FileNotFoundException {
        String methodDeclaration = "";
        String methodName = "";
        NodeList childNodes = rootNode.getChildNodes();

        for (int i = 0; i < childNodes.getLength(); ++i) {
            Node blockNode = childNodes.item(i);
            methodDeclaration = blockNode.getTextContent();

            if (!methodDeclaration.equals("")) {
                String functionDeclaration = macroNode.getTextContent();
                String[] splitted = functionDeclaration.split(",");
                methodName = getFirstPartMethodName(splitted) + "=>" + getSecondPartMethodName(splitted);

                testMethods.add(new TestMethod(methodName, blockNode, javaFile.getAbsolutePath()));
                methodDeclaration = "";
                methodName = "";
            }
        }
        return testMethods;
    }

    public List<TestMethod> gatherAllTestMethodsFromFileWithNamespace(List<TestMethod> testMethods, File javaFile, Node rootNode) throws FileNotFoundException {
        String methodDeclaration = "";
        String methodName = "";
        NodeList childNodes = rootNode.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); ++i) {
            Node namespaceChildNode = childNodes.item(i);
            if (namespaceChildNode.getNodeName().equals("block")) {
                NodeList blockNodeList = namespaceChildNode.getChildNodes();
                for (int j = 0; j < blockNodeList.getLength(); ++j) {
                    Node blockNode = blockNodeList.item(j);
                    if (blockNode.getNodeName().equals("block")) {
                        methodDeclaration = blockNode.getTextContent();
                    } else if (blockNode.getNodeName().equals("macro") && blockNode.getTextContent().contains("TEST")) {
                        String functionDeclaration = blockNode.getTextContent();
                        String[] splitted = functionDeclaration.split(",");
                        methodName = getFirstPartMethodName(splitted) + "=>" + getSecondPartMethodName(splitted);
                    }

                    if (!methodDeclaration.equals("") && !methodName.equals("")) {
                        testMethods.add(new TestMethod(methodName, blockNode, javaFile.getAbsolutePath()));
                        methodDeclaration = "";
                        methodName = "";
                    }
                }
            }
        }
        return testMethods;
    }

    private String getFirstPartMethodName(String[] splitted) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean flag = false;

        for (char ch : splitted[0].toCharArray()) {
            if (flag) {
                if (ch != ' ') {
                    stringBuilder.append(ch);
                }
            }
            if (ch == '(') {
                flag = true;
            }
        }
        return stringBuilder.toString();
    }

    private String getSecondPartMethodName(String[] splitted) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean flag = true;

        for (char ch : splitted[1].toCharArray()) {
            if (ch == ')') {
                flag = false;
            }
            if (flag) {
                if (ch != ' ') {
                    stringBuilder.append(ch);
                }
            }
        }
        return stringBuilder.toString();
    }

    public List<TestClass> getTestClasses() {
        return testClasses;
    }
}
