import java.util.*;

public class Way {
    private final String id;
    private final String[] type;
    private final TreeMap<Integer, Node> nodePositions;
    private Way previousWay;
    private double distanceToCurrentNode;

    public Way(String id,  String[] type) {
        this.id = id;
        this.type = type;
        nodePositions = new TreeMap<>();
    }
    public void addNode(int position, Node node){
        nodePositions.put(position, node);
    }

    public Node getClosestNode(Node target){
        double closest = Double.MAX_VALUE;
        Node closestNode = null;
        for(Map.Entry<Integer,Node> entry : nodePositions.entrySet()) {
            entry.getValue().getDistanceTo(target);
            if (entry.getValue().getDistanceToCurrentNode() < closest && !entry.getValue().equals(target)){
                closest = entry.getValue().getDistanceToCurrentNode();
                closestNode = entry.getValue();

            }

        }
        return closestNode;
    }

    public int getPositionOfNode(Node node){
        for(Map.Entry<Integer,Node> entry : nodePositions.entrySet()) {
            if (entry.getValue().equals(node)){
                return entry.getKey();
            }
        }
        return -1;
    }

    public void calculatePositionsToNode(Node target){
        /*for (Node node: nodePositions){
            node.getDistanceTo(target);
        }*/

    }

    public TreeMap<Integer, Node> getNodePositions() {
        return nodePositions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Way way = (Way) o;
        return Objects.equals(id, way.id);
    }

    public String[] getType() {
        return type;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id);
        result = 31 * result + Arrays.hashCode(type);
        return result;
    }

    public String getid() {
        return this.id;
    }

    public Way getPreviousWay() {
        return previousWay;
    }

    public void setPreviousWay(Way previousWay) {
        if (previousWay == null){
        this.previousWay = previousWay;}
    }

    @Override
    public String toString() {
        return "Way{" +
                "id='" + id + '\'' +
                ", type=" + Arrays.toString(type) +
                '}';
    }
}
