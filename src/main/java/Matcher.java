import java.util.List;

public class Matcher {

    public void match(List<TestMethod> testMethods) {
        for (TestMethod testMethod : testMethods) {
            new AssertionRouletteMatcher().findAssertionRoulette(testMethod);
        }
    }
}
