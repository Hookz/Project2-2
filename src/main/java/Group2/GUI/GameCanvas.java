package Group2.GUI;

import Interop.GameController;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("serial")
public class GameCanvas extends JPanel {
    public Color backgroundColor = Color.RED;
    private GameController controller = null;

    public void paint(Graphics g) {
        setBackground(backgroundColor);
        if (controller != null) {
            //Component updates go here
        }
    }

    public GameCanvas(GameController gc) {
        controller = gc;
    }
}