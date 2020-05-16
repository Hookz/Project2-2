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

        //Reset the map if the agent just got teleported
        if(percepts.getAreaPercepts().isJustTeleported()) resetParameters();

        if(action instanceof Rotate) {
            currentAngle = Angle.fromDegrees((currentAngle.getDegrees() + ((Rotate) action).getAngle().getDegrees())%360);
            while(currentAngle.getDegrees() < 0) currentAngle = Angle.fromDegrees(currentAngle.getDegrees() + 360);
        }


        if(action instanceof Move || action instanceof Sprint) {
            Distance distance = new Distance(0);
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

            extendMap(shiftInX, shiftInY, newPosition);

        }

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
         } */



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

            int shiftInX = 0;
            int shiftInY = 0;

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

            extendMap(shiftInX, shiftInY, new Point(currentPosition.getX(), currentPosition.getY()));

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


    /**
     * Method that increases the size of the current map that the agent is keeping track of and updates the agent's position accordingly
     * @param x = -1 if the map needs to be extended on the left, = 1 if it is on the right
     * @param y = -1 if the map needs to be extended on the top, = -1 if it is on the bottom
     * @param newPosition, the position the agent needs to be updated to if it moved (set to current position if the agent isn't moving)
     */
    public void extendMap(int x, int y, Point newPosition) {
        //Variables keeping track of change in size of the map
        int shiftInX = 0;
        int shiftInY = 0;

        //Agent/ point in vision is on the left of the current known map, extend the map to that area and shift all the points to the left
        if(x == -1) {
            //System.out.println("Extend map to left");
            ObjectPerceptType[][] newMap = new ObjectPerceptType[currentMap.length][currentMap[0].length +50];
            shiftInX = 50;
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
            ObjectPerceptType[][] newMap = new ObjectPerceptType[currentMap.length][currentMap[0].length +50];
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
            ObjectPerceptType[][] newMap = new ObjectPerceptType[currentMap.length +50][currentMap[0].length];
            shiftInY = 50;
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
            ObjectPerceptType[][] newMap = new ObjectPerceptType[currentMap.length +50][currentMap[0].length];
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

    public void resetParameters() {
        this.currentMap = new ObjectPerceptType[100][100];
        this.currentPosition = new Point(0,0);
        this.currentMapBottomRight = new Point(100,100);
        this.currentAngle = Angle.fromDegrees(0);
    }
}
