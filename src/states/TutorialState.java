package states;
import core.App;
import java.awt.Graphics;

public class TutorialState implements GameState {
    private App app;

    public TutorialState(App app) {
        this.app = app;
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
        g.drawString("How To Play", 100, 100);
    }
}
