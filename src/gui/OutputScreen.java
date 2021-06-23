package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

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
        Image icon = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB_PRE); //Removes the ugly icon...
        setIconImage(icon);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("WalkAWay");
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
