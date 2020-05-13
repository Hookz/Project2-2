package Group2.Map;

import Interop.Percept.Vision.ObjectPerceptType;

import java.util.ArrayList;

public class Node {

    private final ObjectPerceptType object;
    private ArrayList<Node> neighbours = new ArrayList<>();
    private boolean visited;

    public Node(ObjectPerceptType object){
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

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public boolean getVisited() {
        return visited;
    }
}
