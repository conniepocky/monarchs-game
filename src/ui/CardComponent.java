package ui;
import java.awt.*;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

public class CardComponent {
    public static void draw(Graphics g, Rectangle rect, Point mouse, String text, String name, String imagePath, Color fillColor) { 
        g.setColor(fillColor);
        g.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 20, 20);
        g.setColor(Color.BLACK);

        // draw an image if imagePath is provided
        if (imagePath != null && !imagePath.isEmpty()) { // 256 x 256
            try {
                Image img = Toolkit.getDefaultToolkit().getImage(imagePath);
                int imgWidth = 150;
                int imgHeight = 150; 
                int imgX = rect.x + (rect.width - imgWidth) / 2; 
                int imgY = rect.y + (rect.height - imgHeight) / 4; 
                g.drawImage(img, imgX, imgY, imgWidth, imgHeight, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // text content below the image

        int contentFontSize = (int) (rect.height * 0.035); 

        // multiline text rendering

        g.setFont(new Font("Telegraf", Font.PLAIN, contentFontSize));
        FontMetrics textFm = g.getFontMetrics();

        int textX = rect.x + 10;
        int textY = rect.y + ((rect.height + textFm.getAscent()) / 2) + 50; 

        String[] words = text.split(" ");
        List<String> linesList = new java.util.ArrayList<>();
        StringBuilder currentLine = new StringBuilder(); 

        for (String word : words) {
            if (currentLine.length() + word.length() + 1 <= 35) { // 35 characters per line, plus one accounts for space
                if (currentLine.length() > 0) { // if not the first word add a space 
                    currentLine.append(" ");
                }

                currentLine.append(word);
            } else { // if the current line is full
                linesList.add(currentLine.toString());

                currentLine = new StringBuilder(word);
            }
        }

        if (currentLine.length() > 0) {  
            linesList.add(currentLine.toString());
        }

        for (String line : linesList) {
            g.drawString(line, textX, textY);
            textY += textFm.getHeight(); 
        }

        // character name text

        int characterFontSize = (int) (rect.height * 0.05); 

        if (name != null && !name.isEmpty()) {
            g.setFont(new Font("Telegraf", Font.BOLD, characterFontSize));
            FontMetrics nameFm = g.getFontMetrics();

            int nameX = rect.x + (rect.width - nameFm.stringWidth(name.toUpperCase())) / 2;
            int nameY = (rect.y + rect.height - nameFm.getHeight() + characterFontSize) - 20; 

            g.drawString(name.toUpperCase(), nameX, nameY);
        }
    }
}
