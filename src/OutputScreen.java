import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * The output screen which shows the data of the path when the algorithm is done.
 */
public class OutputScreen extends JFrame {
    private JLabel distanceLabel;
    private JLabel caloriesLabel;
    private JButton closeButton;
    private JPanel outputPanel;
    private JLabel timeLabel;

    /**
     * Initialises the screen according to the parameters.
     *
     * @param distance The total distance traveled in meters.
     * @param calories The calories burnt after traveling the distance.
     * @param time     The estimated time it takes to walk the path.
     */
    public OutputScreen(String distance, String calories, String time) {
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        distanceLabel.setText("Total distance of path: " + distance + "m.");
        caloriesLabel.setText("Estimated amount of calories burnt when walking this route: " + calories);
        timeLabel.setText("Estimated duration of activity: " + time + " minutes.");
        closeButton.setText("Close");
        closeButton.addActionListener(this::stop);
        setLayout(new GridLayout());
        add(outputPanel);
        validate();
        pack();
        setVisible(true);

    }

    /**
     * Called when the user clicks on the close button.
     * @param actionEvent The user clicked the button.
     */
    private void stop(ActionEvent actionEvent) {
        dispose();
        setVisible(false);
    }


}
