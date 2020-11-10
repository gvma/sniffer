import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ExceptionHandlingTest {

    @Test
    public void exceptionHandlingTestFalse1() {
        try {

        } catch (Exception e) {

        }
    }

    @Test
    public void exceptionHandlingTestTrue1() {
        try {
            int a = 0;
            assertEquals(a, a);
        } catch (Exception e) {
        }
    }

    @Test
    public void exceptionHandlingTestTrue2() {
        try {
        } catch (Exception e) {
            int a = 0;
            assertEquals(a, a);
        }
    }

    @Test
    public void exceptionHandlingTestTrue3() {
        try {
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void exceptionHandlingTestTrue4() {
        try {
            fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void exceptionHandlingTestTrue5() {
        try {
            fail();
        } catch (Exception e) {
        }
        int a = 0;
    }

    @Test
    public void exceptionHandlingTestTrue6() {
        int a = 0;
        try {
            fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void exceptionHandlingTestTrue7() {
        for (int i = 0; i < 1; ++i) {
            int a = 0;
        }
        try {
            fail();
        } catch (Exception e) {
        }
    }
}
