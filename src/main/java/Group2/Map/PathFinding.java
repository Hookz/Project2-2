package Group2.Map;

import java.util.*;

public class PathFinding {

    private Graph graph;
    private List<List<Node>> allPaths = new ArrayList<>();
    private HashSet<Node> nodeSet = new HashSet<>();

    public PathFinding(Graph graph) {
        this.graph = graph;
    }


    public List<Node> shortestPathDijkstra(Node source, Node target) {

        Map<Node, Double> dist = new HashMap<>();
        Map<Node, Node> previous = new HashMap<>();
        HashSet<Node> nodes = new HashSet<>(graph.getNodes());

        for (Node node : graph.getNodes()) {
            dist.put(node, Double.MAX_VALUE);
            previous.put(node, null);
        }

        dist.put(source, 0.0);

        while (!nodes.isEmpty()) {
            //Node current = (Node) nodes.stream().min(Comparator.comparingDouble(dist).getKey());
            double min = Double.MAX_VALUE;
            Node current = null;
            for(Node n: nodes){
                if(dist.containsKey(n)) {
                    if(dist.get(n)<min) {
                        min = dist.get(n);
                        current = n;
                    }
                }
            }
            nodes.remove(current);

            for (Node neighbour : current.getNeighbours()) {
                Edge edge = graph.getEdge(current, neighbour);
                double alt = dist.get(current) + edge.getWeight();
                if (alt < dist.get(neighbour)) {
                    dist.put(neighbour, alt);
                    previous.put(neighbour, current);
                }
            }
        }

        LinkedList<Node> path = new LinkedList<>();
        if (previous.get(target) != null || target.equals(source)) {
            while (target != null) {
                path.addFirst(target);
                target = previous.get(target);
            }
        }
        return path;
    }


    //Find all possible paths from source to target
    public void createPaths(Node source, Node target, List<Node> currentPath) {

        this.nodeSet.add(source);

        if(source.equals(target)) {

            List<Node> newPath = new ArrayList<>();
            newPath.addAll(currentPath);
            allPaths.add(newPath);
            System.out.println(currentPath);

            //Backtrack
            this.nodeSet.remove(source);
            return;
        }

        for(Object o: source.getNeighbours()) {
            Node x = (Node) o;

            if(!this.nodeSet.contains(x)) {
                currentPath.add(x);
                createPaths(x, target, currentPath);

                //Backtrack
                currentPath.remove(x);
            }
        }
        //Backtrack
        this.nodeSet.remove(source);
    }

    public List<List<Node>> getAllPaths() {
        return allPaths;
    }
}
