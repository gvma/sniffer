import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

public class AssertionRouletteTest {

    @Test
    public void assertionRouletteTrue1() {
        int a = 0;
        assertEquals(a, a);
        assertEquals(a, a);
    }

    @Test
    public void assertionRouletteTrue2() {
        int a = 0;
        assertEquals(a, a);
        assertEquals(a, a);
    }

    @Test
    public void assertionRouletteTrue3() {
        int a = 0;
        assertEquals(a, a);
    }
}
