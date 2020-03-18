package Group2.GUI;

import Interop.Agent.Guard;
import Interop.Agent.Intruder;
import Interop.GameController;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Map;

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
        Graphics2D g2 = (Graphics2D) g;
        Dimension currentDimension = getSize();

        if (controller != null) {
            multiplier = calculateMultiplier();
            List<Rectangle> walls = controller.walls;
            HashMap<Intruder, Ellipse2D> intruderLocations = controller.intruderLocations;
            HashMap<Guard,Ellipse2D> guardLocations = controller.guardLocations;
            //Component updates go here
            for (Rectangle wall : walls) {
                g.setColor(Color.BLACK);
                Rectangle2D tempWall = new Rectangle2D.Double(norm(wall.x) + xCenterMargin(), norm(wall.y) + yCenterMargin(), norm(wall.width), norm(wall.height));
                g2.setColor(Color.BLACK);
                g2.fill(tempWall);
                g.setColor(Color.DARK_GRAY);
                g.drawRect(norm(wall.x) + xCenterMargin(), norm(wall.y) + yCenterMargin(), norm(wall.width), norm(wall.height));
                g2.draw(tempWall);
            }
            for(Map.Entry<Intruder,Ellipse2D> intruder: intruderLocations.entrySet()){
                Ellipse2D agentEllipse = intruder.getValue();
                agentEllipse = new Ellipse2D.Double(agentEllipse.getX() * multiplier, agentEllipse.getY() * multiplier, agentEllipse.getWidth() * multiplier, agentEllipse.getHeight() * multiplier);
                g2.setColor(Color.RED);
                g2.fill(agentEllipse);
                g2.setColor(Color.PINK);
                g2.draw(agentEllipse);
            }

            for(Map.Entry<Guard,Ellipse2D> guard: guardLocations.entrySet()){
                Ellipse2D agentEllipse = guard.getValue();
                agentEllipse = new Ellipse2D.Double(agentEllipse.getX() * multiplier, agentEllipse.getY() * multiplier, agentEllipse.getWidth() * multiplier, agentEllipse.getHeight() * multiplier);
                g2.setColor(Color.BLUE);
                g2.fill(agentEllipse);
                g2.setColor(Color.CYAN);
                g2.draw(agentEllipse);
            }
        }
    }

    public GameCanvas(GameController gc) {
        controller = gc;
    }

    private double multiplier = 1;
    private double calculateMultiplier() {
        Dimension currentDimension = getSize();

        double m = (double) currentDimension.width / controller.width;
        if (currentDimension.width / controller.width > currentDimension.height / controller.height) {
            m = (double) currentDimension.height / controller.height;
        }
        return m;
    }
    private int norm(int val) {
        int res = (int) (val * multiplier);
        return res;
    }
    private int xCenterMargin() {
        return (int) ((getSize().width - (controller.width * multiplier)) / 2);
    }
    private int yCenterMargin() {
        return (int) ((getSize().height - (controller.height * multiplier)) / 2);
    }
}