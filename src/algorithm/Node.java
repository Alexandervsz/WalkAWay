package algorithm;

import java.util.Objects;

/**
 * A node is a location point tied to an id, multiple nodes make up a way.
 */
public class Node implements Comparable<Node> {
    private final String id;
    private final double lon; //decimal degrees.
    private final double lat; //decimal degrees.
    private double distanceToCurrentNode; //meters.

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
    }

    /**
     * Calculates the distance between this node, and target node, using the Haversine formula.
     * Has to set distanceToCurrentNode, to help with sorting by distance.
     * Based on the formulas by: <a href="https://www.movable-type.co.uk/scripts/latlong.html">Movable Type</a>
     *
     * @param target The target node
     * @return The distance in meters.
     */
    public double getDistanceTo(Node target) {
        double R = 6371000.0;
        double phi1 = lat * Math.PI / 180;
        double phi2 = target.getLat() * Math.PI / 180;
        double deltaPhi = (target.getLat() - lat) * Math.PI / 180;
        double deltaLambda = (target.getLon() - lon) * Math.PI / 180;
        double a = Math.sin(deltaPhi / 2) * Math.sin(deltaPhi / 2) + Math.cos(phi1) * Math.cos(phi2) *
                Math.sin(deltaLambda / 2) * Math.sin(deltaLambda / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        distanceToCurrentNode = R * c; // In meters.
        return R * c;
    }

    /**
     * For use in pathfinding.
     *
     * @return The distance to the current node (in this object).
     */
    public double getDistanceToCurrentNode() {
        return distanceToCurrentNode;
    }

    /**
     * @return The longitude in decimal degrees.
     */
    public double getLon() {
        return lon;
    }

    /**
     * @return The latitude in decimal degrees.
     */
    public double getLat() {
        return lat;
    }

    /**
     * Returns the id
     * @return A string containing the id.
     */
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
        return "algorithm.Node{" +
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
