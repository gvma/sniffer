//package matchers;
//
//import com.github.javaparser.ast.Node;
//import com.github.javaparser.ast.visitor.TreeVisitor;
//import utils.OutputWriter;
//import utils.TestClass;
//import utils.TestMethod;
//
//import java.util.LinkedList;
//import java.util.List;
//import java.util.logging.Logger;
//
//public class SleepyTestMatcher extends SmellMatcher {
//    @Override
//    protected void match(TestClass testClass) {
//        for (TestMethod testMethod : testClass.getTestMethods()) {
//            for (Node node : testMethod.getMethodDeclaration().getChildNodes()) {
//                List<Integer> lines = new LinkedList<>();
//                matchSleepyTest(node.getChildNodes(), lines);
//                if (!lines.isEmpty()) {
//                    write(testMethod.getTestFilePath(), "Sleepy Test", testMethod.getMethodDeclaration().getNameAsString(), lines.toString());
//                }
//            }
//        }
//    }
//
//    @Override
//    public void write(String filePath, String testSmell, String name, String lines) {
//        OutputWriter.getInstance().write(filePath, testSmell, name, lines);
//        Logger.getLogger(AssertionRouletteMatcher.class.getName()).info("Found sleepy test in method \"" + name + "\" in lines " + lines);
//    }
//
//    private void matchSleepyTest(List<Node> nodeList, List<Integer> lines) {
//        for (Node n : nodeList) {
//            new TreeVisitor() {
//                @Override
//                public void process(Node node) {
//                    if (node.toString().contains("Thread.sleep(") || node.toString().contains("sleep(")) {
//                        if (isSleep(node.toString())) {
//                            if (node.getRange().isPresent()) {
//                                lines.add(node.getRange().get().begin.line);
//                            }
//                        }
//                    }
//                }
//            }.visitPreOrder(n);
//        }
//    }
//
//    private boolean isSleep(String str) {
//        char[] sleepString = {'s', 'l', 'e', 'e', 'p'};
//        for (int i = 0; i < 5; ++i) {
//            if (str.charAt(i) != sleepString[i]) {
//                return false;
//            }
//        }
//        return true;
//    }
//}
