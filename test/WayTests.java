import algorithm.Node;
import algorithm.Way;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.TreeMap;

/**
 * Test class for the algorithm.Way class.
 */
public class WayTests {
    final Node test1 = new Node("test1", 5.068873, 52.636179);
    final Node test2 = new Node("test2", 5.069118, 52.636297);
    final Node test3 = new Node("test3", 5.069439, 52.636467);
    final Node test4 = new Node("test4", 5.069630, 52.636555);
    final Node test5 = new Node("test5", 5.069799, 52.636649);
    final Node test6 = new Node("test6", 5.070024, 52.636760);
    final Way testWay = new Way("test");

    /**
     * Tests whether the way returns the right positions and nodes. Also verifies that direction is handled correctly.
     */
    @Test
    public void testNodePositions() {
        testWay.addNode(1, test2);
        testWay.addNode(2, test3);
        testWay.addNode(3, test4);
        testWay.addNode(4, test5);
        testWay.addNode(5, test6);
        testWay.setDirection(1);
        TreeMap<Integer, Node> npTest1 = new TreeMap<>();
        npTest1.put(3, test4);
        npTest1.put(4, test5);
        npTest1.put(5, test6);
        TreeMap<Integer, Node> npTest2 = new TreeMap<>();
        npTest2.put(3, test4);
        npTest2.put(2, test3);
        npTest2.put(1, test2);
        Assertions.assertAll(
                () -> Assertions.assertEquals(test2, testWay.getClosestNode(test1)),
                () -> Assertions.assertEquals(test6, testWay.getLastNode()),
                () -> Assertions.assertEquals(1, testWay.getPositionOfNode(test2)),
                () -> Assertions.assertEquals(-1, testWay.getPositionOfNode(test1)),
                () -> Assertions.assertEquals(npTest1, testWay.getNodePositionsFromPoint(3)),
                () -> Assertions.assertEquals(npTest2, testWay.getNodePositionsFromPointReversed(3))
        );
        testWay.setDirection(-1);
        Assertions.assertEquals(test2, testWay.getLastNode());
    }


}
