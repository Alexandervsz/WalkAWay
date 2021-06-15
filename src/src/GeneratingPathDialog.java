import javax.swing.*;

public class GeneratingPathDialog extends JFrame {
    private JPanel contentPane;
    private JLabel loadingLabel;

    public GeneratingPathDialog(String text) {
        setContentPane(contentPane);
        setUndecorated(true);
        getRootPane().setWindowDecorationStyle(JRootPane.NONE);
        setLocationRelativeTo(null);
        loadingLabel.setText(text);
        pack();
        setVisible(true);
    }

    public void stop() {
        dispose();
        setVisible(false);

    }

}
