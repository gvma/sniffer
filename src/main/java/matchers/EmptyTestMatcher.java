package matchers;

import org.w3c.dom.NodeList;
import utils.OutputWriter;
import utils.TestClass;
import utils.TestMethod;

import java.util.logging.Logger;

public class EmptyTestMatcher extends SmellMatcher {
    @Override
    protected void match(TestClass testClass) {
        for (TestMethod testMethod : testClass.getTestMethods()) {
            NodeList methodChilds = testMethod.getMethodDeclaration().getChildNodes();
            for (int i = 0; i < methodChilds.getLength(); ++i) {
                if (methodChilds.item(i).getNodeName().equals("block_content")) {
                    if (methodChilds.item(i).getTextContent().isBlank()) {
                        write(testClass.getAbsolutePath(), "Empty Test", testMethod.getMethodName(), "[]");
                    }
                }
            }
        }
    }

    @Override
    public void write(String filePath, String testSmell, String name, String lines) {
        OutputWriter.getInstance().write(filePath, testSmell, name, lines);
        Logger.getLogger(AssertionRouletteMatcher.class.getName()).info("Found empty test in method \"" + name + "\" in lines " + lines);
    }
}
