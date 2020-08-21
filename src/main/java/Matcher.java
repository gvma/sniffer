import matchers.AssertionRouletteMatcher;
import matchers.ExceptionHandlingMatcher;
import utils.OutputWriter;
import utils.TestClass;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.lang.Thread.sleep;

public class Matcher {

    private final List<Runnable> matchers;
    private final ProjectCrawler projectCrawler;
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public Matcher(String projectPath) throws FileNotFoundException {
        OutputWriter.init(projectPath);
        this.projectCrawler = new ProjectCrawler(projectPath);
        projectCrawler.run();
        matchers = new LinkedList<>();
        registerDefaultMatchers();
    }

    private void registerDefaultMatchers() {
        for (TestClass testClass : projectCrawler.getTestClasses()) {
            matchers.add(AssertionRouletteMatcher.match(testClass, readWriteLock.writeLock()));
            matchers.add(ExceptionHandlingMatcher.match(testClass, readWriteLock.writeLock()));
        }
    }

    public void match() throws InterruptedException {
        Lock lock = readWriteLock.writeLock();
        for (Runnable matcher : matchers) {
            Thread thread = new Thread(matcher);
            thread.start();
        }
        try {
            sleep(100);
            lock.lock();
            OutputWriter.csvWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
