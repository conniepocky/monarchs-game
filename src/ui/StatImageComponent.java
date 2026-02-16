package ui;
import java.awt.*;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class StatImageComponent {
    private Image image;
    
    private Float percentageFilled = 0.5f; 

    public StatImageComponent(String imagePath) {
        try {
            java.net.URL imageUrl = getClass().getResource("/assets/" + imagePath);
            if (imageUrl == null) {
                throw new IOException("Could not find image resource");
            }
            this.image = ImageIO.read(imageUrl);
        } catch (IOException e) {
            e.printStackTrace();
            this.image = null; // Fallback if image loading fails
        }
    }

    public void draw(Graphics g, Rectangle r, Point mouse) {
        if (image != null) {
            g.drawImage(image, r.x, r.y, r.width, r.height, null);
        }
    }

    public void updatePercentageFilled(Graphics g, Rectangle r, Float newPercentage, Color fillColor) {
        if (newPercentage >= 0 && newPercentage <= 1) {
            this.percentageFilled = newPercentage;

            // draw the filled portion over image from the bottom up

            int filledHeight = (int) (r.height * percentageFilled);
            g.setColor(fillColor); // use the provided fill color
            g.fillRect(r.x, r.y + r.height - filledHeight, r.width, filledHeight);

        } else {
            System.out.println("game over");
        }
    }
}

