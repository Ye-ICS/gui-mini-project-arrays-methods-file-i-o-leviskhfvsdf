import javafx.scene.layout.Pane;

public class Utils {

    static double scale(double value, double inMin, double inMax, double outMin, double outMax) {
        return (value - inMin) * (outMax - outMin) / (inMax - inMin) + outMin;
    }
    static void proximityDisplay(Pane col[], int distance) {
        
        col[0].setStyle("-fx-background-color: #2b2b2bff;");
        col[1].setStyle("-fx-background-color: #2b2b2bff;");
        col[2].setStyle("-fx-background-color: #2b2b2bff;");
        col[3].setStyle("-fx-background-color: #2b2b2bff;");
        col[4].setStyle("-fx-background-color: #2b2b2bff;");
        for (int i = 4; i < col.length; i--){
            if (distance == i ) {
                col[i].setStyle("-fx-background-color: #11ff11ff;");
            }
        }
    }
}
