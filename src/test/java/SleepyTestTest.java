import org.junit.Test;

import static java.lang.Thread.sleep;

public class SleepyTestTest {

    @Test
    public void sleepyTest() throws InterruptedException {
        sleep(1);
    }

    @Test
    public void sleepyTest1() throws InterruptedException {
        sleep(1);
    }

    @Test
    public void sleepyTest2() throws InterruptedException {
        iWantToSleep();
    }

    public void iWantToSleep() {

    }
}
