import org.junit.jupiter.api.Test;

import java.net.URI;
import java.nio.file.*;
import java.util.LinkedList;
import java.util.List;

import static java.util.Collections.emptyMap;
import static org.junit.jupiter.api.Assertions.*;

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
    public void magicNumberTrue1() {
        int a = 0;
        assertEquals(1.0, 1.0, 1.0);
        assertEquals(1.0, 1.0, 1.0);
    }

    @Test
    public void magicNumberTrue2() {
        int a = 0;
        assertEquals(a, 1.0, 1.0);
        assertEquals(a, 1.0, 1.0);
    }

    @Test
    public void magicNumberTrue3() {
        int a = 0;
        assertEquals(a, a, 1.0);
        assertEquals(a, a, 1.0);
    }

    @Test
    public void magicNumberFalse1() {
        int a = 0;
        assertEquals(a, a, a);
    }

    @Test
    public void magicNumberTrue4() {
        double a = 1.0;
        assertEquals(Math.pow(1, 1), a, a);
    }

    @Test
    public void magicNumberFalse2() {
        assertNotNull(null);
    }

    @Test
    public void magicNumberFalse3() {
        assertEquals('a', 'a');
    }

    @Test
    public void magicNumberGetListFalse4() {
        List<Integer> list = new LinkedList<>();
        list.add(1);
        assertNotNull(list.get(0));
    }

    @Test
    public void magicNumberTrue5() {
        int[] a = {1}, b = {1};
        assertEquals(a[0], b[0]);
    }

    @Test
    public void magicNumberTrue6() {
        Integer a = 1;
        assertEquals(1, a.intValue());
    }

    @Test
    public void magicNumberFalse5() {
        Boolean b = true;
        assertTrue(b.booleanValue());
    }
}
