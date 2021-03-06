package gui;

import data.DatabaseManager;
import data.MetValue;
import data.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * The main screen, the user enters the input, and when the button is pressed the input is validated. Afterwards the
 * pathfinder is launched.
 */
public class UserInputScreen extends JDialog {
    private JButton confirmButton;
    private JLabel metLabel;
    private JLabel weightLabel;
    private JLabel walkingSpeedLabel;
    private JLabel kcalLabel;
    private JPanel mainPanel;
    private JTextField weightField;
    private JTextField walkingSpeedField;
    private JTextField kcalField;
    private JComboBox<MetValue> metBox;
    private JTextField lonField;
    private JLabel lonLabel;
    private JTextField latField;
    private JLabel latLabel;

    // Unused but required for form to work.
    private JPanel southPanel;
    private JPanel northPanel;
    private JCheckBox randomizeCheckBox;
    private JLabel hiddenlabel;

    /**
     * Initialises the UI.
     */
    public UserInputScreen() {
        metLabel.setText("Activity: ");
        weightLabel.setText("Weight: ");
        DatabaseManager databaseManager = new DatabaseManager();
        List<MetValue> metValues = databaseManager.getBoxOptions();
        walkingSpeedLabel.setText("Speed: ");
        kcalLabel.setText("Calories to burn: ");
        lonLabel.setText("Longitude: ");
        latLabel.setText("Latitude: ");
        confirmButton.setText("Confirm");
        randomizeCheckBox.setText("Randomize");
        confirmButton.addActionListener(this::createUser);
        metBox.addActionListener(this::changeUI);
        for (MetValue met : metValues) {
            metBox.addItem(met);
        }
        Image icon = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB_PRE); //Removes the ugly icon...
        setIconImage(icon);
        setLayout(new GridLayout());
        add(mainPanel);
        validate();
        this.setTitle("WalkAWay");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setVisible(true);
        this.setLocationRelativeTo(null);
        this.pack();
    }

    /**
     * Creates a new data.User object, and passes this to the pathfinding algorithm, if the input is invalid it resets
     * the input fields.
     *
     * @param actionEvent The user pressed the button.
     */
    private void createUser(ActionEvent actionEvent) {
        MetValue metValue = (MetValue) metBox.getSelectedItem();
        assert metValue != null;
        try {
            double walkingSpeed;
            double weight = Double.parseDouble(weightField.getText());
            if (metValue.getSpeedA() == 0 || metValue.getSpeedB() != 0) {
                walkingSpeed = Double.parseDouble(walkingSpeedField.getText());
            } else {
                walkingSpeed = metValue.getSpeedA();
            }
            double kcal = Double.parseDouble(kcalField.getText());
            double met = metValue.getMetValue();
            double lon = Double.parseDouble(lonField.getText());
            double lat = Double.parseDouble(latField.getText());
            if (kcal < 0){
                clearScreen("Invalid amount of calories entered!");
                return;
            }
            if (weight < 0){
                clearScreen("Invalid weight entered!");
                return;
            }
            if (!(lat < 90.0 && lat > -90.0)){
                clearScreen("Invalid latitude entered!");
                return;
            }
            if (!(lon < 180.0 && lon > -180.0)){
                clearScreen("Invalid longitude entered!");
                return;
            }
            boolean isRandom = randomizeCheckBox.isSelected();
            User user = new User(met, weight, walkingSpeed, kcal, lon, lat, isRandom);
            dispose();
            setVisible(false);
            new PathFindingActivity(user).start();
        } catch (NumberFormatException e) {
            clearScreen("Invalid number entered!");
        }
    }

    /**
     * Clears the screen when invalid input is entered
     * @param text An error message to be displayed.
     */
    public void clearScreen(String text){
        weightField.setText("");
        walkingSpeedField.setText("");
        kcalField.setText("");
        lonField.setText("");
        latField.setText("");
        hiddenlabel.setText(text);

    }

    /**
     * Changes the UI based on the user's selected item.
     * If the user selects an item with a speed range (or without a speed at all) the speed entry field is hidden.
     *
     * @param actionEvent The user chose an option.
     */
    private void changeUI(ActionEvent actionEvent) {
        MetValue metValue = (MetValue) metBox.getSelectedItem();
        assert metValue != null;
        walkingSpeedLabel.setText("Speed: ");
        if (metValue.getSpeedA() != 0) {
            if (metValue.getSpeedB() != 0) {
                walkingSpeedLabel.setVisible(true);
                walkingSpeedField.setVisible(true);
            } else {
                walkingSpeedLabel.setVisible(false);
                walkingSpeedField.setVisible(false);
            }
        } else {
            walkingSpeedLabel.setVisible(true);
            walkingSpeedField.setVisible(true);
        }
    }

    /**
     * The main method which starts up the entire application.
     *
     * @param args arguments. (not implemented)
     */
    public static void main(String[] args) {
        new UserInputScreen();
    }
}
