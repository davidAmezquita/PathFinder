package main.java;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.Random;


public class PathFinder extends Application {
    private final int col = 40;
    private final int row = 40;
    private final int WIDTH = 1000;
    private final int HEIGHT = 650;
    private int numNodes = col * row;

    private Group grid = new Group();
    private ObservableList<Node> list;
    private Button setStartBtn = new Button("Set StartPoint");
    private Button setEndBtn = new Button("Set EndPoint");
    private Button startBtn = new Button("Start");
    private Button resetBtn = new Button("Reset");
    private Button genMaze = new Button("Generate Maze");
    private Label alertLabel = new Label("");

    private int previousIndex = 0;
    private int currentIndex = 0;
    private boolean startNodeSet = false;
    private boolean endNodeSet = false;
    private boolean makingWalls = false;

    private int startNodeIndex = -1;
    private int endNodeIndex = -1;

    public static void main(String args) {
        launch();
    }

    public void start(Stage stage) throws Exception {

        //layout
        VBox mainLayout = new VBox(10);
        BackgroundFill backFill = new BackgroundFill(Color.STEELBLUE, CornerRadii.EMPTY, Insets.EMPTY);
        mainLayout.setBackground(new Background(backFill));

        //create grid
        createGrid();
        list = grid.getChildren();  //reference to every node in the grid for quick access


        //create main UI console
        HBox console = Console();
        mainLayout.getChildren().addAll(grid,console);

        Scene mainScene = new Scene(mainLayout, WIDTH, HEIGHT);

        stage.setTitle("Visualizer");
        stage.setScene(mainScene);
        stage.show();

    }

    private void createGrid() {

        int nodeWidth = WIDTH/row;
        int nodeHeight = (HEIGHT - 50)/col;
        int y = -1;
        int x = 0;


        //create rows of nodes to form the grid
        for (int i = 0; i < numNodes; i++) {
            //when reaching the edge of the screen, proceed to add nodes to new row
            //by increasing the value by which the y value is being multiplied with
            //as well as reset the x value to start at the left edge of the screen
            if (i % row == 0) {
                y++;
                x=0;
            }

            //sets the cosmetics of the rectangles and gives it and id for quick reference
            final Rectangle rec = new Rectangle(x * nodeWidth, y*nodeHeight, nodeWidth, nodeHeight);
            rec.setFill(Color.BLACK);
            rec.setStroke(Color.GREEN);
            rec.setId(Integer.toString(i));

            rec.setOnMouseClicked(new EventHandler<MouseEvent>() {
                public void handle(MouseEvent event) {
                    //grabs the id of the rectangle that has been clicked
                    currentIndex = Integer.parseInt(rec.getId());

                    Rectangle temp2 = (Rectangle)list.get(currentIndex);   //grabs current rectangle from list
                    if (!startNodeSet) {
                        //start node is represented as the color blue
                        temp2.setFill(Color.BLUE);
                        startNodeIndex = currentIndex;  //keep a reference of which rectangle is the starting node
                    }
                    else if(!endNodeSet && startNodeSet && currentIndex != startNodeIndex) {
                        //end node is represented as the color red, we save a
                        // reference to it once the setEndBtn is pressed
                        temp2.setFill(Color.RED);
                    }

                    //when the user selects a new start point or end point, we must reset the previous selected
                    // rectangle back to its original color of black
                    if (previousIndex >= 0 && previousIndex != startNodeIndex && previousIndex != endNodeIndex && !makingWalls) {
                        Rectangle temp = (Rectangle)list.get(previousIndex);
                        temp.setFill(Color.BLACK);
                    }
                    previousIndex = currentIndex;  //keeps a reference to the previous clicked on rectangle
                }

            });

            //used for setting walls when mouse is dragged across screen
            rec.setOnMouseMoved(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    int id = Integer.parseInt(rec.getId());

                    //once the starting and end point is set, walls can be drawn by holding shift
                    if(startNodeSet && endNodeSet && event.isShiftDown()) {
                        if (id == startNodeIndex || id == endNodeIndex) {
                            //can't allow starting/end point from being changed to walls
                            return;
                        }
                        //walls are represented as orange
                        rec.setFill(Color.ORANGE);
                    }
                }
            });

            //draws rec onto grid
            grid.getChildren().add(rec);
            x++;
        }
    }

    //all code having to do with buttons is handled here
    private HBox Console() {
        HBox v1 = new HBox(10);
        HBox console = new HBox(10);

        //simple background color for buttons
        BackgroundFill bgFill = new BackgroundFill(Color.PINK, CornerRadii.EMPTY, Insets.EMPTY);
        Background bg = new Background(bgFill);

        setStartBtn.setBackground(bg);
        setEndBtn.setBackground(bg);
        startBtn.setBackground(bg);
        genMaze.setBackground(bg);
        resetBtn.setBackground(bg);

        //this button is disable until a starting node has been selected
        setEndBtn.setDisable(true);

        //this button is disabled until both a starting node and end node is selected
        startBtn.setDisable(true);


        //finalize the position of the starting node
        setStartBtn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                //button was clicked before a node was selected
                if (startNodeIndex == -1) {
                    //tell the user he must choose a starting node
                    alertLabel.setText("Please set a starting point.");
                    alertLabel.setTextFill(Color.RED);
                    alertLabel.setFont(Font.font(null, FontWeight.BOLD, 20));
                }
                else {
                    //disable the button and allow the user to choose an end node
                    startNodeSet = true;
                    setStartBtn.setDisable(true);
                    setEndBtn.setDisable(false);
                }
            }
        });


        //finalize the position of the end node
        setEndBtn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                //can't have the current chosen end node position to be the in the same
                // position as the start node's position
                if (currentIndex == startNodeIndex) {
                    alertLabel.setText("Please select an end point.");
                    alertLabel.setTextFill(Color.RED);
                    alertLabel.setFont(Font.font(null, FontWeight.BOLD, 20));
                }
                else {
                    //inform the user as to what to do next
                    alertLabel.setText("You may now set walls by holding shift and/or hit start to find the path!");
                    alertLabel.setTextFill(Color.YELLOW);
                    alertLabel.setWrapText(true);
                    endNodeSet = true;
                    makingWalls = true;
                    endNodeIndex = currentIndex;
                    setEndBtn.setDisable(true);
                    startBtn.setDisable(false);
                }
            }
        });

        //creates random walls
        genMaze.setOnAction(e->{
            randMaze();
            genMaze.setDisable(true);
        });

        //starts the path finding process
        startBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Dijkstra g = new Dijkstra(list.size(), row, startNodeIndex, endNodeIndex);
                int retVal = g.getShortestPath(list);
                if (retVal == -1) {
                    alertLabel.setText("No path found!");
                    alertLabel.setTextFill(Color.RED);
                }
                else if(retVal == 1) {
                    alertLabel.setText("Shortest path found!");
                    alertLabel.setTextFill(Color.YELLOW);
                }
            }
        });

        //resets grid, buttons, all references and booleans
        resetBtn.setOnAction(e->{
            resetGrid();
        });


        v1.getChildren().addAll(setStartBtn, setEndBtn);
        console.getChildren().addAll(v1, genMaze, startBtn, resetBtn, alertLabel);

        return console;
    }

    //reset all variables to its originally predefined value
    private void resetGrid() {
        for(int i = 0; i < numNodes; i++) {
            Rectangle r = (Rectangle) list.get(i);
            r.setFill(Color.BLACK);
        }

        //reset start/end point
        startNodeIndex = -1;
        endNodeIndex = -1;
        currentIndex = 0;
        previousIndex = 0;

        startNodeSet = false;
        endNodeSet = false;
        makingWalls = false;

        //reset the button's actions
        setStartBtn.setDisable(false);
        setEndBtn.setDisable(true);
        startBtn.setDisable(true);
        genMaze.setDisable(false);

        alertLabel.setText("");
    }

    //picks random nodes to be represented as a wall
    private void randMaze() {

        Random rand = new Random();
        int len = numNodes/4;
        for (int i = 0; i < len; i++) {
            int randNum = rand.nextInt(numNodes);
            //start node and end node can't be walls
            if (randNum == startNodeIndex || randNum == endNodeIndex) {
                continue;
            }
            Rectangle r = (Rectangle) list.get(randNum);
            r.setFill(Color.ORANGE);
        }

    }

}//end of PathFinder class

