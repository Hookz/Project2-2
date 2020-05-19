package Group2;

import Group2.Map.GridMap;
import Interop.Agent.Intruder;
import Interop.Action.*;
import Interop.Geometry.*;
import Interop.Percept.IntruderPercepts;
import Interop.Percept.Percepts;
import Interop.Percept.Scenario.ScenarioPercepts;
import Interop.Percept.Vision.ObjectPercept;
import Interop.Percept.Vision.ObjectPerceptType;


import java.util.*;

public class IntruderAgent implements Intruder{
    //Variable which is negative if the previous turn was to the left (i.e negative angle), positive if the previous angle was to the right and 0 if it was a move
    private int rotateFlag = 0;
    private int ID;
    private int moveForward = 0;
    private boolean reachesTargetArea;
    private boolean avoidingGuard;

    private GridMap currentMap;


    public IntruderAgent(int ID){
        this.ID = ID;
        this.currentMap = new GridMap();
    }

    @Override
    public IntruderAction getAction(IntruderPercepts percepts) {



        //Stop the intruder if it reaches the target area
        if(reachesTargetArea) return new NoAction();

        Set<ObjectPercept> objects = percepts.getVision().getObjects().getAll();

        Point targetAreaLocation = new Point(0,0);



        //Check if the target area is within the vision field
        for(ObjectPercept object : objects){
            if(object.getType() == ObjectPerceptType.TargetArea)
                targetAreaLocation = object.getPoint();
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
            System.out.println("Sprint to avoid guard");
            IntruderAction action = new Sprint(percepts.getScenarioIntruderPercepts().getMaxSprintDistanceIntruder());
            this.currentMap.updateMap(action, percepts);
            return action;
        }



        Direction targetDirection = Direction.fromDegrees(percepts.getTargetDirection().getDegrees()%360);


        Angle rotationAngle = chooseRotationAngle(targetDirection, objects, percepts.getVision().getFieldOfView().getViewAngle(),
                percepts.getVision().getFieldOfView().getRange(), percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle());


        if(Math.abs(rotationAngle.getDegrees()) > 1.5) {
            System.out.println("Rotation of angle: " +rotationAngle.getDegrees());
            IntruderAction action = new Rotate(rotationAngle);
            this.currentMap.updateMap(action, percepts);
            return action;
        }

        //Move forward towards target direction
        System.out.println("-----------------------------Move forward-----------------------------");
        rotateFlag = 0;
        IntruderAction action = new Move(percepts.getScenarioIntruderPercepts().getMaxMoveDistanceIntruder());
        this.currentMap.updateMap(action, percepts);
        return action;
    }

    /**
     * Gradient descent algorithm giving the locally most optimal angle of rotation
     * Each ray in the vision field is given an optimality value based on:
     *      - If there is an obstacle (wall or guard)
     *      - The angle distance between the object and the target area
     *      - The presence of a guard near that ray
     * @param targetDirection the angle of the target direction
     * @param objectPercepts the set of object percepts of the intruder at that moment
     * @param viewAngle the vision angle of the intruder
     * @param range the vision range of the intruder
     * @param maxRotationAngle the maximum rotation angle of the intruder
     * @return the most optimal angle from which the intruder should rotate
     */
    public Angle chooseRotationAngle(Angle targetDirection,  Set<ObjectPercept> objectPercepts, Angle viewAngle, Distance range, Angle maxRotationAngle) {

        HashMap<Angle, Double> rays = new HashMap<>();
        boolean directionIsObstructed = false;
        int rayWithoutObstacle = 0;




        //For each ray, compute its angle with the target direction and the distance to its object
        //Give each ray a value, the f(x) the value of the function we try to minimize
        for(ObjectPercept objectPercept: objectPercepts) {

            double deltaX = objectPercept.getPoint().getX();
            double distanceToObstacle = new Distance(objectPercept.getPoint(), new Point(0,0)).getValue();
            Angle rayAngle = Angle.fromRadians(Math.asin(deltaX/distanceToObstacle));

            //System.out.println("-------");
            //System.out.println("Ray angle: " +rayAngle.getDegrees());
            //System.out.println("Distance to obstacle: " +distanceToObstacle);
            Angle objectToTargetAngle = Angle.fromDegrees(Math.abs(targetDirection.getDegrees() - rayAngle.getDegrees()));

            //Check if there are obstacles in the rays directly surrounding the direction (between -7 and 7 degrees)
            if(rayAngle.getDegrees() < 7 && rayAngle.getDegrees() > -7) {
                if (!objectPercept.getType().isSolid())
                    rayWithoutObstacle++;
            }

            //Check if there are obstacles in the direction the agent is facing
            if(rayAngle.getDegrees() < 2 && rayAngle.getDegrees() > -2 && objectPercept.getType().isSolid()) {
                directionIsObstructed = true;
            }

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
        if(rotationAngle.getDegrees() > 22 && targetDirection.getDegrees() > 22) {
            if(targetDirection.getDegrees() > maxRotationAngle.getDegrees()) {
                rotationAngle = maxRotationAngle;
            }
            else rotationAngle = targetDirection;
        }
        else if(rotationAngle.getDegrees() < -21 && targetDirection.getDegrees() < -21) {
            if(Math.abs(targetDirection.getDegrees()) > maxRotationAngle.getDegrees()) {
                rotationAngle = Angle.fromDegrees(-maxRotationAngle.getDegrees());
            }
            else rotationAngle = targetDirection;
        }

        //If there is a guard in front, rotate from a bigger angle to avoid him
        if(avoidingGuard) {
            while(Math.abs(rotationAngle.getDegrees()) < 10) {
                if(rotationAngle.getDegrees() < 0) rotationAngle = Angle.fromDegrees(rotationAngle.getDegrees()-1);
                else rotationAngle = Angle.fromDegrees(rotationAngle.getDegrees()+1);
            }
        }


        //AVOIDING OBJECTS

        // If the field surrounding the direction is full with obstacles (walls or guards), make the agent rotate from its maximum angle (add a small random value to avoid getting stuck in the same areas)
        if(rayWithoutObstacle == 0) {
            System.out.println("-----------------------------Rotate to avoid obstacles-----------------------------");
            //Distance from which we force the agent to move forward to avoid an obstacle
            this.moveForward = 5;
            //Rotate to the right if the previous action was a move or a right rotation, else rotate left
            if (rotateFlag >= 0) {
                rotateFlag++;
                return Angle.fromDegrees(maxRotationAngle.getDegrees());
            } else {
                rotateFlag--;
                return Angle.fromDegrees(maxRotationAngle.getDegrees());
            }
        }

        //Make the agent move forward if it rotated more than 10 times in a row
        if(Math.abs(rotateFlag) > 10 && this.moveForward == 0) this.moveForward = 1;

        //Make the agent move forward to avoid obstacles
        if(moveForward > 0 && !directionIsObstructed) {
            rotateFlag = 0;
            moveForward--;
            System.out.println("-----------------------------Move forward to stop rotating-----------------------------");
            rotationAngle = Angle.fromDegrees(0);
        }

        //System.out.println("Rotation angle: " +rotationAngle.getDegrees());
        //System.out.println();
        //System.out.println();


        //Set the flag to know on which side the previous rotation was made
        if(rotationAngle.getDegrees() > 0 && rotateFlag >= 0) rotateFlag++;
        else if(rotationAngle.getDegrees() < 0 && rotateFlag <= 0)rotateFlag--;
            //Prevent opposite rotations to occur (i.e. rotating to the left then to the right)
        else {
            System.out.println("-------------------------------------Avoid opposite rotation: moving forward------------------------------");
            rotationAngle  = Angle.fromDegrees(0);
        }

        return rotationAngle;
    }




    //Method which avoids objects by returning max rotation angle on the side the closest to the target area
//    public Angle avoidObjectAngle(ObjectPercept objectPercept, IntruderPercepts percepts, int objectIndex) {
//
//        Point object = objectPercept.getPoint();
//        double rotationAngle = 0;
//        int smallerAngleIndex = objectIndex;
//        int largerAngleIndex = objectIndex;
//        ArrayList<ObjectPercept> objectsList = new ArrayList<>(percepts.getVision().getObjects().getAll());
//
//        Distance objectDistance= new Distance(object, new Point(0,0));
//
//        boolean avoidFromRight = false;
//        boolean avoidFromLeft = false;
//
//
//
//        while(smallerAngleIndex > 0 && objectsList.get(smallerAngleIndex).getType() == objectsList.get(objectIndex).getType()) {
//            smallerAngleIndex--;
//        }
//        int objectSizeLeft = objectIndex - smallerAngleIndex;
//
//        while(largerAngleIndex < objectsList.size() && objectsList.get(largerAngleIndex).getType() == objectsList.get(objectIndex).getType()){
//            largerAngleIndex++;
//        }
//
//        int objectSizeRight = largerAngleIndex - objectIndex;
//
//
//        //If the agent is close to the object, rotate from the max rotation angle
//        if(objectSizeLeft == objectSizeRight || objectDistance.getValue() < 2) {
//            if(objectSizeLeft == objectSizeRight && objectPercept.getType() == ObjectPerceptType.Wall) {
//                this.moveForward = 3;
//                System.out.println("Wall is on whole field of view");
//            }
//            Distance firstRay = new Distance(objectsList.get(0).getPoint(), new Point(0,0));
//            Distance lastRay = new Distance(objectsList.get(objectsList.size()-1).getPoint(), new Point(0,0));
//            if(rotateFlag > 0) {
//                rotateFlag++;
//                return Angle.fromDegrees(percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getDegrees());
//            }
//            else if(rotateFlag < 0){
//                rotateFlag--;
//                return Angle.fromDegrees(-1 * percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getDegrees());
//            }
//            else {
//                if(firstRay.getValue() > lastRay.getValue()) {
//                    rotateFlag++;
//                    return Angle.fromDegrees(percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getDegrees());
//                }
//                else {
//                    rotateFlag--;
//                    return Angle.fromDegrees(-1 * percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getDegrees());
//                }
//            }
//        }
//        //Avoid object from its left side
//        else if(objectSizeLeft > objectSizeRight) {
//            avoidFromLeft = true;
//        }
//        //Avoid object from its right side
//        else {
//            avoidFromRight = true;
//        }
//
//
//        if(avoidFromLeft) {
//            //System.out.println("Avoiding from left");
//            //If the leftmost vision ray still detects the object, turn from the max angle to avoid it
//            if(smallerAngleIndex <= 0) {
//                //System.out.println("Avoiding max angle");
//                rotationAngle = (-1 * percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getDegrees());
//            }
//            //Rotate from the smallest angle for which we don't detect the object anymore
//            else {
//                //System.out.println("Smaller Angle Index: " + smallerAngleIndex +" ; Rotation angle: " +(smallerAngleIndex-23));
//                rotationAngle = -24 + smallerAngleIndex;
//            }
//        }
//        else if(avoidFromRight) {
//            //System.out.println("Avoiding from right");
//            //If the rightmost vision ray still detects the object, turn from the max angle to avoid it
//            if(largerAngleIndex >= objectsList.size()) {
//                //System.out.println("Avoiding max angle");
//                rotationAngle = (percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getDegrees());
//            }
//            //Rotate from the smallest angle for which we don't detect the object anymore
//
//            else {
//                //System.out.println("Larger Angle Index: " + largerAngleIndex +" ; Rotation angle: " +(largerAngleIndex-23));
//                rotationAngle = -21 + largerAngleIndex;
//            }
//        }
//
//        if(rotationAngle > 0 && rotateFlag >= 0) {
//            rotateFlag ++;
//            return Angle.fromDegrees(rotationAngle);
//        }
//        else if(rotationAngle < 0 && rotateFlag <= 0) {
//            rotateFlag--;
//            return Angle.fromDegrees(rotationAngle);
//        }
//        else if(rotateFlag > 0) {
//            System.out.println("Forced rotation");
//            rotateFlag++;
//            return Angle.fromDegrees(percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getDegrees());
//        }
//        else if(rotateFlag < 0 ){
//            System.out.println("Forced rotation");
//            rotateFlag--;
//            return Angle.fromDegrees(-1 * percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getDegrees());
//        }
//        else {
//            System.out.println("Rotated too many times, moving forward");
//            rotateFlag = 0;
//            return Angle.fromDegrees(0);
//        }
//    }


    public GridMap getCurrentMap() {
        return currentMap;
    }
}
