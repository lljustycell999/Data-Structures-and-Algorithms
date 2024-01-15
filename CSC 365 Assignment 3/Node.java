package csc.pkg365.assignment.pkg3.pkg1;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Node implements Comparable<Node>{
    
    // Name of webpage
    public final String name;
    
    // All nodes will have a distance of infinity to start with, 
    // except for the source node, which will be zero
    private Double distance = Double.MAX_VALUE;
    
    // This list will store the shortest path from the source node 
    // (Will be updated with dijkstra's)
    private List<Node> shortestPath = new LinkedList<>();
    
    // This map will store the adjacent nodes of the node of interest and the 
    // weight separating them
    private Map<Node, Double> adjacentNodes = new HashMap<>();
    
    public Node(String name){
        this.name = name;
    }
    
    // Takes the node and the edge weight
    public void addAdjacentNode(Node node, double weight){
        adjacentNodes.put(node, weight);
    }
    
    // Useful with Priority Queue to pull the node with the smallest distance
    @Override
    public int compareTo(Node node){
        return Double.compare(this.distance, node.getDistance());
    }
    
    public void setDistance(Double distance){
        this.distance = distance;
    }
    public Double getDistance(){
        return distance;
    }
    
    public void setAdjacentNodes(Map<Node, Double> adjacentNodes){
        this.adjacentNodes = adjacentNodes;
    }
    
    public Map<Node, Double> getAdjacentNodes(){
        return adjacentNodes;
    }
    
    public void setShortestPath(List<Node> shortestPath){
        this.shortestPath = shortestPath;
    }
    
    public List<Node> getShortestPath(){
        return shortestPath;
    }
    
    public String getName(){
        return name;
    }
    
    // Dijkstra's Algorithm
    public static void calculateShortestPath(Node source){
        source.setDistance(0.0); // Set source node distance to zero
        
        // Settled and Unsettled Sets
        Set<Node> settledNodes = new HashSet<>();
        Queue<Node> unsettledNodes = new PriorityQueue<>(Collections.singleton(source));
        
        // While our priority queue is not empty
        while(!unsettledNodes.isEmpty()){
            // Poll the node with the minimum distance from it
            Node currentNode = unsettledNodes.poll();
            
            // Loop over its adjacent nodes
            
            // For each adjacent node that isn't settled (meaning we don't have a decisive minimum distance yet),
            // Update the nodes' distance via the evaluateDistanceAndPath method,
            // then insert into the set of unsettled nodes.
            currentNode.getAdjacentNodes().entrySet().stream().filter(entry -> !settledNodes.contains(entry.getKey())).forEach(entry -> {
                evaluateDistanceAndPath(entry.getKey(), entry.getValue(), currentNode);
                unsettledNodes.add(entry.getKey());
            });
            
            // Finally, after we loop through all of the node's adjacent nodes,
            // add that node to the set of settled nodes
            settledNodes.add(currentNode);
        }
        
    }
    
    private static void evaluateDistanceAndPath(Node adjacentNode, Double edgeWeight, Node sourceNode){
        
        // Compare the sum of the edge weight and the distance of the source node it connects
        // to the destination's distance
        Double newDistance = sourceNode.getDistance() + edgeWeight;
        
        // If distance value is smaller, we found a more optimal path,
        // so update the path by adding the node we are at to the path at hand
        if(newDistance < adjacentNode.getDistance()){
            adjacentNode.setDistance(newDistance);
            adjacentNode.setShortestPath(Stream.concat(sourceNode.getShortestPath().stream(), Stream.of(sourceNode)).toList());
        }
    }
    
    public static void printPaths(List<Node> nodes, MyFrame frame){
        
        nodes.forEach(node -> {
            String path = node.getShortestPath().stream().map(Node::getName).collect(Collectors.joining(" -> "));   
            frame.textArea.append((path.isBlank() ? "%s : %s".formatted(node.getName(), node.getDistance()) : "%s -> %s : %s".formatted(path, node.getName(), node.getDistance())));
        });
    }
}  
