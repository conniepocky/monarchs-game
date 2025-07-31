package states;

import java.awt.Graphics;
import ui.ButtonComponent;
import java.awt.Rectangle;

import core.App;
import java.util.List;
import java.util.Date;
import java.awt.Point;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;

public class AchievementsState implements GameState, MouseInteractable {
    @Override
    public void mousePressed(java.awt.event.MouseEvent e) {
        // Handle mouse press events
        if (backButton.contains(e.getPoint())) {
            app.setCurrentState(new MainMenuState(app));
        }
    }

    @Override
    public void mouseMoved(java.awt.event.MouseEvent e) {
        // Handle mouse move events
        mouse.setLocation(e.getPoint());
    }

    private App app;

    // TO DO: Define data structures to hold achievements, description and completion status

    // sample achievements list
    private List<String> achievements;

    // ui components

    private Rectangle backButton;

    private Point mouse = new Point();

    public AchievementsState(App app) {
        this.app = app;

        // TOOD load achievements and their details from database. sql table with achievement names, descriptions and whether they are completed

        this.achievements = List.of( // sample list
            new String("First Monarch"),
            new String("Heir to the Throne"),
            new String("Conequeror")
        );

    }

    @Override
    public void update() {
        // Update logic
    }

    @Override
    public void render(Graphics g, int width, int height) {

        // draw title text

        Font titleFont = new Font("Telegraf", Font.BOLD, 24);
        Font generalFont = new Font("Telegraf", Font.PLAIN, 15);
        g.setFont(titleFont);
        
        FontMetrics fm = g.getFontMetrics(titleFont);
        String title = "Achievements";
        int titleWidth = fm.stringWidth(title);
        g.drawString(title, (width - titleWidth) / 2, 50);

        // list of achievements

        g.setFont(generalFont);
        g.setColor(Color.BLACK);

        for (String achievement : achievements) {
            g.drawString(achievement, 50, 150 + achievements.indexOf(achievement) * 20);
        }

        // back button to return to main menu
        backButton = new Rectangle(width - 150, height - 50, 100, 30);
        ButtonComponent.draw(g, "Back", backButton, mouse);
    }
}