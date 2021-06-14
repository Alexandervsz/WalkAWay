import java.io.File;
import java.io.IOException;
import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class NodeFetcher {
    private final Set<Node> nodeSet;
    private double distanceTraveled;
    private Node currentNode;
    private final double totalDistance;
    private final List<Node> path;

    public NodeFetcher(Node start, double totalDistance) {
        this.nodeSet = new HashSet<>();
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
                    Way newWay = new Way(wayId, typesList);
                    int count = 0;
                    for (String nodeId : nodes) {
                        Node targetNode = null;
                        for (Node node: nodeSet){
                            if (node.getId().equals(nodeId)){
                                targetNode = node;
                            }
                        }
                        assert targetNode != null;
                        targetNode.setWay(newWay);
                        targetNode.setPathnumber(count);
                        newWay.addNode(targetNode);
                        count++;
                    }
                }
            }
        }
    }

    public void getRoute() {
        path.add(currentNode);
        while (distanceTraveled < totalDistance) {
            Set<Node> removedNodes = new HashSet<>(nodeSet);
            //System.out.println("Starting from node: "+ currentNode.getId());
            for (Node node : nodeSet) {
                node.getDistanceTo(currentNode);
                node.getBearingTo(currentNode);
                if (node.getDistanceToCurrentNode() > 40){
                    removedNodes.remove(node);
                }
            }
            List<Node> sortedList = new ArrayList<>(removedNodes);
            Collections.sort(sortedList);
            Node closestNode = sortedList.get(0);
            //System.out.println("closest node is: "+ closestNode.getId());
            Way currentWay = closestNode.getWay();
            //System.out.println("Current way is: "+ currentWay.getid());
            nodeSet.remove(closestNode);
            closestNode.getDistanceTo(currentNode);
            distanceTraveled += closestNode.getDistanceToCurrentNode();
            path.add(closestNode);
            currentWay.calculatePositionsToNode(closestNode);
            Set<Node> nodesInWay = currentWay.getNodePositions();
            List<Node> nodeList = new ArrayList<>(nodesInWay);
            Collections.sort(nodeList);
            //System.out.println("walking path: "+ nodeList);
            for (Node node: nodeList){
                if (distanceTraveled > totalDistance){
                    break;
                }
                node.getBearingTo(closestNode);
                if (node.getBearingToCurrentNode() < 1){
                    nodeSet.remove(node);
                    node.getDistanceTo(currentNode);
                    distanceTraveled += node.getDistanceToCurrentNode();
                    path.add(node);
                    closestNode = node;
                    currentNode = node;
                }else{
                    nodeSet.remove(node);
                }
            }
        }
        System.out.println(distanceTraveled);
        File file = new File("walking_route.gpx");
        FileManager fileManager = new FileManager();
        fileManager.generateGpx(file, "walking_route", path);
    }

    public static void main(String[] args) throws IOException, ParseException, InterruptedException {
        Node start = new Node("start", 5.056657, 52.651975);
        double totalDistance = 1000;
        FileManager fileManager = new FileManager();
        NodeFetcher nodeFetcher = new NodeFetcher(start, totalDistance);
        JSONObject jsonObject = fileManager.getOverpassData(start, totalDistance);
        nodeFetcher.parseJson(jsonObject);
        nodeFetcher.getRoute();
    }
}
