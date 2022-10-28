package matchers;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import utils.OutputWriter;
import utils.TestClass;
import utils.TestMethod;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class ExceptionHandlingMatcher extends SmellMatcher {

  @Override
  protected void match(TestClass testClass) {
    for (TestMethod testMethod : testClass.getTestMethods()) {
      Node root = testMethod.getMethodDeclaration();
      List<Boolean> hasExceptionHandlingSmell = new LinkedList<>();
      matchExceptionHandlingRecursive(root, false, hasExceptionHandlingSmell);
      if (hasExceptionHandlingSmell.size() > 0 ) {
        for(Boolean ignored: hasExceptionHandlingSmell) {
          write(testMethod.getTestFilePath(), "Exception Handling", testMethod.getMethodName(), "[]");
        }
      }
    }
  }

  @Override
  public void write(String filePath, String testSmell, String name, String lines) {
    OutputWriter.getInstance().write(filePath, testSmell, name, lines);
    Logger.getLogger(ExceptionHandlingMatcher.class.getName()).info("Found Exception Handling in method \"" + name + "\" in line " + lines);
  }

  private void matchExceptionHandlingRecursive(Node root, boolean isInsideTryCatch, List<Boolean> hasExceptionHandlingSmell) {
    NodeList rootChildren = root.getChildNodes();
    Node rootChild = null;
    for(int i = 0; i < rootChildren.getLength(); ++i) {
      rootChild = rootChildren.item(i);
      if(rootChild.getNodeName().equals("try") || rootChild.getNodeName().equals("catch")) {
        matchExceptionHandlingRecursive(rootChild, true, hasExceptionHandlingSmell);
      } else {
        String textContent = rootChild.getTextContent().trim();
        if(isInsideTryCatch && rootChild.getNodeName().equals("expr") && (textContent.startsWith("EXPECT_") || textContent.startsWith("FAIL") || textContent.startsWith("ASSERT_"))) {
          hasExceptionHandlingSmell.add(true);
        }
        matchExceptionHandlingRecursive(rootChild, isInsideTryCatch, hasExceptionHandlingSmell);
      }
    }
  }
}