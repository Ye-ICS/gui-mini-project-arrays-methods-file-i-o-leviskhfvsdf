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

    final static int serialDelay = 710; // millis
    final static int radarSize = 200; // dimention of grid and image

    final static int gridCol = 10;  // amount of cells in the grid
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

        Button openFile = new Button("read data");

        openFile.setOnAction(event -> {  // file window
            Stage newStage = new Stage();
            newStage.setResizable(false);    
            try {
                 Utils.file(newStage);
            } catch (FileNotFoundException e) {
                System.out.println(e);
            }
        });

        VBox contentBox = new VBox(10);

        Image obj = new Image("/semiCircle.png");  //background image for grid
        ImageView semiCircle = new ImageView(obj);
        semiCircle.setFitWidth(radarSize);
        semiCircle.setFitHeight(radarSize / 2); // hight should be half of width 
       

        Image img = new Image("/blackbox.jpeg");   // rotating bar for image
        ImageView displayBar = new ImageView(img);
        displayBar.setFitWidth(5);
        displayBar.setFitHeight(radarSize / 2);
        Rotate rotate = new Rotate();
        rotate.setPivotX(displayBar.getFitWidth());    // sets pivot point to the top of the bar         
        rotate.setAngle(90);  // initial rotation angle
        displayBar.getTransforms().add(rotate); // adds rotate properties to displayBar

        imageStack.getChildren().addAll(semiCircle, displayBar); // stackPane
        imageStack.setAlignment(Pos.CENTER);
        
        Pane grid[][] = new Pane[gridCol][gridRow];
        gridLayout.prefWidthProperty().bind(semiCircle.fitWidthProperty());
        gridLayout.prefHeightProperty().bind(semiCircle.fitHeightProperty()); // gridlayouts hight should be imgages hight

        for (int y = 0; y < grid.length; y++) { // loop for 2d grid array. goes through each cell
            for (int x = 0; x < grid[0].length; x++) {
       
                Pane tile = new Pane();
                grid[y][x] = tile;
                gridLayout.add(tile, y, x); // placement of the tiles
                tile.setStyle("-fx-background-color: #29302fff;");
                tile.setOpacity(0.3); // to see the image behind the tiles
                
                tile.prefWidthProperty().bind(gridLayout.prefWidthProperty().divide(gridCol)); // tile should be gridLayouts width divided by amount of collumns
                tile.prefHeightProperty().bind(gridLayout.prefHeightProperty().divide(gridRow));
            }
        }
        gridLayout.setAlignment(Pos.CENTER); // align grid with image

        Label distanceLabel = new Label("Distance:");
        Label angleLabel = new Label("Angle:");

        gridAndImage.getChildren().addAll(imageStack, gridLayout); // stackPane

        radarGraphs.getChildren().addAll(gridAndImage); // HBox
        radarGraphs.setAlignment(Pos.CENTER);

        contentBox.getChildren().addAll(distanceLabel, angleLabel, radarGraphs, openFile); // VBox
        contentBox.setAlignment(Pos.CENTER);
        
        Scene scene = new Scene(contentBox, 500, 250); // window dimentions
        stage.setScene(scene);
        stage.setTitle("Radar Graphs 1.0");
        stage.show();
        Thread.sleep(1000);
        
        Thread serialThread = new Thread(() -> {


            while (true) { // scanner loop
                try {           
                    Thread.sleep(serialDelay);
                    

                    if (!input.hasNextLine()) {
                        System.out.println("no line");   // retrys if no line is found
                        Thread.sleep(500);
                        continue;
                    }
                        
                    String intLine = input.nextLine();
                    String doubleLine = input.nextLine();
                    
                    if (rawAngle <= 0 || rawAngle >= 180) {
                        Utils.resetGrid(grid);                  // clears 2d grid oon angle 180 and 0
                        System.out.println("Reseting");
                    }

                    try {
                        rawAngle = Integer.parseInt(intLine);
                        rawDistance = Double.parseDouble(doubleLine);
                        System.out.println("Angle = " + rawAngle);   // gets angle and try to convert it
                        System.out.println("Distance = " + rawDistance);
                    } catch (NumberFormatException e1) {
                        try {
                            rawDistance = Double.parseDouble(intLine);
                            rawAngle = Integer.parseInt(doubleLine);
                            System.out.println("Angle = " + rawAngle);   
                            System.out.println("Distance = " + rawDistance);
                        } catch (NumberFormatException e2) {
                            System.out.println("Invalid input: " + intLine);
                        }
                    }

                    
                    Utils.writeImportantPoints(rawDistance, rawAngle); // sends distance and angle to a fileWriter 

                    Double mappedDistance = Utils.scale(rawDistance, 0, 200, 0, grid[0].length - 1);
                    Double mappedAngle = Utils.scale(rawAngle, 0, 180, 0, grid.length - 1);
                    

                    int distance = (int) Math.round(mappedDistance);
                    int angle = (int) Math.round(mappedAngle);

                    // UI update on JavaFX main Thread
                    Platform.runLater(() -> {
                        distanceLabel.setText("Distance:\t" + rawDistance); 
                        angleLabel.setText("Angle:\t" + rawAngle);
                        grid[angle][distance].setStyle("-fx-background-color: #3cff00ff;"); // highlights objects positions within the grid
                        rotate.setAngle(-rawAngle + 90); // rotates the bar to the angle of the servo motor
                    });
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
        });
        serialThread.start();
    }
}