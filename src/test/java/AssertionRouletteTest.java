import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AssertionRouletteTest {

    @Test
    public void assertionRouletteTrue1() {
        int a = 0;
        int b = 0;
        int c = 0;
        assertEquals(a, b);
        assertEquals(a, c);
    }

    @Test
    public void assertionRouletteTrue2() {
        int a = 0;
        int b = 0;
        int c = 0;
        assertEquals(a, b);
        assertEquals(a, c);
    }

    @Test
    public void assertionRouletteTrue3() {
        int a = 0;
        assertEquals(a, a);
    }
}
