package Group2.Map;

import Interop.Percept.Vision.ObjectPerceptType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Graph {

    private ArrayList<Node> nodes = new ArrayList<>();
    private ArrayList<Edge> edges = new ArrayList<>();

    public Graph() {}


    //TODO: Change implementation so that the edges added correspond to the neighbour's reference (instead of creating a new one)
    //TODO: Add weights to the edges (?depending on what object is there?)
    public Graph(ObjectPerceptType[][] matrixMap) {

        for(int i=0; i<matrixMap.length; i++) {
            for(int j=0; j<matrixMap[0].length;j++) {

                Node newNode = new Node(matrixMap[i][j]);
                if(i != 0) {
                    Node neighbour = new Node(matrixMap[i-1][j]);
                    newNode.addNeighbour(neighbour);
                    this.addEdge(newNode, neighbour, 0);
                }
                if(i != matrixMap.length){
                    Node neighbour = new Node(matrixMap[i+1][j]);
                    newNode.addNeighbour(neighbour);
                    this.addEdge(newNode, neighbour, 0);
                }
                if(j != 0) {
                    Node neighbour = new Node(matrixMap[i][j-1]);
                    newNode.addNeighbour(neighbour);
                    this.addEdge(newNode, neighbour, 0);
                }
                if(j != matrixMap[0].length){
                    Node neighbour = new Node(matrixMap[i][j+1]);
                    newNode.addNeighbour(neighbour);
                    this.addEdge(newNode, neighbour, 0);
                }
                this.addNode(newNode);
            }
        }
    }


    public Edge addEdge(Node x, Node y, double weight) {
        x.addNeighbour(y);
        edges.add(new Edge(x, y, weight));
        return new Edge(x, y, weight);
    }

    public void addNode(Node x) {
        nodes.add(x);
    }

    public double getPathWeight(Node start, Node end, List<Edge> path) {
        double value = 0;
        for(Edge e: path) {
            value += e.getWeight();
        }
        return value;
    }


    public ArrayList<Node> getNodes() {
        return nodes;
    }

    public ArrayList<Edge> getEdges() {
        return edges;
    }

    public Edge getEdge(Node x, Node y) {
        for (int i=0; i<edges.size(); i++) {
            if(edges.get(i).getSource().equals(x) && edges.get(i).getTarget().equals(y) ||
                    edges.get(i).getSource().equals(y) && edges.get(i).getTarget().equals(x))
                return edges.get(i);
        }
        return null;
    }


}
