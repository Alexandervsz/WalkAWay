import javax.swing.*;
import java.awt.*;

public class LoadingDialog extends JDialog {
    private JPanel contentPane;
    private JLabel loadingLabel;
    private final JProgressBar progressBar;

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
    public void setText(String text){
        loadingLabel.setText(text);
    }

    public void setProgress(int progress){
        progressBar.setValue(progress);
    }

}
