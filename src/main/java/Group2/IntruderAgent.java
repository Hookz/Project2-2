package Group2;

import Interop.Agent.Intruder;
import Interop.Action.*;
import Interop.Geometry.*;
import Interop.Percept.IntruderPercepts;
import Interop.Percept.Vision.ObjectPercept;
import Interop.Percept.Vision.ObjectPerceptType;
import Interop.Percept.Vision.ObjectPercepts;
import Interop.Utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
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
        ArrayList<ObjectPercept> guards = new ArrayList<>();

        Direction targetDirection = Direction.fromDegrees(percepts.getTargetDirection().getDegrees()%360);
        Angle rotationAngle = targetDirection.getDistance(Angle.fromDegrees(90));
        Angle maxRotationAngle = percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle();

        if(rotationAngle.getDegrees() > maxRotationAngle.getDegrees())
            rotationAngle = maxRotationAngle;
        if(targetDirection.getDegrees() < 90 && targetDirection.getDegrees() > 270)
            rotationAngle = Angle.fromDegrees(-rotationAngle.getDegrees());

        for(ObjectPercept object : objects){
            if (object.getType() == ObjectPerceptType.Wall)
                walls.add(object);

            else if(object.getType() == ObjectPerceptType.Guard)
                guards.add(object);
        }

        /*
        if(guards.size() > 0) {
            Distance minDistance = new Distance(0);
            int minDistanceGuard = 0;
            for (int i = 0; i < guards.size(); i++) {
                Distance distanceToGuard = new Distance(new Point(0, 0), guards.get(i).getPoint());
                if (distanceToGuard.getValue() < minDistance.getValue()) {
                    minDistance = distanceToGuard;
                    minDistanceGuard = i;
                }
            }

            Angle rotation = avoidObjectAngle(guards.get(minDistanceGuard).getPoint(), percepts);
            System.out.println("Avoiding a guard, rotation with angle: " +rotation.getDegrees());
            return new Rotate(rotation);
        }

        if(walls.size() > 0) {
            Distance minDistance = new Distance(0);
            int minDistanceWall = 0;
            for (int i = 0; i < walls.size(); i++) {
                Distance distanceToWall = new Distance(new Point(0, 0), walls.get(i).getPoint());
                if (distanceToWall.getValue() < minDistance.getValue()) {
                    minDistance = distanceToWall;
                    minDistanceWall = i;
                }
            }

            //Only avoid the wall if it is "close enough", in this case half the range
            if(minDistance.getValue() < percepts.getVision().getFieldOfView().getRange().getValue()/2){
                Angle rotation = avoidObjectAngle(walls.get(minDistanceWall).getPoint(), percepts);
                System.out.println("Avoiding a wall, rotation with angle: " + rotation.getDegrees());
                return new Rotate(rotation);
            }
        } */


        while(Math.abs(rotationAngle.getDegrees()) > 5) {
            System.out.println("Rotation with angle: " +rotationAngle.getDegrees());
            return new Rotate(rotationAngle);
        }
        System.out.println("Moving forward");
        return new Move(percepts.getScenarioIntruderPercepts().getMaxMoveDistanceIntruder());








//        if(walls.size()>3){
//            return new Rotate(Angle.fromRadians(0.5*Math.PI));
//        }


        /*
        double random = Math.random();
        if(random<0.95) {
            return new Move(percepts.getScenarioIntruderPercepts().getMaxMoveDistanceIntruder());
        }else{
            if (Math.random()<50) {
                return new Rotate(Angle.fromDegrees(Math.random() * percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getDegrees()));
            }else{
                return new Rotate(Angle.fromDegrees(-1 * Math.random() * percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getDegrees()));
            }
        } */
    }


    //Method which avoids objects by returning max rotation angle on the side the closest to the target area
    public Angle avoidObjectAngle(Point object, IntruderPercepts percepts) {
        double rotationAngle = 0;
        Distance objectDistance = new Distance(new Point(0,0), object);
        Angle wallDirection = Angle.fromRadians(Math.acos(object.getX() / objectDistance.getValue()));
        double targetDirection = percepts.getTargetDirection().getDegrees()%360;
        if(targetDirection < 90 || targetDirection > 270) {
            rotationAngle = percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getDegrees()/2;
        }
        else rotationAngle = -1 * percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getDegrees()/2;

        return Angle.fromDegrees(rotationAngle);
    }
}
