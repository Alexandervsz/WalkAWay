package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

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
        Image icon = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB_PRE); //Removes the ugly icon...
        setIconImage(icon);
        add(panel1);
        pack();
        setVisible(true);
    }
}
