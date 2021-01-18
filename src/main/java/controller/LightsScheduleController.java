package controller;

import entity.ConsoleComponents;
import entity.Room;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.net.URL;
import java.text.ParseException;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Controller responsible for the scheduling of lights in away mode
 */
public class LightsScheduleController implements Initializable {

    /**
     * Declaring variables
     */
    @FXML
    private ComboBox<String> rooms;
    @FXML
    private ComboBox<Integer> startHour;
    @FXML
    private ComboBox<Integer> startMinutes;
    @FXML
    private ComboBox<Integer> endHour;
    @FXML
    private ComboBox<Integer> endMinutes;

    private Map<String, Room> house;

    private static LoginInfoController parentController;

    private final ObservableList<Integer> li_hour = FXCollections.observableArrayList(
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23);

    private final ObservableList<Integer> li_minute = FXCollections.observableArrayList(
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
            20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35,
            36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51,
            52, 53, 54, 55, 56, 57, 58, 59);

    /**
     * This function will add the rooms and numbers to the combo boxes
     *
     * @param location  The URL of the resource file
     * @param resources The set of resources used
     */
    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        house = LoginInfoController.getHouse();
        if (Objects.nonNull(house)) {
            rooms.getItems().addAll(house.keySet());
            rooms.getItems().add("Outside");
            rooms.getSelectionModel().selectFirst();
        }
        startHour.setItems(li_hour);
        startHour.getSelectionModel().selectFirst();
        startMinutes.setItems(li_minute);
        startMinutes.getSelectionModel().selectFirst();
        endHour.setItems(li_hour);
        endHour.getSelectionModel().selectFirst();
        endMinutes.setItems(li_minute);
        endMinutes.getSelectionModel().selectFirst();
    }

    /**
     * This function sets the parent controller as the loginInfoController
     *
     * @param loginInfoController set
     */
    public static void setParentController(LoginInfoController loginInfoController) {
        parentController = loginInfoController;
    }

    /**
     * This function passes the values inputted by the user to the parent controller
     *
     * @param actionEvent the event that triggers this function
     */
    public void confirm(ActionEvent actionEvent) throws ParseException {
        String room = rooms.getValue();
        String startTime = String.format("%02d:%02d", startHour.getValue(), startMinutes.getValue());
        String endTime = String.format("%02d:%02d", endHour.getValue(), endMinutes.getValue());

        String times = startTime + "-" + endTime;
        parentController.setRoomLightSchedule(room, times);
        LoginInfoController.consoleLogFile("New light schedule for " + startTime + " to " + endTime+".", ConsoleComponents.SHS);
        Stage stage = (Stage) rooms.getScene().getWindow();
        stage.close();
    }

    /**
     * This function closes the lightsSchedule pane
     *
     * @param actionEvent the event that triggers this function
     */
    public void cancel(ActionEvent actionEvent) {
        Stage stage = (Stage) rooms.getScene().getWindow();
        stage.close();
    }

}
