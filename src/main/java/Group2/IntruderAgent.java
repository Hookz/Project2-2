package Group2;

import Interop.Agent.Intruder;
import Interop.Action.*;
import Interop.Geometry.*;
import Interop.Percept.IntruderPercepts;
import Interop.Percept.Vision.ObjectPercept;
import Interop.Percept.Vision.ObjectPerceptType;


import java.util.*;

public class IntruderAgent implements Intruder{
    //Variable which is negative if the previous turn was to the left (i.e negative angle), positive if the previous angle was to the right and 0 if it was a move
    private int rotateFlag = 0;
    private int ID;
    private boolean avoidingGuard;
    private boolean avoidingWall;
    private int continuousAvoiding = 0;
    private boolean reachesTargetArea;

    public IntruderAgent(int ID){
        this.ID = ID;
    }

    @Override
    public IntruderAction getAction(IntruderPercepts percepts) {

        if(reachesTargetArea) return new NoAction();

        Set<ObjectPercept> objects = percepts.getVision().getObjects().getAll();
        HashMap<Integer, ObjectPercept> walls = new HashMap<>();
        HashMap<Integer, ObjectPercept> guards = new HashMap<>();


        int counter = 0;
        Point targetAreaLocation = new Point(0,0);


        for(ObjectPercept object : objects){
            if (object.getType() == ObjectPerceptType.Wall && counter >= 17 && counter <= 27) {
                walls.put(counter, object);
            }

            else if(object.getType() == ObjectPerceptType.Guard && counter >= 17 && counter <= 27) {
                guards.put(counter, object);
            }

            else if(object.getType() == ObjectPerceptType.TargetArea)
                targetAreaLocation = object.getPoint();

            counter++;
        }

        if(targetAreaLocation.getX() != 0 && targetAreaLocation.getY() !=0) {
            Distance targetAreaDistance = new Distance(targetAreaLocation, new Point(0,0));
            if(targetAreaDistance.getValue() < 2) {
                reachesTargetArea = true;
                return new Move(new Distance(2));
            }

        }


        //Make the agent sprint after rotating to avoid the guard
        if(avoidingGuard) {
            avoidingGuard = false;
            rotateFlag = 0;
            System.out.println("Sprint to avoid guard");
            return new Sprint(percepts.getScenarioIntruderPercepts().getMaxSprintDistanceIntruder());
        }

        //Make the agent move forward 3 times after rotating to avoid the wall
        if(avoidingWall || continuousAvoiding > 0) {
            if(avoidingWall) avoidingWall = false;
            if(continuousAvoiding > 0) {
                rotateFlag = 0;
                continuousAvoiding--;
            }
            System.out.println("Move forward after avoiding wall");
            return new Move(percepts.getScenarioIntruderPercepts().getMaxMoveDistanceIntruder());
        }



        int indexGuards = 0;
        if(guards.size() > 0) {
            Distance minDistance = new Distance(Double.MAX_VALUE);
            Iterator it = (new HashMap<>(guards)).entrySet().iterator();
            while(it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                Distance distanceToGuard = new Distance(new Point(0, 0), guards.get(pair.getKey()).getPoint());
                if (distanceToGuard.getValue() < minDistance.getValue()) {
                    minDistance = distanceToGuard;
                    indexGuards = (int) pair.getKey();
                }
                it.remove();
            }

            Angle rotation = avoidObjectAngle(guards.get(indexGuards), percepts, indexGuards);
            System.out.println("Avoiding a guard, rotation with angle: " +rotation.getDegrees());
            if (Math.abs(rotation.getDegrees()) != 45) avoidingGuard = true;
            return new Rotate(rotation);
        }

        int indexWalls = 0;
        if(walls.size() > 0) {
            Distance minDistance = new Distance(Double.MAX_VALUE);
            Iterator it = (new HashMap<>(walls)).entrySet().iterator();
            while(it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                Distance distanceToWall = new Distance(new Point(0, 0), walls.get(pair.getKey()).getPoint());
                if (distanceToWall.getValue() < minDistance.getValue()) {
                    minDistance = distanceToWall;
                    indexWalls = (int) pair.getKey();
                }
                it.remove();
            }

            Angle rotation = avoidObjectAngle(walls.get(indexWalls), percepts, indexWalls);
            System.out.println("Avoiding a wall, rotation with angle: " + rotation.getDegrees());
            if (Math.abs(rotation.getDegrees()) != 45) avoidingWall = true;
            return new Rotate(rotation);
        }


        //If the intruder isn't avoiding, make it aim for the target area
        Direction targetDirection = Direction.fromDegrees(percepts.getTargetDirection().getDegrees()%360);
        Angle rotationAngle = targetDirection.getDistance(Angle.fromDegrees(90));
        Angle maxRotationAngle = percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle();

        if(rotationAngle.getDegrees() > maxRotationAngle.getDegrees())
            rotationAngle = maxRotationAngle;


        if(targetDirection.getDegrees() < 90 && targetDirection.getDegrees() > 270)
            rotationAngle = Angle.fromDegrees(-rotationAngle.getDegrees());



        if(Math.abs(rotationAngle.getDegrees()) > 5) {
            if(rotationAngle.getDegrees() > 0 && rotateFlag >= 0) {
                rotateFlag ++;
                System.out.println("Degree check: " + rotationAngle.getDegrees());
                return new Rotate(rotationAngle);
            }
            else if(rotationAngle.getDegrees() < 0 && rotateFlag <= 0) {
                rotateFlag--;
                System.out.println("Degree check: " + rotationAngle.getDegrees());
                return new Rotate(rotationAngle);
            }
            else if(rotateFlag > 0) {
                rotateFlag++;
                System.out.println("Forced degree check: " + 45);
                return new Rotate(Angle.fromDegrees(percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getDegrees()));
            }
            else if(rotateFlag < 0){
                rotateFlag--;
                System.out.println("Forced degree check with angle: " + (-45));
                return new Rotate(Angle.fromDegrees(-1 * percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getDegrees()));
            }
            else {
                System.out.println("Rotated too many times, moving forward");
                rotateFlag = 0;
                return new Move(percepts.getScenarioIntruderPercepts().getMaxMoveDistanceIntruder());
            }
        }


        rotateFlag = 0;
        System.out.println("Moving forward");
        return new Move(percepts.getScenarioIntruderPercepts().getMaxMoveDistanceIntruder());

    }



    //Method which avoids objects by returning max rotation angle on the side the closest to the target area
    public Angle avoidObjectAngle(ObjectPercept objectPercept, IntruderPercepts percepts, int objectIndex) {

        Point object = objectPercept.getPoint();
        double rotationAngle = 0;
        int smallerAngleIndex = objectIndex;
        int largerAngleIndex = objectIndex;
        ArrayList<ObjectPercept> objectsList = new ArrayList<>(percepts.getVision().getObjects().getAll());

        Distance objectDistance= new Distance(object, new Point(0,0));

        boolean avoidFromRight = false;
        boolean avoidFromLeft = false;



        while(smallerAngleIndex > 0 && objectsList.get(smallerAngleIndex).getType() == objectsList.get(objectIndex).getType()) {
            smallerAngleIndex--;
        }
        int objectSizeLeft = objectIndex - smallerAngleIndex;

        while(largerAngleIndex < objectsList.size() && objectsList.get(largerAngleIndex).getType() == objectsList.get(objectIndex).getType()){
            largerAngleIndex++;
        }

        int objectSizeRight = largerAngleIndex - objectIndex;


        //If the agent is close to the object, rotate from the max rotation angle
        if(objectSizeLeft == objectSizeRight || objectDistance.getValue() < 2) {
            if(objectSizeLeft == objectSizeRight && objectPercept.getType() == ObjectPerceptType.Wall) {
                this.continuousAvoiding = 3;
                System.out.println("Wall is on whole field of view");
            }
            Distance firstRay = new Distance(objectsList.get(0).getPoint(), new Point(0,0));
            Distance lastRay = new Distance(objectsList.get(objectsList.size()-1).getPoint(), new Point(0,0));
            if(rotateFlag > 0) {
                rotateFlag++;
                return Angle.fromDegrees(percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getDegrees());
            }
            else if(rotateFlag < 0){
                rotateFlag--;
                return Angle.fromDegrees(-1 * percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getDegrees());
            }
            else {
                if(firstRay.getValue() > lastRay.getValue()) {
                    rotateFlag++;
                    return Angle.fromDegrees(percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getDegrees());
                }
                else {
                    rotateFlag--;
                    return Angle.fromDegrees(-1 * percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getDegrees());
                }
            }
        }
        //Avoid object from its left side
        else if(objectSizeLeft > objectSizeRight) {
            avoidFromLeft = true;
        }
        //Avoid object from its right side
        else {
            avoidFromRight = true;
        }


        if(avoidFromLeft) {
            //System.out.println("Avoiding from left");
            //If the leftmost vision ray still detects the object, turn from the max angle to avoid it
            if(smallerAngleIndex <= 0) {
                //System.out.println("Avoiding max angle");
                rotationAngle = (-1 * percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getDegrees());
            }
            //Rotate from the smallest angle for which we don't detect the object anymore
            else {
                //System.out.println("Smaller Angle Index: " + smallerAngleIndex +" ; Rotation angle: " +(smallerAngleIndex-23));
                rotationAngle = -24 + smallerAngleIndex;
            }
        }
        else if(avoidFromRight) {
            //System.out.println("Avoiding from right");
            //If the rightmost vision ray still detects the object, turn from the max angle to avoid it
            if(largerAngleIndex >= objectsList.size()) {
                //System.out.println("Avoiding max angle");
                rotationAngle = (percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getDegrees());
            }
            //Rotate from the smallest angle for which we don't detect the object anymore

            else {
                //System.out.println("Larger Angle Index: " + largerAngleIndex +" ; Rotation angle: " +(largerAngleIndex-23));
                rotationAngle = -21 + largerAngleIndex;
            }
        }

        if(rotationAngle > 0 && rotateFlag >= 0) {
            rotateFlag ++;
            return Angle.fromDegrees(rotationAngle);
        }
        else if(rotationAngle < 0 && rotateFlag <= 0) {
            rotateFlag--;
            return Angle.fromDegrees(rotationAngle);
        }
        else if(rotateFlag > 0) {
            System.out.println("Forced rotation");
            rotateFlag++;
            return Angle.fromDegrees(percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getDegrees());
        }
        else if(rotateFlag < 0 ){
            System.out.println("Forced rotation");
            rotateFlag--;
            return Angle.fromDegrees(-1 * percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getDegrees());
        }
        else {
            System.out.println("Rotated too many times, moving forward");
            rotateFlag = 0;
            return Angle.fromDegrees(0);
        }
    }
}
