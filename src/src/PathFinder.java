import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.json.simple.parser.ParseException;

import javax.swing.*;

public class PathFinder {
    private Set<Way> waySet;
    private final User user;
    private Way previousWay;
    private final Node beginNode;
    private Node currentNode;
    private final double requiredDistance;
    private double totalDistance;
    private final List<Node> path;

    public PathFinder(User user) {
        this.user = user;
        this.path = new ArrayList<>();
        this.totalDistance = 0;
        this.beginNode = new Node("start", user.getLon(), user.getLat());
        Way beginWay = new Way("start");
        this.currentNode = beginNode;
        this.previousWay = beginWay;
        path.add(beginNode);
        this.requiredDistance = user.getDistance();
    }

    /**
     * This starts the pathfinding algorithm. Also updates the progress bar, therefore a second thread is required.
     */
    public void start() {
        FileManager fileManager = new FileManager();
        final LoadingDialog dialog = new LoadingDialog(" Loading overpass data...");
        SwingWorker<Void, Void> worker = new SwingWorker<>() {

            @Override
            protected Void doInBackground() throws IOException, ParseException, InterruptedException {
                fileManager.getOverpassData(beginNode, requiredDistance, dialog, user.isRandom());
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

    /**
     * This program files a route by first getting the closest node and then finding out which way has the closest node.
     * Then, it "walks" to the next node (adding the distance it walked to the total) and follows the path from
     * the node it entered. Once the path has been traversed, it checks whether the required distance has been met,
     * and starts over again if needed. Also, when it meets a dead end, it follows the routes back to either start
     * (no path found) or until a viable path is found.
     */
    public void getRoute() {
        while (totalDistance < requiredDistance) {
            Way currentWay = getClosestWay(currentNode);
            currentWay.setPreviousWay(previousWay);
            while (currentWay.equals(new Way("empty"))) {
                if (currentWay.getDirection() == 1) {
                    NavigableMap<Integer, Node> waynodes = currentWay.getNodePositionsReversed();
                    revertPath(waynodes, currentWay);
                } else if (currentWay.getDirection() == -1) {
                    TreeMap<Integer, Node> waynodes = currentWay.getNodePositions();
                    revertPath(waynodes, currentWay);
                }
                currentWay = currentWay.getPreviousWay();
                if (currentWay.equals(new Way("start"))) {
                    new PathNotFound();
                    System.exit(0);
                }
                currentWay = getClosestWay(currentWay.getLastNode());
            }
            waySet.remove(currentWay);
            Node closestNode = currentWay.getClosestNode(currentNode);
            closestNode.getDistanceTo(currentNode);
            totalDistance += closestNode.getDistanceToCurrentNode();
            path.add(closestNode);
            processWay(closestNode, currentWay);
        }
    }

    /**
     * This function removes the distance traveled along a path up until the point it was entered.
     *
     * @param map        The nodes of the way.
     * @param currentWay The current way being processed.
     */
    public void revertPath(Map<Integer, Node> map, Way currentWay) {
        for (Map.Entry<Integer, Node> entry : map.entrySet()) {
            if (entry.getKey() == currentWay.getEntryPoint()) {
                break;
            }
            entry.getValue().getDistanceTo(currentNode);
            path.remove(entry.getValue());
            totalDistance = totalDistance - entry.getValue().getDistanceToCurrentNode();
            currentNode = entry.getValue();
        }
    }

    /**
     * Returns the closest way of all the ways in the current set.
     *
     * @param target The target node of which the distance is required.
     * @return The way with the closest node in it.
     */
    public Way getClosestWay(Node target) {
        double closest = Double.MAX_VALUE;
        Way closestWay = null;
        for (Way way : waySet) {
            Node closestWayNode = way.getClosestNode(target);
            if (closestWayNode.getDistanceToCurrentNode() < closest) {
                closest = closestWayNode.getDistanceToCurrentNode();
                closestWay = way;
            }
        }
        if (closest > 61) {
            System.out.println("empty");
            return new Way("empty");
        } else {
            return closestWay;
        }
    }

    /**
     * Cuts the treemap down to the right position and length, based on the direction the algorithm is going,
     * and where the algorithm entered the path.
     *
     * @param entryNode  The node where the algorithm entered the path.
     * @param currentWay The current way being processed.
     */
    public void processWay(Node entryNode, Way currentWay) {
        TreeMap<Integer, Node> nodeMap = currentWay.getNodePositions();
        int midpoint = nodeMap.firstKey() + nodeMap.lastKey() / 2;
        int entry = currentWay.getPositionOfNode(entryNode);
        currentWay.setEntryPoint(entry);
        if (entry > midpoint) {
            currentWay.setDirection(-1);
        } else {
            currentWay.setDirection(1);
        }
        if (currentWay.getDirection() == 1) {
            previousWay = currentWay;
            walkPath(currentWay.getNodePositionsFromPoint(entry), entryNode);
        } else {
            previousWay = currentWay;
            walkPath(currentWay.getNodePositionsFromPointReversed(entry), entryNode);
        }

    }

    /**
     * Processes the required nodes. Adds their distance to the total, and the nodes themselves to the path.
     *
     * @param wayPath   The processed treemap.
     * @param entryNode The node where the algorithm entered the path.
     */
    public void walkPath(SortedMap<Integer, Node> wayPath, Node entryNode) {
        Node currentPathNode = entryNode;
        wayPath.remove(wayPath.firstKey());
        for (Map.Entry<Integer, Node> entry : wayPath.entrySet()) {
            if (totalDistance >= requiredDistance) {
                return;
            }
            totalDistance += entry.getValue().getDistanceTo(currentPathNode);
            path.add(entry.getValue());
            currentPathNode = entry.getValue();
        }
        currentNode = currentPathNode;

    }

    /**
     * Generates a GPX file, shows a screen with information about the generated path, and the generated path itself.
     *
     * @throws IOException When the html template cannot be interpreted correctly.
     */
    public void showOutput() throws IOException {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        DecimalFormat timeFormat = new DecimalFormat("#.#");
        String distance = String.valueOf(decimalFormat.format(totalDistance));
        String calories = String.valueOf((int) user.getEstimatedKcal(totalDistance));
        String time = String.valueOf(timeFormat.format(user.getTime()));
        new OutputScreen(distance, calories, time);
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
        User user = new User(6.0, 70.0, 6.4, 200, 5.061843, 52.650095, false);
        PathFinder pathFinder = new PathFinder(user);
        pathFinder.start();
    }
}
