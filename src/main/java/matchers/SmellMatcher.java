package matchers;

import utils.TestClass;

public abstract class SmellMatcher {
    protected abstract void match(TestClass testClass);
    public abstract void write(String filePath, String testSmell, String name, String lines);
}
