import java.util.*;

public class Way {
    private final String id;
    private final TreeMap<Integer, Node> nodePositions;
    private final Set<Node> nodeSet;
    private Way previousWay;
    private int direction;
    private int entryPoint;

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
            node.getDistanceTo(target);
        }
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

    public TreeMap<Integer, Node> getNodePositions() {
        return nodePositions;
    }

    public NavigableMap<Integer, Node> getNodePositionsReversed() {
        return nodePositions.descendingMap();
    }

    public void setDirection(int direction) {
        this.direction = direction;
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

    public Way getPreviousWay() {
        return previousWay;
    }

    public void setPreviousWay(Way previousWay) {
        if (previousWay != null) {
            this.previousWay = previousWay;
        }
    }

    public int getEntryPoint() {
        return entryPoint;
    }

    public void setEntryPoint(int entryPoint) {
        this.entryPoint = entryPoint;
    }

    @Override
    public String toString() {
        return "Way{" +
                "id='" + id + '\'' +
                '}';
    }
}
