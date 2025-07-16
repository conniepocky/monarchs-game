package states;
import java.awt.Graphics;
import core.App;
import states.GameState;

public class AchievementsState implements GameState {
    private App app;

    public AchievementsState(App app) {
        this.app = app;
    }

    @Override
    public void handleInput() {
        // Handle input for the achievements state
    }

    @Override
    public void update() {
        // Update achievements logic
    }

    @Override
    public void render(Graphics g, int width, int height) {
        g.drawString("Achievements", 100, 100);
        // Render achievements here
    }
    
}
