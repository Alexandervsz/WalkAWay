import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

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

    // Ununsed but required for form to work.
    private JPanel southPanel;
    private JPanel northPanel;

    public UserInputScreen() {
        metsLabel.setText("Activity: ");
        weightLabel.setText("Weight: ");
        DatabaseManager databaseManager = new DatabaseManager();
        List<MetValue> metValues = databaseManager.getBoxOptions();
        walkingSpeedLabel.setText("Speed: ");
        kcalLabel.setText("Calories to burn: ");
        lonLabel.setText("Longitude: ");
        latLabel.setText("Lattitude: ");
        confirmButton.setText("Confirm.");


        confirmButton.addActionListener(this::createUser);
        metsBox.addActionListener(this::changeUI);
        for (MetValue mets : metValues) {
            setData(mets);
        }

        Image icon = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB_PRE); //Removes the ugly icon...
        setIconImage(icon);
        setLayout(new GridLayout());
        add(mainPanel);
        validate();
    }

    private void start() {
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setVisible(true);
        this.setLocationRelativeTo(null);
        this.pack();
        this.setTitle("Enter your data");
    }

    private void createUser(ActionEvent actionEvent) {
        MetValue metValue = (MetValue) metsBox.getSelectedItem();
        assert metValue != null;
        try {
            double walkingSpeed;
            double weight = Double.parseDouble(weightField.getText());
            if (metValue.getSpeedA() == -1 || metValue.getSpeedB() != -1) {
                walkingSpeed = Double.parseDouble(walkingSpeedField.getText());
            } else {
                walkingSpeed = metValue.getSpeedA();
            }
            double kcal = Double.parseDouble(kcalField.getText());
            double mets = metValue.getMetValue();
            double lon = Double.parseDouble(lonField.getText());
            double lat = Double.parseDouble(latField.getText());
            User user = new User(mets, weight, walkingSpeed, kcal, lon, lat);
            dispose();
            setVisible(false);
            NodeFetcher nodeFetcher = new NodeFetcher(user);
            nodeFetcher.start();

        } catch (NumberFormatException e) {
            weightField.setText("");
            walkingSpeedField.setText("");
            kcalField.setText("");
            lonField.setText("");
        } catch (IOException | ParseException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void changeUI(ActionEvent actionEvent) {
        MetValue metValue = (MetValue) metsBox.getSelectedItem();
        assert metValue != null;
        walkingSpeedLabel.setText("Please enter your " + metValue.getActivity() + " speed: ");
        if (metValue.getSpeedA() != -1) {
            if (metValue.getSpeedB() != -1) {
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

    public static void main(String[] args) {
        UserInputScreen gui = new UserInputScreen();
        gui.start();
    }

    public void setData(MetValue data) {
        metsBox.addItem(data);
    }
}
