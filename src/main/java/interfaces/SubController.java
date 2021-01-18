package interfaces;

import javafx.event.ActionEvent;
import java.io.IOException;

/**
 * Adapter interface for controllers that can go to LoginInfo page
 */
public interface SubController extends MainController {

    /**
     * This function loads the login info page(scene) into the window(stage)
     *
     * @param event The event that called this function
     * @throws IOException Thrown if the view file cannot be read
     */
    void goToLoginInfo(ActionEvent event) throws IOException;
}
