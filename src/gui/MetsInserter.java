package gui;

import data.DatabaseManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

/**
 * This class is used to make it easier to insert metvalues into the database. Not meant for end users.
 */
public class MetsInserter extends JFrame {
    private JPanel mainPanel;
    private JButton confirmButton;
    private JLabel metsLabel;
    private JTextField metsField;
    private JLabel speedALabel;
    private JTextField speedAField;
    private JLabel speedBLabel;
    private JTextField speedBField;
    private JLabel activityLabel;
    private JTextField activityField;
    // Unused but required for form to work.
    private JPanel topPanel;
    private JPanel bottomPanel;

    /**
     * Initializes the layout of the UI.
     */
    public MetsInserter() {
        this.setLocationRelativeTo(null);
        metsLabel.setText("Please enter the mets value of your activity: ");
        speedALabel.setText("Please enter the beginning of the speed range of the activity(mph): ");
        speedBLabel.setText("Please enter the end of the speed range of the activity(mph): ");
        activityLabel.setText("Please enter a description of the activity: ");
        confirmButton.setText("Confirm");
        confirmButton.addActionListener(this::addMets);
        Image icon = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB_PRE); //Removes the ugly icon...
        setIconImage(icon);
        add(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Enter your data");
        pack();
        setVisible(true);
    }

    /**
     * Verifies the input given by the user.
     *
     * @param actionEvent The button is pressed.
     * @implNote speedA and speedB are in miles per hour, they are converted to kilometers per hour.
     */
    private void addMets(ActionEvent actionEvent) {
        try {
            double mets = Double.parseDouble(metsField.getText());
            double speedA;
            double speedB;
            if (speedAField.getText().equals("")) {
                speedA = -1;
            } else {
                speedA = Double.parseDouble(speedAField.getText()) * 1.609344; // convert to metric.
            }
            if (speedBField.getText().equals("")) {
                speedB = -1;
            } else {
                speedB = Double.parseDouble(speedBField.getText()) * 1.609344; // convert to metric.
            }
            String activity = activityField.getText();
            DatabaseManager databaseManager = new DatabaseManager();
            databaseManager.insertMets(mets, speedA, speedB, activity);
            dispose();

        } catch (NumberFormatException e) {
            metsField.setText("");
            speedAField.setText("");
            speedBField.setText("");
            activityField.setText("");
        }
    }

    public static void main(String[] args) {
        new MetsInserter();
    }
}