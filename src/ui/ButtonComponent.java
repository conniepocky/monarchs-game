package ui;
import java.awt.*;

public class ButtonComponent {
    public static void draw(Graphics g, String text, Rectangle r, Point mouse) {
        boolean hover = r.contains(mouse);

        g.setColor(hover ? new Color(200, 200, 200) : new Color(220, 220, 220));
        g.fillRect(r.x, r.y, r.width, r.height);
        g.setColor(Color.BLACK);
        
        FontMetrics fm = g.getFontMetrics();

        int tx = r.x + (r.width - fm.stringWidth(text)) / 2;
        int ty = r.y + (r.height + fm.getAscent()) / 2 - 4;

        g.drawString(text, tx, ty);
    }
}
