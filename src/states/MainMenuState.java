package states;
import states.GameState;
import ui.Button;

import java.awt.Graphics;
import java.awt.Rectangle;

import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

import core.App;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Point;

public class MainMenuState implements GameState, MouseInteractable {

    private App app;
    private Rectangle startBtn, tutorialBtn;
    private Point mouse = new Point();

    @Override
    public void mousePressed(MouseEvent e) {
        if (startBtn.contains(e.getPoint())) {
            app.setCurrentState(new PlayingState(app));
        } else if (tutorialBtn.contains(e.getPoint())) {
            app.setCurrentState(new TutorialState(app));
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
    }

    @Override
    public void handleInput() {
        // Handle input for the main menu
    }

    @Override
    public void update() {
        // Update the main menu
    }

    @Override
    public void render(Graphics g, int width, int height) {

        g.setFont(new Font("Arial Black", Font.BOLD, 36));
        g.setColor(new Color(50, 150, 250));  
        
        String text = "Main Menu";

        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent(); 

        int x = (width - textWidth) / 2;
        int y = (height + textHeight) / 4; 

        g.drawString(text, x, y);

        startBtn.setBounds((width-200)/2, height/3, 200, 50);
        tutorialBtn.setBounds((width-200)/2, height/3+70, 200, 50);
        Button.draw(g, "Start", startBtn, mouse);
        Button.draw(g, "How To", tutorialBtn, mouse);
    }
}