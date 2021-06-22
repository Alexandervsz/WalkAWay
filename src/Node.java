import java.util.Objects;

/**
 * A node is a location point tied to an id, multiple nodes make up a way.
 */
public class Node implements Comparable<Node> {
    private final String id;
    private final double lon; //decimal degrees.
    private final double lat; //decimal degrees.
    private double distanceToCurrentNode; //meters.
    private double bearingToCurrentNode;

    /**
     * Creates a node.
     *
     * @param id  The id of the node
     * @param lon The longitude of the node in decimal degrees.
     * @param lat The latitude of the node in decimal degrees.
     */
    public Node(String id, double lon, double lat) {
        this.id = id;
        this.lon = lon;
        this.lat = lat;
        this.bearingToCurrentNode = 0;
    }

    /**
     * Calculates the distance between this node, and target node, using the Haversine formula.
     * Has to set distanceToCurrentNode, to help with sorting by distance.
     * Based on the formulas by: <a href="https://www.movable-type.co.uk/scripts/latlong.html">Movable Types</a>
     *
     * @param target The target node
     * @return The distance in meters.
     */
    public double getDistanceTo(Node target) {
        double R = 6371000.0;
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

    /**
     * Based on the formulas by: <a href="https://www.movable-type.co.uk/scripts/latlong.html">Movable Types</a>
     */
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

    /**
     * @return The longitude in decimal degrees.
     */
    public double getLon() {
        return lon;
    }

    /**
     * @return The lattitude in decimal degrees.
     */
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
    public int compareTo(Node o) {
        return Double.compare(distanceToCurrentNode, o.distanceToCurrentNode);
    }
}
