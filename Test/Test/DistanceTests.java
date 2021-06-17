import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class DistanceTests {

    @Test
    public void testUser() {
        Assertions.assertAll(
                () -> Assertions.assertEquals(4.6258503401f, new User(3.0f, 70, 5.1f, 200,0,0).getDistance()),
                () -> Assertions.assertEquals(1.0f+(1.0f/12), new User(3.5f, 80, 4.9f, 65,0,0).getDistance()),
                () -> Assertions.assertEquals(3.059858f, new User(8.3f, 60, 8, 200,0,0).getDistance())
        );
    }
}
