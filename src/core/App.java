package core;
import javax.swing.*;

import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

import states.GameState;
import states.MainMenuState;
import states.MouseInteractable;
import states.PlayingState;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class App extends JPanel {
    private GameState currentState;

    public App() {
        setFocusable(true);
        setCurrentState(new MainMenuState(this));

        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (currentState instanceof MouseInteractable) { 
                    ((MouseInteractable) currentState).mousePressed(e); 
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                if (currentState instanceof MouseInteractable) {
                    ((MouseInteractable) currentState).mouseMoved(e);
                }
            }
        };

        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
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