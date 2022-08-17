package projectCrawler;

import com.github.javaparser.ParseProblemException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.sun.xml.internal.bind.annotation.XmlLocation;
import matchers.Sniffer;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import projectCrawler.positionalXMLReader.PositionalXMLReader;
import utils.TestClass;
import utils.TestMethod;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
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
                        rt.exec(String.format("C:\\Program Files\\srcML\\srcml \"%s\" -o \"%s\"", filePath, xmlFilePath));

                        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
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
                        System.out.println(childNodes.item(0).getNodeName());
                        System.out.println("Line number: " + childNodes.item(0).getUserData("lineNumber"));

                        for (int i = 0; i < childNodes.getLength(); ++i) {
                            Node node = childNodes.item(i);
                            if (node.getNodeName().equals("class")) {
                                NodeList classNodeList = node.getChildNodes();
                                for (int j = 0; j < classNodeList.getLength(); ++j) {
                                    Node classNode = classNodeList.item(j);
                                    if (classNode.getNodeName().equals("block")) {
                                        NodeList blockNodeList = classNode.getChildNodes();
                                        for (int k = 0; k < blockNodeList.getLength(); ++k) {
                                            Node blockNode = blockNodeList.item(k);
                                            if (blockNode.getNodeName().equals("function")) {
                                                NodeList functionNodeList = blockNode.getChildNodes();
                                                for (int l = 0; l < functionNodeList.getLength(); ++l) {
                                                    Node functionNode = functionNodeList.item(l);
                                                    if (functionNode.getNodeName().equals("annotation")) {
                                                        NodeList annotationNodeList = functionNode.getChildNodes();
                                                        for (int m = 0; m < annotationNodeList.getLength(); ++m) {
                                                            Node nameNode = annotationNodeList.item(m);
                                                            if (nameNode.getNodeName().equals("name")) {
                                                                Node testNode = nameNode.getFirstChild();
                                                                if (testNode.getNodeValue().equals("Test")) {
//                                                                    System.out.println(testNode.getNodeName());
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
//                        doc.getDocumentElement().normalize();
//                        NodeList nodeList = doc.getElementsByTagName("annotation");
//                        if (nodeList.getLength() > 0) {
//
//                            for (int i = 0; i < nodeList.getLength(); ++i) {
//                                NodeList annotationChilds = nodeList.item(i).getChildNodes();
//                                for (int j = 0; j < annotationChilds.getLength(); ++j) {
//                                    Node nameNode = annotationChilds.item(j);
//                                    if (annotationChilds.item(j).getNodeName().equals("name")) {
//                                        Node nameNodeFirstChild = nameNode.getFirstChild();
//                                        if (nameNodeFirstChild.getNodeValue().equals("Test")) {
//                                            System.out.println(nameNodeFirstChild.getUserData("lineNumber"));
//                                        }
//                                    }
//                                }
//                            }
//                        }
                        List<TestMethod> testMethods = gatherAllTestMethodsFromFile(new LinkedList<>(), file);
//                        if (testMethods.size() > 0) {
//                            testClasses.add(new TestClass(testMethods, file.getName(), StaticJavaParser.parse(file).toString(), file.getAbsolutePath()));
//                        }
                    }
                }
            }
        }
    }

    public List<TestMethod> gatherAllTestMethodsFromFile(List<TestMethod> testMethods, File javaFile) throws FileNotFoundException {
        try {
            new VoidVisitorAdapter<>() {
                @Override
                public void visit(MethodDeclaration n, Object arg) {
                    super.visit(n, arg);
                    if (n.getAnnotations().size() != 0) {
                        for (AnnotationExpr annotationExpr : n.getAnnotations()) {
                            if (annotationExpr.getNameAsString().equals("Test")) {
                                if (n.getRange().isPresent()) {
                                    testMethods.add(new TestMethod(n.getRange().get().begin.line,
                                            n.getRange().get().end.line,
                                            n.getNameAsString(),
                                            n.asMethodDeclaration(),
                                            javaFile.getAbsolutePath()));
                                    break;
                                }
                            }
                        }
                    }
                }
            }.visit(StaticJavaParser.parse(javaFile), null);
        } catch (ParseProblemException e) {
            return new LinkedList<>();
        }
        return testMethods;
    }

    public List<TestClass> getTestClasses() {
        return testClasses;
    }
}
