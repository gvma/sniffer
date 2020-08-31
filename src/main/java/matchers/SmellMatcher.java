package matchers;

import utils.OutputWriter;
import utils.TestClass;

public abstract class SmellMatcher {
    protected abstract void match(TestClass testClass);
    public abstract void write(String filePath, String testSmell, String methodName, String lines);
}
