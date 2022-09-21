package utils;

import org.w3c.dom.NodeList;

import java.util.List;

public class TestClass {
//    private final String className;
    private final List<TestMethod> testMethods;
    private final String classContent;
    private final String absolutePath;

    private final NodeList fileContentXml;

    public TestClass(List<TestMethod> testMethods, String classContent, String absolutePath, NodeList fileContentXml) {
        this.testMethods = testMethods;
        this.classContent = classContent;
        this.absolutePath = absolutePath;
        this.fileContentXml = fileContentXml;
    }

    public List<TestMethod> getTestMethods() {
        return testMethods;
    }

    public NodeList getFileContentXml() {
        return fileContentXml;
    }

    //    public String getClassName() {
//        return className;
//    }

    public String getClassContent() { return classContent; }

    public String getAbsolutePath() { return absolutePath; }

    @Override
    public String toString() {
        return "TestClass{" +
//                "className='" + className + '\'' +
                ", testMethods=" + testMethods +
                ", classContent='" + classContent + '\'' +
                '}';
    }
}
