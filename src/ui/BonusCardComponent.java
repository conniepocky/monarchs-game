package ui;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import data.BonusCard;

public class BonusCardComponent {

    public static void drawPlaceholder(Graphics g, Rectangle r) {
        g.setColor(new Color(249, 251, 253)); // light gray for placeholder
        g.fillRoundRect(r.x, r.y, r.width, r.height, 20, 20);
        g.setColor(Color.BLACK);
    }

    public static void draw(Graphics g, BonusCard bonusCard, Rectangle r, Point mouse) {

        Graphics2D g2d = (Graphics2D) g.create(); 

        try {
            // handle card color and opacity
            g2d.setColor(bonusCard.getCardColor());
            
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, bonusCard.getOpacity()));

            // draw card background and font colour

            g2d.fillRoundRect(r.x, r.y, r.width, r.height, 20, 20);
            g2d.setColor(Color.BLACK);

            // handle text rendering

            String text = bonusCard.getTitle();

            if (text == null || text.isEmpty()) return; // no text to draw

            drawWrappedText(g2d, text, r);
        } finally {
            g2d.dispose();
        }
    }

    private static void drawWrappedText(Graphics2D g, String text, Rectangle r) {
        FontMetrics fm = g.getFontMetrics();
        int lineHeight = fm.getHeight();
        int padding = 10;
        int maxLineWidth = r.width - (padding * 2);

        // split text into lines that fit
        List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        String currentLine = "";

        for (String word : words) {
            if (fm.stringWidth(currentLine + word) < maxLineWidth) {
                currentLine += word + " ";
            } else {
                lines.add(currentLine);
                currentLine = word + " ";
            }
        }
        lines.add(currentLine);

        // calculate vertical centering
        int totalTextHeight = lines.size() * lineHeight;
        int currentY = r.y + (r.height - totalTextHeight) / 2 + fm.getAscent();

        // draw the lines
        g.setColor(Color.BLACK);
        for (String line : lines) {
            int lineWidth = fm.stringWidth(line);
            int currentX = r.x + (r.width - lineWidth) / 2;
            g.drawString(line, currentX, currentY);
            currentY += lineHeight;
        }
    }
}
