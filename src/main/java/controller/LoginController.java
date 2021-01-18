package controller;

import javafx.animation.FillTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import service.LoginService;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class LoginController {
    /**
     * declaring variables
     */
    @FXML
    private TextField userD;
    @FXML
    private PasswordField passD;
    @FXML
    private Label displayMessage;

    private double xOffset = 0;
    private double yOffset = 0;

    /**
     * This function loads the forgot password page(scene) into the window(stage)
     *
     * @param event
     * @throws IOException
     */
    public void goToForgotPassword(ActionEvent event) throws IOException {
        Parent forgotPassword = FXMLLoader.load(getClass().getResource("/view/forgotPassword.fxml"));
        Scene forgotPasswordScene = new Scene(forgotPassword);

        // stage info
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(forgotPasswordScene);
        window.show();
    }

    /**
     * This function loads the sign up page(scene) into the window(stage)
     *
     * @param event
     * @throws IOException
     */
    public void goToSignUp(ActionEvent event) throws IOException {
        Parent signUp = FXMLLoader.load(getClass().getResource("/view/signUp.fxml"));
        Scene signUpScene = new Scene(signUp);

        // stage info
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(signUpScene);
        window.show();
    }

    /**
     * This function loads the loginInfo page(scene) into the window(stage)
     *
     * @param event
     * @throws IOException
     */
    public void loginInfo(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/loginInfo.fxml"));
        Parent login = loader.load();

        Scene loginScene = new Scene(login);

        LoginInfoController controller = loader.getController();

        Map<String, Object> userInfo = LoginService.login(userD.getText(), passD.getText());

        if (Objects.nonNull(userInfo)) {
            LoginInfoController.setUserParent(userInfo.get("username").toString());
            controller.setSelectedUser(userInfo.get("username").toString());
            controller.setUser(userInfo.get("firstname").toString() + " " + userInfo.get("lastname").toString());

            // stage info
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(loginScene);
            window.show();
        } else {
            // Create Popup with no user correlating to given username and password
            displayMessage.setText("Incorrect username and password");
        }
    }

    /**
     * This class creates a toggle switch.
     */
    private static class ToggleSwitch extends Parent {

        private final BooleanProperty switchedOn = new SimpleBooleanProperty(false);

        private final TranslateTransition translateAnimation = new TranslateTransition(Duration.seconds(0.25));
        private final FillTransition fillAnimation = new FillTransition(Duration.seconds(0.25));

        private final ParallelTransition animation = new ParallelTransition(translateAnimation, fillAnimation);

        public BooleanProperty switchedOnProperty() {
            return switchedOn;
        }

        public ToggleSwitch() {
            Rectangle background = new Rectangle(60, 30);
            background.setArcWidth(30);
            background.setArcHeight(30);
            background.setFill(Color.WHITE);
            background.setStroke(Color.LIGHTGRAY);

            Circle trigger = new Circle(15);
            trigger.setCenterX(15);
            trigger.setCenterY(15);
            trigger.setFill(Color.WHITE);
            trigger.setStroke(Color.LIGHTGRAY);

            DropShadow shadow = new DropShadow();
            shadow.setRadius(2);
            trigger.setEffect(shadow);

            translateAnimation.setNode(trigger);
            fillAnimation.setShape(background);

            getChildren().addAll(background, trigger);

            switchedOn.addListener((obs, oldState, newState) -> {
                boolean isOn = newState.booleanValue();
                translateAnimation.setToX(isOn ? 60 - 30 : 0);
                fillAnimation.setFromValue(isOn ? Color.RED : Color.GREEN);
                fillAnimation.setToValue(isOn ? Color.GREEN : Color.RED);

                animation.play();
            });

            setOnMouseClicked(event -> {
                switchedOn.set(!switchedOn.get());
            });
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
