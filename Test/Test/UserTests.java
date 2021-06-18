import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class UserTests {

    @Test
    public void testDistance() {
        Assertions.assertAll(
                () -> Assertions.assertEquals(4625.850340136053, new User(3.0, 70, 5.1, 200, 0, 0).getDistance()),
                () -> Assertions.assertEquals(1083.3333333333335, new User(3.5, 80, 4.9, 65, 0, 0).getDistance()),
                () -> Assertions.assertEquals(3059.8584815452277, new User(8.3, 60, 8, 200, 0, 0).getDistance()),
                () -> Assertions.assertEquals(0, new User(0, 0, 0, 0, 0, 0).getDistance())
        );
    }
}
