package Group2.Agents;

import Interop.Action.IntruderAction;
import Interop.Action.Move;
import Interop.Action.Rotate;
import Interop.Action.Sprint;
import Interop.Agent.Intruder;
import Interop.Geometry.Angle;
import Interop.Geometry.Direction;
import Interop.Percept.IntruderPercepts;

public class ExplorerAgent implements Intruder {

    int sprintCooldown = 0;
    @Override
    public IntruderAction getAction(IntruderPercepts percepts) {

        Direction target = percepts.getTargetDirection();
        System.out.println("Target Angle: "+target.getDegrees());
        if(target.getDegrees()>0.1){
            if(target.getDegrees()<=percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getDegrees()){
                return new Rotate(Direction.fromDegrees(target.getDegrees()));
            }else{
                if (target.getDegrees()>0) {
                    return new Rotate(percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle());
                }else{
                    return new Rotate(Angle.fromDegrees(-1 * percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getDegrees()));
                }
            }
        }else{
            if(sprintCooldown==0){
                sprintCooldown = percepts.getScenarioIntruderPercepts().getSprintCooldown();
                return new Sprint(percepts.getScenarioIntruderPercepts().getMaxSprintDistanceIntruder());
            }else{
                return new Move(percepts.getScenarioIntruderPercepts().getMaxMoveDistanceIntruder());
            }
        }

    }
}
