import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class NodeFetcher {
    private final Set<Node> nodeSet;
    private double distanceTraveled;
    private Node start;
    private double totalDistance;
    private List<Node> path;

    public NodeFetcher(Node start, double totalDistance) {
        this.nodeSet = new HashSet<>();
        this.path = new ArrayList<>();
        this.distanceTraveled = 0;
        this.start = start;
        this.totalDistance = totalDistance;
    }

    public JSONObject getOverpassData() throws IOException, InterruptedException, ParseException {
        String bbox = "52.646617767509,5.0588822364807,52.652287173554,5.0702011585236";
        DatabaseManager databaseManager = new DatabaseManager();
        List<NodeType> nodeTypes = databaseManager.getNodeTypes();
        List<String> options = new ArrayList<>();
        //options.add("node");
        options.add("way");
        //options.add("relation");
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
        System.out.println(response.body());
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(response.body());
        return (JSONObject) obj;

    }

    public void parseJson(JSONObject jsonObject) {
        JSONArray elements = (JSONArray) jsonObject.get("elements");
        for (Object object : elements) {
            String string = object.toString();
            if (!string.startsWith("{\"nodes")) {
                string = string.replace("{", "");
                string = string.replace("}", "");
                String[] nodelist = string.split(",");
                double lon = Double.parseDouble(nodelist[0].substring(6));
                String id = nodelist[1].substring(5);
                double lat = Double.parseDouble(nodelist[3].substring(6));
                nodeSet.add(new Node(id, lon, lat));
            }
        }
    }

    public void getClosestNode() {
        while (distanceTraveled < totalDistance) {
            for (Node node : nodeSet) {
                node.getDistanceTo(start);
            }
            List<Node> sortedList = new ArrayList<>(nodeSet);
            Collections.sort(sortedList);
            Node closestNode = sortedList.get(0);
            distanceTraveled += closestNode.getDistanceToCurrentNode();
            start = closestNode;
            nodeSet.remove(closestNode);
            path.add(closestNode);

        }
        StringBuilder output = new StringBuilder();
        output.append("[out:json];node(id:");
        for (Node node: path){
            output.append(node.getId());
            output.append(",");
        }
        output.append(");out skel;");
        System.out.println(output);
    }

    public static void main(String[] args) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        Node start = new Node("start", 5.061405, 52.650243);
        Object obj = parser.parse(new FileReader("SampleJson.json"));
        JSONObject jsonObject = (JSONObject) obj;
        NodeFetcher nodeFetcher = new NodeFetcher(start, 1000);
        //nodeFetcher.getOverpassData();
        nodeFetcher.parseJson(jsonObject);
        nodeFetcher.getClosestNode();

    }
}
