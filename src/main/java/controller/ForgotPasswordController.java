package controller;

import constants.RegistrationStatus;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import service.RegistrationService;
import java.io.IOException;

/**
 * Controller responsible for the control and flow of the ForgotPassword scene
 */
public class ForgotPasswordController {
    /**
     * declaring variables
     */
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private PasswordField passwordVerification;
    @FXML
    private Label displayMessage;

    private double xOffset = 0;
    private double yOffset = 0;

    /**
     * This function loads the login page(scene) into the window(stage)
     *
     * @param event
     * @throws IOException
     */
    public void goToLoginPage(ActionEvent event) throws IOException {
        Parent forgotPassword = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
        Scene forgotPasswordScene = new Scene(forgotPassword);

        /**
         * Stage info
         */
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(forgotPasswordScene);
        window.show();
    }

    /**
     * This function update the user's password and shows the proper message.
     *
     * @param event
     */
    public void updatePassword(ActionEvent event) {
        RegistrationStatus status = RegistrationService.updatePassword(username.getText(), password.getText(), passwordVerification.getText());
        switch (status) {
            //changes the label's text to "Password has been updated"
            case PASSWORD_UPDATED: {
                displayMessage.setTextFill(Color.BLACK);
                displayMessage.setText("Password has been updated");
                break;
            }
            //changes the label's text to "Passwords do not match"
            case PASSWORD_NOT_EQUAL: {
                displayMessage.setTextFill(Color.RED);
                displayMessage.setText("Passwords do not match");
                break;
            }
            //changes the label's text to "Username not found"
            case USERNAME_NOT_FOUND: {
                displayMessage.setTextFill(Color.RED);
                displayMessage.setText("Username not found");
                break;
            }
            //changes the label's text to "Seems like an error occurred"
            default: {
                displayMessage.setTextFill(Color.RED);
                displayMessage.setText("Seems like an error occurred");
            }
        }
    }

    /**
     * This function will close the application
     *
     * @param event
     * @throws IOException
     */
    public void close(MouseEvent event) throws IOException {
        System.exit(0);
    }

    /**
     * Gets the location of a mouse.
     *
     * @param event
     */
    public void getLocation(MouseEvent event) {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }

    /**
     * Changes the location of the window(stage) based on the mouse location..
     *
     * @param event
     */
    public void move(MouseEvent event) {
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setX(event.getScreenX() - xOffset);
        window.setY(event.getScreenY() - yOffset);
    }
}
