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
 * Controller responsible for the control and flow of the SignUp scene
 */
public class SignUpController {
    /**
     * declaring variables
     */
    @FXML
    private TextField username;
    @FXML
    private TextField firstname;
    @FXML
    private TextField lastname;
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
        Parent loginPage = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
        Scene loginScene = new Scene(loginPage);

        // stage info
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(loginScene);
        window.show();
    }

    /**
     * processes the registration and displays a proper message
     *
     * @param event
     */
    public void processRegistration(ActionEvent event) {
        RegistrationStatus status = RegistrationService.registration(username.getText(), firstname.getText(), lastname.getText(), password.getText(), passwordVerification.getText());

        switch (status) {
            //changes the label's text to "User has been created"
            case USER_CREATED: {
                displayMessage.setTextFill(Color.BLACK);
                displayMessage.setText("User has been created");
                break;
            }
            //changes the label's text to "Passwords do not match"
            case PASSWORD_NOT_EQUAL: {
                displayMessage.setTextFill(Color.RED);
                displayMessage.setText("Passwords do not match");
                break;
            }
            //changes the label's text to "Username has already been taken"
            case NOT_UNIQUE_USERNAME: {
                displayMessage.setTextFill(Color.RED);
                displayMessage.setText("Username has already been taken");
                break;
            }
            //changes the label's text to "First name and Last name must not be empty"
            case NAME_IS_EMPTY: {
                displayMessage.setTextFill(Color.RED);
                displayMessage.setText("First name and Last name must not be empty");
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
