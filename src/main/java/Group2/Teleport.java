package Group2;
import java.awt.*;

public class Teleport{
  public Teleport(Rectangle area, Point goal){
      this.area = area;
      this.goal = goal;
    }
    public Rectangle area;
    public Point goal;

    public Rectangle getArea() {
        return area;
    }

    public Point getGoal() {
        return goal;
    }
}

