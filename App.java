import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import java.util.Scanner;
import com.fazecast.jSerialComm.SerialPort;

public class App extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage stage) throws InterruptedException {

        SerialPort port = SerialPort.getCommPort("COM10");
        port.setBaudRate(9600);
        port.openPort();

        if (!port.openPort()) {
            System.out.println("Failed to open port!");
            return;
        }
        System.out.println("Opened port!");

        Scanner input = new Scanner(port.getInputStream());
        
        Label distanceLabel = new Label("distance:   ");
        // Create components to add.
        VBox contentBox = new VBox();

        contentBox.getChildren().addAll(distanceLabel);
        // Set up the window and display it.
        Scene scene = new Scene(contentBox, 300, 200);
        stage.setScene(scene);
        stage.setTitle("Radar");
        stage.show();
        

        Thread serialThread = new Thread(() -> {

           
            while (true) {
                try {   
                        Thread.sleep(1500);
                        
                        String distance = input.nextLine();
                        String angle = input.nextLine();

                        System.out.println("Distance received: " + distance);
                        System.out.println("Angle received: " + angle);


                        // UI update must be done on JavaFX Application Thread
                        Platform.runLater(() -> {
                            distanceLabel.setText("Distance: " + distance);
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

