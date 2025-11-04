package states;
import java.awt.Graphics;

public interface GameState {
    void update();
    void render(Graphics g, int width, int height);
    
    MouseInteractable getInputHandler();
}