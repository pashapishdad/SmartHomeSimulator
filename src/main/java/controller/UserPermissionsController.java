package controller;

import constants.UserRoles;
import entity.CommandType;
import entity.PermissionType;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.EnumUtils;
import service.PermissionService;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Controller responsible for the view for User Permissions
 */
public class UserPermissionsController {

    /**
     * Values is the location for the command and their permissions
     */
    @FXML
    public AnchorPane values;
    @FXML
    private Label title;

    private static String username;
    private String menuUsername;
    private List<CommandType> commandTypeList;
    private List<PermissionType> permissionTypeList;
    private Map<CommandType, PermissionType> currentPermissions;
    private static Map<String, Map<CommandType, PermissionType>> userPermissions;

    public static void setUsername(String newUsername) {
        username = newUsername;
    }

    public void setMenuUsername(String menuUsername) {
        this.menuUsername = menuUsername;
    }

    public static Map<String, Map<CommandType, PermissionType>> getUserPermissions() {
        return userPermissions;
    }

    public static void setUserPermissions(Map<String, Map<CommandType, PermissionType>> userPermissions) {
        UserPermissionsController.userPermissions = userPermissions;
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    /**
     * On initialization, add the grid of information into values
     */
    @FXML
    public void initialize() {
        if (Objects.isNull(userPermissions)) {
            userPermissions = new HashMap<>();
        }
        commandTypeList = EnumUtils.getEnumList(CommandType.class);
        permissionTypeList = EnumUtils.getEnumList(PermissionType.class);
        currentPermissions = userPermissions.get(username);
        if (Objects.isNull(currentPermissions)) {
            currentPermissions = PermissionService.getDefaultPermissions(UserRoles.valueOf(username));
        }
        Set<CommandType> difference = SetUtils.difference(new HashSet<>(commandTypeList), currentPermissions.keySet());
        difference.forEach(commandType -> {
            currentPermissions.put(commandType, PermissionType.RESTRICTED);
        });
        values.getChildren().add(processRows());
    }

    /**
     * Method that will create the grid placed in the {@link AnchorPane}.
     *
     * @return The grid pane used by the display
     */
    private GridPane processRows() {
        AtomicInteger index = new AtomicInteger();
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        commandTypeList.forEach(commandType -> {
            gridPane.addRow(gridPane.getRowCount(), createLabel(commandType.toString(), index.get()), createRoleComboBox(currentPermissions.get(commandType), index.get()));
            index.getAndIncrement();
        });
        return gridPane;
    }

    /**
     * Creates a Label with the username
     *
     * @param user  The username placed in the label
     * @param index The index value used for the label's ID
     * @return The label with the username and ID with the index
     */
    private Node createLabel(final String user, final int index) {
        Label userLabel = new Label();
        userLabel.setMinWidth(100);
        userLabel.setId("gridLabel" + index);
        userLabel.setText(user);
        return userLabel;
    }

    /**
     * Creates the drop down box to select and change a user's role
     *
     * @param permissionType  The current permission level of the user.
     * @param index The index used to create the ID used to fetch its linked username label's value.
     * @return The drop down box with the user's current role as the default value and the options available.
     */
    private Node createRoleComboBox(final PermissionType permissionType, final int index) {
        ComboBox<PermissionType> box = new ComboBox<>(FXCollections.observableList(permissionTypeList));
        box.setId("gridBox" + index);
        box.getSelectionModel().select(permissionType.ordinal());
        box.valueProperty().addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends PermissionType> observable, PermissionType oldValue, PermissionType newValue) {
                if (Objects.nonNull(newValue) && !newValue.equals(oldValue)) {
                    String userLabelName = ((Label) values.lookup("#gridLabel" + index)).getText();
                    currentPermissions.put(CommandType.valueOf(userLabelName), newValue);
                }
            }
        });
        return box;
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

        userPermissions.put(username, currentPermissions);

        UserRolesController controller = loader.getController();
        controller.setUsername(menuUsername);

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(loginScene);
        window.show();
    }

    /**
     * This function will close the application
     *
     * @param event The event that called this function
     */
    public void close(MouseEvent event) {
        System.exit(0);
    }
}
