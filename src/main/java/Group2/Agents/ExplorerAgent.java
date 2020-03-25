package Group2.Agents;

import Interop.Action.IntruderAction;
import Interop.Action.Move;
import Interop.Action.Rotate;
import Interop.Action.Sprint;
import Interop.Agent.Intruder;
import Interop.Geometry.Angle;
import Interop.Geometry.Direction;
import Interop.Percept.IntruderPercepts;
import Interop.Utils.Utils;

public class ExplorerAgent implements Intruder {

    int sprintCooldown = 0;
    @Override
    public IntruderAction getAction(IntruderPercepts percepts) {

        Direction target = percepts.getTargetDirection();
        System.out.println("Target Angle: "+target.getDegrees());
        if(target.getDegrees()>0.001||target.getDegrees()<359.999){
            if((Math.abs(target.getDegrees()-90))<=percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getDegrees()){
                double turn = -1*(90-target.getDegrees());
                System.out.println("Alligning with Target");
                System.out.println("Turning "+turn+" degrees to allign");
                return new Rotate(Angle.fromDegrees(turn));
            }else{
                if (target.getDegrees()<90) {
                    System.out.println("Max Rotation");
                    return new Rotate(Angle.fromDegrees(-1 * percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getDegrees()));
                }else{
                    System.out.println("Max Rotation");
                    return new Rotate(Angle.fromDegrees(percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getDegrees()));
                }
            }
        }else{
            if(sprintCooldown==0){
                sprintCooldown = percepts.getScenarioIntruderPercepts().getSprintCooldown();
                return new Sprint(percepts.getScenarioIntruderPercepts().getMaxSprintDistanceIntruder());
            }else{
                sprintCooldown--;
                return new Move(percepts.getScenarioIntruderPercepts().getMaxMoveDistanceIntruder());
            }
        }

    }
}
