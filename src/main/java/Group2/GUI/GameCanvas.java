package Group2.GUI;
import Interop.Agent.Guard;
import Interop.Agent.Intruder;
import Interop.GameController;
import Interop.Smell;
import Interop.Sound;
import Interop.Teleport;

import javax.swing.JPanel;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
@SuppressWarnings("serial")
public class GameCanvas extends JPanel {
    public Color backgroundColor = Color.DARK_GRAY;
    private GameController controller = null;
    public boolean showDebug = true;
    //default page texts.
    public final String TEXT_MIDDLE_DEFAULT = "No Scenario Loaded";
    public String text1 = TEXT_MIDDLE_DEFAULT;
    public final String TEXT_SMALL_DEFAULT = "Please load a scenario from File > Load Scenario";
    public String text2 = TEXT_SMALL_DEFAULT;
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
        Stroke defaultStroke = g2.getStroke();
        Stroke dashed = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);

        if (controller != null) {
            multiplier = calculateMultiplier();
            //Component updates go here
            if (GUI.drawWalls) for (Rectangle wall : controller.walls) {
                Rectangle2D tempWall = new Rectangle2D.Double(norm(wall.x) + xCenterMargin(), norm(wall.y) + yCenterMargin(), norm(wall.width), norm(wall.height));
                g2.setColor(Color.BLACK);
                g2.fill(tempWall);
                g.setColor(Color.DARK_GRAY);
                g2.draw(tempWall);
            }



            if (GUI.drawTargetArea) for(Rectangle area : controller.targetArea){
                Rectangle2D tempArea = new Rectangle2D.Double(norm(area.x) + xCenterMargin(), norm(area.y) + yCenterMargin(), norm(area.width), norm(area.height));
                g2.setColor(Color.GREEN);
                g2.fill(tempArea);
                g.setColor(Color.GREEN);
                g2.draw(tempArea);
            }

            if (GUI.drawTeleport) for(Teleport teleport: controller.teleports){
                Rectangle2D teleportOrigin = new Rectangle2D.Double(norm(teleport.area.x)+xCenterMargin(),norm(teleport.area.y)+yCenterMargin(),norm(teleport.area.width),norm(teleport.area.height));
                g2.setColor(teleport.teleportColor);
                g2.setStroke(dashed);
                g2.draw(teleportOrigin);
                g2.setStroke(defaultStroke);
            }

            for(Rectangle shadedArea: controller.shaded){
                Rectangle2D tempShaded = new Rectangle2D.Double(norm(shadedArea.x)+xCenterMargin(),norm(shadedArea.y)+yCenterMargin(),norm(shadedArea.width),norm(shadedArea.height));
                g2.setColor(Color.WHITE);
                g2.draw(tempShaded);
            }

            for(Map.Entry<Intruder,Ellipse2D> intruder: controller.intruderLocations.entrySet()){
                Ellipse2D agentEllipse = intruder.getValue();
                agentEllipse = new Ellipse2D.Double(norm(agentEllipse.getX()) + xCenterMargin(), norm(agentEllipse.getY()) + yCenterMargin(), norm(agentEllipse.getWidth()), norm(agentEllipse.getHeight()));
                g2.setColor(Color.RED);
                g2.fill(agentEllipse);
                g2.setColor(Color.PINK);
                g2.draw(agentEllipse);
            }

            for(Intruder intruder:controller.intruders) {
                Ellipse2D location = controller.intruderLocations.get(intruder);
                location = new Ellipse2D.Double(norm(location.getX()) + xCenterMargin(), norm(location.getY()) + yCenterMargin(), norm(location.getWidth()), norm(location.getHeight()));
                Point2D center = new Point2D.Double(location.getCenterX(),location.getCenterY());
                double newX = center.getX() + 20*Math.cos(controller.intruderDirections.get(intruder).getRadians());
                double newY = center.getY() + 20*Math.sin(controller.intruderDirections.get(intruder).getRadians());
                Point2D newPoint = new Point2D.Double(newX,newY);
                Line2D line = new Line2D.Double(center,newPoint);
                g2.setColor(Color.WHITE);
                g2.fill(line);
                g2.draw(line);
            }

            for(Map.Entry<Guard,Ellipse2D> guard: controller.guardLocations.entrySet()){
                Ellipse2D agentEllipse = guard.getValue();
                agentEllipse = new Ellipse2D.Double(norm(agentEllipse.getX()) + xCenterMargin(), norm(agentEllipse.getY()) + yCenterMargin(), norm(agentEllipse.getWidth()), norm(agentEllipse.getHeight()));
                g2.setColor(Color.BLUE);
                g2.fill(agentEllipse);
                g2.setColor(Color.CYAN);
                g2.draw(agentEllipse);
            }

            /*for(Map.Entry<Smell,Ellipse2D> guardSmell: controller.guardSmellLocations.entrySet()){
                Ellipse2D smellEllipse = guardSmell.getValue();
                smellEllipse = new Ellipse2D.Double(norm(smellEllipse.getX()) + xCenterMargin(), norm(smellEllipse.getY()) + yCenterMargin(), norm(smellEllipse.getWidth()), norm(smellEllipse.getHeight()));
                Color guardSmellColor = new Color(51, 153, 255, 127);
                g2.setColor(guardSmellColor);
                g2.fill(smellEllipse);
                g2.draw(smellEllipse);
            }*/

            if (GUI.drawIntruderSmell) for(Map.Entry<Smell,Ellipse2D> intruderSmell: controller.intruderSmellLocations.entrySet()){
                Ellipse2D smellEllipse = intruderSmell.getValue();
                smellEllipse = new Ellipse2D.Double(norm(smellEllipse.getX()) + xCenterMargin(), norm(smellEllipse.getY()) + yCenterMargin(), norm(smellEllipse.getWidth()), norm(smellEllipse.getHeight()));
                Color intruderSmellColor = new Color(255, 153, 153, 127);
                g2.setColor(intruderSmellColor);
                g2.fill(smellEllipse);
                g2.draw(smellEllipse);
            }

            if (GUI.drawSounds) for(Map.Entry<Sound,Ellipse2D> sound: controller.soundLocations.entrySet()){
                Ellipse2D soundEllipse = sound.getValue();
                soundEllipse = new Ellipse2D.Double(norm(soundEllipse.getX()) + xCenterMargin(), norm(soundEllipse.getY()) + yCenterMargin(), norm(soundEllipse.getWidth()), norm(soundEllipse.getHeight()));
                Color soundColor = new Color(255, 255, 102, 127);
                g2.setColor(soundColor);
                g2.fill(soundEllipse);
                g2.draw(soundEllipse);
            }

        }
        else {
            //No controller present:
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
        if (showDebug) {
            Color bgPanelColor = new Color(31, 31, 31, 127);
            g.setColor(bgPanelColor);
            g.fillRect(1,1, 600,99);
            g.fillRect(300,85, 298,13);

            Font fontdebug = new Font("Monospace", Font.PLAIN, 11);
            g.setFont(fontdebug);
            g.setColor(Color.LIGHT_GRAY);
            g.drawString(GUI.SCN_TITLE + " GUI 1.0SNAPSHOT, DKE P2-2 G02", 4,15);
            g.drawString("Gameloop    : " + Launcher.gameRunning, 4,15+11);
            g.drawString("Paused         : " + Launcher.paused, 4,15+22);
            g.drawString("Controller   : " + this.controller, 4,15+33);
            g.drawString("FPS Cycle     : " + Launcher.fps, 4,15+44);
            g.drawString("FPS         : " + Launcher.fpsThisSecond, 4,15+55);
            g.drawString("Target Rate : " + Launcher.UPDATE_PER_SECOND + "per second", 4,15+66);
            g.drawString("Turn Count : " + Launcher.UPDATE_PER_SECOND, 4,15+77);

            for (int i = GUI.debugTexts.size() - 1; i >= 0; i--) {
                int currLine = GUI.debugTexts.size() - i;
                if (currLine < 8) g.drawString(GUI.debugTexts.get(i), 305, 99 - (11 * currLine) - 10);
            }
            g.drawString(GUI.debugInput, 301,97);
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