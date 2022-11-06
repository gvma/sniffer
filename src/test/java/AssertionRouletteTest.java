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
        int c = 1;
        assertEquals(a, a);
        assertEquals(a, c);
    }

    @Test
    public void assertionRouletteTrue4() {
        double a = 2.8;
        double b = 2.8;
        assertEquals(a, b, 0.1);
        assertEquals(a, b, 0.3);
    }

    @Test
    public void assertionRouletteTrue5() {
        double a = 2.8;
        double b = 2.8;
        double delta1 = 0.1;
        double delta2 = 0.3;
        assertEquals(a, b, delta1);
        assertEquals(a, b, delta2);
    }

    @Test
    public void assertionRouletteFalse1() {
        double a = 2.8;
        double b = 2.8;
        assertEquals(a, b, 0.1);
    }

    @Test
    public void assertionRouletteFalse2() {
        int a = 1;
        int b = 2;
        assertEquals(a, b);
    }
}
