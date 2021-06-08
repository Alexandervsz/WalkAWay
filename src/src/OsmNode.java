public class OsmNode {
    private final String id;
    private double lon;
    private double lat;

    public OsmNode(String id) {
        this.id = id;
    }

    public OsmNode(String id, double lon, double lat) {
        this.id = id;
        this.lon = lon;
        this.lat = lat;
    }
}
