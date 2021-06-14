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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FileManager {
    public JSONObject getOverpassData(Node currentNode, double totalDistance) throws IOException, InterruptedException, ParseException {
        String bbox = generateBbox(currentNode, totalDistance);
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

    public String generateBbox(Node currentNode, double totalDistance) {
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

}
