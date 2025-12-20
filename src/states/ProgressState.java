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

import data.Monarch;
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
            chronologicalOrderSort();
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

    private List<Monarch> monarchs; 

    // ui components for displaying monarchs and reign lengths

    private Rectangle sortByReignLengthBtn;
    private Rectangle sortByChronologicalOrderBtn;
    private Rectangle backButton;

    private Point mouse = new Point();

    public ProgressState(App app) {
        this.app = app;

        this.monarchs = new java.util.ArrayList<>(); 

        fetchMonarchs();

    }

    @Override
    public void update() {
        // Update progress logic
    }

    public void fetchMonarchs() {
        String url = "jdbc:sqlite:res/monarchs-database.db";
        String sql = "SELECT id, monarchName, reignLength, causeOfDeath, timestamp FROM progress ORDER BY timestamp DESC"; // SQL query to fetch progress

        try (Connection conn = DriverManager.getConnection(url); // establish connection
            PreparedStatement pstmt = conn.prepareStatement(sql); 

            // execute SQL query and store in a result set
            ResultSet rs = pstmt.executeQuery()) { 

            monarchs.clear();
            while (rs.next()) {
                // get monarch details from result set

                Integer id = rs.getInt("id");
                String name = rs.getString("monarchName");
                Integer reignLength = rs.getInt("reignLength");
                String causeOfDeath = rs.getString("causeOfDeath");
                Date time = rs.getTimestamp("timestamp");

                Monarch monarch = new Monarch(id, name, reignLength, causeOfDeath, time); // create new monarch object

                monarchs.add(monarch); // add to list
            }

        } catch (SQLException e) {
            e.printStackTrace(); // print error
        }
    }

    public int partition(List<Monarch> list, int low, int high, java.util.Comparator<Monarch> comparator) {
        Monarch pivot = list.get(high);
        int i = (low - 1);

        for (int j = low; j < high; j++) {
            if (comparator.compare(list.get(j), pivot) < 0) {
                i++;

                // swap list[i] and list[j]
                Monarch temp = list.get(i);
                list.set(i, list.get(j));
                list.set(j, temp);
            }
        }

        // swap list[i + 1] and list[high] (or pivot)
        Monarch temp = list.get(i + 1);
        list.set(i + 1, list.get(high));
        list.set(high, temp);

        return i + 1;
    }

    public void quickSort(List<Monarch> list, int low, int high, java.util.Comparator<Monarch> comparator) {
        if (low < high) {
            int pi = partition(list, low, high, comparator);

            quickSort(list, low, pi - 1, comparator);
            quickSort(list, pi + 1, high, comparator);
        }
    }

    public void descendingOrderSort() {
        java.util.Comparator<Monarch> comparator = new java.util.Comparator<Monarch>() {
            @Override
            public int compare(Monarch m1, Monarch m2) {
                return m2.getReignLength().compareTo(m1.getReignLength());
            }
        };

        quickSort(monarchs, 0, monarchs.size() - 1, comparator);
    }

    public void chronologicalOrderSort() {
        java.util.Comparator<Monarch> comparator = new java.util.Comparator<Monarch>() { 
            @Override
            public int compare(Monarch m1, Monarch m2) {
                return m2.getTimestamp().compareTo(m1.getTimestamp());
            }
        };

        quickSort(monarchs, 0, monarchs.size() - 1, comparator);
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
    
        int buttonStartX = (width - totalButtonWidth) / 2;
    
        this.sortByReignLengthBtn = new Rectangle(buttonStartX, 75, buttonWidth, buttonHeight);
        this.sortByChronologicalOrderBtn = new Rectangle(buttonStartX + buttonWidth + buttonSpacing, 75, buttonWidth, buttonHeight);

        ButtonComponent.draw(g, "Sort by Reign Length", sortByReignLengthBtn, mouse);
        ButtonComponent.draw(g, "Sort by Chronological Order", sortByChronologicalOrderBtn, mouse);

        // list of monarchs

        int startX = (width - 500) / 2;
        g.setFont(generalFont);

        if (monarchs.isEmpty()) {
            g.setColor(Color.RED);
            g.drawString("No monarchs found in database. Come back once you have played a game!", startX, 150);
        } else {
            for (Monarch monarch : monarchs) {
                g.drawString(monarch.getMonarchName(), startX, 150 + monarchs.indexOf(monarch) * 20);
    
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");
                String formattedDate = monarch.getTimestamp()
                                                .toInstant() // UTC time
                                                .atZone(java.time.ZoneId.systemDefault()) // set to system default timezone
                                                .toLocalDate() // removes time section
                                                .format(formatter);
    
                g.drawString(formattedDate, startX+150, 150 + monarchs.indexOf(monarch) * 20);
    
                g.drawString(monarch.getReignLength().toString() + " years", startX+300, 150 + monarchs.indexOf(monarch) * 20);
    
                g.drawString(monarch.getCauseOfDeath(), startX+450, 150 + monarchs.indexOf(monarch) * 20);
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
