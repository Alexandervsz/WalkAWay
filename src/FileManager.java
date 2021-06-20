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
    public void getOverpassData(Node currentNode, double totalDistance, LoadingDialog dialog, boolean isRandom) throws IOException, InterruptedException, ParseException {
        String bbox = generateBbox(currentNode, totalDistance, isRandom);
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
     * The bbox is way bigger than needed, this is because it's an approximation upon an approximation,
     * so keep distances < 100km.
     *
     * @param currentNode   The center node to draw the bbox around.
     * @param totalDistance The total required distance of the path.
     * @return A string to be used in further processing.
     */
    public String generateBbox(Node currentNode, double totalDistance, boolean isRandom) {
        if (isRandom) {
            double generatedDouble = 0.5 + new Random().nextDouble() * (1 - 0.5);
            totalDistance = totalDistance * generatedDouble;
        } else {
            totalDistance = totalDistance * 0.705; // To correct for the fact that bbox generates too big.
        }
        double lat = currentNode.getLat();
        double lon = currentNode.getLon();
        double R = 6_356_752.314245D;
        double dLat = totalDistance / R;
        double dLon = totalDistance / (R * Math.cos(Math.PI * lat / 180));
        double lat0 = lat - dLat * 180 / Math.PI;
        double lon0 = lon - dLon * 180 / Math.PI;
        double lat1 = lat + dLat * 180 / Math.PI;
        double lon1 = lon + dLon * 180 / Math.PI;

        return lat0 + "," + lon0 + "," + lat1 + "," + lon1;
    }

    /**
     * Adds all nodes to the node set, then attaches the nodes to the corresponding ways, and finally saves the ways.
     *
     * @param jsonObject The JSON fetched from overpass.
     * @throws ParseException If incorrect JSON is passed.
     */
    public void parseJson(JSONObject jsonObject) throws ParseException, IOException {
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
                    String nodesString = osmObject.get("nodes").toString();
                    String[] nodes = nodesString.split(",");
                    for (int x = 0; x < nodes.length; x++) {
                        nodes[x] = nodes[x].replace("[", "");
                        nodes[x] = nodes[x].replace("]", "");
                    }
                    Way newWay = new Way(wayId);
                    int count = 0;
                    for (String nodeId : nodes) {
                        Node targetNode = null;
                        for (Node node : nodeSet) {
                            if (node.getId().equals(nodeId)) {
                                targetNode = node;
                            }
                        }
                        if (targetNode == null) {
                            throw new IOException("Node not found! (this means there's something wrong with the json file.)");
                        }
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

    public Set<Node> getNodeSet() {
        return nodeSet;
    }

    public Set<Way> getWaySet() {
        return waySet;
    }


}