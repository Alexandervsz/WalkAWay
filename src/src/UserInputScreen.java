import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

public class UserInputScreen extends JFrame {
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

    // Ununsed but required for form to work.
    private JPanel southPanel;
    private JPanel northPanel;


    public UserInputScreen() {
        metsLabel.setText("Please enter the mets value of your activity: ");
        weightLabel.setText("Please enter your weight: ");
        MetValue metValue = (MetValue) metsBox.getSelectedItem();
        walkingSpeedLabel.setText("Please enter your walking speed: ");
        kcalLabel.setText("Please enter the amount of Calories you want to burn during the exercise: ");
        confirmButton.addActionListener(this::createUser);
        metsBox.addActionListener(this::changeUI);
        DatabaseManager databaseManager = new DatabaseManager();
        for (MetValue mets : databaseManager.getBoxOptions()) {
            setData(mets);
        }

        Image icon = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB_PRE); //Removes the ugly icon...
        setIconImage(icon);
        setLayout(new GridLayout());
        add(mainPanel);
        validate();
    }


    private void createUser(ActionEvent actionEvent) {
        MetValue metValue = (MetValue) metsBox.getSelectedItem();
        assert metValue != null;
        try {
            float walkingSpeed;
            float weight = Float.parseFloat(weightField.getText());
            if (metValue.getSpeedA() == -1 || metValue.getSpeedB() != -1){
            walkingSpeed = Float.parseFloat(walkingSpeedField.getText());}
            else{
                walkingSpeed = metValue.getSpeedA();
            }
            float kcal = Float.parseFloat(kcalField.getText());
            float mets = metValue.getMetValue();
            User user = new User(mets, weight, walkingSpeed, kcal);
            System.out.println(user.getDistance());
        }
        catch (NumberFormatException e) {
            weightField.setText("");
            walkingSpeedField.setText("");
            kcalField.setText("");
        }


    }

    private void changeUI(ActionEvent actionEvent){
        MetValue metValue = (MetValue) metsBox.getSelectedItem();
        assert metValue != null;
        walkingSpeedLabel.setText("Please enter your "+metValue.getActivity()+" speed: ");
        if (metValue.getSpeedA() != -1){
            if (metValue.getSpeedB() != -1){
                walkingSpeedLabel.setVisible(true);
                walkingSpeedField.setVisible(true);
            }else{
            walkingSpeedLabel.setVisible(false);
            walkingSpeedField.setVisible(false);}
        }
        else{
            walkingSpeedLabel.setVisible(true);
            walkingSpeedField.setVisible(true);
        }
    }

    public static void main(String[] args) {
        UserInputScreen gui = new UserInputScreen();
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.setVisible(true);
        gui.pack();
        gui.setTitle("Enter your data");
    }

    public void setData(MetValue data) {
        metsBox.addItem(data);
    }

    public void getData(MetValue data) {
    }

    public boolean isModified(MetValue data) {
        return false;
    }
}
