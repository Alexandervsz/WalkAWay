import java.util.*;

public class Way {
    private final String id;
    //private final String[] type;
    private final TreeMap<Integer, Node> nodePositions;
    private final Set<Node> nodeSet;
    private Way previousWay;
    private int direction;
    private int entryPoint;

    public Way(String id) {
        this.id = id;
        //this.type = type;
        nodePositions = new TreeMap<>();
        nodeSet = new HashSet<>();
    }

    public void addNode(int position, Node node) {
        nodePositions.put(position, node);
        nodeSet.add(node);
    }

    public Node getClosestNode(Node target) {
        for (Node node : nodeSet) {
            node.getDistanceTo(target);
        }
        return Collections.min(nodeSet);
    }

    public int getDirection() {
        return direction;
    }

    public Node getLastNode() {
        return nodePositions.lastEntry().getValue();
    }

    public Node getFirstNode() {
        return nodePositions.firstEntry().getValue();
    }

    public int getPositionOfNode(Node node) {
        for (Map.Entry<Integer, Node> entry : nodePositions.entrySet()) {
            if (entry.getValue().equals(node)) {
                return entry.getKey();
            }
        }
        return -1;
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

    /*public String[] getType() {
        return type;
    }*/

    @Override
    public int hashCode() {
        int result = Objects.hash(id);
        result = 31 * result;
        return result;
    }

    public SortedMap<Integer, Node> getNodePositionsFromPoint(int point) {
        return nodePositions.tailMap(point);
    }

    public SortedMap<Integer, Node> getNodePositionsFromPointReversed(int point) {
        NavigableMap<Integer, Node> reverse = nodePositions.descendingMap();
        return reverse.tailMap(point);
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
