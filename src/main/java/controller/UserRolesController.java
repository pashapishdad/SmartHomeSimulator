package controller;

import constants.UserRoles;
import entity.UserRole;
import interfaces.SubController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;
import service.RegistrationService;
import service.RoleService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Controller responsible for the view for User Control
 */
public class UserRolesController implements SubController {

    /**
     * Values is the location for the username label, the role box, and the remove button
     */
    @FXML
    public AnchorPane values;

    /**
     * Cached value for the name displayed in the main menu
     */
    private String username;
    private String parentUser;
    private static AtomicInteger indexCache = new AtomicInteger();

    /**
     * On initialization, add the grid of information into values
     */
    @FXML
    public void initialize() {
        parentUser = LoginInfoController.getUserParent();
        values.getChildren().add(processRows());
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
     * Button function to go back to the Main Menu
     *
     * @param event The event that triggered this method
     * @throws IOException Thrown if the method is unable to locate the view resource
     */
    public void goToLoginInfo(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/loginInfo.fxml"));
        Parent login = loader.load();
        Scene loginScene = new Scene(login);

        LoginInfoController controller = loader.getController();
        controller.setUser(username);

        Stage stage = (Stage) values.getScene().getWindow();
        stage.setScene(loginScene);
        stage.show();
    }

    /**
     * Button function to go back to the User Role creation scene
     *
     * @param event The event that triggered this method
     * @throws IOException Thrown if the method is unable to locate the view resource
     */
    public void goToCreate(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/newUserRole.fxml"));
        Parent login = loader.load();
        Scene loginScene = new Scene(login);

        NewUserRoleController controller = loader.getController();
        controller.setUsername(username);

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(loginScene);
        window.show();
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
     * Method that will create the grid placed in the {@link AnchorPane}.
     * Also calls the {@link RoleService} to obtain the Users and Roles.
     *
     * @return The grid pane used by the display
     */
    private GridPane processRows() {
        AtomicInteger index = new AtomicInteger();
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        List<UserRole> userRoles = RoleService.getRoles(parentUser);
        if (Objects.nonNull(userRoles)) {
            userRoles.forEach(result -> {
                try {
                    gridPane.addRow(gridPane.getRowCount(), createUserLabel(result.getUsername(), index.get()), createRoleComboBox(result.getRole().toString(), index.get()), createPermissionsButton(index.get()), createDeleteButton(index.get()));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                index.getAndIncrement();
            });
        }
        indexCache.set(index.get());
        return gridPane;
    }

    /**
     * Creates a Label with the username
     *
     * @param user  The username placed in the label
     * @param index The index value used for the label's ID
     * @return The label with the username and ID with the index
     */
    private Node createUserLabel(final String user, final int index) {
        Label userLabel = new Label();
        userLabel.setMinWidth(100);
        userLabel.setId("gridLabel" + index);
        userLabel.setText(user);
        return userLabel;
    }

    /**
     * Creates the drop down box to select and change a user's role
     *
     * @param role  The role for the user, possible values defined in {@link UserRoles}.
     * @param index The index used to create the ID used to fetch its linked username label's value.
     * @return The drop down box with the user's current role as the default value and the options available.
     */
    private Node createRoleComboBox(final String role, final int index) {
        ComboBox<String> box = new ComboBox<>(RoleService.getAllRoles());
        box.setId("gridBox" + index);
        box.getSelectionModel().select(UserRoles.valueOf(role).ordinal());
        box.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (Objects.nonNull(newValue) && !newValue.equals(oldValue)) {
                    String userLabelName = ((Label) values.lookup("#gridLabel" + index)).getText();
                    RoleService.changeRole(parentUser, userLabelName, newValue);
                }
            }
        });
        return box;
    }

    /**
     * Creates the button to click in order to delete the user
     *
     * @param index The index used to create the ID used to fetch its linked username label's value.
     * @return The delete button
     */
    private Node createDeleteButton(final int index) throws FileNotFoundException {
//        Button deleteButton = new Button();
//        deleteButton = new Button(new FileInputStream("src/main/resources/Images/deleteIcon.png"), 60, 14, true, false);
//        FileInputStream input = new FileInputStream("resources/images/iconmonstr-home-6-48.png");
        Image image = new Image(new FileInputStream("src/main/resources/Images/deleteIcon.png"), 60, 14, true, false);
        ImageView imageView = new ImageView(image);
        Button deleteButton = new Button("", imageView);
        deleteButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Label userToDelete = ((Label) values.lookup("#gridLabel" + index));
                ComboBox comboBoxToDelete = ((ComboBox) values.lookup("#gridBox" + index));
                values.lookup("#gridBox" + index).setDisable(true);
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete user " + userToDelete.getText() + "?", ButtonType.YES, ButtonType.NO);
                alert.showAndWait();
                if (alert.getResult() == ButtonType.YES) {
                    RegistrationService.deleteUser(parentUser, userToDelete.getText());
                    userToDelete.setTextFill(Color.LIGHTGRAY);
                    comboBoxToDelete.setDisable(true);
                    deleteButton.setText("Deleted");
                    deleteButton.setDisable(true);
                }
            }
        });
        return deleteButton;
    }

    /**
     * Creates the button to click in order to go change user's permissions
     *
     * @param index The index used to create the ID used to fetch its linked username label's value.
     * @return The permissions button
     */
    private Node createPermissionsButton(final int index) {
        Button permissionButton = new Button();
        permissionButton.setText("Permissions");
        permissionButton.setId("gridPerms" + index);
        permissionButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("/view/userPermissions.fxml"));
                    String user = ((Label) values.lookup("#gridLabel" + index)).getText();
                    UserPermissionsController.setUsername(user);

                    Parent login = loader.load();

                    UserPermissionsController controller = loader.getController();
                    controller.setMenuUsername(username);
                    controller.setTitle("User Permissions for: " + username);

                    Scene loginScene = new Scene(login);

                    Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    window.setScene(loginScene);
                    window.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        return permissionButton;
    }

    /**
     * Method responsible for exporting current user and role list into a txt file
     *
     * @param event The event that triggered this function
     */
    public void exportList(ActionEvent event) {
        AtomicInteger index = indexCache;
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extensionFilter);
        JSONArray jsonArray = new JSONArray();

        for (int i = 0; i < index.get(); i++) {
            Label user = ((Label) values.lookup("#gridLabel" + i));
            ComboBox role = ((ComboBox) values.lookup("#gridBox" + i));
            JSONObject object = new JSONObject();

            object.put("username", user.getText());
            object.put("role", role.getSelectionModel().getSelectedItem().toString());

            jsonArray.put(object);
        }
        JSONObject allUserRoles = new JSONObject();
        allUserRoles.put("users", jsonArray);
        allUserRoles.put("permissions", UserPermissionsController.getUserPermissions());

        File file = fileChooser.showSaveDialog(values.getParent().getScene().getWindow());
        if (Objects.nonNull(file)) {
            saveToFile(allUserRoles, file);
        }
    }

    /**
     * Method responsible for saving a JSON object onto a file
     *
     * @param json The JSON object to save
     * @param file The file to save into
     */
    protected void saveToFile(final JSONObject json, final File file) {
        try {
            PrintWriter writer = new PrintWriter(file);
            writer.println(json);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
