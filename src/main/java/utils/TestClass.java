package utils;

import java.util.List;

public class TestClass {
    private final String className;
    private final List<TestMethod> testMethods;
    private final String classContent;

    public TestClass(List<TestMethod> testMethods, String className, String classContent) {
        this.testMethods = testMethods;
        this.className = className;
        this.classContent = classContent;
    }

    public List<TestMethod> getTestMethods() {
        return testMethods;
    }

    public String getClassName() {
        return className;
    }

    @Override
    public String toString() {
        return "TestClass{" +
                "className='" + className + '\'' +
                ", testMethods=" + testMethods +
                ", classContent='" + classContent + '\'' +
                '}';
    }
}
