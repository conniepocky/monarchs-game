package ui;
import java.awt.*;
import java.util.List;

public class ButtonComponent {
    public static void draw(Graphics g, String text, Rectangle rect, Point mouse) {
        boolean hover = rect.contains(mouse);

        // shadow effect

        Color shadowColor = new Color(0, 0, 0, 70); 
        int shadowOffset = 3; 

        g.setColor(shadowColor);
        for (int i = 0; i < shadowOffset; i++) {
            g.fillRoundRect(rect.x + shadowOffset - i, rect.y + shadowOffset - i, rect.width, rect.height, 5, 5);
        }

        // main button

        g.setColor(hover ? new Color(200, 200, 200) : new Color(220, 220, 220));
        g.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 5, 5);

        // text

        Integer fontSize = (int)(rect.height * 0.40);

        if (rect.height > 45) {
            fontSize = (int)(rect.height * 0.25);
        }

        g.setColor(Color.BLACK);

        g.setFont(new Font("Telegraf", Font.PLAIN, fontSize));

        FontMetrics fm = g.getFontMetrics();

        String[] words = text.split(" ");

        List<String> linesList = new java.util.ArrayList<>();
        StringBuilder currentLine = new StringBuilder(); 

        for (String word : words) {
            if (fm.stringWidth(currentLine.toString() + " " + word) <= rect.width) { // if adding the next word doesn't exceed the button width
                if (currentLine.length() > 0) {
                    currentLine.append(" "); // add a space if it's not the first word
                }
                currentLine.append(word);
            } else {
                // if the line is full, add it to the list and start a new line
                linesList.add(currentLine.toString());
                currentLine = new StringBuilder(word);
            }
        }

        if (currentLine.length() > 0) {  
            linesList.add(currentLine.toString());
        }

        // Calculate the total height of all lines
        int totalTextHeight = linesList.size() * fm.getHeight();

        // Starting y position to center the text block vertically

        int textY = rect.y + (rect.height - totalTextHeight) / 2 + fm.getAscent();

        for (String line : linesList) {
            int textX = rect.x + (rect.width - fm.stringWidth(line)) / 2;

            g.drawString(line, textX, textY);
            textY += fm.getHeight(); 
        }
    }
}
