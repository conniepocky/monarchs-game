package core;
import javax.swing.*;
import states.GameState;
import states.MainMenuState;
import states.PlayingState;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class App extends JPanel {
    private GameState currentState;

    public App() {
        setCurrentState(new MainMenuState(this));
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleInput(e);
            }
        });
        setFocusable(true);
    }

    private void handleInput(KeyEvent e) {
        if (currentState != null) {
            currentState.handleInput();
            // if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            //     setCurrentState(new PlayingState());
            // }
        }
    }

    public void setCurrentState(GameState newState) {
        currentState = newState;
    }

    public void update() {
        if (currentState != null) {
            currentState.update();
        }
    }

    public void render(Graphics g) {
        if (currentState != null) {
            currentState.render(g, getWidth(), getHeight());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        render(g);
    }
}