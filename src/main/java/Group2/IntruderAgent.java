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
        Angle rotationAngle = percepts.getTargetDirection();
        for(ObjectPercept object : objects){
            if (object.getType() == ObjectPerceptType.Wall){
                walls.add(object);
            }
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


            Angle rotation = avoidWallAngle(walls.get(minDistanceWall).getPoint(), percepts);
            System.out.println("Rotation with angle: " +rotation.getDegrees());
            return new Rotate(rotation);
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

    public Angle avoidWallAngle(Point wall, IntruderPercepts percepts) {
        double rotationAngle = 0;
        Distance wallDistance = new Distance(new Point(0,0), wall);
        Angle wallDirection = Angle.fromRadians(Math.acos(wall.getX() / wallDistance.getValue()));
        if(percepts.getTargetDirection().getDegrees()%360 < wallDirection.getDegrees()%360) {
            rotationAngle = percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getDegrees();
        }
        else rotationAngle = -1 * percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getDegrees();

        return Angle.fromDegrees(rotationAngle);
    }
}
