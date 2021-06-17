import java.util.*;

public class Way {
    private final String id;
    private final String[] type;
    private final TreeMap<Integer, Node> nodePositions;
    private double distanceToCurrentNode;

    public Way(String id,  String[] type) {
        this.id = id;
        this.type = type;
        nodePositions = new TreeMap<>();
    }
    public void addNode(int position, Node node){
        nodePositions.put(position, node);
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

    @Override
    public String toString() {
        return "Way{" +
                "id='" + id + '\'' +
                ", type=" + Arrays.toString(type) +
                '}';
    }
}
