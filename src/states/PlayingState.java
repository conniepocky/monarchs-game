package states;
import java.awt.Graphics;
import core.App;
import states.GameState;

public class PlayingState implements GameState {

    private App app;

    public PlayingState(App app) {
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
        g.drawString("Playing Game", 100, 100);
    }
}