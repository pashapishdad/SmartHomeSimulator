import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;

/**
 * Controller that will load the starting screen of the JavaFX application
 */
public class JavaFXController extends Application implements EventHandler<ActionEvent> {

    /**
     * Method starting up the application and loads in the first scene
     *
     * @param primaryStage The first stage of the application
     * @throws IOException Exception thrown when the application is unable to find the view files
     */
    @Override
    public void start(Stage primaryStage) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
        Scene scene = new Scene(root, 900, 750);
        scene.getStylesheets().add(getClass().getResource("/view/application.css").toExternalForm());

        //removing title bar from the window(stage)
        primaryStage.initStyle(StageStyle.UNDECORATED);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Main method which runs the program.
     *
     * @param args Console arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Overwrites the default handle method to prevent its use in the JavaFXController
     *
     * @param event The event that triggered this method
     */
    @Override
    public void handle(ActionEvent event) {
        // Do Nothing

    }
}
