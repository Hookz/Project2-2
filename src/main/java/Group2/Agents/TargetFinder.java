package Group2.Agents;

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

public class TargetFinder implements Intruder {
    Direction a = null;
    Direction b = null;
    Direction c = null;

    Distance u = null;
    int step = 0;
    @Override
    public IntruderAction getAction(IntruderPercepts percepts) {
        IntruderAction action = null;
        System.out.println("Step " + step);
        System.out.println("View Angle in degrees: " + percepts);
        if (step == 0) {
            System.out.println("Recorded point a.");
            a = percepts.getTargetDirection();
            u = percepts.getScenarioIntruderPercepts().getMaxMoveDistanceIntruder();

            action = new Move(u);
        }
        if (step == 1) {
            System.out.println("Recorded point b.");
            b = percepts.getTargetDirection();
            action = new Rotate(Angle.fromDegrees(180));
        }
        if (step == 2) {
            action = new Move(u);
        }
        if (step == 3) {
            if ((a.getDegrees() % 180) == (percepts.getTargetDirection().getDegrees() % 180)) System.out.println("Came back to point a");
            action = new Rotate(Angle.fromDegrees(90));
        }
        if (step == 4) {
            action = new Move(u);
        }
        if (step == 5) {
            System.out.println("Recorded point c.");
            c = percepts.getTargetDirection();
            Point targetLocation = MapBumf.targetLocationInUnits(a, b, c);
            System.out.println("Target location secured: (" + targetLocation.getX() + ", " + targetLocation.getY() + ") (in units of " + u.getValue() + ")");
            System.out.println("This translates into: (" + (targetLocation.getX() * u.getValue()) + ", " + (targetLocation.getY() * u.getValue()) + ").");
        }
        step++;
        return action;
    }
}
