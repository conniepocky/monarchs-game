package ui;
import java.awt.*;

public class Card {
    public static void draw(Graphics g, Rectangle r, Point mouse, String text) {
        boolean hover = r.contains(mouse);

        g.setColor(new Color(102, 204, 255));
        g.fillRoundRect(r.x, r.y, r.width, r.height, 20, 20);
        g.setColor(Color.BLACK);
        
        FontMetrics fm = g.getFontMetrics();
        int tx = r.x + (r.width - fm.stringWidth(text)) / 2;
        int ty = r.y + (r.height + fm.getAscent()) / 2 - 4;
        g.drawString(text, tx, ty);
    }
}
