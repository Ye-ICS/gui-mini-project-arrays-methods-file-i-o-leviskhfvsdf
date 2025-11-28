import java.io.FileWriter;
import java.io.IOException;
import javafx.scene.layout.Pane;

public class Utils {

    static double scale(double value, double inMin, double inMax, double outMin, double outMax) {
        return (value - inMin) * (outMax - outMin) / (inMax - inMin) + outMin;
    }

    static void proximityDisplay(Pane col[], double rawDistance) {

        int distance = (int) Math.round(scale(rawDistance, 0, 200, 0, 4));

        for (int i = 0; i < col.length; i++) {
            col[i].setStyle("-fx-background-color: #2b2b2bff;");
        }
        
        for (int i = 4; i < col.length; i--){
            if (distance == i ) {
                col[i].setStyle("-fx-background-color: #11ff11ff;");
            }
        }
    }

    static void writeImportantPoints(double rawDistance, int rawAngle) {
        if (rawDistance < 7) {
            try {
                FileWriter writer = new FileWriter("importantDataPoints.txt", true);
                writer.write("recieved Distance of close proximity: " + rawDistance + "cm ");
                writer.write(" angle of " + rawAngle + "\n");
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
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
}
