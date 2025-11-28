import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.Scanner;
import java.util.Stack;

import com.fazecast.jSerialComm.SerialPort;
import javafx.scene.transform.Rotate;

public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    final static String userSerialPort = "COM13";
    Double rawDistance = 0.0;
    int rawAngle = 0;

    public void start(Stage stage) throws InterruptedException {

        SerialPort port = SerialPort.getCommPort(userSerialPort);
        port.setBaudRate(9600);
        port.openPort();

        Scanner input = new Scanner(port.getInputStream());

        Pane col[] = new Pane[5];
        VBox proximityDisplay = new VBox();
        for (int i = 0; i < col.length; i++) {
            Pane light = new Pane();
            
            col[i] = light;
            light.setPrefSize(10,10);
            proximityDisplay.getChildren().addAll(light);
            light.setOpacity(0.3);
          
        }
        proximityDisplay.setTranslateY(7);
        proximityDisplay.setTranslateX(20);

        GridPane gridLayout = new GridPane();
        HBox radarGraphs = new HBox();
        StackPane gridStack = new StackPane();
        StackPane imageStack = new StackPane();
        StackPane proximityStack = new StackPane();

        VBox legend = new VBox();
        VBox contentBox = new VBox(10);

        for (int i = 0; i < col.length; i++) {
            Label level = new Label("distance: " + Utils.scale(i, 0, 4, 0, 200) + " - " +  Utils.scale(i + 1, 0, 4, 0, 200));
            legend.getChildren().add(level);
        }
        legend.setTranslateX(40);
        legend.setTranslateY(15);
        
        Pane grid[][] = new Pane[18][10];
        for (int y = 0; y < grid.length; y++)
        for (int x = 0; x < grid[0].length; x++) {
       
            Pane tile = new Pane();
            grid[y][x] = tile;
            gridLayout.add(tile, y, x);
            tile.setStyle("-fx-background-color: #29302fff;");
            tile.setOpacity(0.3);
            tile.setPrefSize(11,11);
                    
         }
        gridLayout.setTranslateX(5);

        Label gridDistanceLabel = new Label("Distance");
        gridDistanceLabel.setRotate(90);
        gridDistanceLabel.setTranslateX(110);
        gridDistanceLabel.setTranslateY(10);

        Label gridAngleLabel = new Label("Angle");
        gridAngleLabel.setTranslateY(65);
        gridAngleLabel.setTranslateX(0);

        Label distanceLabel = new Label("Distance:");
        Label angleLabel = new Label("Angle:");
        
        Image semi = new Image("file:C:\\Users\\martinln\\OneDrive - Limestone DSB\\computer coding class g11\\unit 2\\gui-mini-project-arrays-methods-file-i-o-leviskhfvsdf\\semiCircle.png");
        ImageView semiCircle = new ImageView(semi);
        semiCircle.setFitWidth(200);
        semiCircle.setPreserveRatio(true);

        Image bar = new Image("file:C:\\Users\\martinln\\OneDrive - Limestone DSB\\computer coding class g11\\unit 2\\gui-mini-project-arrays-methods-file-i-o-leviskhfvsdf\\blackbox.jpeg");
        ImageView displayBar = new ImageView(bar);
        displayBar.setFitWidth(5);
        displayBar.setFitHeight(100);
        Rotate rotate = new Rotate();
        rotate.setPivotX(displayBar.getFitWidth());                  // left edge
        rotate.setPivotY(0); // bottom edge
        rotate.setAngle(90);  // initial rotation angle
        displayBar.getTransforms().add(rotate);

        proximityStack.getChildren().addAll(proximityDisplay, legend);
        gridStack.getChildren().addAll(gridLayout, gridDistanceLabel, gridAngleLabel);
        imageStack.getChildren().addAll(semiCircle, displayBar);
        radarGraphs.getChildren().addAll(imageStack, gridStack, proximityStack); // HBox
        contentBox.getChildren().addAll(distanceLabel, angleLabel, radarGraphs); // VBox

        Scene scene = new Scene(contentBox, 600, 600);
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
                        System.out.println("no line");
                        Thread.sleep(500);
                        continue;
                    }

                    if (rawAngle == 0 || rawAngle == 180) {
                        Utils.resetGrid(grid);
                        System.out.println("Reseting");
                    }

                    String line = input.nextLine();

                    try {
                        rawAngle = Integer.parseInt(line);
                        System.out.println("Angle = " + rawAngle);
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