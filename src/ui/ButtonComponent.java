package ui;
import java.awt.*;

public class ButtonComponent {
    public static void draw(Graphics g, String text, Rectangle r, Point mouse) {
        boolean hover = r.contains(mouse);

        // shadow effect

        Color shadowColor = new Color(0, 0, 0, 70); 
        int shadowOffset = 3; 

        g.setColor(shadowColor);
        for (int i = 0; i < shadowOffset; i++) {
            g.fillRoundRect(r.x + shadowOffset - i, r.y + shadowOffset - i, r.width, r.height, 5, 5);
        }

        // main button

        g.setColor(hover ? new Color(200, 200, 200) : new Color(220, 220, 220));
        g.fillRoundRect(r.x, r.y, r.width, r.height, 5, 5);

        // text
        
        g.setColor(Color.BLACK);
        
        FontMetrics fm = g.getFontMetrics();

        int tx = r.x + (r.width - fm.stringWidth(text)) / 2;
        int ty = r.y + (r.height + fm.getAscent()) / 2 - 4;

        g.drawString(text, tx, ty);
    }
}
