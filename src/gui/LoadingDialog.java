package gui;

import javax.swing.*;
import java.awt.*;

/**
 * loading dialogs are created when the application starts the pathfinder, to show the progress of the application.
 */
public class LoadingDialog extends JDialog {
    private JPanel contentPane;
    private JLabel loadingLabel;
    private final JProgressBar progressBar;

    /**
     * Initialises the loading dialog with the given text.
     *
     * @param text The text to be displayed.
     */
    public LoadingDialog(String text) {
        setLocationRelativeTo(null);
        setUndecorated(true);
        progressBar = new JProgressBar();
        progressBar.setMinimum(0);
        progressBar.setMaximum(100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        add(progressBar);
        setAlwaysOnTop(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        loadingLabel.setText(text);
        add(contentPane);
        setLayout(new GridLayout());
        pack();
        setVisible(true);
    }

    /**
     * Changes the text of the dialog.
     *
     * @param text the new text to be displayed.
     */
    public void setText(String text) {
        loadingLabel.setText(text);
    }

    /**
     * Set the progresbar to the given progress
     *
     * @param progress an int between 0 and 100.
     */
    public void setProgress(int progress) {
        progressBar.setValue(progress);
    }

}
