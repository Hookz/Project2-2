package Group2.Map;

import Interop.Geometry.Point;
import Interop.Percept.Vision.ObjectPerceptType;

import java.util.ArrayList;

public class Node {

    private final ObjectPerceptType object;
    private ArrayList<Node> neighbours = new ArrayList<>();
    private Point pos;

    public Node(ObjectPerceptType object, Point pos){
        this.pos = pos;
        this.object = object;
    }

    public ObjectPerceptType getObject() {
        return object;
    }

    public ArrayList<Node> getNeighbours() {
        return neighbours;
    }


    public void addNeighbour(Node neighbour) {
        this.neighbours.add(neighbour);
        neighbour.neighbours.add(this);
    }

    public Point getPos() {
        return pos;
    }

    @Override
    public String toString() {
        return "Node{" +
                "object=" + object +
                "point=" +pos +
                '}';
    }
}
