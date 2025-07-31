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

public class ProgressState implements GameState, MouseInteractable {
    @Override
    public void mousePressed(java.awt.event.MouseEvent e) {
        // Handle mouse press events
        if (sortByReignLengthBtn.contains(e.getPoint())) {
            descendingOrderSort();
        } else if (sortByChronologicalOrderBtn.contains(e.getPoint())) {
            chronoglogicalOrderSort();
        } else if (backButton.contains(e.getPoint())) {
            app.setCurrentState(new MainMenuState(app));
        }
    }

    @Override
    public void mouseMoved(java.awt.event.MouseEvent e) {
        // Handle mouse move events
        mouse.setLocation(e.getPoint());
    }

    private App app;

    // TO DO: Define data structures to hold monarchs and their reign lengths

    // sample monarch list

    private List<String> monarchs; 

    // ui components for displaying monarchs and reign lengths

    private Rectangle sortByReignLengthBtn;
    private Rectangle sortByChronologicalOrderBtn;
    private Rectangle backButton;

    private Point mouse = new Point();

    public ProgressState(App app) {
        this.app = app;

        // TOOD load monarchs and reign lengths from database. sql table with monarch names and reign lengths

        this.monarchs = List.of( // sample list
            new String("Monarch A"),
            new String("Monarch B"),
            new String("Monarch C")
        ); 

    }

    @Override
    public void update() {
        // Update progress logic
    }

    public void descendingOrderSort() {
        // TODO quick sort the monarchs by reign length in descending order
    }

    public void chronoglogicalOrderSort() {
        // TODO quick sort the monarchs by most recently played in chronological order
    }

    @Override
    public void render(Graphics g, int width, int height) {

        // draw title text, centrally aligned

        Font titleFont = new Font("Telegraf", Font.BOLD, 24);
        Font generalFont = new Font("Telegraf", Font.PLAIN, 15);
        g.setFont(titleFont);
        
        FontMetrics fm = g.getFontMetrics(titleFont);
        String title = "Monarchs and their Reign Lengths:";
        int titleWidth = fm.stringWidth(title);
        g.drawString(title, (width - titleWidth) / 2, 50);

        // buttons to sort by descending order and chronological order

        g.setFont(generalFont);

        int buttonWidth = 225;
        int buttonHeight = 30;
        int buttonSpacing = 20;
        int totalButtonWidth = buttonWidth * 2 + buttonSpacing;
    
        int startX = (width - totalButtonWidth) / 2;
    
        this.sortByReignLengthBtn = new Rectangle(startX, 75, buttonWidth, buttonHeight);
        this.sortByChronologicalOrderBtn = new Rectangle(startX + buttonWidth + buttonSpacing, 75, buttonWidth, buttonHeight);

        ButtonComponent.draw(g, "Sort by Reign Length", sortByReignLengthBtn, mouse);
        ButtonComponent.draw(g, "Sort by Chronological Order", sortByChronologicalOrderBtn, mouse);

        // list of monarchs

        for (String monarch : monarchs) {
            g.drawString(monarch, 50, 150 + monarchs.indexOf(monarch) * 20);
        }

        // back button to return to main menu
        backButton = new Rectangle(width - 150, height - 50, 100, 30);
        ButtonComponent.draw(g, "Back", backButton, mouse);
    }
    
}
