package matchers;

import utils.OutputWriter;
import utils.TestClass;
import utils.TestMethod;

import java.util.LinkedList;
import java.util.logging.Logger;

public class DefaultTestMatcher extends SmellMatcher {
    @Override
    protected void match(TestClass testClass) {
      for (TestMethod testMethod : testClass.getTestMethods()) {
          String methodName = testMethod.getMethodName();
          if(methodName.equals("addition_isCorrect") || methodName.equals("useAppContext")) {
            write(testMethod.getTestFilePath(), "Default Test", testMethod.getMethodName(), new LinkedList<>().toString());
          }
        }
    }

    @Override
    public void write(String filePath, String testSmell, String name, String lines) {
        OutputWriter.getInstance().write(filePath, testSmell, name, lines);
        Logger.getLogger(AssertionRouletteMatcher.class.getName()).info("Found default test in method \"" + name + "\" in lines " + lines);
    }
}
