import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class OutputScreen extends JFrame{
    private JLabel distanceLabel;
    private JLabel caloriesLabel;
    private JButton closeButton;
    private JPanel outputPanel;

    public OutputScreen(String distance, String calories){
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        distanceLabel.setText("Total distance of path: "+distance);
        caloriesLabel.setText("Estimated amount of calories burned when walking this route: "+calories);
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
