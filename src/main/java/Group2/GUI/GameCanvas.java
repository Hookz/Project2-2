package Group2.GUI;

import Interop.GameController;

import javax.swing.*;
import java.awt.*;
import java.util.List;

@SuppressWarnings("serial")
public class GameCanvas extends JPanel {
    public Color backgroundColor = Color.RED;
    private GameController controller = null;

    public void paint(Graphics g) {
        super.paint(g);
        setBackground(backgroundColor);

        if (controller != null) {
            //Painting updates go here
        }
    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Dimension currentDimension = getSize();

        if (controller != null) {
            double multiplier = (double) currentDimension.width / controller.width;
            if (currentDimension.width / controller.width > currentDimension.height / controller.height) {
                multiplier = (double) currentDimension.height / controller.height;
            }
            List<Rectangle> walls = controller.walls;
            //Component updates go here
            for (Rectangle wall : walls) {
                g.setColor(Color.BLACK);
                g.fillRect((int) (wall.x * multiplier), (int) (wall.y * multiplier), (int) (wall.width * multiplier), (int) (wall.height * multiplier));
                g.setColor(Color.DARK_GRAY);
                g.drawRect((int) (wall.x * multiplier), (int) (wall.y * multiplier), (int) (wall.width * multiplier), (int) (wall.height * multiplier));
            }
        }
    }

    public GameCanvas(GameController gc) {
        controller = gc;
    }
}