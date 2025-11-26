import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.Scanner;
import com.fazecast.jSerialComm.SerialPort;


public class App extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage stage) throws InterruptedException {

        SerialPort port = SerialPort.getCommPort("COM13");
        port.setBaudRate(9600);
        port.openPort();

        GridPane gridLayout = new GridPane();

        StackPane stack = new StackPane();

        Button grid[][] = new Button[5][5];
        for (int y = 0;y < grid.length; y++)
         for (int x = 0; x < grid.length; x++) {
       
                Button button = new Button();
                grid[y][x] = button;
                gridLayout.add(button, y, x);
                button.setOpacity(0.7);
                button.setPrefSize(20,20);
                
            
         }
        
        Scanner input = new Scanner(port.getInputStream());
        
        Label distanceLabel = new Label();

        Label angleLabel = new Label();
        
        Image img = new Image("file:C:\\Users\\martinln\\OneDrive - Limestone DSB\\computer coding class g11\\unit 2\\gui-mini-project-arrays-methods-file-i-o-leviskhfvsdf\\creatingsemicircle2013-06.png");
        ImageView imageView = new ImageView(img);
        imageView.setFitWidth(200);
        imageView.setPreserveRatio(true);

        VBox contentBox = new VBox(10);

        stack.getChildren().addAll(imageView, gridLayout);
        contentBox.getChildren().addAll(distanceLabel, angleLabel, stack);

        Scene scene = new Scene(contentBox, 400, 400);
        stage.setScene(scene);
        stage.setTitle("Radar");
        stage.show();
        

        Thread serialThread = new Thread(() -> {

        
            while (true) {
                try {   
                        Thread.sleep(2000);
                        
                        String distance = input.nextLine();
                        String angle = input.nextLine();

                        System.out.println("Distance received: " + distance);
                        System.out.println("Angle received: " + angle);


                        // UI update must be done on JavaFX Application Thread
                        Platform.runLater(() -> {
                            distanceLabel.setText("Distance: " + distance);
                            angleLabel.setText("Angel: " + angle);
                        });

                        double mappedDistance = MathUtils.scale(distance, 0, 200, 0, 5);
                        Double mappedAngle = MathUtils.scale(angle, 0, 200, 0, 5);
                        
                        int Distance = (int) Math.round(mappedDistance);
                        int Angle = (int) Math.round(mappedAngle);

                        grid[Angle][Distance].setStyle("-fx-background-color: #000000ff;");

                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
        });
        serialThread.start();
    }
}

