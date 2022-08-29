//package matchers;
//
//import utils.OutputWriter;
//import utils.TestClass;
//import utils.TestMethod;
//
//import java.util.logging.Logger;
//
//public class DefaultTestMatcher extends SmellMatcher {
//    @Override
//    protected void match(TestClass testClass) {
//        if (testClass.getClassName().startsWith("ExampleUnitTest") || testClass.getClassName().startsWith("ExampleInstrumentedTest")) {
//            for (TestMethod testMethod : testClass.getTestMethods()) {
//                if (testMethod.getMethodName().equals("addition_isCorrect") || testMethod.getMethodName().equals("useAppContext")) {
//                    write(testClass.getAbsolutePath(), "Default Test", testClass.getClassName(), "");
//                }
//            }
//        }
//    }
//
//    @Override
//    public void write(String filePath, String testSmell, String name, String lines) {
//        OutputWriter.getInstance().write(filePath, testSmell, name, lines);
//        Logger.getLogger(AssertionRouletteMatcher.class.getName()).info("Found default test in method \"" + name + "\" in lines " + lines);
//    }
//}
