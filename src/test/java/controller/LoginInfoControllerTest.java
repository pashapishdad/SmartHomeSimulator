package controller;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import org.apache.commons.io.FileUtils;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationTest;
import service.DatabaseService;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginInfoControllerTest extends ApplicationTest {

    LoginInfoController controller;
    FXMLLoader loader;

    EditSimulationController editSimulationController;
    FXMLLoader editSimulationLoader;

    static DB db;
    static MockedStatic<EditSimulationController> mock;

    @BeforeAll
    static void setupDB() throws ManagedProcessException, SQLException {
        DBConfigurationBuilder config = DBConfigurationBuilder.newBuilder();
        config.setPort(0);

        db = DB.newEmbeddedDB(config.build());
        db.start();
        db.createDB("test");

        final String databaseUrl = db.getConfiguration().getURL("test") + "?serverTimezone=UTC";
        DatabaseService.SetupDBController(databaseUrl);

        Flyway flyway = Flyway.configure().dataSource(databaseUrl, "root", "").load();
        flyway.migrate();

        mock = Mockito.mockStatic(EditSimulationController.class);
        mock.when(EditSimulationController::getUserLocations).thenReturn(null);
    }

    @BeforeEach
    public void setup() throws IOException {
        LoginInfoController.setUsername("user1");
        loader = new FXMLLoader(getClass().getResource("/view/loginInfo.fxml"));
        loader.load();
        controller = loader.getController();
        PrintWriter printWriter = new PrintWriter(FileUtils.getFile("src", "main", "resources", "consoleLogs.txt"));
        printWriter.print("");
        printWriter.close();
    }

    @Test
    public void should_change_date_and_time() {
        controller.setTime("2020 - January - 01 12:00:00");
        controller.moveClock();
        assertThat(controller.getDate(), is(equalTo("2020 - January - 01")));
        assertThat(controller.getTime(), is(equalTo("12:00:01")));
    }

    @Test
    public void should_change_temperature() {
        Event.fireEvent(controller.getTemperature(), new MouseEvent(MouseEvent.MOUSE_CLICKED, 0,
                0, 0, 0, MouseButton.PRIMARY, 1, true, true, true, true,
                true, true, true, true, true, true, null));
        controller.getTemperatureField().setText("90");
        controller.changeTemperatureOnEnter();
        assertEquals("90", controller.getTemperature().getText());
    }

    @Test
    public void should_start_simulator() {
        AnchorPane parent = controller.getAnc();

        LoginInfoController.ToggleSwitch selector = from(parent).lookup("#toggleSwitch").query();

        Text text = from(parent).lookup("#toggleText").queryText();

        assertEquals("OFF", text.getText());
        Event.fireEvent(selector, new MouseEvent(MouseEvent.MOUSE_CLICKED, 0,
                0, 0, 0, MouseButton.PRIMARY, 1, true, true, true, true,
                true, true, true, true, true, true, null));
        assertEquals("ON", text.getText());
    }

    @Test
    public void should_allow_away_mode() {
        controller.onMouseClickAwayToggleON(null);
        assertTrue(LoginInfoController.isAwayMode());
    }

    @Test
    public void should_not_allow_away_mode() {
        mock.when(EditSimulationController::getUserLocations).thenReturn(Map.of("user1", "not outside"));

        controller.onMouseClickAwayToggleON(null);
        assertFalse(LoginInfoController.isAwayMode());
    }

    @Test
    public void should_set_time_before_alert_on_user_input_away_mode() {
        mock.when(EditSimulationController::getUserLocations).thenReturn(Map.of("user1", "Outside"));
        controller.onMouseClickAwayToggleON(null);
        controller.getTimeBeforeAlertField().setText("10");
        controller.onSetTimeBeforeAlert();
        assertEquals("10", controller.getTimeBeforeAlert());
    }

    @Test
    public void should_notify_users_when_motion_detected_away_mode() throws IOException {
        editSimulationLoader = new FXMLLoader(getClass().getResource("/view/editSimulation.fxml"));
        editSimulationLoader.load();
        editSimulationController = editSimulationLoader.getController();

        mock.when(EditSimulationController::getUserLocations).thenReturn(Map.of("user1", "Outside"));
        controller.onMouseClickAwayToggleON(null);
        controller.getTimeBeforeAlertField().setText("10");

        Map<String, String> map = new HashMap<>();
        map.put("user1", "Kitchen");
        mock.when(EditSimulationController::getUserLocations).thenReturn(map);
        editSimulationController.getRoomsMove().setValue("Kitchen");
        editSimulationController.changeLocation(new ActionEvent());

        Scanner scanner = new Scanner(FileUtils.getFile("src", "main", "resources", "consoleLogs.txt"));
        String line = "";
        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
        }
        assertEquals("user1 was detected in the Kitchen during Away Mode. " +
                "Authorities will be alerted in 10 minutes.", line.substring(22));
    }
}
