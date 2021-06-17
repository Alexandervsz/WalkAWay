import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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

public class FileManager {
    Set<Node> nodeSet;
    Set<Way> waySet;

    /**
     * Fetches all nodes and ways in a bbox, which is based on the begin node and total distance required to walk.
     *
     * @param currentNode   The node for which the ways must be fetched.
     * @param totalDistance The total required distance of the path.
     * @param dialog        A LoadingDialog to show the progress.
     * @throws IOException          If the html request is invalid somehow.
     * @throws InterruptedException If the html request is interrupted.
     * @throws ParseException       If the JSON is incorrect.
     */
    public void getOverpassData(Node currentNode, double totalDistance, LoadingDialog dialog) throws Exception {
        String bbox = generateBbox(currentNode, totalDistance);
        DatabaseManager databaseManager = new DatabaseManager();
        List<WayType> wayTypes = databaseManager.getWayTypes();
        List<String> options = new ArrayList<>();
        options.add("way");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[out:json];(");
        for (WayType wayType : wayTypes) {
            for (String option : options) {
                stringBuilder.append(option);
                stringBuilder.append("[");
                stringBuilder.append(wayType.getMainType());
                if (!wayType.getSubType().equals("-1")) {
                    stringBuilder.append("=");
                    stringBuilder.append(wayType.getSubType());
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
        JSONObject obj = (JSONObject) parser.parse(response.body());
        dialog.setProgress(30);
        dialog.setText(" Processing Overpass data...");
        parseJson(obj);
    }

    /**
     * Generates a bbox, which is required in an overpass request. It generates a bbox of totaldistance * totaldistance
     * so even in the worst case scenario (straight way from A to B) enough data is available.
     *
     * @param currentNode   The center node to draw the bbox around.
     * @param totalDistance The total required distance of the path.
     * @return A string to be used in further processing.
     */
    public String generateBbox(Node currentNode, double totalDistance) {
        double lat = currentNode.getLat();
        double lon = currentNode.getLon();
        double R = 6378137;
        double dLat = totalDistance / R;
        double dLon = totalDistance / (R * Math.cos(Math.PI * lat / 180));
        double latO = lat - dLat * 180 / Math.PI;
        double lonO = lon - dLon * 180 / Math.PI;
        double lat1 = lat + dLat * 180 / Math.PI;
        double lon1 = lon + dLon * 180 / Math.PI;
        return latO + "," + lonO + "," + lat1 + "," + lon1;
    }

    /**
     * Adds all nodes to the node set, then attaches the nodes to the corresponding ways, and finally saves the ways.
     *
     * @param jsonObject The JSON fetched from overpass.
     * @throws ParseException If incorrect JSON is passed.
     */
    public void parseJson(JSONObject jsonObject) throws Exception {
        nodeSet = new HashSet<>();
        waySet = new HashSet<>();
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
                    Way newWay = new Way(wayId, typesList);
                    int count = 0;
                    for (String nodeId : nodes) {
                        Node targetNode = null;
                        for (Node node : nodeSet) {
                            if (node.getId().equals(nodeId)) {
                                targetNode = node;
                            }
                        }
                        if (targetNode == null){
                            throw new Exception("Node not found");
                        }
                        targetNode.setWay(newWay);
                        newWay.addNode(count, targetNode);
                        count++;
                    }
                    waySet.add(newWay);
                }
            }
        }
    }

    /**
     * Generates a gpx files based on the parameters.
     *
     * @param file   The file object to write to.
     * @param name   The name of the path in the gpx file.
     * @param points The path to base the gpx file on, ordered from start to finish.
     */
    public void generateGpx(File file, String name, List<Node> points) {
        String header = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?><gpx xmlns=\"https://www.topografix.com/GPX/1/1\" creator=\"MapSource 6.15.5\" version=\"1.1\" xmlns:xsi=\"https://www.w3.org/2001/XMLSchema-instance\"  xsi:schemaLocation=\"https://www.topografix.com/GPX/1/1 https://www.topografix.com/GPX/1/1/gpx.xsd\"><trk>\n";
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

    public Set<Node> getNodeSet() {
        return nodeSet;
    }

    public Set<Way> getWaySet() {
        return waySet;
    }
}
