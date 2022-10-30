package utils;

import org.w3c.dom.Node;

public class TestMethod {
  private final String methodName;
  private final Node methodDeclaration;
  private final String testFilePath;

  public TestMethod(String methodName, Node methodDeclaration, String testFilePath) {
    this.methodName = methodName;
    this.methodDeclaration = methodDeclaration;
    this.testFilePath = testFilePath;
  }

  public String getMethodName() {
    return methodName;
  }

  public Node getMethodDeclaration() {
    return methodDeclaration;
  }

  public String getTestFilePath() {
    return testFilePath;
  }

  @Override
  public String toString() {
    return "\nutils.TestMethod {\n" +
            "\ttestFilePath='" + testFilePath + '\'' + ",\n" +
            "\tmethodName='" + methodName + '\'' +
            "\tmethodDeclaration='" + methodDeclaration.getTextContent() + '\'' +
            "\n}";
  }
}