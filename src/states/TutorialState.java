package states;
import core.App;
import ui.ButtonComponent;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.FontMetrics;

public class TutorialState implements GameState, MouseInteractable {
    private App app;
    private Rectangle backButton;
    private Point mouse = new Point();

    public TutorialState(App app) {
        this.app = app;

        this.backButton = new Rectangle(50, 50, 100, 30); 
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (backButton.contains(e.getPoint())) {
            app.setCurrentState(new MainMenuState(app));
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouse = e.getPoint();
    }

    @Override
    public void update() {
        // Update game logic
    }

    @Override
    public void render(Graphics g, int width, int height) {
        Font titleFont = new Font("Telegraf", Font.BOLD, 24);
        Font buttonFont = new Font("Telegraf", Font.PLAIN, 15);
        g.setFont(titleFont);

        // title text
        
        FontMetrics fm = g.getFontMetrics(titleFont);
        String title = "How to Play";
        int titleWidth = fm.stringWidth(title);
        g.drawString(title, (width - titleWidth) / 2, 50);

        // tutorial instructions
        
        g.setFont(new Font("Telegraf", Font.PLAIN, 16));
        int startY = 150;
        int startX = (width - 500) / 3; 
        int lineHeight = 30;
        
        g.drawString("1. Press the play button in the main menu and enter a monarch name to begin.", startX, startY);
        g.drawString("2. Carefully read each card and its choices, they affect your kingdom's resources.", startX, startY + lineHeight);
        g.drawString("3. Manage your resources wisely: People, Wealth, Knowledge and Army.", startX, startY + 2 * lineHeight);
        g.drawString("4. If any of your 4 resources reach 0% or 100% the game will be over.", startX, startY + 3 * lineHeight);
        g.drawString("5. Some decisions unlock bonus cards with lasting effects each turn.", startX, startY + 4 * lineHeight);
        g.drawString("6. Track your progress and unlock achievements along the way.", startX, startY + 5 * lineHeight);
        g.drawString("7. Enjoy the game and aim for a long, prosperous reign!", startX, startY + 6 * lineHeight);

        // back button to return to main menu
        g.setFont(buttonFont);

        backButton = new Rectangle(width - 150, height - 50, 100, 30);
        ButtonComponent.draw(g, "Back", backButton, mouse);
    }
}
