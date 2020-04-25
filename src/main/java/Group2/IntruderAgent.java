package Group2;

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

    private ObjectPerceptType[][] currentMap; //Cell will be null if it hasn't been discovered
    private Point currentPosition;
    private Point currentMapBottomRight;

    private Angle currentAngle;

    public IntruderAgent(int ID){
        this.ID = ID;
        this.currentMap = new ObjectPerceptType[100][100];
        this.currentPosition = new Point(0,0);
        this.currentMapBottomRight = new Point(100,100);
        this.currentAngle = Angle.fromDegrees(0);
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
            updateCurrentMap(action, percepts);
            return action;
        }



        Direction targetDirection = Direction.fromDegrees(percepts.getTargetDirection().getDegrees()%360);


        Angle rotationAngle = chooseRotationAngle(targetDirection, objects, percepts.getVision().getFieldOfView().getViewAngle(),
                percepts.getVision().getFieldOfView().getRange(), percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle());


        if(Math.abs(rotationAngle.getDegrees()) > 1.5) {
            System.out.println("Rotation of angle: " +rotationAngle.getDegrees());
            IntruderAction action = new Rotate(rotationAngle);
            updateCurrentMap(action, percepts);
            return action;
        }

        //Move forward towards target direction
        System.out.println("-----------------------------Move forward-----------------------------");
        rotateFlag = 0;
        IntruderAction action = new Move(percepts.getScenarioIntruderPercepts().getMaxMoveDistanceIntruder());
        updateCurrentMap(action, percepts);
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

        //Computes the angle between the target area and the y-axis (=intruder's direction)
        Angle directionToTargetAngle = targetDirection.getDistance(Angle.fromDegrees(90));
        if(targetDirection.getDegrees() < 90 && targetDirection.getDegrees() > 270)
            directionToTargetAngle = Angle.fromDegrees(-directionToTargetAngle.getDegrees());


        //For each ray, compute its angle with the target direction and the distance to its object
        //Give each ray a value, the f(x) the value of the function we try to minimize
        for(ObjectPercept objectPercept: objectPercepts) {

            double deltaX = objectPercept.getPoint().getX();
            double distanceToObstacle = new Distance(objectPercept.getPoint(), new Point(0,0)).getValue();
            Angle rayAngle = Angle.fromRadians(Math.asin(deltaX/distanceToObstacle));

            //System.out.println("-------");
            //System.out.println("Ray angle: " +rayAngle.getDegrees());
            //System.out.println("Distance to obstacle: " +distanceToObstacle);
            Angle objectToTargetAngle = Angle.fromDegrees(Math.abs(directionToTargetAngle.getDegrees() - rayAngle.getDegrees()));

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


        //AVOIDING OBJECTS

        // If the field surrounding the direction is full with obstacles (walls or guards), make the agent rotate from its maximum angle (add a small random value to avoid getting stuck in the same areas)
        if(rayWithoutObstacle == 0) {
            //System.out.println("-----------------------------Rotate to avoid obstacles-----------------------------");
            this.moveForward = 5;
            //Rotate to the right if the previous action was a move or a right rotation, else rotate left
            if (rotateFlag >= 0) {
                rotateFlag++;
                return Angle.fromDegrees(maxRotationAngle.getDegrees() + Math.random());
            } else {
                rotateFlag--;
                return Angle.fromDegrees(maxRotationAngle.getDegrees() + Math.random());
            }
        }

        //Make the agent move forward if it rotated more than 10 times in a row
        if(Math.abs(rotateFlag) > 10 && this.moveForward == 0) this.moveForward = 1;

        //Make the agent move forward to avoid obstacles
        if(moveForward > 0 && !directionIsObstructed) {
            rotateFlag = 0;
            moveForward--;
            //System.out.println("-----------------------------Move forward to stop rotating-----------------------------");
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
            //System.out.println("-------------------------------------Avoid opposite rotation: moving forward------------------------------");
            rotationAngle  = Angle.fromDegrees(0);
        }

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


    /**
     * Method that keeps track of the current map discovered by the agent
     * @param action, the action the agent just took
     * @param percepts, the set of percepts of the agent
     */
    public void updateCurrentMap(Action action, Percepts percepts) {

        if(action instanceof Rotate) {
            currentAngle = Angle.fromDegrees((currentAngle.getDegrees() + ((Rotate) action).getAngle().getDegrees())%360);
            while(currentAngle.getDegrees() < 0) currentAngle = Angle.fromDegrees(currentAngle.getDegrees() + 360);
        }


        if(action instanceof Move || action instanceof Sprint) {
            Distance distance = new Distance(0);
            if(action instanceof Move) distance = ((Move) action).getDistance();
            else distance = ((Sprint) action).getDistance();

            //Change the sign of y to keep the y-axis pointing downwards
            Point changeInPosition = new Point(Math.cos(currentAngle.getRadians())*distance.getValue(), -Math.sin(currentAngle.getRadians())*distance.getValue());
            Point newPosition = new Point(currentPosition.getX() + changeInPosition.getX(), currentPosition.getY()+changeInPosition.getY());



            int shiftInX = 0;
            int shiftInY = 0;

            //Increase the size of the current map if the agent is outside
            if(newPosition.getX() <= 0) shiftInX = -1;
            else if(newPosition.getX() >= currentMapBottomRight.getX()) shiftInX = 1;

            if(newPosition.getY() <= 0) shiftInY = -1;
            else if(newPosition.getY() >= currentMapBottomRight.getY()) shiftInY = 1;

            extendCurrentMap(shiftInX, shiftInY, newPosition);



            /*
            //Add all the points in the range of view to the map
            for(int i=0; i<currentMap.length; i++) {
                for (int j=0; j<currentMap[0].length; j++) {

                    //Set the map point's coordinate system to origin = agent to check if it is in the agent's field of view
                    Point mapPoint = new Point(currentPosition.getX() - j, currentPosition.getY() - i);

                    if(percepts.getVision().getFieldOfView().isInView(mapPoint)) {

                        double distanceAgentToPoint = new Distance(new Point(0,0), mapPoint).getValue();
                        Angle pointAngle = Angle.fromRadians(Math.asin(deltaX/distanceAgentToPoint));

                        for(ObjectPercept objectPercept : percepts.getVision().getObjects().getAll()) {
                            double distanceAgentToObstacle = new Distance(objectPercept.getPoint(), new Point(0,0)).getValue();
                            Angle rayAngle = Angle.fromRadians(Math.asin(deltaX/distanceAgentToObstacle));
                            if(pointAngle.getDegrees() < rayAngle.getDegrees() + 0.5 && pointAngle.getDegrees() > rayAngle.getDegrees() - 0.5) {

                                //If the point is on a ray without
                                if(distanceAgentToPoint < distanceAgentToObstacle) currentMap[i][j] = ObjectPerceptType.EmptySpace;
                                else if(distanceAgentToPoint == distanceAgentToObstacle) currentMap[i][j] = objectPercept.getType();
                            }
                        }
                    }
                }
            }*/



            //Add all the points in the range of view to the map
            for(ObjectPercept objectPercept: percepts.getVision().getObjects().getAll()) {

                Point objectPoint = new Point(objectPercept.getPoint().getX(), objectPercept.getPoint().getY());


                if(currentAngle.getDegrees() > 180 && currentAngle.getDegrees() < 360) {
                    objectPoint = new Point(-objectPoint.getX(), -objectPoint.getY());
                }

                double distanceToObject = new Distance(objectPoint, new Point(0,0)).getValue();
                double deltaX = Math.abs(objectPoint.getX());
                Angle objectAngle = Angle.fromRadians(Math.asin(deltaX/distanceToObject));
                while(objectAngle.getDegrees() < 0) objectAngle = Angle.fromDegrees(objectAngle.getDegrees()+360);
                if(objectAngle.getDegrees() < 90 || objectAngle.getDegrees() > 270) objectAngle = Angle.fromDegrees(-objectAngle.getDegrees());


                //Angle of the object point in the map's coordinate system
                Angle angleInMap = Angle.fromDegrees(currentAngle.getDegrees() + objectAngle.getDegrees());


                int objectXInMap = (int) Math.round(currentPosition.getX() + Math.cos(angleInMap.getRadians())*distanceToObject);
                int objectYInMap = (int) Math.round(currentPosition.getY() - Math.sin(angleInMap.getRadians())*distanceToObject);

                shiftInX = 0;
                shiftInY = 0;

                if(objectXInMap <= 0) {
                    shiftInX = -1;
                    objectXInMap += currentMap[0].length;
                }
                else if(objectXInMap >= currentMapBottomRight.getX()) shiftInX = 1;


                if(objectYInMap <= 0) {
                    shiftInY = -1;
                    objectYInMap += currentMap.length;
                }
                else if(objectYInMap >= currentMapBottomRight.getY()) shiftInY = 1;

                extendCurrentMap(shiftInX, shiftInY, new Point(currentPosition.getX(), currentPosition.getY()));

                //Do not add guards to the map as they are not static
                if(objectPercept.getType() != ObjectPerceptType.Guard) {
                    currentMap[objectYInMap][objectXInMap] = objectPercept.getType();
                }
                //System.out.println("Add in Map at [" + objectXInMap +", " + objectYInMap +"]: " +objectPercept.getType());


                //Set all points between the object percept point and the agent to empty spaces
                for(int i = 1; i < (int) distanceToObject; i++) {
                    double x = Math.cos(angleInMap.getRadians()) * i;
                    double y = Math.sin(angleInMap.getRadians()) * i;

                    int xInMap = (int) Math.round(currentPosition.getX() + x);
                    int yInMap = (int) Math.round(currentPosition.getY() - y);
                    //System.out.println("Add in Map at [" + xInMap +", " + yInMap +"] EmptySpace");
                    if(currentMap[yInMap][xInMap] == null) currentMap[yInMap][xInMap] = ObjectPerceptType.EmptySpace;
                }

            }
        }
    }


    /**
     * Method that increases the size of the current map that the agent is keeping track of and updates the agent's position accordingly
     * @param x = -1 if the map needs to be extended on the left, = 1 if it is on the right
     * @param y = -1 if the map needs to be extended on the top, = -1 if it is on the bottom
     * @param newPosition, the position the agent needs to be updated to if it moved (set to current position if the agent isn't moving)
     */
    public void extendCurrentMap(int x, int y, Point newPosition) {
        //Variables keeping track of change in size of the map
        int shiftInX = 0;
        int shiftInY = 0;

        //Agent/ point in vision is on the left of the current known map, extend the map to that area and shift all the points to the left
        if(x == -1) {
            //System.out.println("Extend map to left");
            ObjectPerceptType[][] newMap = new ObjectPerceptType[currentMap.length][currentMap[0].length * 2];
            shiftInX = currentMap[0].length;
            for(int i=0; i<currentMap.length; i++) {
                for(int j=0; j<currentMap[0].length ;j++) {
                    newMap[i][j+shiftInX] = currentMap[i][j];
                }
            }
            currentMap = newMap;
        }


        //Agent/ point in vision is on the right of the current known map, extend the map to that area
        if(x == 1) {
            //System.out.println("Extend map to right");
            ObjectPerceptType[][] newMap = new ObjectPerceptType[currentMap.length][currentMap[0].length * 2];
            for(int i=0; i<currentMap.length; i++) {
                for(int j=0; j<currentMap[0].length ;j++) {
                    newMap[i][j] = currentMap[i][j];
                }
            }
            currentMap = newMap;
        }

        //Agent/ point in vision is above the current known map, extend the map to that area and shift all points to the top
        if(y == -1) {
            //System.out.println("Extend map to top");
            ObjectPerceptType[][] newMap = new ObjectPerceptType[currentMap.length * 2][currentMap[0].length];
            shiftInY = currentMap.length;
            for(int i=0; i<currentMap.length; i++) {
                for(int j=0; j<currentMap[0].length ;j++) {
                    newMap[i+shiftInY][j] = currentMap[i][j];
                }
            }
            currentMap = newMap;
        }

        //Agent/ point in vision is under the current known map, extend the map to that area
        if(y == 1) {
            //System.out.println("Extend map to bottom");
            ObjectPerceptType[][] newMap = new ObjectPerceptType[currentMap.length * 2][currentMap[0].length];
            for(int i=0; i<currentMap.length; i++) {
                for(int j=0; j<currentMap[0].length ;j++) {
                    newMap[i][j] = currentMap[i][j];
                }
            }
            currentMap = newMap;
        }

        currentPosition = new Point(newPosition.getX(), newPosition.getY());
        if(x == -1) currentPosition = new Point(currentPosition.getX() + shiftInX, currentPosition.getY());
        if(y == -1) currentPosition = new Point(currentPosition.getX(), currentPosition.getY() + shiftInY);

        currentMapBottomRight = new Point(currentMapBottomRight.getX() +shiftInX, currentMapBottomRight.getY()+shiftInY);


    }

    public ObjectPerceptType[][] getCurrentMap() {
        return currentMap;
    }
}
