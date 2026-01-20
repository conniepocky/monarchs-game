package states;

import java.awt.event.MouseEvent;

public interface MouseInteractable {
    void mousePressed(MouseEvent e);
    void mouseMoved(MouseEvent e);
    void mouseWheelMoved(java.awt.event.MouseWheelEvent e);
}
