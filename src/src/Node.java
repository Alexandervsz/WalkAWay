import java.util.Objects;

public class Node implements Comparable<Node> {
    private final String id;
    private final double lon;
    private final double lat;
    private double distanceToCurrentNode;
    private double bearingToCurrentNode;

    public Node(String id, double lon, double lat) {
        this.id = id;
        this.lon = lon;
        this.lat = lat;
        this.bearingToCurrentNode = 0;
    }

    /**
     * Calculates the distance between this node, and target node, using the Haversine formula.
     * Has to set distanceToCurrentNode, to help with sorting by distance.
     *
     * @param target The target node
     * @return The distance in meters.
     */
    public double getDistanceTo(Node target) {
        double R = 6_356_752.314245D;
        double phi1 = lat * Math.PI / 180;
        double phi2 = target.getLat() * Math.PI / 180;
        double deltaphi = (target.getLat() - lat) * Math.PI / 180;
        double deltalambda = (target.getLon() - lon) * Math.PI / 180;
        double a = Math.sin(deltaphi / 2) * Math.sin(deltaphi / 2) + Math.cos(phi1) * Math.cos(phi2) *
                Math.sin(deltalambda / 2) * Math.sin(deltalambda / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        distanceToCurrentNode = R * c; // In meters.
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

    /**
     * For use in pathfinding.
     *
     * @return The distance to the current node (in this object).
     */
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

    public String getId() {
        return id;
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
    //returns -1 if distance of target is greater than distance of current, 1 otherwise (0 if equal).
    public int compareTo(Node o) {
        return Double.compare(distanceToCurrentNode, o.distanceToCurrentNode);
    }
}
