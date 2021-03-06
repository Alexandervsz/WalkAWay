import data.MetValue;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

/**
 * Test class for the toString method of the MetValues class.
 */
public class MetValueTests {

    /**
     * Tests whether the right string is returned based on whether or not the activity has a speed range.
     */
    @Test
    public void metValueTests() {
        MetValue test1 = new MetValue(0, 0, 0, "test");
        MetValue test2 = new MetValue(0, 1, 0, "test");
        MetValue test3 = new MetValue(0, 0, 1, "test");
        MetValue test4 = new MetValue(0, 1, 1, "test");
        Assertions.assertAll(
                () -> Assertions.assertEquals("test", test1.toString()),
                () -> Assertions.assertEquals("test at 1km/h", test2.toString()),
                () -> Assertions.assertEquals("test between 0 and 1km/h", test3.toString()),
                () -> Assertions.assertEquals("test between 1 and 1km/h", test4.toString())
        );

    }
}
