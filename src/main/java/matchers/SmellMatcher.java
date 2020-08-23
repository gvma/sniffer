package matchers;

import utils.TestClass;

import java.util.concurrent.Callable;

public abstract class SmellMatcher {
    protected abstract Callable<?> match(TestClass testClass);
}
