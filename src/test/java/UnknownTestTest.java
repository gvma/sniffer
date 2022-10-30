import org.junit.Test;

public class UnknownTestTest {

    @Test
    public void unknownTestTrue1() {
    }

    @Test
    public void unknownTestTrue2() {
        for (int i = 0; i < 1; ++i) {

        }
    }

    @Test
    public void unknownTestTrue3() {
        while (true) {

        }
    }

//    @Test(expected = Exception.class)
//    public void unknownTestAnnotationParamFalse() throws Exception {
//        throw new Exception();
//    }
}
