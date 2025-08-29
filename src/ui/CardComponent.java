package ui;
import java.awt.*;

public class CardComponent {
    public static void draw(Graphics g, Rectangle r, Point mouse, String text, String name, String imagePath) { 
        boolean hover = r.contains(mouse);

        g.setColor(new Color(102, 178, 255));
        g.fillRect(r.x, r.y, r.width, r.height);
        g.setColor(Color.BLACK);

        // draw an image if imagePath is provided
        if (imagePath != null && !imagePath.isEmpty()) { // 256 x 256
            try {
                Image img = Toolkit.getDefaultToolkit().getImage(imagePath);
                int imgWidth = 150;
                int imgHeight = 150; 
                int imgX = r.x + (r.width - imgWidth) / 2; 
                int imgY = r.y + (r.height - imgHeight) / 4; 
                g.drawImage(img, imgX, imgY, imgWidth, imgHeight, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // text content below the image
        
        g.setFont(new Font("Telegraf", Font.PLAIN, 15));
        FontMetrics fm = g.getFontMetrics();
        int tx = r.x + (r.width - fm.stringWidth(text)) / 2 - 50;
        int ty = r.y + ((r.height + fm.getAscent()) / 2) + 50; 
        g.drawString(text, tx, ty);

        // character name text

        if (name != null && !name.isEmpty()) {
            g.setFont(new Font("Telegraf", Font.PLAIN, 20));
            FontMetrics nameFm = g.getFontMetrics();
            int nx = r.x + (r.width - nameFm.stringWidth(name)) / 2;
            int ny = r.y + r.height - nameFm.getHeight() + 20; 
            g.drawString(name, nx, ny);
        }
    }
}
