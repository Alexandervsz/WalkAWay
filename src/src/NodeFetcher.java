import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;

public class NodeFetcher {
    private final Set<Node> nodeSet;
    private final User user;
    private double distanceTraveled;
    private Node currentNode;
    private final double totalDistance;
    private final List<Node> path;

    public NodeFetcher(User user) {
        this.user = user;
        this.nodeSet = new HashSet<>();
        this.path = new ArrayList<>();
        this.distanceTraveled = 0;
        this.currentNode = new Node("start", user.getLon(), user.getLat());
        this.totalDistance = user.getDistance();
    }

    public void start(){
        FileManager fileManager = new FileManager();
        final LoadingDialog dialog = new LoadingDialog(" Loading overpass data...");
        SwingWorker<Void, Void> worker = new SwingWorker<>() {

            @Override
            protected Void doInBackground() throws ParseException, IOException, InterruptedException {
                JSONObject jsonObject = fileManager.getOverpassData(currentNode, totalDistance);
                dialog.setProgress(30);
                dialog.setText(" Processing Overpass data...");
                parseJson(jsonObject);
                dialog.setProgress(60);
                dialog.setText(" Generating path...");
                getRoute();
                dialog.setProgress(100);
                showOutput();
                return null;
            }

            @Override
            protected void done() {
                dialog.dispose();
            }
        };
        worker.execute();
        dialog.setVisible(true);
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
                        for (Node node : nodeSet) {
                            if (node.getId().equals(nodeId)) {
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
            for (Node node : nodeSet) {
                node.getDistanceTo(currentNode);
                node.getBearingTo(currentNode);
            }
            List<Node> sortedList = new ArrayList<>(nodeSet);
            Collections.sort(sortedList);
            Node closestNode = sortedList.get(0);
            Way currentWay = closestNode.getWay();
            nodeSet.remove(closestNode);
            closestNode.getDistanceTo(currentNode);
            distanceTraveled += closestNode.getDistanceToCurrentNode();
            path.add(closestNode);
            currentWay.calculatePositionsToNode(closestNode);
            Set<Node> nodesInWay = currentWay.getNodePositions();
            List<Node> nodeList = new ArrayList<>(nodesInWay);
            Collections.sort(nodeList);
            for (Node node : nodeList) {
                if (distanceTraveled > totalDistance) {
                    break;
                }
                node.getBearingTo(closestNode);
                if (node.getBearingToCurrentNode() < 1) {
                    nodeSet.remove(node);
                    node.getDistanceTo(currentNode);
                    distanceTraveled += node.getDistanceToCurrentNode();
                    path.add(node);
                    closestNode = node;
                    currentNode = node;
                } else {
                    nodeSet.remove(node);
                }
            }
        }
    }

    public void showOutput() throws IOException {
        new OutputScreen(String.valueOf(distanceTraveled), String.valueOf(user.getEstimatedKcal(distanceTraveled)));
        File file = new File("path/walking_route.gpx");
        FileManager fileManager = new FileManager();
        fileManager.generateGpx(file, "path/walking_route", path);
        File htmlTemplateFile = new File("template.html");
        String htmlString = FileUtils.readFileToString(htmlTemplateFile, "ISO-8859-1");
        StringBuilder nodes = new StringBuilder();
        for (int x = 0; x < path.size(); x++) {
            nodes.append("[");
            nodes.append(path.get(x).getLat());
            nodes.append(", ");
            nodes.append(path.get(x).getLon());
            nodes.append("]");
            if (x < path.size() - 1) {
                nodes.append(", ");
            }
        }
        htmlString = htmlString.replace("$insertnode", nodes.toString());
        File newHtmlFile = new File("path/new.html");
        FileUtils.writeStringToFile(newHtmlFile, htmlString, "ISO-8859-1");
        Desktop.getDesktop().browse(newHtmlFile.toURI());
    }

    public static void main(String[] args){
        User user = new User(6.0, 70.0, 6.4, 10, 5.072073, 52.645407);
        NodeFetcher nodeFetcher = new NodeFetcher(user);
        nodeFetcher.start();
    }
}
