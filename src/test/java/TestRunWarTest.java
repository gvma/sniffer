import org.junit.Test;

public class TestRunWarTest {

    @Test
    public void test() {
        System.getProperty("user.dir");
    }

    @Test
    public void test1() {
        System.setProperty("testing", "true");
    }

    @Test
    public void test2() {
        System.exit(0);
    }
}
