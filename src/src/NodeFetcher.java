import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class NodeFetcher {
    private final Set<Node> nodeSet;
    private final Set<Way> waySet;
    private double distanceTraveled;
    private Node currentNode;
    private final double totalDistance;
    private final List<Node> path;
    private Way currentWay;

    public NodeFetcher(Node start, double totalDistance) {
        this.nodeSet = new HashSet<>();
        this.waySet = new HashSet<>();
        this.path = new ArrayList<>();
        this.distanceTraveled = 0;
        this.currentNode = start;
        this.totalDistance = totalDistance;
    }

    public JSONObject getOverpassData() throws IOException, InterruptedException, ParseException {
        String bbox = generateBbox();
        DatabaseManager databaseManager = new DatabaseManager();
        List<NodeType> nodeTypes = databaseManager.getNodeTypes();
        List<String> options = new ArrayList<>();
        options.add("way");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[out:json];(");
        for (NodeType nodeType : nodeTypes) {
            for (String option : options) {
                stringBuilder.append(option);
                stringBuilder.append("[");
                stringBuilder.append(nodeType.getMainType());
                if (!nodeType.getSubType().equals("-1")) {
                    stringBuilder.append("=");
                    stringBuilder.append(nodeType.getSubType());
                }
                stringBuilder.append("](");
                stringBuilder.append(bbox);
                stringBuilder.append(");");
            }
        }
        stringBuilder.append(");(._;>;);out;");
        String query = URLEncoder.encode(stringBuilder.toString(), StandardCharsets.UTF_8);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://overpass.kumi.systems/api/interpreter?data=" + query))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(response.body());
        return (JSONObject) obj;

    }

    public void parseJson(JSONObject jsonObject) throws ParseException {
        JSONArray elements = (JSONArray) jsonObject.get("elements");
        for (Object object : elements) {
            JSONParser parser = new JSONParser();
            JSONObject osmObject = (JSONObject) parser.parse(object.toString());
            switch (osmObject.get("type").toString()) {
                case "node" -> {
                    String id = osmObject.get("id").toString();
                    double lon = Double.parseDouble(osmObject.get("lon").toString());
                    double lat = Double.parseDouble(osmObject.get("lat").toString());
                    nodeSet.add(new Node(id, lon, lat));
                }
                case "way" -> {
                    String wayId = osmObject.get("id").toString();
                    String tags = osmObject.get("tags").toString();
                    String[] tagList = tags.split(",");
                    String type = tagList[tagList.length - 1];
                    type = type.replace("{", "");
                    type = type.replace("}", "");
                    type = type.replace("\"", "");
                    String[] typesList = type.split(":");
                    String nodesString = osmObject.get("nodes").toString();
                    String[] nodes = nodesString.split(",");
                    for (int x = 0; x < nodes.length; x++) {
                        nodes[x] = nodes[x].replace("[", "");
                        nodes[x] = nodes[x].replace("]", "");

                    }
                    List<Node> nodeList = new ArrayList<>();
                    List<Node> nodesList = new ArrayList<>(nodeSet);
                    Way newWay = new Way(wayId, typesList);
                    for (String nodeId : nodes) {
                        Node node = nodesList.get(nodesList.indexOf(new Node(nodeId)));
                        node.setWay(newWay);
                        nodeList.add(node);
                    }
                    waySet.add(newWay);


                }
            }

        }
    }

    public String generateBbox() {
        //Position, decimal degrees
        double lat = currentNode.getLat();
        double lon = currentNode.getLon();

        //Earth’s radius, sphere
        double R = 6378137;
        //Coordinate offsets in radians
        double dLat = totalDistance / R;
        double dLon = totalDistance / (R * Math.cos(Math.PI * lat / 180));

        //OffsetPosition, decimal degrees
        double latO = lat - dLat * 180 / Math.PI;
        double lonO = lon - dLon * 180 / Math.PI;
        double lat1 = lat + dLat * 180 / Math.PI;
        double lon1 = lon + dLon * 180 / Math.PI;
        return latO + "," + lonO + "," + lat1 + "," + lon1;

    }


    public void getClosestNode() {
        while (distanceTraveled < totalDistance) {
            for (Node node : nodeSet) {
                node.getDistanceTo(currentNode);
            }
            List<Node> sortedList = new ArrayList<>(nodeSet);
            Collections.sort(sortedList);
            Node closestNode = sortedList.get(0);
            nodeSet.remove(closestNode);
            closestNode.getDistanceTo(currentNode);
            distanceTraveled += closestNode.getDistanceToCurrentNode();
            path.add(closestNode);

            List<Node> wayNodes = getAllWayNodes(closestNode.getWay());
            while (wayNodes.size() != 0) {
                for (Node node : wayNodes) {
                    node.getDistanceTo(closestNode);
                }
                Collections.sort(wayNodes);
                closestNode = wayNodes.get(0);
                distanceTraveled += closestNode.getDistanceToCurrentNode();
                path.add(closestNode);
                nodeSet.remove(closestNode);
                wayNodes.remove(closestNode);
                currentNode = closestNode;
            }



        }
        StringBuilder output = new StringBuilder();
        output.append("[out:json];node(id:");
        for (Node node : path) {
            output.append(node.getId());
            if (path.indexOf(node) != path.size() - 1) {
                output.append(",");
            }
        }
        output.append(");out skel;");
        System.out.println(output);
        System.out.println(distanceTraveled);
        File file = new File("test.gpx");
        generateGpx(file, "test", path);
    }


    public List<Node> getAllWayNodes(Way way){
        List<Node> nodeList = new ArrayList<>();
        for (Node node:nodeSet){
            if (node.getWay().equals(way)){
                nodeList.add(node);
            }
        }
        return nodeList;
    }

    public static void generateGpx(File file, String name, List<Node> points) {

        String header = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?><gpx xmlns=\"http://www.topografix.com/GPX/1/1\" creator=\"MapSource 6.15.5\" version=\"1.1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"  xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\"><trk>\n";
        name = "<name>" + name + "</name><trkseg>\n";

        StringBuilder segments = new StringBuilder();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        for (Node location : points) {
            segments.append("<trkpt lat=\"").append(location.getLat()).append("\" lon=\"").append(location.getLon()).append("\"><time>").append(df.format(new Date(System.currentTimeMillis()))).append("</time></trkpt>\n");
        }

        String footer = "</trkseg></trk></gpx>";

        try {
            FileWriter writer = new FileWriter(file, false);
            writer.append(header);
            writer.append(name);
            writer.append(segments.toString());
            writer.append(footer);
            writer.flush();
            writer.close();

        } catch (IOException e) {
            System.out.println("Error Writting Path" + e);
        }
    }

    public static void main(String[] args) throws IOException, ParseException, InterruptedException {
        JSONParser parser = new JSONParser();
        Node start = new Node("start", 5.069284, 52.636537);
        NodeFetcher nodeFetcher = new NodeFetcher(start, 4000);
        JSONObject jsonObject = nodeFetcher.getOverpassData();
        nodeFetcher.parseJson(jsonObject);
        nodeFetcher.getClosestNode();

    }
}
