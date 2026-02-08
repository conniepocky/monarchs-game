package states;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.FontMetrics;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

import javax.imageio.ImageIO;

import core.App; 
import ui.ButtonComponent;
import java.awt.Rectangle;
import java.util.List;

public class GameOverState implements GameState, MouseInteractable {

    private App app;
    private Point mouse = new Point();
    private Rectangle backButton;
    private Integer finalYear;
    private String monarchName;
    private String reason;
    private String endMessage;

    private enum GameOverReason {
        PEOPLE_TOO_LOW("Discontent spreads like wildfire. The people rise against your rule, chanting your downfall. Overwhelmed and abandoned, your reign ends in revolution."),
        WEALTH_TOO_LOW("The royal treasury is empty. With no coin to pay officials or allies, the court descends into chaos. You are blamed for the collapse and quietly disposed of."),
        KNOWLEDGE_TOO_LOW("Your kingdom falls into ignorance and superstition. With no investment in learning or progress, society stagnates. You are ousted by reformers who promise a brighter future."),
        ARMY_TOO_LOW("With a weakened army, your enemies grow bold. A military faction stages a swift coup. You are taken from your chambers before dawn, never to return."),
        PEOPLE_TOO_HIGH("Your popularity reaches dizzying heights. The people adore you, but their expectations become impossible. When you fail to meet them, they turn on you. A mob storms the palace, and you are never seen again."),
        WEALTH_TOO_HIGH("Your kingdom's riches overflow, attracting envy and corruption. Greedy nobles and foreign powers plot to seize your fortune. One night, a trusted advisor poisons your wine."),
        KNOWLEDGE_TOO_HIGH("Obsessed with unraveling nature’s deepest secrets, your royal scientist crossed the final threshold. Their experiments defied morality, birthing something neither living nor dead. The creature turned on its creator, and the kingdom recoiled in horror, forcing your abdication in fear of what you had allowed to exist."),
        ARMY_TOO_HIGH("Your army grows into an unstoppable force, and decides it doesn’t need a king. The generals declare you should be exiled."),
        VAMPIRE_1("The Count turned you into a vampire. Your reign is over."),
        REVOLT("Your people and army have revolted. Your reign is over."),
        UNKNOWN("An unknown fate has befallen your reign.");

        private final String message;

        GameOverReason(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    public GameOverState(App app, Integer finalYear, String monarchName, String reason) {
        this.app = app;

        this.finalYear = finalYear;
        this.monarchName = monarchName;
        this.reason = reason;
        this.backButton = new Rectangle(50, 50, 150, 30); 

        GameOverReason gameOverReason = GameOverReason.UNKNOWN;

        try {
            gameOverReason = GameOverReason.valueOf(reason.toUpperCase().replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            // unknown reason
        }

        this.endMessage = gameOverReason.getMessage();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // Handle mouse press events if needed

        if (backButton.contains(e.getPoint())) {
            if (reason.contains("vampire")) {
                DatabaseManager.uploadReign(monarchName, finalYear, "turned into a vampire");
            } else {
                DatabaseManager.uploadReign(monarchName, finalYear, reason);
            }

            app.setCurrentState(new MainMenuState(app));
        }
    }

    @Override
    public void mouseWheelMoved(java.awt.event.MouseWheelEvent e) {
        // Handle mouse wheel movement if needed
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouse = e.getPoint();
    }

    @Override
    public void update() {
        // Update game logic if needed
    }

    @Override
    public void render(Graphics g, int width, int height) {
        // fonts 

        Font buttonFont = new Font("Telegraf", Font.PLAIN, 15);
        Font generalFont = new Font("Telegraf", Font.PLAIN, 20);
        Font endMessageFont = new Font("Telegraf", Font.ITALIC, 15);

        // draw title text

        try { // load game over title image from files
            File file = new File("src/assets/gameover.png");

            Image img = ImageIO.read(file);

            int imgWidth = img.getWidth(null) / 8;
            int imgHeight = img.getHeight(null) / 8;

            int x = (width - imgWidth) / 2;
            int y = (height - imgHeight) / 8;

            g.drawImage(img, x, y, imgWidth, imgHeight, null);

        } catch (IOException e) {
            e.printStackTrace(); // Handle error
        }

        // end of game messages

        g.setFont(generalFont);
        FontMetrics fm = g.getFontMetrics(generalFont);

        String endMessage = monarchName + " reigned for " + finalYear + " years.";
        int messageWidth = fm.stringWidth(endMessage);

        g.drawString(endMessage, (width - messageWidth) / 2, 200);

        // reason for game over

        g.setColor(Color.RED);
        g.setFont(endMessageFont);
        FontMetrics fm2 = g.getFontMetrics(endMessageFont);

        String[] words = this.endMessage.split(" ");
        List<String> linesList = new java.util.ArrayList<>();
        StringBuilder currentLine = new StringBuilder(); 

        for (String word : words) {
            if (currentLine.length() + word.length() + 1 <= 60) { // 60 characters per line, plus one accounts for space
                if (currentLine.length() > 0) { // if not the first word add a space 
                    currentLine.append(" ");
                }

                currentLine.append(word);
            } else { // if the current line is full
                linesList.add(currentLine.toString());

                currentLine = new StringBuilder(word);
            }
        }

        if (currentLine.length() > 0) {  // add final line if it exists
            linesList.add(currentLine.toString());
        }

        int yPosition = 250; 

        for (String line : linesList) {
            int lineWidth = fm2.stringWidth(line);
            g.drawString(line, (width - lineWidth) / 2, yPosition);
            yPosition += fm2.getHeight(); 
        }

        // back button

        g.setFont(buttonFont);

        backButton = new Rectangle(width - 225, height - 50, 175, 30);
        ButtonComponent.draw(g, "Return to Main Menu", backButton, mouse);
    } 

    @Override
    public MouseInteractable getInputHandler() {
        return this;
    }
}
