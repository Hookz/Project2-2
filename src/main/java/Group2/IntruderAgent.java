package Group2;

import Interop.Agent.Intruder;
import Interop.Action.*;
import Interop.Geometry.*;
import Interop.Percept.IntruderPercepts;
import Interop.Percept.Vision.ObjectPercept;
import Interop.Percept.Vision.ObjectPerceptType;
import Interop.Percept.Vision.ObjectPercepts;
import Interop.Utils.Utils;

import java.util.*;

public class IntruderAgent implements Intruder{
    private int rotateFlag = 0;
    private int ID;

    public IntruderAgent(int ID){
        this.ID = ID;
    }

    @Override
    public IntruderAction getAction(IntruderPercepts percepts) {

        Set<ObjectPercept> objects = percepts.getVision().getObjects().getAll();
        HashMap<Integer, ObjectPercept> walls = new HashMap<>();
        HashMap<Integer, ObjectPercept> guards = new HashMap<>();

        Direction targetDirection = Direction.fromDegrees(percepts.getTargetDirection().getDegrees()%360);
        Angle rotationAngle = targetDirection.getDistance(Angle.fromDegrees(90));
        Angle maxRotationAngle = percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle();

        if(rotationAngle.getDegrees() > maxRotationAngle.getDegrees())
            rotationAngle = maxRotationAngle;
        if(targetDirection.getDegrees() < 90 && targetDirection.getDegrees() > 270)
            rotationAngle = Angle.fromDegrees(-rotationAngle.getDegrees());



        int indexWalls = 0;
        int indexGuards = 0;
        int counter = 0;
        for(ObjectPercept object : objects){
            if (object.getType() == ObjectPerceptType.Wall && counter >= 17 && counter <= 27) {
                walls.put(indexWalls, object);
                indexWalls++;
            }

            else if(object.getType() == ObjectPerceptType.Guard && counter >= 17 && counter <= 27) {
                guards.put(indexGuards, object);
                indexGuards++;
            }
            counter++;
        }


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

            Angle rotation = avoidObjectAngle(guards.get(minDistanceGuard).getPoint(), percepts, minDistanceGuard);
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
                Angle rotation = avoidObjectAngle(walls.get(minDistanceWall).getPoint(), percepts, minDistanceWall);
                System.out.println("Avoiding a wall, rotation with angle: " + rotation.getDegrees());
                return new Rotate(rotation);
            }
        }


        while(Math.abs(rotationAngle.getDegrees()) > 5) {
            if(rotationAngle.getDegrees() > 0 && rotateFlag >= 0) {
                rotateFlag ++;
                System.out.println("Rotation with angle: " +rotationAngle.getDegrees());
                return new Rotate(rotationAngle);
            }
            else if(rotationAngle.getDegrees() < 0 && rotateFlag <= 0) {
                rotateFlag--;
                System.out.println("Rotation with angle: " +rotationAngle.getDegrees());
                return new Rotate(rotationAngle);
            }
            else if(rotateFlag > 0) {
                System.out.println("Rotation with angle: " +rotationAngle.getDegrees());
                return new Rotate(Angle.fromDegrees(percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getDegrees()));
            }
            else {
                System.out.println("Rotation with angle: " + rotationAngle.getDegrees());
                return new Rotate(Angle.fromDegrees(-1 * percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getDegrees()));
            }
        }


        rotateFlag = 0;
        System.out.println("Rotate flag: " +rotateFlag);
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
    public Angle avoidObjectAngle(Point object, IntruderPercepts percepts, int objectIndex) {

        double rotationAngle = 0;
        int smallerAngleIndex = objectIndex;
        int largerAngleIndex = objectIndex;
        ArrayList<ObjectPercept> objectsList = new ArrayList<>(percepts.getVision().getObjects().getAll());

        while(smallerAngleIndex > 0 && objectsList.get(smallerAngleIndex).getType() == objectsList.get(objectIndex).getType()) {
            smallerAngleIndex--;
        }
        int objectSizeLeft = objectIndex - smallerAngleIndex;

        while(largerAngleIndex < objectsList.size() && objectsList.get(largerAngleIndex).getType() == objectsList.get(objectIndex).getType()){
            largerAngleIndex++;
        }

        int objectSizeRight = largerAngleIndex - objectIndex;

        //Avoid object from its left side
        if(objectSizeLeft > objectSizeRight && rotateFlag <= 0) {
            //If the leftmost vision ray still detects the object, turn from the max angle to avoid it
            if(smallerAngleIndex <= 1) {
                rotateFlag--;
                rotationAngle = (-1 * percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getDegrees());
            }
            //Rotate from the smallest angle for which we don't detect the object anymore
            else {
                rotationAngle = -23 + smallerAngleIndex;
                if(rotationAngle > 0) rotateFlag++;
                else rotateFlag--;
            }
        }
        //Avoid object from its right side
        else if(rotateFlag >= 0){
            //If the rightmost vision ray still detects the object, turn from the max angle to avoid it
            if(largerAngleIndex >= objectsList.size()-1) {
                rotateFlag++;
                rotationAngle = (percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getDegrees());
            }
            //Rotate from the smallest angle for which we don't detect the object anymore

            else {
                rotationAngle = -23 + smallerAngleIndex;
                if(rotationAngle > 0) rotateFlag++;
                else rotateFlag--;
            }


        }
        else if (rotateFlag <= 0) {
            rotateFlag--;
            rotationAngle = (-1 * percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getDegrees());
        }

        return Angle.fromDegrees(rotationAngle);



    }
}
