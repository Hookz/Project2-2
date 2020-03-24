package Interop;
import java.awt.*;
import java.util.Random;

public class Teleport{
  public Teleport(Rectangle area, Rectangle goal){
      this.area = area;
      this.goal = goal;
      generateColor();
    }

    public Teleport(Rectangle area, Rectangle goal, Color teleportColor){
        this.area = area;
        this.goal = goal;
        this.teleportColor = teleportColor;
    }

    public Rectangle area;
    public Rectangle goal;
    public Color teleportColor;
    private static Random rand = new Random(System.currentTimeMillis());

    public Rectangle getArea() {
        return area;
    }

    public Rectangle getGoal() {
        return goal;
    }

    private void generateColor(){
        Color newColor = new Color(rand.nextInt(256),rand.nextInt(256),rand.nextInt(256));
        this.teleportColor = newColor;
    }
}

