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

    public void getOverpassData() throws IOException, InterruptedException, ParseException {
        /*String bbox = "52.646617767509,5.0588822364807,52.652287173554,5.0702011585236";
        DatabaseManager databaseManager = new DatabaseManager();
        List<NodeType> nodeTypes = databaseManager.getNodeTypes();
        List<String> options = new ArrayList<>();
        options.add("node");
        options.add("way");
        options.add("relation");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[out:json];(");
        for (NodeType nodeType: nodeTypes){
            for (String option: options){
                stringBuilder.append(option);
                stringBuilder.append("[");
                stringBuilder.append(nodeType.getMainType());
                if (!nodeType.getSubType().equals("-1")){
                    stringBuilder.append("=");
                    stringBuilder.append(nodeType.getSubType());
                }
                stringBuilder.append("](");
                stringBuilder.append(bbox);
                stringBuilder.append(");");
            }}
        stringBuilder.append(");(._;>;);out;");
        String query = URLEncoder.encode(stringBuilder.toString(), StandardCharsets.UTF_8);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://overpass-api.de/api/interpreter?data="+query))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
        JSONObject jsonObject = new JSONObject(response.body());*/
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(new FileReader("SampleJson.json"));
        JSONObject jsonObject = (JSONObject) obj;
        JSONArray elements = (JSONArray) jsonObject.get("elements");
        Set<OsmNode> nodeSet = new HashSet<>();
        for (Object object: elements) {
            String string = object.toString();
            if (!string.startsWith("{\"nodes")){
                string = string.replace("{", "");
                string = string.replace("}", "");
                String[] nodelist = string.split(",");
                double lon =  Double.parseDouble(nodelist[0].substring(6));
                String id = nodelist[1].substring(5);
                double lat = Double.parseDouble(nodelist[3].substring(6));
                nodeSet.add(new OsmNode(id, lon, lat));
            }
            else {
                //System.out.println(string);
                String query = "[out:json];node(id:7802055775);out;";
            }

        }
        OsmNode osmNode = nodeSet.iterator().next();
        System.out.println(osmNode.toString());
    }

    public static void main(String[] args) throws IOException, InterruptedException, ParseException {
        NodeFetcher nodeFetcher = new NodeFetcher();
        nodeFetcher.getOverpassData();

    }
}
