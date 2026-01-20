package states;
import states.GameState;
import ui.MenuButtonComponent;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Image;

import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import java.awt.event.MouseAdapter;

import core.App;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Point;

public class MainMenuState implements GameState, MouseInteractable {
    @Override
    public void mouseWheelMoved(java.awt.event.MouseWheelEvent e) {
        // Handle mouse wheel movement here if needed
    }

    private App app;
    private Rectangle startBtn, tutorialBtn, achievementsBtn, progressBtn;
    private Point mouse = new Point();

    @Override
    public void mousePressed(MouseEvent e) {
        if (startBtn.contains(e.getPoint())) {
            String name = JOptionPane.showInputDialog(app, "Enter your monarch name:"); // pop-up dialog for name input

            if (name == null || name.trim().isEmpty()) {
                JOptionPane.showMessageDialog(app, "Monarch name cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (name.length() > 20) {
                JOptionPane.showMessageDialog(app, "Monarch name cannot exceed 20 characters.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                app.setCurrentState(new PlayingState(app, name.trim()));
            }

        } else if (tutorialBtn.contains(e.getPoint())) {
            app.setCurrentState(new TutorialState(app));
        } else if (achievementsBtn.contains(e.getPoint())) {
            app.setCurrentState(new AchievementsState(app));
        } else if (progressBtn.contains(e.getPoint())) {
            app.setCurrentState(new ProgressState(app));
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouse = e.getPoint();
    }

    public MainMenuState(App app) {
        this.app = app;

        this.startBtn = new Rectangle();   
        this.tutorialBtn = new Rectangle();
        this.achievementsBtn = new Rectangle();
        this.progressBtn = new Rectangle();
    }

    @Override
    public void update() {
        // Update the main menu
    }

    @Override
    public void render(Graphics g, int width, int height) {
        try { // load menu title image from files
            File file = new File("src/assets/menutitle.png");

            Image img = ImageIO.read(file);

            int imgWidth = img.getWidth(null) / 8;
            int imgHeight = img.getHeight(null) / 8;

            int x = (width - imgWidth) / 2;
            int y = (height - imgHeight) / 8;

            g.drawImage(img, x, y, imgWidth, imgHeight, null);

        } catch (IOException e) {
            e.printStackTrace(); // Handle error
        }

        //MenuButtonComponents

        g.setFont(new Font("Telegraf", Font.PLAIN, 20));

        startBtn.setBounds((width-200)/2, height/3, 200, 50);
        tutorialBtn.setBounds((width-200)/2, height/3+70, 200, 50);
        progressBtn.setBounds((width-200)/2, height/3+140, 200, 50);
        achievementsBtn.setBounds((width-200)/2, height/3+210, 200, 50);

        MenuButtonComponent.draw(g, "Start", startBtn, mouse);
        MenuButtonComponent.draw(g, "How To", tutorialBtn, mouse);
        MenuButtonComponent.draw(g, "Achievements", achievementsBtn, mouse);
        MenuButtonComponent.draw(g, "Progress", progressBtn, mouse);
    }

    @Override
    public MouseInteractable getInputHandler() {
        return this;
    }
}