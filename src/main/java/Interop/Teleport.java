package Interop;
import java.awt.*;

public class Teleport{
  public Teleport(Rectangle area, Rectangle goal){
      this.area = area;
      this.goal = goal;
    }
    public Rectangle area;
    public Rectangle goal;

    public Rectangle getArea() {
        return area;
    }

    public Rectangle getGoal() {
        return goal;
    }
}

