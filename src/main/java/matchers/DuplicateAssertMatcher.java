package matchers;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import utils.OutputWriter;
import utils.TestClass;
import utils.TestMethod;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Logger;

public class DuplicateAssertMatcher extends SmellMatcher {
  private Map<String, Integer> allAsserts = new HashMap<>();
  @Override
  protected void match(TestClass testClass) {
    for (TestMethod testMethod : testClass.getTestMethods()) {
      Node root = testMethod.getMethodDeclaration();
      getAllAssertsRecursive(root);
      for (Map.Entry<String, Integer> entry : this.allAsserts.entrySet()) {
        boolean hasDuplicateAssertSmell = entry.getValue() > 1;
        if (hasDuplicateAssertSmell) {
          write(testMethod.getTestFilePath(), "Duplicate Assert", testMethod.getMethodName(), "[]");
        }
      }
      this.allAsserts.clear();
    }
  }
  @Override
  public void write(String filePath, String testSmell, String name, String lines) {
    OutputWriter.getInstance().write(filePath, testSmell, name, lines);
    Logger.getLogger(DuplicateAssertMatcher.class.getName()).info("Found duplicate assert in method \"" + name + "\" in lines " + lines);
  }
  private void getAllAssertsRecursive(Node root) {
    NodeList rootChildren = root.getChildNodes();
    Node rootChild;
    for(int i = 0; i < rootChildren.getLength(); ++i) {
      rootChild = rootChildren.item(i);
      String textContent = rootChild.getTextContent().trim();
      if (rootChild.getNodeName().equals("expr") && (textContent.startsWith("EXPECT_") || textContent.startsWith("ASSERT_") || textContent.startsWith("FAIL_"))) {
        if (this.allAsserts.containsKey(rootChild.getTextContent())) {
          this.allAsserts.put(rootChild.getTextContent(), allAsserts.get(rootChild.getTextContent()) + 1);
        } else {
          this.allAsserts.put(rootChild.getTextContent(), 1);
        }
      }
      getAllAssertsRecursive(rootChild);
    }
  }
}