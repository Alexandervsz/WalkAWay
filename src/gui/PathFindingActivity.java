package gui;

import algorithm.Node;
import algorithm.PathFinder;
import algorithm.Way;
import data.FileManager;
import data.User;
import org.apache.commons.io.FileUtils;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Set;

/**
 * This class fetches the data for the pathfinding algorithm and passes it to the pathfinding algorithm, and shows
 * the progress. When it's done it shows some data and the generated path.
 */
public class PathFindingActivity {
    final User user;
    final Node beginNode;

    /**
     * Creates a new pathfinding activity.
     *
     * @param user A user to generate the path for.
     */
    public PathFindingActivity(User user) {
        this.user = user;
        this.beginNode = new Node("Start", user.getLon(), user.getLat());

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
                fileManager.getOverpassData(beginNode, user.getDistance(), dialog, user.isRandom());
                Set<Way> waySet = fileManager.getWaySet();
                if (waySet.size() == 0){
                    new PathNotFound();
                }
                dialog.setProgress(60);
                dialog.setText(" Generating path...");
                PathFinder pathFinder = new PathFinder(beginNode, waySet, user.getDistance());
                List<Node> path = pathFinder.getRoute();
                if (path.isEmpty()) {
                    new PathNotFound();
                } else {
                    dialog.setProgress(100);
                    showOutput(path, pathFinder.getTotalDistance());
                }
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
     * Generates a GPX file, shows a screen with information about the generated path and creates a new HTML file
     * based on the template. Then displays this HTML file which in turn displays the generated route.
     *
     * @throws IOException When there's an error with the html file.
     */
    private void showOutput(List<Node> path, double totalDistance) throws IOException {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        DecimalFormat timeFormat = new DecimalFormat("#.#");
        String distance = String.valueOf(decimalFormat.format(totalDistance));
        String calories = String.valueOf((int) user.getEstimatedKcal(totalDistance));
        String time = String.valueOf(timeFormat.format(user.getTime(totalDistance)));
        File file = new File("res/walking_route.gpx");
        FileManager fileManager = new FileManager();
        fileManager.generateGpx(file, "res/walking_route", path);

        File htmlTemplateFile = new File("res/template.html");
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
        File newHtmlFile = new File("res/new.html");
        FileUtils.writeStringToFile(newHtmlFile, htmlString, "ISO-8859-1");

        new OutputScreen(distance, calories, time);
        Desktop.getDesktop().browse(newHtmlFile.toURI());
    }
}
