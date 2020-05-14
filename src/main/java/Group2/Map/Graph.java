package Group2.Map;

import Interop.Percept.Vision.ObjectPerceptType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Graph {

    private ArrayList<Node> nodes = new ArrayList<>();
    private ArrayList<Edge> edges = new ArrayList<>();

    public Graph() {}


    //TODO: Add weights to the edges (?depending on what object is there?)
    public Graph(ObjectPerceptType[][] matrixMap) {

        for(int i=0; i<matrixMap.length; i++) {
            for(int j=0; j<matrixMap[0].length;j++) {
                Node newNode = new Node(matrixMap[i][j]);
                this.addNode(newNode);
            }
        }

        for(int i=0; i<this.nodes.size(); i++) {
            Node current = this.nodes.get(i);

            //Top node
            if(i > matrixMap[0].length)
               this.addEdge(current, this.nodes.get(i - matrixMap[0].length), 0);

            //Bottom node
            if(i < (matrixMap.length -1)*matrixMap[0].length)
                this.addEdge(current, this.nodes.get(i + matrixMap[0].length), 0);

            //Left node
            if(i % matrixMap[0].length != 0)
                this.addEdge(current, this.nodes.get(i - 1), 0);

            //Right node
            if(i % matrixMap[0].length != matrixMap[0].length -1 )
                this.addEdge(current, this.nodes.get(i +1), 0);

            //Top left node
            if(i > matrixMap[0].length && i % matrixMap[0].length != 0)
                this.addEdge(current, this.nodes.get(i - matrixMap[0].length -1), 0);

            //Top right node
            if(i > matrixMap[0].length && i % matrixMap[0].length != matrixMap[0].length -1)
                this.addEdge(current, this.nodes.get(i - matrixMap[0].length +1), 0);

            //Bottom left node
            if(i < (matrixMap.length -1)*matrixMap[0].length && i % matrixMap[0].length != 0)
                this.addEdge(current, this.nodes.get(i + matrixMap[0].length -1), 0);

            //Bottom right node
            if(i < (matrixMap.length -1)*matrixMap[0].length && i % matrixMap[0].length != matrixMap[0].length -1)
                this.addEdge(current, this.nodes.get(i + matrixMap[0].length +1), 0);

        }
    }


    public void addEdge(Node x, Node y, double weight) {
        //Check if the edge does not already exist
        if(!x.getNeighbours().contains(y)) {
            x.addNeighbour(y);
            edges.add(new Edge(x, y, weight));
        }
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
