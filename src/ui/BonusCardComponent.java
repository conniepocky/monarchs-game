package ui;
import java.awt.*;

public class BonusCardComponent {
    public static void draw(Graphics g, String text, Rectangle r, Point mouse) {
        g.setColor(new Color(249, 251, 253));
        g.fillRoundRect(r.x, r.y, r.width, r.height, 20, 20);
        g.setColor(Color.BLACK);

        if (text != null || !text.isEmpty()) {
            FontMetrics fm = g.getFontMetrics();
            int tx = r.x + (r.width - fm.stringWidth(text)) / 2;
            int ty = r.y + (r.height + fm.getAscent()) / 2 - 4;
            g.drawString(text, tx, ty);
        }
    }
}
