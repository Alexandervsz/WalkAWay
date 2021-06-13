import java.io.File;
import java.io.IOException;
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
                    List<Node> nodesList = new ArrayList<>(nodeSet);
                    Way newWay = new Way(wayId, typesList);
                    for (String nodeId : nodes) {
                        Node node = nodesList.get(nodesList.indexOf(new Node(nodeId)));
                        node.setWay(newWay);
                    }
                    waySet.add(newWay);


                }
            }

        }
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
        FileManager fileManager = new FileManager();
        fileManager.generateGpx(file, "test", path);
    }


    public List<Node> getAllWayNodes(Way way) {
        List<Node> nodeList = new ArrayList<>();
        for (Node node : nodeSet) {
            if (node.getWay().equals(way)) {
                nodeList.add(node);
            }
        }
        return nodeList;
    }


    public static void main(String[] args) throws IOException, ParseException, InterruptedException {
        Node start = new Node("start", 5.069284, 52.636537);
        double totalDistance = 250;
        FileManager fileManager = new FileManager();
        NodeFetcher nodeFetcher = new NodeFetcher(start, totalDistance);
        JSONObject jsonObject = fileManager.getOverpassData(start, totalDistance);
        nodeFetcher.parseJson(jsonObject);
        nodeFetcher.getClosestNode();

    }
}
