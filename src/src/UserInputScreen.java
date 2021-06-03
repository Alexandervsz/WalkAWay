import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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
        walkingSpeedLabel.setText("Please enter your walking speed: ");
        kcalLabel.setText("Please enter the amount of Calories you want to burn during the exercise: ");
        confirmButton.addActionListener(this::createUser);
        for (MetValue mets : getBoxOptions()) {
            setData(mets);
        }

        Image icon = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB_PRE); //Removes the ugly icon...
        setIconImage(icon);
        setLayout(new GridLayout());
        add(mainPanel);
        validate();
    }

    private List<MetValue> getBoxOptions() {
        List<MetValue> metValues = new ArrayList<>();
        try {
            Class.forName("org.postgresql.Driver");
            Connection c = DriverManager
                    .getConnection("jdbc:postgresql://localhost:5432/IPASS",
                            "postgres", "postgres");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM metvalues;");
            while (rs.next()) {
                metValues.add(new MetValue(rs.getString("metvalue"), rs.getString("speeda"), rs.getString("speedb"), rs.getString("activity")));
            }
            rs.close();
            stmt.close();
            c.close();


        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return metValues;
    }

    private void createUser(ActionEvent actionEvent) {
        MetValue metValue = (MetValue) metsBox.getSelectedItem();
        try {
            float weight = Float.parseFloat(weightField.getText());
            float walkingSpeed = Float.parseFloat(walkingSpeedField.getText());
            float kcal = Float.parseFloat(kcalField.getText());
            assert metValue != null;
            float mets = metValue.getMetValue();
            User user = new User(mets, weight, walkingSpeed, kcal);
            System.out.println(user.getKilometers());
        }
        catch (NumberFormatException e) {
            weightField.setText("");
            walkingSpeedField.setText("");
            kcalField.setText("");
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
