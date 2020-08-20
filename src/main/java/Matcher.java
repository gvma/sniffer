import java.util.List;
import java.util.logging.Logger;

public class Matcher {

    public void match(List<TestMethod> testMethods) {
        for (TestMethod testMethod : testMethods) {
            new AssertionRouletteMatcher().match(testMethod);
            new ExceptionHandlingMatcher().match(testMethod);
        }
    }
}
