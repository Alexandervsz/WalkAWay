import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
        List<OsmNode> nodeList = new ArrayList<>();
        for (Object object: elements) {
            if (!object.toString().startsWith("{\"nodes")){
                String string = object.toString();
                System.out.println(string);
                double lon = Float.parseFloat(string.substring(string.indexOf("\"lon\""),string.indexOf(",\"id")).substring(6));
                String id = string.substring(string.indexOf(",\"id"),string.indexOf(",\"type\"")).substring(6);
                double lat;
                if(!string.contains(",\"tags")){
                    lat = Double.parseDouble(string.substring(string.indexOf(",\"lat\":"), string.length()-1).substring(7));
                }
                else{
                    lat = Double.parseDouble(string.substring(string.indexOf(",\"lat\":"), string.indexOf(",\"tags")).substring(7));
                }
                nodeList.add(new OsmNode(id, lon, lat));

            }

        }
    }

    public static void main(String[] args) throws IOException, InterruptedException, ParseException {
        NodeFetcher nodeFetcher = new NodeFetcher();
        nodeFetcher.getOverpassData();

    }
}
