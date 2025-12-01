import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.Scanner;

import com.fazecast.jSerialComm.SerialPort;
import javafx.scene.transform.Rotate;

public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    final static int proximityLenth = 5;
    final static int gridCol = 18;
    final static int gridRow = 10;
    final static String userSerialPort = "COM13";
    Double rawDistance = 0.0;
    int rawAngle = 0;

    public void start(Stage stage) throws InterruptedException {

        SerialPort port = SerialPort.getCommPort(userSerialPort);
        port.setBaudRate(9600);
        port.openPort();

        Scanner input = new Scanner(port.getInputStream());

        Pane col[] = new Pane[proximityLenth];
        VBox proximityDisplay = new VBox();
        
        for (int i = 0; i < col.length; i++) {
            Pane light = new Pane();
            
            col[i] = light;
            light.setStyle("-fx-background-color: #29302fff;");         
            light.setMinSize(16, 16);
            light.setMaxSize(16, 16);
            proximityDisplay.getChildren().addAll(light);
            light.setOpacity(0.3);
          
        }
        proximityDisplay.setTranslateY(18);
        proximityDisplay.setTranslateX(20);

        GridPane gridLayout = new GridPane();
        HBox radarGraphs = new HBox();
        StackPane gridStack = new StackPane();
        StackPane imageStack = new StackPane();
        StackPane proximityStack = new StackPane();

        Button game = new Button("Play Mini game");

        game.setOnAction(event -> { 
            Stage newStage = new Stage();
            newStage.setResizable(false);
            Utils.game(newStage);
        });

        VBox legend = new VBox();
        VBox contentBox = new VBox(10);

        for (int i = 0; i < col.length; i++) {
            Label level = new Label("distance: " + Utils.scale(i, 0, 4, 0, 200) + " - " +  Utils.scale(i + 1, 0, 4, 0, 200));
            legend.getChildren().add(level);
        }
        legend.setTranslateX(40);
        legend.setTranslateY(15);
        
        Pane grid[][] = new Pane[gridCol][gridRow];
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[0].length; x++) {
       
                Pane tile = new Pane();
                grid[y][x] = tile;
                gridLayout.add(tile, y, x);
                tile.setStyle("-fx-background-color: #29302fff;");
                tile.setOpacity(0.3);
                tile.setPrefSize(11,11);
                    
            }
        }
        gridLayout.setTranslateX(5);

        Label gridDistanceLabel = new Label("Distance");
        gridDistanceLabel.setRotate(90);
        gridDistanceLabel.setTranslateX(grid.length * 6.5);

        Label gridAngleLabel = new Label("Angle");
        gridAngleLabel.setTranslateY(grid[0].length * 6);

        Label distanceLabel = new Label("Distance:");
        Label angleLabel = new Label("Angle:");
        
        Image semi = new Image("/semiCircle.png");
        ImageView semiCircle = new ImageView(semi);
        semiCircle.setFitWidth(200);
        semiCircle.setPreserveRatio(true);

        Image bar = new Image("/blackbox.jpeg");
        ImageView displayBar = new ImageView(bar);
        displayBar.setFitWidth(5);
        displayBar.setFitHeight(100);
        Rotate rotate = new Rotate();
        rotate.setPivotX(displayBar.getFitWidth());               
        rotate.setAngle(90);  // initial rotation angle
        displayBar.getTransforms().add(rotate);

        proximityStack.setTranslateX(20);

        proximityStack.getChildren().addAll(proximityDisplay, legend);
        gridStack.getChildren().addAll(gridLayout, gridDistanceLabel, gridAngleLabel);
        imageStack.getChildren().addAll(semiCircle, displayBar);
        radarGraphs.getChildren().addAll(imageStack, gridStack, proximityStack); // HBox
        contentBox.getChildren().addAll(distanceLabel, angleLabel, radarGraphs, game); // VBox

        Scene scene = new Scene(contentBox, 600, 200);
        stage.setScene(scene);
        stage.setTitle("Radar Graphs 1.0");
        stage.show();
        Thread.sleep(2500);
          
        Thread serialThread = new Thread(() -> {

            int distance = -1;
            int angle = -1;

            while (true) {
                try {           

                    if (!input.hasNextLine()) {
                        System.out.println("no line");   // retrys if no line is found
                        Thread.sleep(500);
                        continue;
                    }

                    if (rawAngle == 0 || rawAngle == 180) {
                        Utils.resetGrid(grid);                  // clears 2d grid oon angle 180 and 0
                        System.out.println("Reseting");
                    }

                    String line = input.nextLine();

                    try {
                        rawAngle = Integer.parseInt(line);
                        System.out.println("Angle = " + rawAngle);   // gets angle and try to convert it
                    } catch (NumberFormatException e1) {
                        try {
                            rawDistance = Double.parseDouble(line);
                            System.out.println("Distance = " + rawDistance);
                        } catch (NumberFormatException e2) {
                            System.out.println("Invalid input: " + line);
                        }
                    }
                
                    System.out.println("Distance received: " + rawDistance);
                    System.out.println("Angle received: " + rawAngle);


                    // UI update on JavaFX Application Thread
                    Platform.runLater(() -> {
                        distanceLabel.setText("Distance:\t" + rawDistance);
                        angleLabel.setText("Angle:\t" + rawAngle);
                    });
                    
                    Utils.writeImportantPoints(rawDistance, rawAngle);

                    Double mappedDistance = Utils.scale(rawDistance, 0, 200, 0, grid[0].length - 1);
                    Double mappedAngle = Utils.scale(rawAngle, 0, 180, 0, grid.length - 1);
                    
                    distance = (int) Math.round(mappedDistance);
                    angle = (int) Math.round(mappedAngle);
                    
                    rotate.setAngle(-rawAngle + 90);

                    Utils.proximityDisplay(col, rawDistance);

                    grid[angle][distance].setStyle("-fx-background-color: #3cff00ff;");
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
        });
        serialThread.start();
    }
}