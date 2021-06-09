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
    private final Set<OsmNode> nodeSet;

    public NodeFetcher() {
        this.nodeSet = new HashSet<>();
    }

    public JSONObject getOverpassData() throws IOException, InterruptedException, ParseException {
        String bbox = "52.646617767509,5.0588822364807,52.652287173554,5.0702011585236";
        DatabaseManager databaseManager = new DatabaseManager();
        List<NodeType> nodeTypes = databaseManager.getNodeTypes();
        List<String> options = new ArrayList<>();
        options.add("node");
        options.add("way");
        options.add("relation");
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
                .uri(URI.create("https://overpass-api.de/api/interpreter?data=" + query))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(response.body());
        return (JSONObject) obj;

    }

    public void parseJson(JSONObject jsonObject) throws IOException, InterruptedException, ParseException {
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
                nodeSet.add(new OsmNode(id, lon, lat));
            } else {
                parseWays(string);
            }
        }
        OsmNode osmNode = nodeSet.iterator().next();
        System.out.println(osmNode.toString());
    }

    private void parseWays(String string) throws IOException, InterruptedException, ParseException {
        string = string.replace("{", "");
        string = string.replace("}", "");
        String[] nodelist = string.split(":");
        String[] nodelist2 = nodelist[1].split(",");
        for (int x = 0; x <= nodelist2.length - 2; x++) {
            String nodeId = nodelist2[x];
            if (x == 0) {
                nodeId = nodeId.substring(1);
            }
            if (x == nodelist2.length - 2) {
                nodeId = nodeId.substring(0, nodeId.length() - 1);
            }
            String query = "[out:json];node(id:" + nodeId + ");out;";
            query = URLEncoder.encode(query, StandardCharsets.UTF_8);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://overpass-api.de/api/interpreter?data=" + query))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONParser parser = new JSONParser();
            System.out.println(response.body());
            Object obj = parser.parse(response.body());
            JSONObject nodeObject = (JSONObject) obj;
            JSONArray nodeElements = (JSONArray) nodeObject.get("elements");
            for (Object node : nodeElements) {
                String nodeVariables = node.toString();
                nodeVariables = nodeVariables.replace("{", "");
                nodeVariables = nodeVariables.replace("}", "");
                String[] nodeElementlist = nodeVariables.split(",");
                System.out.println(Arrays.toString(nodeElementlist));
                for (String f : nodeElementlist) {
                    System.out.println(f);
                }
                int subInt = 6;
                if (nodeElementlist[0].substring(6).startsWith("\":[")) {
                    subInt += 3;
                }
                double lat = Double.parseDouble(nodeElementlist[0].substring(subInt));
                double lon = Double.parseDouble(nodeElementlist[3].substring(6));
                nodeSet.add(new OsmNode(nodeId, lon, lat));


            }
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException, ParseException {
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(new FileReader("SampleJson.json"));
        JSONObject jsonObject = (JSONObject) obj;
        NodeFetcher nodeFetcher = new NodeFetcher();
        nodeFetcher.parseJson(jsonObject);

    }
}
