package matchers;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.visitor.TreeVisitor;
import utils.OutputWriter;
import utils.TestClass;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class ConstructorInitializationMatcher extends SmellMatcher {
    @Override
    protected void match(TestClass testClass) {
        CompilationUnit cu = StaticJavaParser.parse(testClass.getClassContent());
        final Integer[] line = {null};
        new TreeVisitor() {
            @Override
            public void process(Node node) {
                if (node.getMetaModel().getTypeName().equals("ConstructorDeclaration")) {
                    List<Node> nodes = node.getChildNodes();
                    for (Node n : nodes) {
                        if (testClass.getClassName().startsWith(n.toString())) {
                            if (node.getRange().isPresent()) {
                                line[0] = node.getRange().get().begin.line;
                            }
                        }
                    }
                }
            }
        }.visitPreOrder(cu.findRootNode());
        if (line[0] != null) {
            write(testClass.getAbsolutePath(), "Constructor Initialization", testClass.getClassName(), Arrays.toString(line));
        }
    }

    @Override
    public void write(String filePath, String testSmell, String name, String lines) {
        OutputWriter.getInstance().write(filePath, testSmell, name, lines);
        Logger.getLogger(AssertionRouletteMatcher.class.getName()).info("Found constructor initialization in class \"" + name + "\" in lines " + lines);
    }
}
