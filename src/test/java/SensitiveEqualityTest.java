import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SensitiveEqualityTest {

    private void testingToString() {

    }

    @Test
    public void toStr() {
        assertEquals("", "".toString());
    }

    @Test
    public void test() {
        Integer integer = 0;
        integer.toString();
        testingToString();
    }
}
