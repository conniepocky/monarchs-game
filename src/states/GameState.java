package states;
import java.awt.Graphics;

public interface GameState {
    void handleInput();
    void update();
    void render(Graphics g, int width, int height);
}