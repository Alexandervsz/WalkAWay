import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class MetsInserter extends JFrame {
    private JPanel mainPanel;
    private JPanel topPanel;
    private JPanel bottomPanel;
    private JButton confirmButton;
    private JTextField metsField;
    private JTextField speedAField;
    private JTextField activityField;
    private JLabel metsLabel;
    private JLabel speedALabel;
    private JLabel activityLabel;
    private JLabel speedBLabel;
    private JTextField speedBField;

    public MetsInserter() {
        metsLabel.setText("Please enter the mets value of your activity: ");
        speedALabel.setText("Please enter the beginning of the speed range of the activity: ");
        speedBLabel.setText("Please enter the end of the speed range of the activity: ");
        activityLabel.setText("Please enter a description of the activity: ");
        confirmButton.addActionListener(this::addMets);

        Image icon = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB_PRE); //Removes the ugly icon...
        setIconImage(icon);
        setLayout(new GridLayout());
        add(mainPanel);
        validate();
    }

    private void addMets(ActionEvent actionEvent) {
        Connection c;
        try {
            float mets = Float.parseFloat(metsField.getText());
            float speedA = Float.parseFloat(speedAField.getText()) * 1.609344f; // convert to metric.
            float speedB;
            if (speedBField.getText().equals("")) {
                speedB = -1;
            } else {
                speedB = Float.parseFloat(speedBField.getText()) * 1.609344f;
            }
            String activity = activityField.getText();
            DatabaseManager databaseManager = new DatabaseManager();
            databaseManager.insertMets(mets, speedA, speedB, activity);

        } catch (NumberFormatException e) {
            metsField.setText("");
            speedAField.setText("");
            speedBField.setText("");
            activityField.setText("");

        }
    }

    public static void main(String[] args) {
        MetsInserter metsInserter = new MetsInserter();
        metsInserter.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        metsInserter.setVisible(true);
        metsInserter.pack();
        metsInserter.setTitle("Enter your data");
    }

}
