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
    private final Set<Way> waySet;
    private double distanceTraveled;
    private Node currentNode;
    private double totalDistance;
    private List<Node> path;
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
        //String bbox = "52.646617767509,5.0588822364807,52.652287173554,5.0702011585236";
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
                    for (String nodeId : nodes) {
                        Node node = nodesList.get(nodesList.indexOf(new Node(nodeId)));
                        nodeList.add(node);
                    }
                    waySet.add(new Way(wayId, nodeList, typesList));


                }
            }

        }
    }

    public String generateBbox(){
        //Position, decimal degrees
        double lat = currentNode.getLat();
        double lon = currentNode.getLon();

        //Earth’s radius, sphere
        double R=6378137;

        //offsets in meters
        double dn = totalDistance;
        double de = totalDistance;

        //Coordinate offsets in radians
        double dLat = dn/R;
        double dLon = de/(R*Math.cos(Math.PI*lat/180));

        //OffsetPosition, decimal degrees
        double latO = lat - dLat * 180/Math.PI;
        double lonO = lon - dLon * 180/Math.PI;
        double lat1 = lat + dLat * 180/Math.PI;
        double lon1 = lon + dLon * 180/Math.PI;
        return latO+","+lonO+","+lat1+","+lon1;

    }

    public void calculateDistances(){
        for (Node node : nodeSet) {
            node.getDistanceTo(currentNode);
        }
    }

    public void getClosestNode() {
        while (distanceTraveled < totalDistance) {
            calculateDistances();
            List<Node> sortedList = new ArrayList<>(nodeSet);
            Collections.sort(sortedList);
            Node closestNode = sortedList.get(0);
            nodeSet.remove(closestNode);
            closestNode.getDistanceTo(currentNode);
            distanceTraveled += closestNode.getDistanceToCurrentNode();
            path.add(closestNode);

            getClosestWay(closestNode);

            int startIndex = currentWay.getNodes().indexOf(closestNode);
            System.out.println(currentWay.getNodes());
            Node subNode = closestNode;
            System.out.println(subNode);
            for (int i = startIndex + 1; i < currentWay.getNodes().size(); i++){
                subNode.getDistanceTo(currentWay.getNodes().get(i));
                distanceTraveled += subNode.getDistanceToCurrentNode();
                //path.add(subNode);
                subNode = currentWay.getNodes().get(i);
                if (distanceTraveled >= totalDistance){
                    break;
                }

            }
            currentNode = subNode;




        }
        StringBuilder output = new StringBuilder();
        output.append("[out:json];node(id:");
        for (Node node : path) {
            output.append(node.getId());
            if (path.indexOf(node) != path.size()-1){
            output.append(",");}
        }
        output.append(");out skel;");
        System.out.println(output);
        System.out.println(distanceTraveled);
    }

    public void getClosestWay(Node target){
        for (Way way: waySet) {
            List<Node> nodes = way.getNodes();
            if (nodes.contains(target)) {
                currentWay = way;
                waySet.remove(way);
                return;
            }
        }
    }

    public static void main(String[] args) throws IOException, ParseException, InterruptedException {
        JSONParser parser = new JSONParser();
        Node start = new Node("start", 5.058249, 52.641260);
        //Object obj = parser.parse(new FileReader("SampleJson.json"));
        //JSONObject jsonObject = (JSONObject) obj;
        NodeFetcher nodeFetcher = new NodeFetcher(start, 4000);
        JSONObject jsonObject = nodeFetcher.getOverpassData();
        nodeFetcher.parseJson(jsonObject);
        nodeFetcher.getClosestNode();

    }
}
