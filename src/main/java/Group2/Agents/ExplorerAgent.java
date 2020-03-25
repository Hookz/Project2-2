package Group2.Agents;

import Interop.Action.IntruderAction;
import Interop.Action.Move;
import Interop.Action.Rotate;
import Interop.Action.Sprint;
import Interop.Agent.Intruder;
import Interop.Geometry.Angle;
import Interop.Geometry.Direction;
import Interop.Percept.IntruderPercepts;
import Interop.Percept.Vision.ObjectPercept;
import Interop.Percept.Vision.ObjectPerceptType;
import Interop.Utils.Utils;

import java.util.Random;
import java.util.Set;

public class ExplorerAgent implements Intruder {

    int counter = 0;
    @Override
    public IntruderAction getAction(IntruderPercepts percepts) {

        Direction targetDirection = Direction.fromDegrees(percepts.getTargetDirection().getDegrees()%360);
        Angle rotationAngle = targetDirection.getDistance(Angle.fromDegrees(90));
        Angle maxRotationAngle = percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle();

        if(rotationAngle.getDegrees() > maxRotationAngle.getDegrees())
            rotationAngle = maxRotationAngle;


        if(targetDirection.getDegrees() < 90 && targetDirection.getDegrees() > 270)
            rotationAngle = Angle.fromDegrees(-rotationAngle.getDegrees());

        if(counter == 1){
            counter = 0;
            return new Rotate(Angle.fromDegrees(rotationAngle.getDegrees()));
        }else{
            counter++;
            return new Move(percepts.getScenarioIntruderPercepts().getMaxMoveDistanceIntruder());
        }
    }
}
