import java.util.Objects;

public class Node implements Comparable<Node> {
    private final String id;
    private double lon;
    private double lat;
    private double distanceToCurrentNode;

    public String getId() {
        return id;
    }

    public Node(String id) {
        this.id = id;
    }

    public Node(String id, double lon, double lat) {
        this.id = id;
        this.lon = lon;
        this.lat = lat;
    }

    public void getDistanceTo(Node osmNode){
        double R = 6371e3;
        double phi1 = osmNode.getLat() * Math.PI/180;
        double phi2 = this.lat * Math.PI/180;
        double deltaphi = (this.lat - osmNode.getLat()) * Math.PI/180;
        double deltalambda = (this.lon - osmNode.getLon()) * Math.PI/180;
        double a = Math.sin(deltaphi/2) * Math.sin(deltaphi / 2) + Math.cos(phi1) * Math.cos(phi2) *
                Math.sin(deltalambda / 2) * Math.sin(deltalambda / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        distanceToCurrentNode = R * c;
    }

    public double getDistanceToCurrentNode() {
        return distanceToCurrentNode;
    }

    public double getLon() {
        return lon;
    }

    public double getLat() {
        return lat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(id, node.id);
    }


    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Node{" +
                "id='" + id + '\'' +
                ", lon=" + lon +
                ", lat=" + lat +
                '}';
    }


    @Override
    public int compareTo(Node o) {
        return Double.compare(distanceToCurrentNode, o.distanceToCurrentNode);
    }

}
