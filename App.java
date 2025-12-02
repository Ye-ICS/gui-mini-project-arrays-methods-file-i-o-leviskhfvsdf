import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
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
import java.io.FileNotFoundException;

public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }
    final static int gridCol = 10;  
    final static int gridRow = 10;
    final static String userSerialPort = "COM13";   // serial port
    Double rawDistance = 0.0;
    int rawAngle = 0;

    public void start(Stage stage) throws InterruptedException, FileNotFoundException {

        SerialPort port = SerialPort.getCommPort(userSerialPort);
        port.setBaudRate(9600);
        port.openPort();

        Scanner input = new Scanner(port.getInputStream());

        GridPane gridLayout = new GridPane();
        HBox radarGraphs = new HBox();
        StackPane gridAndImage = new StackPane();
        StackPane imageStack = new StackPane();

        Button game = new Button("Play Mini game");

        Button openFile = new Button("read files");

        
        openFile.setOnAction(event -> {
            Stage newStage = new Stage();
            newStage.setResizable(false);    
            try {
                 Utils.file(newStage);
            } catch (FileNotFoundException e) {
                System.out.println(e);
            }
        });
         
        game.setOnAction(event -> { 
            Stage newStage = new Stage();
            newStage.setResizable(false);
            Utils.game(newStage);
        });

        VBox contentBox = new VBox(10);

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

        
        Pane grid[][] = new Pane[gridCol][gridRow];
        gridLayout.prefWidthProperty().bind(semiCircle.fitWidthProperty());
        gridLayout.prefHeightProperty().bind(semiCircle.fitWidthProperty().multiply(0.5));

        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[0].length; x++) {
       
                Pane tile = new Pane();
                grid[y][x] = tile;
                gridLayout.add(tile, y, x);
                tile.setStyle("-fx-background-color: #29302fff;");
                tile.setOpacity(0.3);
                
                tile.prefWidthProperty().bind(gridLayout.prefWidthProperty().divide(gridCol));
                tile.prefHeightProperty().bind(gridLayout.prefHeightProperty().divide(gridRow));
            }
        }

        Label distanceLabel = new Label("Distance:");
        Label angleLabel = new Label("Angle:");

        imageStack.getChildren().addAll(semiCircle, displayBar);
        gridAndImage.getChildren().addAll(imageStack, gridLayout);
        gridAndImage.setMaxHeight(100);
        gridAndImage.setMinHeight(100);

        radarGraphs.getChildren().addAll(gridAndImage); // HBox
        radarGraphs.setAlignment(Pos.CENTER);

        contentBox.getChildren().addAll(distanceLabel, angleLabel, radarGraphs, game, openFile); // VBox
        contentBox.setAlignment(Pos.CENTER);
        
        Scene scene = new Scene(contentBox, 500, 250);
        stage.setScene(scene);
        stage.setTitle("Radar Graphs 1.0");
        stage.show();
      
          
        Thread serialThread = new Thread(() -> {

            int distance = -1;
            int angle = -1;

            while (true) {
                try {           
                    Thread.sleep(400);

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