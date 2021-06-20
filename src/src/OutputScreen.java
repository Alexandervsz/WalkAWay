import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

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

    private void stop(ActionEvent actionEvent) {
        dispose();
        setVisible(false);
    }


}
