import java.util.*;

/**
 * A way contains two or more nodes, linked together by a number. (the position)
 */
public class Way {
    private final String id;
    private final TreeMap<Integer, Node> nodePositions;
    private final Set<Node> nodeSet;
    private Way previousWay;
    private int direction;
    private int entryPoint;

    /**
     * Creates a new way object
     * @param id The id number of the way.
     */
    public Way(String id) {
        this.id = id;
        nodePositions = new TreeMap<>();
        nodeSet = new HashSet<>();
    }

    /**
     * Called when the json file is being processed, and is setting up the way.
     *
     * @param position The position of the node in the way.
     * @param node     Part of the way.
     */
    public void addNode(int position, Node node) {
        nodePositions.put(position, node);
        nodeSet.add(node);
    }

    /**
     * Returns the closest node in the way, in relation to target node.
     *
     * @param target The node to be compared against.
     * @return The node with the smallest distance.
     */
    public Node getClosestNode(Node target) {
        for (Node node : nodeSet) {
            node.getDistanceTo(target);}
        return Collections.min(nodeSet);
    }

    /**
     * The pathfinding algorithm uses this to decide if it's traveling the way forwards or backwards.
     *
     * @return The direction the algorithm is currently heading (1 for forward, -1 for backward.)
     */
    public int getDirection() {
        return direction;
    }

    /**
     * Gets the last node in the path,
     *
     * @return The last node relative to the direction the path was traveled.
     */
    public Node getLastNode() {
        if (direction == 1) {
            return nodePositions.lastEntry().getValue();
        } else {
            return nodePositions.firstEntry().getValue();
        }
    }

    /**
     * Returns the position of the node in the way.
     *
     * @param target The target node.
     * @return The position of the target.
     */
    public int getPositionOfNode(Node target) {
        for (Map.Entry<Integer, Node> entry : nodePositions.entrySet()) {
            if (entry.getValue().equals(target)) {
                return entry.getKey();
            }
        }
        return -1;
    }

    /**
     * Returns the nodes of the way starting from the given point.
     *
     * @param point The start point
     * @return A map with the required nodes and their positions.
     */
    public SortedMap<Integer, Node> getNodePositionsFromPoint(int point) {
        return nodePositions.tailMap(point);
    }

    /**
     * Returns the nodes of the way starting from the given point, if entered from the last point.
     *
     * @param point The start point
     * @return A map with the required nodes and their positions.
     */
    public SortedMap<Integer, Node> getNodePositionsFromPointReversed(int point) {
        NavigableMap<Integer, Node> reverse = nodePositions.descendingMap();
        return reverse.tailMap(point);
    }

    /**
     * Returns the contents of the way (the nodes and their positions)
     * @return A map with the nodes and their positions.
     */
    public TreeMap<Integer, Node> getNodePositions() {
        return nodePositions;
    }

    /**
     * Returns the contents of the way, reversed.
     * @return A map with the nodes and this positions.
     */
    public NavigableMap<Integer, Node> getNodePositionsReversed() {
        return nodePositions.descendingMap();
    }

    /**
     * Sets the direction the pathfinding algorithm traveled the path.
     * @param direction 1 for forwards, -1 for backwards.
     */
    public void setDirection(int direction) {
        this.direction = direction;
    }

    /**
     * Gets the way by which the algorithm got there.
     * @return The way the algorithm processed before this one.
     */
    public Way getPreviousWay() {
        return previousWay;
    }

    /**
     * Function for the pathfinding algorithm to set the previous way.
     * @param previousWay The way the algorithm was processing before this one.
     */
    public void setPreviousWay(Way previousWay) {
        if (previousWay != null) {
            this.previousWay = previousWay;
        }
    }

    /**
     * Returns the point at which the algorithm entered the way.
     * @return the number of the node, by which the algorithm entered.
     */
    public int getEntryPoint() {
        return entryPoint;
    }

    /**
     * Is called when the algorithm starts processing a way.
     * @param entryPoint The point the algorithm was processing.
     */
    public void setEntryPoint(int entryPoint) {
        this.entryPoint = entryPoint;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Way way = (Way) o;
        return Objects.equals(id, way.id);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id);
        result = 31 * result;
        return result;
    }

    @Override
    public String toString() {
        return "Way{" +
                "id='" + id + '\'' +
                '}';
    }
}
