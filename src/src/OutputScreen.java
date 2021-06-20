import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class OutputScreen extends JFrame{
    private JLabel distanceLabel;
    private JLabel caloriesLabel;
    private JButton closeButton;
    private JPanel outputPanel;
    private JLabel timeLabel;

    public OutputScreen(String distance, String calories, String time){
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        distanceLabel.setText("Total distance of path: "+distance);
        caloriesLabel.setText("Estimated amount of calories burnt when walking this route: "+calories);
        timeLabel.setText("Estimated duration of activity: "+ time+" minutes.");
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
