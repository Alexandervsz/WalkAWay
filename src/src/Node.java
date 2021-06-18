import java.util.Objects;

public class Node implements Comparable<Node> {
    private final String id;
    private final double lon;
    private final double lat;
    private double distanceToCurrentNode;
    private Way way;
    private double bearingToCurrentNode;

    public String getId() {
        return id;
    }

    public Node(String id, double lon, double lat) {
        this.id = id;
        this.lon = lon;
        this.lat = lat;
        this.bearingToCurrentNode = 0;
    }

    public Way getWay() {
        return way;
    }

    public void setWay(Way way) {
        this.way = way;
    }

    public double getDistanceTo(Node node) {
        double R = 6371e3;
        double phi1 = node.getLat() * Math.PI / 180;
        double phi2 = this.lat * Math.PI / 180;
        double deltaphi = (this.lat - node.getLat()) * Math.PI / 180;
        double deltalambda = (this.lon - node.getLon()) * Math.PI / 180;
        double a = Math.sin(deltaphi / 2) * Math.sin(deltaphi / 2) + Math.cos(phi1) * Math.cos(phi2) *
                Math.sin(deltalambda / 2) * Math.sin(deltalambda / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        distanceToCurrentNode = R * c;
        return R * c;
    }


    public void getBearingTo(Node node) {
        double y = Math.sin(node.getLon() - lon) * Math.cos(node.getLat());
        double x = Math.cos(lat) * Math.sin(node.getLat()) -
                Math.sin(lat) * Math.cos(node.getLat()) * Math.cos(node.getLon() - lon);
        double theta = Math.toDegrees(Math.atan2(y, x));
        /*if (theta < 0) {
            theta += 360.0;
        }*/
        //bearingToCurrentNode = theta;
        //theta = (theta * 180 / Math.PI + 360) % 360;
        bearingToCurrentNode = (theta * 180 / Math.PI + 360) % 360;

    }

    public double getDistanceToCurrentNode() {
        return distanceToCurrentNode;
    }

    public double getBearingToCurrentNode() {
        return bearingToCurrentNode;
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
