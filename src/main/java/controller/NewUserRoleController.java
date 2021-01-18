package controller;

import constants.UserRoles;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.json.JSONObject;
import org.json.JSONTokener;
import service.PermissionService;
import service.RoleService;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class NewUserRoleController {

    private String username;
    private String parentUser;
    @FXML
    private TextField newUserField;
    @FXML
    private ComboBox<String> roles;

    @FXML
    public void initialize() {
        parentUser = LoginInfoController.getUserParent();
        roles.getItems().addAll(RoleService.getAllRoles());
        roles.getSelectionModel().select(UserRoles.STRANGER.ordinal());
    }

    /**
     * This function will close the application
     *
     * @param event The event that called this function
     */
    public void close(MouseEvent event) {
        System.exit(0);
    }

    /**
     * Setter method to set the cached value of the username
     *
     * @param username The cached username for Main Menu
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Function to go back to the UserRole
     *
     * @param event The event that triggered this method
     * @throws IOException Thrown if the method is unable to locate the view resource
     */
    public void goToUserRole(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/userRoles.fxml"));
        Parent login = loader.load();
        Scene loginScene = new Scene(login);

        UserRolesController controller = loader.getController();
        controller.setUsername(username);

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(loginScene);
        window.show();
    }

    /**
     * Function to go create a userRole
     *
     * @param event The event that triggered this method
     * @throws IOException Thrown if the method is unable to locate the view resource
     */
    public void createUserRole(ActionEvent event) throws SQLException {
        String newUsername = newUserField.getText();
        AtomicBoolean existence = new AtomicBoolean(false);
        RoleService.getRoles(parentUser).forEach(r -> {
            if (r.getUsername().equals(newUsername)) {
                existence.set(true);
            }
        });
        if (Objects.nonNull(newUsername) && !existence.get()) {
            RoleService.createRole(parentUser, newUsername, roles.getValue());
            newUserField.setText("");
            roles.getSelectionModel().select(UserRoles.STRANGER.ordinal());
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "User has been created");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "User needs a name or already exists");
            alert.showAndWait();
        }
    }

    /**
     * Function to go import a userRole text file
     *
     * @param event The event that triggered this method
     * @throws IOException Thrown if the method is unable to locate the view resource
     */
    public void importUserRole(ActionEvent event) throws SQLException, IOException {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TEXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());
        if (Objects.isNull(file)) {
            return;
        }

        JSONTokener tokener = new JSONTokener(Files.readString(file.toPath()));
        JSONObject object = new JSONObject(tokener);

        RoleService.importRoles(object, parentUser);
        PermissionService.importPermissions(object);
    }
}
