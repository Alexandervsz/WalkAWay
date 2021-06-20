import java.util.*;
import java.util.List;

/**
 * This class generates a path of a given length, or null if a path canot be found.
 */
public class PathFinder {
    private final Set<Way> waySet;
    private Way previousWay;
    private Node currentNode;
    private final double requiredDistance;
    private double totalDistance;
    private final List<Node> path;

    /**
     * Creates a new pathfinder object.
     * @param beginNode The node where the pathfinder should begin.
     * @param waySet The overpass data
     * @param distanceToWalk The required distance of the generated path.
     */
    public PathFinder(Node beginNode, Set<Way> waySet, double distanceToWalk) {
        this.waySet = waySet;
        this.path = new ArrayList<>();
        this.totalDistance = 0;
        this.requiredDistance = distanceToWalk;
        this.currentNode = beginNode;
        this.previousWay = new Way("start");
        path.add(beginNode);
    }

    /**
     * This program files a route by first getting the closest node and then finding out which way has the closest node.
     * Then, it "walks" to the next node (adding the distance it walked to the total) and follows the path from
     * the node it entered. Once the path has been traversed, it checks whether the required distance has been met,
     * and starts over again if needed. Also, when it meets a dead end, it follows the routes back to either start
     * (no path found) or until a viable path is found.
     */
    public List<Node> getRoute() {
        while (totalDistance < requiredDistance) {
            Way currentWay = getClosestWay(currentNode);
            currentWay.setPreviousWay(previousWay);
            while (currentWay.equals(new Way("empty"))) {
                if (currentWay.getDirection() == 1) {
                    NavigableMap<Integer, Node> waynodes = currentWay.getNodePositionsReversed();
                    revertPath(waynodes, currentWay);
                } else if (currentWay.getDirection() == -1) {
                    TreeMap<Integer, Node> waynodes = currentWay.getNodePositions();
                    revertPath(waynodes, currentWay);
                }
                currentWay = currentWay.getPreviousWay();
                if (currentWay.equals(new Way("start"))) { // todo: Verify that this is working correctly.
                    return null;
                }
                currentWay = getClosestWay(currentWay.getLastNode());
            }
            waySet.remove(currentWay);
            Node closestNode = currentWay.getClosestNode(currentNode);
            closestNode.getDistanceTo(currentNode);
            totalDistance += closestNode.getDistanceToCurrentNode();
            path.add(closestNode);
            processWay(closestNode, currentWay);
        }
        return path;
    }

    /**
     * This function removes the distance traveled along a path up until the point it was entered.
     *
     * @param map        The nodes of the way.
     * @param currentWay The current way being processed.
     */
    public void revertPath(Map<Integer, Node> map, Way currentWay) {
        for (Map.Entry<Integer, Node> entry : map.entrySet()) {
            if (entry.getKey() == currentWay.getEntryPoint()) {
                break;
            }
            entry.getValue().getDistanceTo(currentNode);
            path.remove(entry.getValue());
            totalDistance = totalDistance - entry.getValue().getDistanceToCurrentNode();
            currentNode = entry.getValue();
        }
    }

    /**
     * Returns the closest way of all the ways in the current set.
     *
     * @param target The target node of which the distance is required.
     * @return The way with the closest node in it.
     */
    public Way getClosestWay(Node target) {
        double closest = Double.MAX_VALUE;
        Way closestWay = null;
        for (Way way : waySet) {
            Node closestWayNode = way.getClosestNode(target);
            if (closestWayNode.getDistanceToCurrentNode() < closest) {
                closest = closestWayNode.getDistanceToCurrentNode();
                closestWay = way;
            }
        }
        if (closest > 200) {
            System.out.println("empty");
            return new Way("empty");
        } else {
            return closestWay;
        }
    }

    /**
     * Cuts the treemap down to the right position and length, based on the direction the algorithm is going,
     * and where the algorithm entered the path.
     *
     * @param entryNode  The node where the algorithm entered the path.
     * @param currentWay The current way being processed.
     */
    public void processWay(Node entryNode, Way currentWay) {
        TreeMap<Integer, Node> nodeMap = currentWay.getNodePositions();
        int midpoint = nodeMap.firstKey() + nodeMap.lastKey() / 2;
        int entry = currentWay.getPositionOfNode(entryNode);
        currentWay.setEntryPoint(entry);
        if (entry > midpoint) {
            currentWay.setDirection(-1);
        } else {
            currentWay.setDirection(1);
        }
        if (currentWay.getDirection() == 1) {
            previousWay = currentWay;
            walkPath(currentWay.getNodePositionsFromPoint(entry), entryNode);
        } else {
            previousWay = currentWay;
            walkPath(currentWay.getNodePositionsFromPointReversed(entry), entryNode);
        }
    }

    /**
     * Processes the required nodes. Adds their distance to the total, and the nodes themselves to the path.
     *
     * @param wayPath   The processed treemap.
     * @param entryNode The node where the algorithm entered the path.
     */
    public void walkPath(SortedMap<Integer, Node> wayPath, Node entryNode) {
        Node currentPathNode = entryNode;
        wayPath.remove(wayPath.firstKey());
        for (Map.Entry<Integer, Node> entry : wayPath.entrySet()) {
            if (totalDistance >= requiredDistance) {
                return;
            }
            totalDistance += entry.getValue().getDistanceTo(currentPathNode);
            path.add(entry.getValue());
            currentPathNode = entry.getValue();
        }
        currentNode = currentPathNode;
    }

    /**
     * @return the total length of the path, in meters.
     */
    public double getTotalDistance() {
        return totalDistance;
    }


}
