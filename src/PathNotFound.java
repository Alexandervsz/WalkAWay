import javax.swing.*;

/**
 * This is created when a path cannot be found.
 */
public class PathNotFound extends JFrame {
    private JPanel panel1;
    private JLabel pnfLabel;

    /**
     * Constructor of the screen.
     */
    public PathNotFound() {
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pnfLabel.setText("There's no path available, try a different set of coordinates and try again!");
        add(panel1);
        pack();
        setVisible(true);
    }
}
