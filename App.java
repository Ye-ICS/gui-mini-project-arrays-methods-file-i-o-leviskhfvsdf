import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Template JavaFX application.
 */
public class App extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage stage) {
        // Create components to add.
        VBox contentBox = new VBox();
        contentBox.setAlignment(Pos.CENTER);

        Label promptLabel = new Label();
        promptLabel.setText("Enter your thoughts");

        TextField thoughtsBox = new TextField();
        thoughtsBox.setMaxWidth(150);
        thoughtsBox.setPromptText("type here");
        
        TextArea messageBox = new TextArea();
        messageBox.setEditable(false);

        Button submissionBtn = new Button();
        submissionBtn.setText("Submit");

        // Set up reactions (aka callbacks).
        submissionBtn.setOnAction(event -> onSubmitThought(thoughtsBox, messageBox));

        // Add components to the content box.
        contentBox.getChildren().add(promptLabel);
        contentBox.getChildren().add(thoughtsBox);
        contentBox.getChildren().add(submissionBtn);
        contentBox.getChildren().add(messageBox);

        // Set up the window and display it.
        Scene scene = new Scene(contentBox, 300, 200);
        stage.setScene(scene);
        stage.setTitle("Amazing App 2000");
        stage.show();
    }

    /**
     * Handle the submission of a thought.
     * @param inputBox  The TextField where the user types their thought.
     * @param outputBox The TextArea where the submitted thoughts are displayed.
     */
    void onSubmitThought(TextField inputBox, TextArea outputBox) {
        String text = inputBox.getText();
        inputBox.clear();
        System.out.println("Interesting thought: " + text);
        outputBox.appendText("Interesting thought: " + text + "\n");
    }
}
