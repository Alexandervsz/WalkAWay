import org.junit.Test;
import org.junit.jupiter.api.Assertions;


public class NodeTests {
    Node test1 = new Node("test", 5.0650352, 52.650352);
    Node test2 = new Node("test", 5.0650627, 52.6502963);
    Node test3 = new Node("test", 5.0650627, 53.6502963);
    Node test4 = new Node("test", 5.0657671, 52.6504847);
    Node test5 = new Node("test", 5.0659635, 52.6505343);
    Node test6 = new Node("test2", 5.0650352, 52.650352);

    @Test
    public void testDistance() {

        Assertions.assertAll(
                () -> Assertions.assertEquals(0, test1.getDistanceTo(test1)),
                () -> Assertions.assertEquals(6.6, test1.getDistanceTo(test2), 6.6 * 0.03), // Haversine formula has an accuracy of 3%.
                () -> Assertions.assertEquals(111111, test2.getDistanceTo(test3), 111111 * 0.03),
                () -> Assertions.assertEquals(14.5, test4.getDistanceTo(test5), 14.5 * 0.03)

        );
    }

    @Test
    public void testEquals() {

        Assertions.assertAll(
                () -> Assertions.assertEquals(test1, test3),
                () -> Assertions.assertNotEquals(test1, test6)
        );

    }

    @Test
    public void testLonLat() {
        Assertions.assertAll(
                () -> Assertions.assertEquals(5.0650352, test1.getLon()),
                () -> Assertions.assertEquals(52.650352, test1.getLat())
        );
    }

    @Test
    public void testCompare() {
        test1.getDistanceTo(test2);
        test2.getDistanceTo(test1);
        test3.getDistanceTo(test1);

        Assertions.assertAll(
                () -> Assertions.assertEquals(0, test1.compareTo(test2)),
                () -> Assertions.assertEquals(-1, test2.compareTo(test3)),
                () -> Assertions.assertEquals(1, test3.compareTo(test2))
        );
    }
}
