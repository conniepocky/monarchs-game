package ui;
import java.awt.*;

public class MenuButtonComponent {
    public static void draw(Graphics g, String text, Rectangle r, Point mouse) {
        boolean hover = r.contains(mouse);

        g.setColor(hover ? new Color(255,102,102) : new Color(255,153,153));
        g.fillRect(r.x, r.y, r.width, r.height);
        g.setColor(Color.WHITE);
        
        FontMetrics fm = g.getFontMetrics();

        int textX = r.x + (r.width - fm.stringWidth(text)) / 2; // center text horizontally by accounting for text width
        int textY = r.y + (r.height + fm.getAscent()) / 2 - 4; // center text vertically by accounting for text height

        g.drawString(text, textX, textY);
    }
}
