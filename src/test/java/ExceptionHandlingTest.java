import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ExceptionHandlingTest {

    @Test
    public void exceptionHandlingTest() {
        try {

        } catch (Exception e) {

        }
    }

    @Test
    public void exceptionHandlingTest1() {
        try {
            int a = 0;
            assertEquals(a, a);
        } catch (Exception e) {
        }
    }

    @Test
    public void exceptionHandlingTest2() {
        try {
        } catch (Exception e) {
            int a = 0;
            assertEquals(a, a);
        }
    }

    @Test
    public void exceptionHandlingTest3() {
        try {
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void exceptionHandlingTest4() {
        try {
            fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void exceptionHandlingTest5() {
        try {
            fail();
        } catch (Exception e) {
        }
        int a = 0;
    }

    @Test
    public void exceptionHandlingTest6() {
        int a = 0;
        try {
            fail();
        } catch (Exception e) {
        }
    }
}
