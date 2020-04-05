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
    private boolean avoidOppositeRotation;
    private int moveForward = 0;
    private boolean reachesTargetArea;
    private boolean avoidingGuard;

    public IntruderAgent(int ID){
        this.ID = ID;
    }

    @Override
    public IntruderAction getAction(IntruderPercepts percepts) {

        //Stop the intruder if it reaches the target area
        if(reachesTargetArea) return new NoAction();

        Set<ObjectPercept> objects = percepts.getVision().getObjects().getAll();
        boolean directionIsObstructed = false;

        Point targetAreaLocation = new Point(0,0);
        int rayWithoutObstacle = 0;
        for(ObjectPercept object : objects){

            double deltaX = object.getPoint().getX();
            double distanceToObstacle = new Distance(object.getPoint(), new Point(0,0)).getValue();
            double objectAngle = Angle.fromRadians(Math.asin(deltaX/distanceToObstacle)).getDegrees();

            //Check if there are obstacles in the rays directly surrounding the direction (between -7 and 7 degrees)
            if(objectAngle < 7 && objectAngle > -7) {
                if (!object.getType().isSolid())
                    rayWithoutObstacle++;
            }


            if(object.getType() == ObjectPerceptType.TargetArea)
                targetAreaLocation = object.getPoint();

            if(objectAngle < 2 && objectAngle > -2 && object.getType().isSolid()) {
                directionIsObstructed = true;
            }
        }

        //Checks if the intruder is close to the target area, if it is bring it inside
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
            //System.out.println("Sprint to avoid guard");
            return new Sprint(percepts.getScenarioIntruderPercepts().getMaxSprintDistanceIntruder());
        }


        //If the field surrounding the direction is full with obstacles (walls or guards), make the agent rotate (add a small random value to avoid getting stuck in the same areas)
        if(rayWithoutObstacle == 0) {
            //System.out.println("-----------------------------Rotate to avoid obstacles-----------------------------");
            this.moveForward = 5;
            //Rotate to the right if the previous action was a move or a right rotation, else rotate left
            if(rotateFlag >= 0) {
                rotateFlag++;
                return new Rotate(Angle.fromDegrees(percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getDegrees() + Math.random()));
            }
            else {
                rotateFlag--;
                return new Rotate(Angle.fromDegrees(-1 * percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getDegrees() + Math.random()));
            }
        }

        //Make the agent move forward if it rotated more than 10 times in a row
        if(Math.abs(rotateFlag) > 10) this.moveForward = 1;

        //Make the agent move forward to avoid obstacles
        if(moveForward > 0 && !directionIsObstructed) {
            rotateFlag = 0;
            moveForward--;
            //System.out.println("-----------------------------Move forward to stop rotating-----------------------------");
            return new Move(percepts.getScenarioIntruderPercepts().getMaxMoveDistanceIntruder());
        }



        //Compute the target direction and convert it to the agent's coordinate system
        Direction targetDirection = Direction.fromDegrees(percepts.getTargetDirection().getDegrees()%360);
        Angle directionToTargetAngle = targetDirection.getDistance(Angle.fromDegrees(90));


        if(targetDirection.getDegrees() < 90 && targetDirection.getDegrees() > 270)
            directionToTargetAngle = Angle.fromDegrees(-directionToTargetAngle.getDegrees());


        Angle rotationAngle = chooseRotationAngle(directionToTargetAngle, objects, percepts.getVision().getFieldOfView().getViewAngle(),
                percepts.getVision().getFieldOfView().getRange(), percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle());


        if(Math.abs(rotationAngle.getDegrees()) > 1 && !avoidOppositeRotation) {
            return new Rotate(rotationAngle);
        }

        //Move forward towards target direction
        //System.out.println("-----------------------------Move forward-----------------------------");
        rotateFlag = 0;
        if(avoidOppositeRotation) {
            avoidOppositeRotation = false;
        }
        return new Move(percepts.getScenarioIntruderPercepts().getMaxMoveDistanceIntruder());

    }

    public Angle chooseRotationAngle(Angle directionToTargetAngle,  Set<ObjectPercept> objectPercepts, Angle viewAngle, Distance range, Angle maxRotationAngle) {

        HashMap<Angle, Double> rays = new HashMap<>();

        //For each ray, compute its angle with the target direction and the distance to its object
        //Give each ray a value, representing how "bad" it would be to rotate from the ray's angle
        for(ObjectPercept objectPercept: objectPercepts) {

            double deltaX = objectPercept.getPoint().getX();
            double distanceToObstacle = new Distance(objectPercept.getPoint(), new Point(0,0)).getValue();
            Angle rayAngle = Angle.fromRadians(Math.asin(deltaX/distanceToObstacle));

            //System.out.println("-------");
            //System.out.println("Ray angle: " +rayAngle.getDegrees());
            //System.out.println("Distance to obstacle: " +distanceToObstacle);
            Angle objectToTargetAngle = Angle.fromDegrees(Math.abs(directionToTargetAngle.getDegrees() - rayAngle.getDegrees()));

            //System.out.println("Object to target angle: " +objectToTargetAngle.getDegrees());
            double rayValue = 0;

            //System.out.println("Object type: " +objectPercept.getType());
            if((objectPercept.getType() == ObjectPerceptType.Wall || objectPercept.getType() == ObjectPerceptType.Guard)) {
                if (distanceToObstacle < range.getValue())
                    rayValue = 10000;
                else rayValue = distanceToObstacle * objectToTargetAngle.getDegrees();

            } else {
                distanceToObstacle = range.getValue();
                rayValue = distanceToObstacle * objectToTargetAngle.getDegrees();
            }

            //System.out.println("Ray value: " +rayValue);

            //The intruder is rotating to avoid a guard
            if(rayAngle.getDegrees() < 7 && rayAngle.getDegrees() > -7) {
                if(objectPercept.getType() == ObjectPerceptType.Guard) {
                    rayValue = 10000;
                    avoidingGuard = true;
                }
            }


            rays.put(rayAngle, rayValue);
        }



        //Select the ray with the minimum value and set the rotation angle to the ray's angle
        double minValue = Double.MAX_VALUE;
        Angle rotationAngle = Angle.fromDegrees(0);
        Iterator it = rays.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            Angle angle = (Angle) pair.getKey();
            double value = (double) pair.getValue();

            if(value < minValue) {
                rotationAngle = angle;
                minValue = value;
            }
            it.remove();
        }


        //Check if the target can be reached by rotating more
        //If yes, set the rotation angle to the target's direction (or to the max rotation angle if the target cannot be reached right away)
        if(rotationAngle.getDegrees() > 22 && directionToTargetAngle.getDegrees() > 22) {
            if(directionToTargetAngle.getDegrees() > maxRotationAngle.getDegrees()) {
                rotationAngle = maxRotationAngle;
            }
            else rotationAngle = directionToTargetAngle;
        }
        else if(rotationAngle.getDegrees() < -21 && directionToTargetAngle.getDegrees() < -21) {
            if(Math.abs(directionToTargetAngle.getDegrees()) > maxRotationAngle.getDegrees()) {
                rotationAngle = Angle.fromDegrees(-maxRotationAngle.getDegrees());
            }
            else rotationAngle = directionToTargetAngle;
        }

        //If there is a guard in front, rotate from a bigger angle to avoid him
        if(avoidingGuard) {
            while(Math.abs(rotationAngle.getDegrees()) < 10) {
                if(rotationAngle.getDegrees() < 0) rotationAngle = Angle.fromDegrees(rotationAngle.getDegrees()-1);
                else rotationAngle = Angle.fromDegrees(rotationAngle.getDegrees()+1);
            }
        }

        //Prevent "opposite" rotations to occur, i.e. to rotate continuously left then right
        //If the agent tries to do so, make it move forward
        if(rotationAngle.getDegrees() > 0 && rotateFlag >= 0) rotateFlag++;
        else if(rotationAngle.getDegrees() < 0 && rotateFlag <= 0)rotateFlag--;
        else {
            //System.out.println("-------------------------------------Avoid opposite rotation: moving forward------------------------------");
            this.avoidOppositeRotation = true;
        }

        //System.out.println("Rotation angle: " +rotationAngle.getDegrees());
        //System.out.println();
        //System.out.println();


        return rotationAngle;
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
                this.moveForward = 3;
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
