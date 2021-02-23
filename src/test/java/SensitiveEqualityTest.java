import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
