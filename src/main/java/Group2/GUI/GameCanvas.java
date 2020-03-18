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
        g.setColor(backgroundColor);
        //Component updates go below!
    }

    public GameCanvas(GameController gc) {
        controller = gc;
    }
}