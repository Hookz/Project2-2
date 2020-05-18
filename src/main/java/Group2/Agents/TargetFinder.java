package Group2.Agents;

import Group2.Map.Graph;
import Group2.Map.GridMap;
import Group2.Map.Node;
import Group2.Map.PathFinding;
import Group2.MapBumf;
import Interop.Action.IntruderAction;
import Interop.Action.Move;
import Interop.Action.Rotate;
import Interop.Agent.Intruder;
import Interop.Geometry.Angle;
import Interop.Geometry.Direction;
import Interop.Geometry.Distance;
import Interop.Geometry.Point;
import Interop.Percept.IntruderPercepts;
import Interop.Percept.Vision.ObjectPerceptType;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TargetFinder implements Intruder {
    Direction a = null;
    Direction b = null;
    Direction c = null;

    Distance u = null;
    int step = 0;

    private GridMap currentMap;
    private Point targetLocation;
    private int ID;
    private LinkedList<Node> path;

    public TargetFinder(int id) {
        this.currentMap = new GridMap();
        this.ID = id;
    }

    @Override
    public IntruderAction getAction(IntruderPercepts percepts) {
        IntruderAction action = null;
        System.out.println("Step " + step);
        System.out.println("View Angle in degrees: " + percepts);
        Angle maxRotationAngle = percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle();

        if (step == 0) {
            System.out.println("Recorded point a.");
            a = percepts.getTargetDirection();
            u = percepts.getScenarioIntruderPercepts().getMaxMoveDistanceIntruder();

            action = new Move(u);
        }
        else if (step == 1) {
            System.out.println("Recorded point b.");
            b = percepts.getTargetDirection();
            action = new Rotate(Angle.fromDegrees(180));
        }
        else if (step == 2) {
            action = new Move(u);
        }
        else if (step == 3) {
            if ((a.getDegrees() % 180) == (percepts.getTargetDirection().getDegrees() % 180)) System.out.println("Came back to point a");
            action = new Rotate(Angle.fromDegrees(90));
        }
        else if (step == 4) {
            action = new Move(u);
        }
        else if (step == 5) {
            System.out.println("Recorded point c.");
            c = percepts.getTargetDirection();
            this.targetLocation = MapBumf.targetLocationInUnits(a, b, c);
            System.out.println("Target location secured: (" + targetLocation.getX() + ", " + targetLocation.getY() + ") (in units of " + u.getValue() + ")");
            System.out.println("This translates into: (" + (targetLocation.getX() * u.getValue()) + ", " + (targetLocation.getY() * u.getValue()) + ").");
        }
        else {

            Point sourcePos = this.currentMap.getCurrentPosition();
            Point targetPos = new Point(Math.round(this.targetLocation.getX()) + sourcePos.getX(),Math.round(this.targetLocation.getY())+sourcePos.getY());


            //Initialize the path and then recompute it every 5 steps
            if(path == null || step%5==0) computePath(sourcePos, targetPos);
            //The target is not fount on the map, rotate to try again
            if(path == null) action = new Rotate(maxRotationAngle);
            else {
                Point subTarget;
                if (path.size() < 5) subTarget = new Point(targetPos.getX(), targetPos.getY());
                else subTarget = new Point(path.get(5).getPos().getX(), path.get(5).getPos().getY());

                double deltaX = Math.abs(sourcePos.getX() - subTarget.getX());
                double deltaY = Math.abs(sourcePos.getY() - subTarget.getY());
                Direction dir = Direction.fromRadians(Math.atan(deltaY / deltaX));
                if (dir.getDegrees() < 5) action = new Move(u);
                else if (Math.abs(dir.getDegrees()) > maxRotationAngle.getDegrees())
                    action = new Rotate(maxRotationAngle);
                else action = new Rotate(dir);
            }


        }
        step++;
        return action;
    }

    public void computePath(Point sourcePos, Point targetPos) {
        Graph graph = new Graph(this.currentMap.getCurrentMap());
        PathFinding finder = new PathFinding(graph);
        Node source = graph.getNode(sourcePos);
        Node target = graph.getNode(targetPos);
        if(target != null) {
            System.out.println("WARNING: Target not found");
            path = (LinkedList) finder.shortestPathDijkstra(source, target);
        }
        else path = null;
    }

}
