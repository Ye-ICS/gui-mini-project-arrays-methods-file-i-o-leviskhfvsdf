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
import com.fazecast.jSerialComm.SerialPort;
import java.io.FileWriter;
import java.io.IOException;
import javafx.scene.transform.Rotate;

public class App extends Application {
    final static String userSerialPort = "COM13";
    public static void main(String[] args) {
        launch(args);
    }
            Double rawDistance = 0.0;
            int rawAngle = 0;

    public void start(Stage stage) throws InterruptedException {


        SerialPort port = SerialPort.getCommPort(userSerialPort);
        port.setBaudRate(9600);
        port.openPort();

        Pane col[] = new Pane[5];
        VBox proximityDisplay = new VBox();
        for (int i = 0; i < 5; i++) {
            Pane light = new Pane();
            col[i] = light;
            proximityDisplay.getChildren().addAll(light);
            light.setStyle("-fx-background-color: #ff0101ff;");

            light.setOpacity(0.3);
            light.setPrefSize(20,20);
        }
        proximityDisplay.setTranslateY(7);
        proximityDisplay.setTranslateX(20);


        GridPane gridLayout = new GridPane();
        HBox radarContainer = new HBox();
        StackPane stack = new StackPane();
        VBox legend = new VBox();

        for (int i = 0; i < 5; i++) {
            Label level = new Label("distance: " + i * 5 + " - " +  i * 10);
            legend.getChildren().add(level);

        }
        legend.setTranslateX(40);
        legend.setTranslateY(15);
        
        Pane grid[][] = new Pane[18][10];
        for (int y = 0;y < grid.length; y++)
         for (int x = 0; x < grid[0].length; x++) {
       
            Pane tile = new Pane();
            grid[y][x] = tile;
            gridLayout.add(tile, y, x);
            tile.setStyle("-fx-background-color: #ffffffff;");
            tile.setOpacity(0.3);
            tile.setPrefSize(11,11);
                    
         }
        gridLayout.setTranslateX(0);
        gridLayout.setTranslateY(5);

        Scanner input = new Scanner(port.getInputStream());
        
        Label distanceLabel = new Label("- Distance:");
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
        
        VBox contentBox = new VBox(10);

        stack.getChildren().addAll(semiCircle, gridLayout, displayBar);
        radarContainer.getChildren().addAll(stack, proximityDisplay, legend);
        contentBox.getChildren().addAll(distanceLabel, angleLabel, radarContainer);

        Scene scene = new Scene(contentBox, 400, 200);
        stage.setScene(scene);
        stage.setTitle("Radar Graph 1.0");
        stage.show();
        Thread.sleep(2500);
          
        Thread serialThread = new Thread(() -> {

            int distance = -1;
            int angle = -1;

            while (input.hasNextLine()) {
                try {   
                        
                    if (angle >= 0 || distance >= 0) {
                        grid[angle][distance].setStyle("-fx-background-color: #ffffffff;");
                    }

                    Thread.sleep(300);

                     try {
                        rawAngle = Integer.parseInt(input.nextLine());
                    } catch (NumberFormatException e) {
                        System.out.println(e);
                    }
                    try {
                        rawDistance = Double.parseDouble(input.nextLine());
                    } catch (NumberFormatException e) {
                       System.out.println(e);
                    }
                    
                
                    System.out.println("Distance received: " + rawDistance);
                    System.out.println("Angle received: " + rawAngle);


                    // UI update on JavaFX Application Thread
                    Platform.runLater(() -> {
                        distanceLabel.setText("Distance: " + rawDistance);
                        angleLabel.setText("Angel: " + rawAngle);
                    });
    
                    Double mappedDistance = Utils.scale(rawDistance, 0, 200, 0, grid[0].length - 1);
                    Double mappedAngle = Utils.scale(rawAngle, 0, 180, 0, grid.length - 1);
                    
                    distance = (int) Math.round(mappedDistance);
                    angle = (int) Math.round(mappedAngle);
                    
                    rotate.setAngle(-rawAngle + 90);
                    Utils.proximityDisplay(col, distance);

                    grid[angle][distance].setStyle("-fx-background-color: #000000ff;");
                    Thread.sleep(300);
                    if (rawDistance < 5) {
                        try {
                            FileWriter writer = new FileWriter("importantDataPoints.txt", true);
                            writer.write("recieved Distance of close proximity: " + rawDistance + "cm ");
                            writer.write(" angle of " + rawAngle + "\n");
                            writer.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
        });
        serialThread.start();
    }
}

