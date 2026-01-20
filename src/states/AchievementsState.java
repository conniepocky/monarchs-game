package states;

import java.awt.Graphics;
import ui.ButtonComponent;
import java.awt.Rectangle;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

import core.App;
import java.util.List;
import java.util.Date;
import java.awt.Point;
import data.Achievement;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;

public class AchievementsState implements GameState, MouseInteractable {
    @Override
    public void mouseWheelMoved(java.awt.event.MouseWheelEvent e) {
        // Handle mouse wheel events
    }
    
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

    private List<Achievement> achievements;

    private List<String> achievements2;

    // ui components

    private Rectangle backButton;

    private Point mouse = new Point();

    public AchievementsState(App app) {
        this.app = app;
        this.achievements = new java.util.ArrayList<>(); 

        fetchAchievements();

    }

    private void fetchAchievements() {
        String url = "jdbc:sqlite:res/monarchs-database.db";
        String sql = "SELECT id, name, description, unlocked, timestamp FROM achievements ORDER BY timestamp DESC"; // SQL query to fetch achievements

        try (Connection conn = DriverManager.getConnection(url); // establish connection
             PreparedStatement pstmt = conn.prepareStatement(sql); 

             // execute SQL query and store in a result set
             ResultSet rs = pstmt.executeQuery()) { 

            achievements.clear();
            while (rs.next()) {
                // get achievement details from result set

                String name = rs.getString("name");
                String description = rs.getString("description");
                Integer unlocked = rs.getInt("unlocked");
                Date time = rs.getTimestamp("timestamp");
                Integer id = rs.getInt("id");

                Achievement achievement = new Achievement(id, name, description, unlocked, time); // create new achievement object

                achievements.add(achievement); // add to list
            }

        } catch (SQLException e) {
            e.printStackTrace(); // print error
        }
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

        int startX = (width - 500) / 3; 

        for (Achievement achievement : achievements) {
            if (achievement.getUnlocked() == 1) {
                g.setColor(new Color(91, 176, 116)); // unlocked achievements in green
            } else {
                g.setColor(Color.BLACK); // locked achievements in black
            }

            g.drawString(achievement.getName(), startX, 150 + achievements.indexOf(achievement) * 20);

            g.drawString(achievement.getDescription(), startX+175, 150 + achievements.indexOf(achievement) * 20);

            if (achievement.getUnlocked() == 1 && achievement.getTimestamp() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");
                String formattedDate = achievement.getTimestamp()
                                                .toInstant() // UTC time
                                                .atZone(java.time.ZoneId.systemDefault()) // set to system default timezone
                                                .toLocalDate() // removes time section
                                                .format(formatter);

                g.drawString(formattedDate, (int)(startX+(175*3.25)), 150 + achievements.indexOf(achievement) * 20);
            } 
        }

        // back button to return to main menu
        backButton = new Rectangle(width - 150, height - 50, 100, 30);
        ButtonComponent.draw(g, "Back", backButton, mouse);
    }

    @Override
    public MouseInteractable getInputHandler() {
        return this;
    }
}