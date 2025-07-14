
import javax.swing.*;

import core.App;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Game");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);

            App appPanel = new App();
            frame.add(appPanel);

            frame.setVisible(true);

            new Timer(16, e -> {
                appPanel.update();
                appPanel.repaint();
            }).start();
        });
    }
}
