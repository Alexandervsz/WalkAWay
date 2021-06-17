import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.json.simple.parser.ParseException;

import javax.swing.*;

public class PathFinder {
    private Set<Node> nodeSet;
    private Set<Way> waySet;
    private final User user;
    private double distanceTraveled;
    private Node beginNode;
    private final double totalDistance;
    private final List<Node> path;

    public PathFinder(User user) {
        this.user = user;
        this.nodeSet = new HashSet<>();
        this.path = new ArrayList<>();
        this.distanceTraveled = 0;
        this.beginNode = new Node("start", user.getLon(), user.getLat());
        this.totalDistance = user.getDistance();
    }

    public void start() {
        FileManager fileManager = new FileManager();
        final LoadingDialog dialog = new LoadingDialog(" Loading overpass data...");
        SwingWorker<Void, Void> worker = new SwingWorker<>() {

            @Override
            protected Void doInBackground() throws ParseException, IOException, InterruptedException {
                fileManager.getOverpassData(beginNode, totalDistance, dialog);
                nodeSet = fileManager.getNodeSet();
                waySet = fileManager.getWaySet();
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

    public void getRoute() {
        path.add(beginNode);
        while (distanceTraveled < totalDistance) {
            for (Node node : nodeSet) {
                node.getDistanceTo(beginNode);
                node.getBearingTo(beginNode);
            }
            List<Node> sortedList = new ArrayList<>(nodeSet);
            Collections.sort(sortedList);
            Node currentNode = sortedList.get(0);
            Way currentWay = currentNode.getWay();
            nodeSet.remove(currentNode);
            currentNode.getDistanceTo(beginNode);
            distanceTraveled += currentNode.getDistanceToCurrentNode();
            path.add(currentNode);
            currentWay.calculatePositionsToNode(currentNode);
            Set<Node> nodesInWay = currentWay.getNodePositions();
            List<Node> nodeList = new ArrayList<>(nodesInWay);
            Collections.sort(nodeList);
            for (Node node : nodeList) {
                if (distanceTraveled > totalDistance) {
                    break;
                }
                node.getBearingTo(currentNode);
                if (node.getBearingToCurrentNode() < 1) {
                    nodeSet.remove(node);
                    node.getDistanceTo(beginNode);
                    distanceTraveled += node.getDistanceToCurrentNode();
                    path.add(node);
                    currentNode = node;
                    beginNode = node;
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

    public static void main(String[] args) {
        User user = new User(6.0, 70.0, 6.4, 10, 5.072073, 52.645407);
        PathFinder pathFinder = new PathFinder(user);
        pathFinder.start();
    }
}
