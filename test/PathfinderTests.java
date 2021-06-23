import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Test class for the PathFinder class.
 */
public class PathfinderTests {
    Node start = new Node("start", 5.070738, 52.637090);
    Node node1 = new Node("1", 5.070749, 52.637081);
    Node node2 = new Node("2", 5.070773, 52.637073);
    Node node3 = new Node("3", 5.070797, 52.637066);
    Way way1 = new Way("1");
    Node node4 = new Node("4", 5.070942, 52.637018);
    Node node5 = new Node("5", 5.070970, 52.637007);
    Node node6 = new Node("6", 5.071000, 52.636997);
    Way way2 = new Way("2");
    Node node7 = new Node("7", 5.071037, 52.636981);
    Node node8 = new Node("8", 5.071066, 52.636971);
    Node node9 = new Node("9", 5.071087, 52.636961);
    Way way3 = new Way("3");
    Node node10 = new Node("10", 5.070760, 52.637236);
    Node node11 = new Node("11", 5.070749, 52.637283);
    Node node12 = new Node("12", 5.070733, 52.637327);
    Way way4 = new Way("4");
    Node node13 = new Node("13", 5.070498, 52.636946);
    Node node14 = new Node("14", 5.070437, 52.636923);
    Node node15 = new Node("15", 5.070386, 52.636889);
    Way way5 = new Way("5");

    /**
     * Tests whether getRoute generates the correct path.
     */
    @Test
    public void testRoute() {
        initialiseNodes();
        Set<Way> waySet = generateWaySet();
        PathFinder pathFinder = new PathFinder(start, waySet, 0);
        List<Node> expectedOutput = new ArrayList<>();
        expectedOutput.add(start);
        Assertions.assertEquals(expectedOutput, pathFinder.getRoute());

        initialiseNodes();
        waySet = generateWaySet();
        pathFinder = new PathFinder(start, waySet, 2000);
        Assertions.assertTrue(pathFinder.getRoute().isEmpty());

        initialiseNodes();
        waySet = generateWaySet();
        pathFinder = new PathFinder(start, waySet, 26);
        expectedOutput = new ArrayList<>();
        expectedOutput.add(start);
        expectedOutput.add(node1);
        expectedOutput.add(node2);
        expectedOutput.add(node3);
        expectedOutput.add(node4);
        expectedOutput.add(node5);
        expectedOutput.add(node6);
        expectedOutput.add(node7);
        expectedOutput.add(node8);
        expectedOutput.add(node9);
        Assertions.assertEquals(expectedOutput, pathFinder.getRoute());

    }

    /**
     * Tests whether getClosestWay returns the closest way.
     */
    @Test
    public void testClosestWay() {
        initialiseNodes();
        Set<Way> waySet = generateWaySet();
        PathFinder pathFinder = new PathFinder(start, waySet, 100);
        Assertions.assertEquals(way1, pathFinder.getClosestWay(start));
        waySet.remove(way3);
        Assertions.assertEquals(way2, pathFinder.getClosestWay(node7));
        waySet.add(way3);
        waySet.remove(way2);
        Assertions.assertEquals(way3, pathFinder.getClosestWay(node6));

    }

    /**
     * Tests whether walkPath correctly walks the path from input to finish.
     */
    @Test
    void testWalkPath() {
        Set<Way> waySet = generateWaySet();
        initialiseNodes();
        PathFinder pathFinder = new PathFinder(start, waySet, 100);
        pathFinder.walkPath(way1.getNodePositionsFromPoint(1), node1);
        List<Node> expectedOutput = new ArrayList<>();
        expectedOutput.add(start);
        expectedOutput.add(node2);
        expectedOutput.add(node3);
        Assertions.assertEquals(expectedOutput, pathFinder.getPath());

        waySet = generateWaySet();
        initialiseNodes();
        pathFinder = new PathFinder(start, waySet, 100);
        pathFinder.walkPath(way1.getNodePositionsFromPoint(2), node2);
        expectedOutput = new ArrayList<>();
        expectedOutput.add(start);
        expectedOutput.add(node3);
        Assertions.assertEquals(expectedOutput, pathFinder.getPath());
    }

    /**
     * Test whether the processWay function is working as intended.
     */
    @Test
    void processWay() {
        Set<Way> waySet = generateWaySet();
        initialiseNodes();
        PathFinder pathFinder = new PathFinder(start, waySet, 100);
        pathFinder.processWay(node1, way1);
        List<Node> expectedOutput = new ArrayList<>();
        expectedOutput.add(start);
        expectedOutput.add(node2);
        expectedOutput.add(node3);
        Assertions.assertEquals(expectedOutput, pathFinder.getPath());
    }

    /**
     * Refreshes the nodes, since the pathfinding algorithm changes the nodes distanceToCurrentNode.
     */
    public void initialiseNodes() {
        way1.addNode(1, node1);
        way1.addNode(2, node2);
        way1.addNode(3, node3);
        way2.addNode(1, node4);
        way2.addNode(2, node5);
        way2.addNode(3, node6);
        way3.addNode(1, node7);
        way3.addNode(2, node8);
        way3.addNode(3, node9);
        way4.addNode(1, node10);
        way4.addNode(2, node11);
        way4.addNode(3, node12);
        way5.addNode(1, node13);
        way5.addNode(2, node14);
        way5.addNode(3, node15);
    }

    /**
     * Initialises the wayset, must be called every test since pathfinder modifies it's contents.
     *
     * @return A set with the test ways.
     */
    public Set<Way> generateWaySet() {
        Set<Way> wayset = new HashSet<>();
        wayset.add(way1);
        wayset.add(way2);
        wayset.add(way3);
        wayset.add(way4);
        wayset.add(way5);
        return wayset;
    }

}
