package matchers;

import projectCrawler.ProjectCrawler;
import utils.OutputWriter;
import utils.TestClass;

import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

public class Sniffer {

    private final List<Callable<?>> matchers;
    private final ProjectCrawler projectCrawler;

    public Sniffer(String projectPath) throws FileNotFoundException {
        OutputWriter.getInstance().setOutputFile(projectPath);
        this.projectCrawler = new ProjectCrawler(projectPath);
        projectCrawler.run();
        matchers = new LinkedList<>();
        registerDefaultMatchers();
    }

    public void addCustomSmellMatchers(List<SmellMatcher> smellMatcherList) {
        for (SmellMatcher smellMatcher : smellMatcherList) {
            addCustomSmellMatcher(smellMatcher);
        }
    }

    public void addCustomSmellMatcher(SmellMatcher smellMatcher) {
        for (TestClass testClass : projectCrawler.getTestClasses()) {
            matchers.add(smellMatcher.match(testClass));
        }
    }

    private void registerDefaultMatchers() {
        for (TestClass testClass : projectCrawler.getTestClasses()) {
            matchers.add(new AssertionRouletteMatcher().match(testClass));
            matchers.add(new ExceptionHandlingMatcher().match(testClass));
        }
    }

    public void sniff() throws Exception {
        for (Callable<?> callable : matchers) {
            callable.call();
        }
        OutputWriter.csvWriter.close();
    }
}
