package ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseWheelEvent;

public class ScrollbarView {
    private Rectangle bounds;
    private int scrollY = 0;
    private int rowHeight = 30;
    private int totalItems = 0;

    public interface ItemRenderer {
        void drawItem(Graphics g, int index, int x, int y);
    }

    public void setBounds(int x, int y, int width, int height) {
        this.bounds.setBounds(x, y, width, height);
    }

    public ScrollbarView(int x, int y, int width, int height) {
        this.bounds = new Rectangle(x, y, width, height);
    }

    public void setRowHeight(int height) {
        this.rowHeight = height;
    }

    public void handleMouseWheel(MouseWheelEvent e) {
        int contentHeight = totalItems * rowHeight;
        
        if (contentHeight > bounds.height) {
            int maxScroll = contentHeight - bounds.height;
            int scrollSpeed = 20;
            
            scrollY += e.getWheelRotation() * scrollSpeed;

            // Clamp values
            if (scrollY < 0) scrollY = 0;
            if (scrollY > maxScroll) scrollY = maxScroll;
        } else {
        }
    }

    public void render(Graphics g, int itemCount, ItemRenderer renderer) {
        this.totalItems = itemCount;
        
        Shape originalClip = g.getClip();
        g.setClip(bounds);

        for (int i = 0; i < itemCount; i++) {
            int itemY = bounds.y + (i * rowHeight) - scrollY;

            if (itemY + rowHeight > bounds.y && itemY < bounds.y + bounds.height) {
                // Ask the caller to draw the specific data for this index
                renderer.drawItem(g, i, bounds.x, itemY);
            }
        }
        g.setClip(originalClip);

        drawScrollbar(g);
    }

    private void drawScrollbar(Graphics g) {
        int contentHeight = totalItems * rowHeight;
        if (contentHeight <= bounds.height) return; // No scrollbar needed

        int barWidth = 10;
        int barX = bounds.x + bounds.width - barWidth;
        
        g.setColor(new Color(230, 230, 230));
        g.fillRect(barX, bounds.y, barWidth, bounds.height);

        float viewportRatio = (float) bounds.height / contentHeight;
        int thumbHeight = (int) (bounds.height * viewportRatio);
        if (thumbHeight < 20) thumbHeight = 20;

        int maxScroll = contentHeight - bounds.height;
        int thumbY = bounds.y + (int) (((float) scrollY / maxScroll) * (bounds.height - thumbHeight));

        g.setColor(Color.GRAY);
        g.fillRect(barX, thumbY, barWidth, thumbHeight);
    }
}