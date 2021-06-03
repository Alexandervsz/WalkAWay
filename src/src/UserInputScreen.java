import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class UserInputScreen extends JFrame {
    private JButton confirmButton;
    private JLabel metsLabel;
    private JLabel weightLabel;
    private JLabel walkingSpeedLabel;
    private JLabel kcalLabel;
    private JPanel mainPanel;
    private JTextField metsField;
    private JTextField weightField;
    private JTextField walkingSpeedField;
    private JTextField kcalField;

    // Ununsed but required for form to work.
    private JPanel southPanel;
    private JPanel northPanel;

    public UserInputScreen(){
        metsLabel.setText("Please enter the mets value of your activity: ");
        weightLabel.setText("Please enter your weight: ");
        walkingSpeedLabel.setText("Please enter your walking speed: ");
        kcalLabel.setText("Please enter the amount of Calories you want to burn during the exercise: ");
        confirmButton.addActionListener(this::onConfirmButton);
        Image icon = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB_PRE); //Removes the ugly icon...
        setIconImage(icon);
        setLayout(new GridLayout());
        add(mainPanel);
        validate();
    }

    private void onConfirmButton(ActionEvent actionEvent) {
        System.out.println(metsField.getText());
        System.out.println(weightField.getText());
        System.out.println(walkingSpeedField.getText());
        System.out.println(kcalField.getText());
    }

    public static void main(String[] args) {
        UserInputScreen gui = new UserInputScreen();
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.setVisible(true);
        gui.pack();
        gui.setTitle("Enter your data");
    }
}
