import com.github.javaparser.ast.body.MethodDeclaration;

public class TestMethod {
    private final int beginLine;
    private final int endLine;
    private final String methodName;
    private final MethodDeclaration methodDeclaration;
    private final String testFilePath;

    public TestMethod(int beginLine, int endLine, String methodName, MethodDeclaration methodDeclaration, String testFilePath) {
        this.beginLine = beginLine;
        this.endLine = endLine;
        this.methodName = methodName;
        this.methodDeclaration = methodDeclaration;
        this.testFilePath = testFilePath;
    }

    public int getBeginLine() {
        return beginLine;
    }

    public int getEndLine() {
        return endLine;
    }

    public String getMethodName() {
        return methodName;
    }

    public MethodDeclaration getMethodDeclaration() {
        return methodDeclaration;
    }

    public String getTestFilePath() {
        return testFilePath;
    }

    @Override
    public String toString() {
        return "\nTestMethod {\n" +
                "\tbeginLine=" + beginLine + ",\n" +
                "\tendLine=" + endLine + ",\n" +
                "\ttestFilePath='" + testFilePath + '\'' + ",\n" +
                "\tmethodName='" + methodName + '\'' +
                "\n}";
    }
}
