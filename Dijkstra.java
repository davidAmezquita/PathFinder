package main.java;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.util.ArrayList;

class MyNode {
    int id;
    double dist;
    boolean isVisited;
    int prev;
    ArrayList<Integer> neighbors;

    public MyNode(int id, double dist, int prev){
        this.id = id;
        this.dist = dist;
        this.prev = prev;
        this.isVisited = false;
    }

    public void addNeighbors(int index, int row_size, int gridSize) {
        this.neighbors = new ArrayList<Integer>();

        //add left neighbor if index is not on the left edge of the grid
        if (index-1 >= 0 && (index%row_size) != 0) {
            neighbors.add(index-1);
        }

        //add right neighbor if index is not on the right edge of the grid
        if((index+1)%row_size != 0 && (index+1) <= gridSize){
            neighbors.add(index+1);
        }

        //add the top neighbor if its not the top edge of the grid
        if ((index-row_size) >= 0){
            neighbors.add(index-row_size);
        }

        //add the bottom neighbor if its not beyond the size of the grid
        if ((index+row_size) < gridSize) {
            neighbors.add(index+row_size);
        }
    }
}

public class Dijkstra {
    ArrayList<MyNode> Q = new ArrayList<MyNode>();
    int source;
    int goal;
    int gridSize;

    public Dijkstra(int gridSize, int nodes_per_row, int startingPoint, int endPoint) {
        source = startingPoint;
        goal = endPoint;
        this.gridSize = gridSize;

        for (int i=0; i< gridSize; i++) {
            MyNode n = new MyNode(i, Integer.MAX_VALUE, -1);
            n.addNeighbors(i, nodes_per_row, gridSize);
            Q.add(n);
        }

        Q.get(source).dist = 0;

    }

    //gets the absolute distance from point u to point v
    private double length(ObservableList<Node> grid, int u, int v) {

        Rectangle U = (Rectangle) grid.get(u);
        Rectangle V = (Rectangle) grid.get(v);

        double len = (U.getX() + U.getY()) - (V.getX() + V.getY());

        return Math.abs(len);
    }

    public Integer getShortestPath(ObservableList<Node> grid)  {

        boolean pathFound = false;
        int numOfUnvisited = Q.size();

        while (numOfUnvisited > 0 && !pathFound) {
            MyNode u = minDist(Q);

            if (u == null) {
                System.out.print("No node with min distance found!");
                return -1;
            }

            u.isVisited = true;
            numOfUnvisited -= 1;


            for (int i = 0; i < u.neighbors.size(); i++) {
                //check if node is a wall
                Rectangle r = (Rectangle) grid.get(u.neighbors.get(i));
                if(r.getFill() == Color.ORANGE) {
                    continue;
                }

                //grab the neighbor nodes of u and check the pixel distance from u
                MyNode v = Q.get(u.neighbors.get(i));
                double alt = u.dist + length(grid, u.id, v.id);

                //if the distance is shorter, set node v distance to the shorter distance
                if (alt < v.dist && v.id != goal){
                    v.dist = alt;
                    v.prev = u.id;
                }
                else if (v.id == goal) {
                    //node v is our end point therefore we stop searching
                    //and we print the path to the grid
                    v.prev = u.id;
                    pathFound = true;
                    printPath(grid);
                    return 1;
                }
            }

        }
        return 0;
    }


    //shows the shortest path on the grid by painting the path nodes white
    private void printPath(ObservableList<Node> grid) {

        MyNode curr = Q.get(goal);
        while (curr.prev != -1) {

            Rectangle r = (Rectangle) grid.get(curr.id);
            r.setFill(Color.WHITE);
            curr = Q.get(curr.prev);
        }
    }

    //returns a MyNode object with the lowest distance
    private MyNode minDist(ArrayList<MyNode> list) {
        MyNode minNode = null;
        double min = Integer.MAX_VALUE;


        for (int i = 0; i < list.size(); i++) {
            MyNode v = list.get(i);

            if (v.isVisited) {
                continue;
            }
            if (v.dist < min) {
                min = v.dist;
                minNode = v;
            }
        }

        return minNode;
    }
}
