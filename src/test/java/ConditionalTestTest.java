import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConditionalTestTest {
    @Test
    public void conditionalTestTrue1() {
        if (true) {
            assertEquals(true, true);
        }
    }

    @Test
    public void conditionalTestTrue2() {
        for (int i = 0; i < 5; ++i) {
            assertEquals(true, true);
        }
    }

    @Test
    public void conditionalTestTrue3() {
        for (int i = 0; i < 5; ++i) {
            assertEquals(true, true);
        }
    }

    @Test
    public void conditionalTestTrue4() {
        do {
            assertEquals(true, true);
        } while (true);
    }

    @Test
    public void conditionalTestFalse1() {
        if (true) {
        }
    }

    @Test
    public void conditionalTestFalse2() {
        for (int i = 0; i < 5; ++i) {
        }
    }

    @Test
    public void conditionalTestFalse3() {
        for (int i = 0; i < 5; ++i) {
        }
    }

    @Test
    public void conditionalTestFalse4() {
        do {
        } while (true);
    }
}
