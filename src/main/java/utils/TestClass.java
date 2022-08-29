package utils;

import java.util.List;

public class TestClass {
    private final List<TestMethod> testMethods;
    private final String classContent;
    private final String absolutePath;

    public TestClass(List<TestMethod> testMethods, String classContent, String absolutePath) {
        this.testMethods = testMethods;
        this.classContent = classContent;
        this.absolutePath = absolutePath;
    }

    public List<TestMethod> getTestMethods() {
        return testMethods;
    }

    @Override
    public String toString() {
        return "TestClass{" +
                "testMethods=" + testMethods +
                ", classContent='" + classContent + '\'' +
                ", absolutePath='" + absolutePath + '\'' +
                '}';
    }

    public String getClassContent() { return classContent; }

    public String getAbsolutePath() { return absolutePath; }

}
