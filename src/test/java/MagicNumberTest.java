import org.assertj.core.api.ListAssert;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.LinkedList;
import java.util.List;

import static java.util.Collections.emptyMap;
import static org.junit.Assert.*;

public class MagicNumberTest {

    public static Path root;

    static {
        try {
            Path jarFilePath = Paths.get(System.getProperty("jarFile"));
            URI jarFileUri = new URI("jar", jarFilePath.toUri().toString(), null);
            FileSystem fileSystem = FileSystems.newFileSystem(jarFileUri, emptyMap());
            root = fileSystem.getPath("/");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void magicNumberTest1() {
        int a = 0;
        assertEquals(1.0, 1.0, 1.0);
        assertEquals(1.0, 1.0, 1.0);
    }

    @Test
    public void magicNumberTest2() {
        int a = 0;
        assertEquals(a, 1.0, 1.0);
        assertEquals(a, 1.0, 1.0);
    }

    @Test
    public void magicNumberTest3() {
        int a = 0;
        assertEquals(a, a, 1.0);
        assertEquals(a, a, 1.0);
    }

    @Test
    public void magicNumberTest4() {
        int a = 0;
        assertEquals(a, a, a);
    }

    @Test
    public void magicNumberTest5() {
        double a = 1.0;
        assertEquals(Math.pow(1, 1), a, a);
    }

    @Test
    public void magicNumberTest6() {
        assertNotNull(null);
    }

    @Test
    public void magicNumberTest7() {
        assertEquals('a', 'a');
    }

    @Test
    public void magicNumberTest8() {
        List<Integer> list = new LinkedList<>();
        list.add(1);
        assertNotNull(list.get(0));
    }

    @Test
    public void testPackages() throws Exception {
        assertThatFileList(root).containsOnly(
                "org",
                "META-INF"
        );
    }

    private ListAssert<String> assertThatFileList(Path path) throws IOException {
        return new ListAssert<String>(new LinkedList<>());
    }
}
