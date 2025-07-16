package states;
import states.GameState;
import ui.Button;
import ui.Card;

import java.awt.Graphics;
import java.awt.Rectangle;

import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

import core.App;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Point;

public class PlayingState implements GameState, MouseInteractable {

    private Point mouse = new Point();

    @Override
    public void mousePressed(MouseEvent e) {
        // mouse pressed
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouse = e.getPoint();
    }

    private Rectangle currentCard;

    private App app;

    public PlayingState(App app) {
        this.app = app;
        this.currentCard = new Rectangle();
    }

    @Override
    public void handleInput() {
        // Handle input for the game
    }

    @Override
    public void update() {
        // Update game logic
    }

    @Override
    public void render(Graphics g, int width, int height) {
        // Render the game
        g.drawString("Playing", 100, 100);

        currentCard.setBounds((width-200)/2, (height/3)-150, 200, 300);
        Card.draw(g, currentCard, mouse, "Text");
    }
}