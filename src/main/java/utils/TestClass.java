package utils;

import java.util.List;

public class TestClass {
    private final String className;
    private final List<TestMethod> testMethods;
    private final String classContent;
    private final String absolutePath;

    public TestClass(List<TestMethod> testMethods, String className, String classContent, String absolutePath) {
        this.testMethods = testMethods;
        this.className = className;
        this.classContent = classContent;
        this.absolutePath = absolutePath;
    }

    public List<TestMethod> getTestMethods() {
        return testMethods;
    }

    public String getClassName() {
        return className;
    }

    public String getClassContent() { return classContent; }

    public String getAbsolutePath() { return absolutePath; }

    @Override
    public String toString() {
        return "TestClass{" +
                "className='" + className + '\'' +
                ", testMethods=" + testMethods +
                ", classContent='" + classContent + '\'' +
                '}';
    }
}
