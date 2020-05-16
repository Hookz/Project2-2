package Group2.Map;

import Interop.Action.Action;
import Interop.Action.Move;
import Interop.Action.Rotate;
import Interop.Action.Sprint;
import Interop.Geometry.Angle;
import Interop.Geometry.Distance;
import Interop.Geometry.Point;
import Interop.Percept.Percepts;
import Interop.Percept.Vision.ObjectPercept;
import Interop.Percept.Vision.ObjectPerceptType;

public class GridMap {

    private Point currentPosition;
    private Point currentMapBottomRight;
    private Angle currentAngle;

    private ObjectPerceptType[][] currentMap; //Cell will be null if it hasn't been discovered

    public GridMap() {
        this.currentMap = new ObjectPerceptType[100][100];
        this.currentPosition = new Point(0,0);
        this.currentMapBottomRight = new Point(100,100);
        this.currentAngle = Angle.fromDegrees(0);
    }

    /**
     * Method that keeps track of the current map discovered by the agent
     * @param action, the action the agent just took
     * @param percepts, the set of percepts of the agent
     */
    public void updateMap(Action action, Percepts percepts) {

        //Length from which we extend the map in case the agent goes outside
        int shiftLength = 50;

        //Reset the map if the agent just got teleported
        if(percepts.getAreaPercepts().isJustTeleported()) resetParameters();

        //Update the direction angle
        if(action instanceof Rotate) {
            currentAngle = Angle.fromDegrees((currentAngle.getDegrees() + ((Rotate) action).getAngle().getDegrees())%360);
            while(currentAngle.getDegrees() < 0) currentAngle = Angle.fromDegrees(currentAngle.getDegrees() + 360);
        }

        //Update the agent's position on the map
        if(action instanceof Move || action instanceof Sprint) {
            Distance distance;
            if (action instanceof Move) distance = ((Move) action).getDistance();
            else distance = ((Sprint) action).getDistance();

            //Change the sign of y to keep the y-axis pointing downwards
            Point changeInPosition = new Point(Math.cos(currentAngle.getRadians()) * distance.getValue(), -Math.sin(currentAngle.getRadians()) * distance.getValue());
            Point newPosition = new Point(currentPosition.getX() + changeInPosition.getX(), currentPosition.getY() + changeInPosition.getY());


            int shiftInX = 0;
            int shiftInY = 0;

            //Increase the size of the current map if the agent is outside
            if (newPosition.getX() <= 0) shiftInX = -1;
            else if (newPosition.getX() >= currentMapBottomRight.getX()) shiftInX = 1;

            if (newPosition.getY() <= 0) shiftInY = -1;
            else if (newPosition.getY() >= currentMapBottomRight.getY()) shiftInY = 1;

            extendMap(shiftInX, shiftInY, newPosition, shiftLength);
        }


        //Add all the points in the range of view of the agent to the map
        for(ObjectPercept objectPercept: percepts.getVision().getObjects().getAll()) {

            //Object point in the agent's cartesian system (agent is at (0,0))
            Point objectPoint = new Point(objectPercept.getPoint().getX(), objectPercept.getPoint().getY());


            double distanceToObject = new Distance(objectPoint, new Point(0,0)).getValue();
            double deltaX = Math.abs(objectPoint.getX());
            //Angle between object point and agent's direction
            Angle objectAngle = Angle.fromRadians(Math.asin(deltaX/distanceToObject));
            if(objectPoint.getX() < 0) objectAngle = Angle.fromDegrees(-objectAngle.getDegrees());


            //Angle of the object point in the map's coordinate system
            Angle angleInMap = Angle.fromDegrees(currentAngle.getDegrees() + objectAngle.getDegrees());

            //Coordinates of the object point in the map's coordinate system
            int objectXInMap = (int) Math.round(currentPosition.getX() + Math.cos(angleInMap.getRadians())*distanceToObject);
            int objectYInMap = (int) Math.round(currentPosition.getY() - Math.sin(angleInMap.getRadians())*distanceToObject);


            //Extend the map if the observed point is outside
            int shiftInX = 0;
            int shiftInY = 0;

            if(objectXInMap <= 0) {
                shiftInX = -1;
                objectXInMap += shiftLength;
            }
            else if(objectXInMap >= currentMapBottomRight.getX()) shiftInX = 1;
            if(objectYInMap <= 0) {
                shiftInY = -1;
                objectYInMap += shiftLength;
            }
            else if(objectYInMap >= currentMapBottomRight.getY()) shiftInY = 1;
            extendMap(shiftInX, shiftInY, new Point(currentPosition.getX(), currentPosition.getY()), shiftLength);



            //Add the observed object to the map (except if it is a guard as they are not static)
            //Only add object to the map if it hasn't been added before
            if(objectPercept.getType() != ObjectPerceptType.Guard && currentMap[objectYInMap][objectXInMap] == null) {
                currentMap[objectYInMap][objectXInMap] = objectPercept.getType();
            }
            //System.out.println("Add in Map at [" + objectXInMap +", " + objectYInMap +"]: " +objectPercept.getType());


            //Set all points between the object percept point and the agent to empty spaces
            for(int i = 1; i < (int) distanceToObject; i++) {

                //Coordinates of the point in the agent's coordinate system (agent at (0,0))
                double x = Math.cos(angleInMap.getRadians()) * i;
                double y = Math.sin(angleInMap.getRadians()) * i;

                int xInMap = (int) Math.round(currentPosition.getX() + x);
                int yInMap = (int) Math.round(currentPosition.getY() - y);
                //System.out.println("Add in Map at [" + xInMap +", " + yInMap +"] EmptySpace");

                if(currentMap[yInMap][xInMap] == null) currentMap[yInMap][xInMap] = ObjectPerceptType.EmptySpace;
            }

        }

    }


    /**
     * Method that increases the size of the current map that the agent is keeping track of and updates the agent's position accordingly
     * @param x = -1 if the map needs to be extended on the left, = 1 if it is on the right
     * @param y = -1 if the map needs to be extended on the top, = -1 if it is on the bottom
     * @param newPosition, the position the agent needs to be updated to if it moved (set to current position if the agent isn't moving)
     */
    public void extendMap(int x, int y, Point newPosition, int shiftLength) {
        //Variables keeping track of change in size of the map

        //Point is on the left of the current known map, extend the map to that area and shift all the points to the left
        if(x == -1) {
            //System.out.println("Extend map to left");
            ObjectPerceptType[][] newMap = new ObjectPerceptType[currentMap.length][currentMap[0].length * 2];
            for(int i=0; i<currentMap.length; i++) {
                for(int j=0; j<currentMap[0].length ;j++) {
                    newMap[i][j+shiftLength] = currentMap[i][j];
                }
            }
            currentMap = newMap;
        }


        //Point is on the right of the current known map, extend the map to that area
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

        //Point is above the current known map, extend the map to that area and shift all points to the top
        if(y == -1) {
            //System.out.println("Extend map to top");
            ObjectPerceptType[][] newMap = new ObjectPerceptType[currentMap.length * 2][currentMap[0].length];
            for(int i=0; i<currentMap.length; i++) {
                for(int j=0; j<currentMap[0].length ;j++) {
                    newMap[i+shiftLength][j] = currentMap[i][j];
                }
            }
            currentMap = newMap;
        }

        //Point is under the current known map, extend the map to that area
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
        if(x == -1) currentPosition = new Point(currentPosition.getX() + shiftLength, currentPosition.getY());
        if(y == -1) currentPosition = new Point(currentPosition.getX(), currentPosition.getY() + shiftLength);

        currentMapBottomRight = new Point(currentMapBottomRight.getX() +shiftLength, currentMapBottomRight.getY()+shiftLength);
    }

    public void resetParameters() {
        this.currentMap = new ObjectPerceptType[100][100];
        this.currentPosition = new Point(0,0);
        this.currentMapBottomRight = new Point(100,100);
        this.currentAngle = Angle.fromDegrees(0);
    }
}
