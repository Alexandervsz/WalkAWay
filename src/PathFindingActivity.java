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
    User user;
    Node beginNode;

    /**
     * Creates a new pathfinding activity.
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
                dialog.setProgress(60);
                dialog.setText(" Generating path...");
                PathFinder pathFinder = new PathFinder(beginNode, waySet, user.getDistance());
                List<Node> path = pathFinder.getRoute();
                dialog.setProgress(100);
                showOutput(path, pathFinder.getTotalDistance());
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
     * Generates a GPX file, shows a screen with information about the generated path, and the generated path itself.
     *
     * @throws IOException When the html template cannot be interpreted correctly.
     */
    private void showOutput(List<Node> path, double totalDistance) throws IOException {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        DecimalFormat timeFormat = new DecimalFormat("#.#");
        String distance = String.valueOf(decimalFormat.format(totalDistance));
        String calories = String.valueOf((int) user.getEstimatedKcal(totalDistance));
        String time = String.valueOf(timeFormat.format(user.getTime(totalDistance)));
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
        User user = new User(6.0, 70.0, 6.4, 20, 5.071998, 52.639074, false);
        new PathFindingActivity(user).start();
    }
}
