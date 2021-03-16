package utils;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.visitor.TreeVisitor;
import projectCrawler.ProjectCrawler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class JUnit5Utilization {

    private final ProjectCrawler projectCrawler;
    private final Set<String> JUnit5NewFeatures;

    public JUnit5Utilization(String projectPath) throws FileNotFoundException {
        OutputWriter.getInstance().setOutputFile(projectPath);
        this.projectCrawler = new ProjectCrawler(projectPath);
        projectCrawler.run();
        JUnit5NewFeatures = new HashSet<>();
        addNewFeatures();
    }

    public void findJUnit5Imports() throws IOException {
        for (TestClass testClass : projectCrawler.getTestClasses()) {
            CompilationUnit cu = StaticJavaParser.parse(testClass.getClassContent());
            Set<String> usedFromJUnit5 = new HashSet<>();
            for (ImportDeclaration importDeclaration : cu.getImports()) {
                String importName = importDeclaration.getName().asString();
                if (importName.contains("org.junit.jupiter")) {
                    String[] splitted = importName.split("\\.");
                    usedFromJUnit5.add(splitted[splitted.length - 1]);
                }
            }
            for (String s : usedFromJUnit5) {
                if (JUnit5NewFeatures.contains(s)) {
                    new TreeVisitor() {
                        @Override
                        public void process(Node node) {
                            if (node.toString().equals(s)) {
                                OutputWriter.getInstance().write(testClass.getAbsolutePath(), s);
                            }
                        }
                    }.visitPreOrder(cu.findRootNode());
                }
            }
        }
        OutputWriter.csvWriter.close();
    }

    public void addNewFeatures() {
        JUnit5NewFeatures.add("ParameterizedTest");
        JUnit5NewFeatures.add("BeforeEach");
        JUnit5NewFeatures.add("AfterEach");
        JUnit5NewFeatures.add("BeforeAll");
        JUnit5NewFeatures.add("AfterAll");
        JUnit5NewFeatures.add("Disabled");
        JUnit5NewFeatures.add("RepeatedTest");
        JUnit5NewFeatures.add("TestFactory");
        JUnit5NewFeatures.add("TestTemplate");
        JUnit5NewFeatures.add("TestMethodOrder");
        JUnit5NewFeatures.add("TestInstance");
        JUnit5NewFeatures.add("DisplayName");
        JUnit5NewFeatures.add("DisplayNameGeneration");
        JUnit5NewFeatures.add("Nested");
        JUnit5NewFeatures.add("Tag");
        JUnit5NewFeatures.add("Timeout");
        JUnit5NewFeatures.add("ExtendWith");
        JUnit5NewFeatures.add("RegisterExtension");
        JUnit5NewFeatures.add("TempDir");
        JUnit5NewFeatures.add("ValueSource");
        JUnit5NewFeatures.add("IndicativeSentencesGeneration");
        JUnit5NewFeatures.add("EnabledOnOs");
        JUnit5NewFeatures.add("DisabledOnOs");
        JUnit5NewFeatures.add("EnabledOnJre");
        JUnit5NewFeatures.add("EnabledForJreRange");
        JUnit5NewFeatures.add("DisabledOnJre");
        JUnit5NewFeatures.add("DisabledForJreRange");
        JUnit5NewFeatures.add("EnabledIfSystemProperty");
        JUnit5NewFeatures.add("DisabledIfSystemProperty");
        JUnit5NewFeatures.add("EnabledIfEnvironmentVariable");
        JUnit5NewFeatures.add("DisabledIfEnvironmentVariable");
        JUnit5NewFeatures.add("EnabledIf");
        JUnit5NewFeatures.add("DisabledIf");
        JUnit5NewFeatures.add("Order");
        JUnit5NewFeatures.add("EnumSource");
        JUnit5NewFeatures.add("MethodSource");
        JUnit5NewFeatures.add("CsvSource");
        JUnit5NewFeatures.add("CsvFileSource");
        JUnit5NewFeatures.add("ArgumentsSource");
        JUnit5NewFeatures.add("ResourceLock");
        JUnit5NewFeatures.add("assertAll");
        JUnit5NewFeatures.add("assertThrows");
        JUnit5NewFeatures.add("assertDoesNotThrows");
        JUnit5NewFeatures.add("assertTimeout");
        JUnit5NewFeatures.add("assertTimeoutPreemptively");
        JUnit5NewFeatures.add("assumingThat");
    }

}