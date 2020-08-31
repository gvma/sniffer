import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MagicNumberTest {

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
}
