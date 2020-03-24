package Group2;

import Interop.Agent.Intruder;
import Interop.Action.*;
import Interop.Geometry.*;
import Interop.Percept.IntruderPercepts;
import Interop.Percept.Vision.ObjectPercept;
import Interop.Percept.Vision.ObjectPerceptType;
import Interop.Percept.Vision.ObjectPercepts;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class IntruderAgent implements Intruder{
    private int ID;

    public IntruderAgent(int ID){
        this.ID = ID;
    }

    @Override
    public IntruderAction getAction(IntruderPercepts percepts) {
        Set<ObjectPercept> objects = percepts.getVision().getObjects().getAll();
        ArrayList<ObjectPercept> walls = new ArrayList<>();
        Angle rotationAngle = percepts.getTargetDirection();
        for(ObjectPercept object : objects){
            if (object.getType() == ObjectPerceptType.Wall){
                walls.add(object);
            }
        }

        for(ObjectPercept wall: walls) {
            Distance distanceToWall = new Distance(new Point(0,0), wall.getPoint());
            if(distanceToWall.getValue() > 5) {
                rotationAngle = Angle.fromDegrees(rotationAngle.getDegrees() +1);
            }
        }




//        if(walls.size()>3){
//            return new Rotate(Angle.fromRadians(0.5*Math.PI));
//        }


        double random = Math.random();
        if(random<0.95) {
            return new Move(percepts.getScenarioIntruderPercepts().getMaxMoveDistanceIntruder());
        }else{
            if (Math.random()<50) {
                return new Rotate(Angle.fromDegrees(Math.random() * percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getDegrees()));
            }else{
                return new Rotate(Angle.fromDegrees(-1 * Math.random() * percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getDegrees()));
            }
        }
    }
}
