package matchers;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import utils.OutputWriter;
import utils.TestClass;
import utils.TestMethod;

import java.util.LinkedList;
import java.util.logging.Logger;

public class UnknownTestMatcher extends SmellMatcher {

    @Override
    protected void match(TestClass testClass) {
        for (TestMethod testMethod : testClass.getTestMethods()) {
            NodeList methodChilds = testMethod.getMethodDeclaration().getChildNodes();
            for (int i = 0; i < methodChilds.getLength(); ++i) {
                Node node = methodChilds.item(i);
                if (node.getNodeName().equals("block_content") && !node.getTextContent().contains("EXPECT_")) {
                    write(testMethod.getTestFilePath(), "Unknown Test", testMethod.getMethodName(), new LinkedList<>().toString());
                }
            }
        }
    }

    @Override
    public void write(String filePath, String testSmell, String name, String lines) {
        OutputWriter.getInstance().write(filePath, testSmell, name, lines);
        Logger.getLogger(AssertionRouletteMatcher.class.getName()).info("Found unknown test in method \"" + name + "\" in lines " + lines);
    }
}