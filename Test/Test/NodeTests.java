import org.junit.Test;
import org.junit.jupiter.api.Assertions;


public class NodeTests {
    @Test
    public void testDistance() {
        Node test1 = new Node("test", 5.0650352, 52.650352);
        Node test2 = new Node("test", 5.0650627, 52.6502963);
        Node test3 = new Node("test", 5.0650627, 53.6502963);
        Node test4 = new Node("test", 5.0657671, 52.6504847);
        Node test5 = new Node("test", 5.0659635, 52.6505343);

        Assertions.assertAll(
                () -> Assertions.assertEquals(0, test1.getDistanceTo(test1)),
                () -> Assertions.assertEquals(6.6, test1.getDistanceTo(test2),0.15), // should be 0.1, but pixel measurements are not 100% accurate.
                () -> Assertions.assertEquals(111111,test2.getDistanceTo(test3),170), // inaccurate with large numbers.
                () -> Assertions.assertEquals(14.5, test4.getDistanceTo(test5), 0.2)

        );
    }
}
