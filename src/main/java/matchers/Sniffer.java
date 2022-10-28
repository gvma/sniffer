package matchers;

import org.reflections.Reflections;
import org.xml.sax.SAXException;
import projectCrawler.ProjectCrawler;
import utils.OutputWriter;
import utils.TestClass;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Set;

public class Sniffer {

    private final ProjectCrawler projectCrawler;

    public Sniffer(String projectPath) throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        OutputWriter.getInstance().setOutputFile(projectPath);
        this.projectCrawler = new ProjectCrawler(projectPath);
        projectCrawler.run();
    }

    public void sniff() throws Exception {
        Set<Class<? extends SmellMatcher>> classes = new Reflections("").getSubTypesOf(SmellMatcher.class);
        for (Class<? extends SmellMatcher> clazz : classes) {
            Method match = clazz.getDeclaredMethod("match", TestClass.class);
            match.setAccessible(true);
            for (TestClass testClass : projectCrawler.getTestClasses()) {
                match.invoke(clazz.getDeclaredConstructor().newInstance(), testClass);
            }
        }
        OutputWriter.csvWriter.close();
    }
}
