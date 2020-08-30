import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AssertionRouletteTest {

    @Test
    public void whatever1() {
        int a = 0;
        assertEquals(a, a);
        assertEquals(a, a);
    }

    @Test
    public void whatever2() {
        int a = 0;
        assertEquals(a, a);
        assertEquals(a, a);
    }

    @Test
    public void whatever3() {
        int a = 0;
        assertEquals(a, a);
    }
}
