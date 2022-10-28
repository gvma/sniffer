package projectCrawler;

import matchers.Sniffer;

import java.io.File;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.ByteArrayInputStream;

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.TreeWalker;
import org.xml.sax.SAXException;
import projectCrawler.positionalXMLReader.PositionalXMLReader;
import utils.TestClass;
import utils.TestMethod;

import javax.xml.parsers.ParserConfigurationException;
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
                    if ((file.getName().endsWith(".cpp") || file.getName().endsWith(".cc"))) {
                        Logger logger = Logger.getLogger(Sniffer.class.getName());
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
                        if(this.testMethods.size() > 0) {
                            List<TestMethod> testMethodsList = new LinkedList<>(this.checkIntegrityOfMethods());
                            for (TestMethod index : testMethodsList) {
                                logger.info("Find method: " + index.getMethodName());
                            }
                            TestClass testClass = new TestClass(testMethodsList, rootNode.getTextContent(), file.getAbsolutePath());
                            this.testClasses.add(testClass);
                            this.testMethods.clear();
                        }
                    }
                }
            }
        }
    }

    private List<TestMethod> checkIntegrityOfMethods() {
        List<TestMethod> testMethodsList = this.testMethods.stream().collect(Collectors.toList());
        List<Integer> removeIndex = new ArrayList<>();
        List<TestMethod> newNodesToAdd = new LinkedList<>();
        for(int i = 0; i < testMethodsList.size(); ++i) {
            // TEST.*\(.*\)\s*{\X*}
            String regexPattern = "TEST.*\\(.*\\)\\s*\\{";
            Pattern pattern = Pattern.compile(regexPattern);
            Matcher m = pattern.matcher(testMethodsList.get(i).getMethodDeclaration().getTextContent());
            boolean hasExtraTestMethods = m.find();
            if(hasExtraTestMethods) {
                TestMethod currentTestMethod = testMethodsList.get(i);
                removeIndex.add(i);
                Node newNode = removeExtraTestMethods(currentTestMethod.getMethodDeclaration());
                boolean isFreakMethod = removeFreakTestMethods(newNode);
                if(!isFreakMethod) {
                    TestMethod newTestMethod = new TestMethod(currentTestMethod.getMethodName(), newNode, currentTestMethod.getTestFilePath());
                    newNodesToAdd.add(newTestMethod);
                    continue;
                }
            }
            boolean isFreakMethod = removeFreakTestMethods(testMethodsList.get(i).getMethodDeclaration());
            if(isFreakMethod) {
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

    private Node removeExtraTestMethods(Node root) {
        DocumentTraversal traversal = (DocumentTraversal) root.getFirstChild().getOwnerDocument();
        TreeWalker iterator = traversal.createTreeWalker(root, NodeFilter.SHOW_ALL, null, false);
        Node node = null;
        boolean isMacro = false;
        Node macroNode = null;
        while ((node = iterator.nextNode()) != null) {
            if(node.getNodeName().equals("macro") && node.getTextContent().startsWith("TEST")) {
                macroNode = node;
                isMacro = true;
            }
            if(isMacro && node.getNodeName().equals("block")) {
                isMacro = false;
                Node parent = node.getParentNode();
                parent.removeChild(node);
            }
        }
        if(macroNode != null) {
            Node parent = macroNode.getParentNode();
            parent.removeChild(macroNode);
        }
        return root;
    }
    private boolean removeFreakTestMethods(Node root) {
        String rootString = root.getTextContent();
        Stack<Boolean> stack = new Stack<>();
        for(int i = 0; i < rootString.length(); ++i) {
            if(rootString.charAt(i) == '{') {
                stack.push(true);
            }
            if(!stack.isEmpty() && rootString.charAt(i) == '}') {
                stack.pop();
            }
        }
        return !stack.isEmpty();
    }

    public void findMethods(Node root, String filePath) {
        String methodDeclaration = "", methodName = "";
        boolean isTestMacro = false, isTestFile = false;
        DocumentTraversal traversal = (DocumentTraversal) root.getFirstChild().getOwnerDocument();
        TreeWalker iterator = traversal.createTreeWalker(root, NodeFilter.SHOW_ALL, null, false);
        Node node = iterator.nextNode();
        while (node != null) {
            if (node.getTextContent().contains("gtest/gtest.h") || node.getTextContent().contains("gmock/gmock.h")) {
                isTestFile = true;
            }
            if (isTestFile && node.getNodeName().equals("macro") && node.getTextContent().startsWith("TEST")) {
                String functionDeclaration = node.getTextContent();
                String[] splitted = functionDeclaration.split(",");
                methodName = getFirstPartMethodName(splitted) + "=>" + getSecondPartMethodName(splitted);
                isTestMacro = true;
            }
            if (isTestFile && node.getNodeName().equals("block") && isTestMacro) {
                methodDeclaration = node.getTextContent();
                if (!methodDeclaration.equals("") && !methodName.equals("")) {
                    this.testMethods.add(new TestMethod(methodName, node, filePath));
                    methodName = "";
                    isTestMacro = false;
                }
            }
            node = iterator.nextNode();
        }
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
