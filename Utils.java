import java.io.FileWriter;
import java.io.IOException;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

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

    static void proximityDisplay(Pane col[], double rawDistance) {

        int distance = (int) Math.round(scale(rawDistance, 0, 200, 0,col.length));

        for (int i = 0; i < col.length; i++) {
            col[i].setStyle("-fx-background-color: #2b2b2bff;");
        }
        
        for (int i = col.length; i < col.length; i--){
            if (distance == i ) {
                col[i].setStyle("-fx-background-color: #11ff11ff;");
            }
        }
    }

    /**
     * checks distance to determine if proximity is importantly close
     * @param rawDistance 
     * @param rawAngle
     */
    static void writeImportantPoints(double rawDistance, int rawAngle) {
        if (rawDistance < 3) {
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




// side game

    /**
     * runs a game on given stage
     * @param stage 
     */
    static void game(Stage stage) {

        final double fillChance = 0.7;
        int[] digets = {109, 97, 100, 101, 32, 98, 121, 32, 108, 101, 118, 105,}; StringBuilder b = new StringBuilder(); for (int num : digets) { b.append((char) num);}  String d = b.toString(); System.out.println("\n\n"+d);
        
            
        String onColor = "#ff0000ff"; // red
        String midColor = "#b823b0ff";
        String offColor = "#000000ff";
        GridPane contentBox = new GridPane();

        Button grid[][] = new Button[5][5];
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[0].length; x++) {
                
                double rand = Math.random();
                Button button = new Button();
                grid[y][x] = button;
                contentBox.add(button, y, x);

                final int row = y;
                final int col = x;

                button.setStyle("-fx-background-color: #000000ff;");
                button.setOpacity(0.3);
                button.setPrefSize(40, 40);

                if (rand > fillChance) {
                    toggle(grid, row, col, onColor, offColor, midColor);
                }

                button.setOnAction(event -> {
            
                    toggle(grid, row, col, onColor, offColor, midColor);     // center
                    if (row > 0) toggle(grid, row - 1, col, onColor, offColor, midColor); // up
                    if (row < 4) toggle(grid, row + 1, col, onColor, offColor, midColor); // down
                    if (col > 0) toggle(grid, row, col - 1, onColor, offColor, midColor); // left
                    if (col < 4)  toggle(grid, row, col + 1, onColor, offColor, midColor); // right
                });     
            }
        }
        contentBox.setTranslateX(5);
        Scene scene = new Scene(contentBox, 210, 210);
        stage.setScene(scene);
        stage.setTitle("Lights out");
        stage.show();
        
    }

    static void toggle(Button[][] grid, int r, int c, String on, String off, String mid) {

        String format = grid[r][c].getStyle().replace(" ", "");

        if (format.equals("-fx-background-color:" + on + ";")) {
            grid[r][c].setStyle("-fx-background-color: " + mid + ";");
        } else if (format.equals("-fx-background-color:" + mid + ";")) {
            grid[r][c].setStyle("-fx-background-color: " + off + ";");
        } else {
            grid[r][c].setStyle("-fx-background-color: " + on + ";");
        }
    }
}
