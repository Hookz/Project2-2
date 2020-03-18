package Group2.GUI;

import Interop.Agent.Guard;
import Interop.Agent.Intruder;
import Interop.GameController;

import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Dimension;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import java.util.Map;

@SuppressWarnings("serial")
public class GameCanvas extends JPanel {
    public Color backgroundColor = Color.DARK_GRAY;
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

        if (controller != null) {
            multiplier = calculateMultiplier();
            //Component updates go here
            for (Rectangle wall : controller.walls) {
                Rectangle2D tempWall = new Rectangle2D.Double(norm(wall.x) + xCenterMargin(), norm(wall.y) + yCenterMargin(), norm(wall.width), norm(wall.height));
                g2.setColor(Color.BLACK);
                g2.fill(tempWall);
                g.setColor(Color.DARK_GRAY);
                g.drawRect(norm(wall.x) + xCenterMargin(), norm(wall.y) + yCenterMargin(), norm(wall.width), norm(wall.height));
                g2.draw(tempWall);
            }
            for(Map.Entry<Intruder,Ellipse2D> intruder: controller.intruderLocations.entrySet()){
                Ellipse2D agentEllipse = intruder.getValue();
                agentEllipse = new Ellipse2D.Double(norm(agentEllipse.getX()) + xCenterMargin(), norm(agentEllipse.getY()) + yCenterMargin(), norm(agentEllipse.getWidth()), norm(agentEllipse.getHeight()));
                g2.setColor(Color.RED);
                g2.fill(agentEllipse);
                g2.setColor(Color.PINK);
                g2.draw(agentEllipse);
            }

            for(Map.Entry<Guard,Ellipse2D> guard: controller.guardLocations.entrySet()){
                Ellipse2D agentEllipse = guard.getValue();
                agentEllipse = new Ellipse2D.Double(norm(agentEllipse.getX()) + xCenterMargin(), norm(agentEllipse.getY()) + yCenterMargin(), norm(agentEllipse.getWidth()), norm(agentEllipse.getHeight()));
                g2.setColor(Color.BLUE);
                g2.fill(agentEllipse);
                g2.setColor(Color.CYAN);
                g2.draw(agentEllipse);
            }
        }
        else {
            //No controller present:
            String text1 = "No Scenario Loaded";
            String text2 = "Please load a scenario from File > Load Scenario";
            Font font = new Font("Serif", Font.PLAIN, 50);
            Font fontsm = new Font("Serif", Font.PLAIN, 21);

            FontMetrics metrics = g.getFontMetrics(font);

            //big text center values
            int x = (getSize().width - metrics.stringWidth(text1)) / 2;
            int y = ((getSize().height - metrics.getHeight()) / 2) + metrics.getAscent();

            g.setFont(font);
            g.setColor(Color.WHITE);
            g.drawString(text1, x, y);

            metrics = g.getFontMetrics(fontsm);
            //sm text center x; y is old y + 50~
            x = (getSize().width - metrics.stringWidth(text2)) / 2;

            g.setFont(fontsm);
            g.drawString(text2, x, y + 25);
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
    private int norm(double val) {
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