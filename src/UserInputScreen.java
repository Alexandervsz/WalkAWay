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
    private JLabel metsLabel;
    private JLabel weightLabel;
    private JLabel walkingSpeedLabel;
    private JLabel kcalLabel;
    private JPanel mainPanel;
    private JTextField weightField;
    private JTextField walkingSpeedField;
    private JTextField kcalField;
    private JComboBox<MetValue> metsBox;
    private JTextField lonField;
    private JLabel lonLabel;
    private JTextField latField;
    private JLabel latLabel;

    // Unused but required for form to work.
    private JPanel southPanel;
    private JPanel northPanel;
    private JCheckBox randomizeCheckBox;

    /**
     * Initialises the UI.
     */
    public UserInputScreen() {
        metsLabel.setText("Activity: ");
        weightLabel.setText("Weight: ");
        DatabaseManager databaseManager = new DatabaseManager();
        List<MetValue> metValues = databaseManager.getBoxOptions();
        walkingSpeedLabel.setText("Speed: ");
        kcalLabel.setText("Calories to burn: ");
        lonLabel.setText("Longitude: ");
        latLabel.setText("Lattitude: ");
        confirmButton.setText("Confirm");
        confirmButton.addActionListener(this::createUser);
        metsBox.addActionListener(this::changeUI);
        for (MetValue mets : metValues) {
            metsBox.addItem(mets);
        }
        Image icon = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB_PRE); //Removes the ugly icon...
        setIconImage(icon);
        setLayout(new GridLayout());
        add(mainPanel);
        validate();
        this.setTitle("Enter your data");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setVisible(true);
        this.setLocationRelativeTo(null);
        this.pack();
    }

    /**
     * Creates a new User object, and passes this to the pathfinding algorithm, if the input is invalid it resets
     * the input fields.
     *
     * @param actionEvent The user pressed the button.
     */
    private void createUser(ActionEvent actionEvent) {
        MetValue metValue = (MetValue) metsBox.getSelectedItem();
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
            double mets = metValue.getMetValue();
            double lon = Double.parseDouble(lonField.getText());
            double lat = Double.parseDouble(latField.getText());
            boolean isRandom = randomizeCheckBox.isSelected();
            User user = new User(mets, weight, walkingSpeed, kcal, lon, lat, isRandom);
            dispose();
            setVisible(false);
            new PathFindingActivity(user).start();
        } catch (NumberFormatException e) {
            weightField.setText("");
            walkingSpeedField.setText("");
            kcalField.setText("");
            lonField.setText("");
        }
    }

    /**
     * Changes the UI based on the user's selected item.
     * If the user selects an item with a speed range (or without a speed at all) the speed entry field is hidden.
     *
     * @param actionEvent The user chose an option.
     */
    private void changeUI(ActionEvent actionEvent) {
        MetValue metValue = (MetValue) metsBox.getSelectedItem();
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
     * @param args arguments. (not implemented)
     */
    public static void main(String[] args) {
        new UserInputScreen();
    }
}
