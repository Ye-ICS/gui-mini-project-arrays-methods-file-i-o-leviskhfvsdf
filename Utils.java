import java.io.FileWriter;
import java.io.IOException;
import java.sql.Time;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Utils {

    /**
     * @param value 
     * @param inMin values lowest form
     * @param inMax values highest form
     * @param outMin newValues lowest form
     * @param outMax newValues highest form
     * @return a newValue between givin parameters.
     */
    static double scale(double value, double inMin, double inMax, double outMin, double outMax) {
        return (value - inMin) * (outMax - outMin) / (inMax - inMin) + outMin;
    }
    
    /**
     * checks distance to determine if proximity is importantly close
     * @param rawDistance 
     * @param rawAngle
     */
    static void writeImportantPoints(double rawDistance, int rawAngle) {
        if (rawDistance < 4) { // checks for close range distances only
            try {
                FileWriter writer = new FileWriter("importantDataPoints.txt", true);
                writer.write("recieved Distance of close proximity: " + rawDistance + "cm "); // sends found distance and angle
                writer.write(" angle of " + rawAngle + "\n");
                writer.close();
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }
    static void resetGrid(Pane[][] grid) {
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[0].length; x++) {
                grid[y][x].setStyle("-fx-background-color: #312b2bff;");
            }
        }
    }

    static void file(Stage stage) throws FileNotFoundException {

        VBox contentBox = new VBox();
        ScrollPane scroll = new ScrollPane();
        
        File file = new File("importantDataPoints.txt");
        Scanner sc = new Scanner(file);
        
        while (sc.hasNext()) {
            Label strand = new Label(sc.nextLine()); // reads contents of file and puts them in a scrollPane
            contentBox.getChildren().add(strand);
        }

        contentBox.setAlignment(Pos.TOP_CENTER); // good alignment for scrollPane
        scroll.setContent(contentBox);
        Scene scene = new Scene(scroll, 350, 500);
        stage.setScene(scene);
        stage.setTitle("Proximity Data");
        stage.show();
    }
}
