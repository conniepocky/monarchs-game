package core;
import javax.swing.*;

public class GameLoop extends JFrame {
    private App game;

    public GameLoop() {
        game = new App();
        setTitle("Monarchs");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(game);
        setVisible(true);
        run();
    }

    private void run() {
        while (true) {
            game.update();
            game.repaint();
            try {
                Thread.sleep(16); 
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new GameLoop();
    }
}