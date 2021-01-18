package controller;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;
import constants.Season;
import constants.UserRoles;
import entity.Room;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.apache.commons.io.FileUtils;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;
import service.DatabaseService;
import service.HouseLayoutService;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class EditSimulationControllerTest extends ApplicationTest {

    EditSimulationController controller;
    FXMLLoader loader;

    static HashMap<String, Room> rooms;
    static DB db;

    @BeforeAll
    static void setupDB() throws ManagedProcessException, SQLException, IOException {
        DBConfigurationBuilder config = DBConfigurationBuilder.newBuilder();
        config.setPort(0);

        db = DB.newEmbeddedDB(config.build());
        db.start();
        db.createDB("test");

        final String databaseUrl = db.getConfiguration().getURL("test") + "?serverTimezone=UTC";
        DatabaseService.SetupDBController(databaseUrl);

        Flyway flyway = Flyway.configure().dataSource(databaseUrl, "root", "").load();
        flyway.migrate();

        DatabaseService.createNewUserRole("testUser0", "testUser0", UserRoles.STRANGER.toString());
        DatabaseService.createNewUserRole("testUser0", "testUser1", UserRoles.STRANGER.toString());
        DatabaseService.createNewUserRole("testUser0", "testUser2", UserRoles.STRANGER.toString());
        DatabaseService.createNewUserRole("testUser0", "testUser3", UserRoles.STRANGER.toString());

        MockedStatic<LoginInfoController> mock = Mockito.mockStatic(LoginInfoController.class);
        mock.when(LoginInfoController::getUserParent).thenReturn("testUser0");
        Room[] roomArray = HouseLayoutService.parseHouseLayout(FileUtils.getFile("src", "test", "resources", "houseLayout.txt"));

        rooms = new HashMap<>();
        for (Room room : roomArray) {
            rooms.put(room.getName(), room);
        }
        mock.when(LoginInfoController::getHouse).thenReturn(rooms);
    }

    @BeforeEach
    public void setup() throws IOException {
        loader = new FXMLLoader(getClass().getResource("/view/editSimulation.fxml"));
        loader.load();
        controller = loader.getController();

    }

    @Test
    public void create_and_set_user_location() {
        Map<String, String> map = EditSimulationController.getUserLocations();
        assertEquals(map.keySet().size(), 4);

        controller.setUsername("testUser1");
        ComboBox<String> selector = from((Node) loader.getRoot()).lookup("#roomsMove").queryComboBox();
        selector.getSelectionModel().select("Outside");
        controller.changeLocation(new ActionEvent());

        map = EditSimulationController.getUserLocations();
        assertEquals(map.keySet().size(), 4);
        assertEquals(map.get("testUser1"), "Outside");
    }

    @Test
    public void block_a_window() throws IOException {
        assertFalse(rooms.get("Bathroom").getWindows().get(0).getBlocking());

        ComboBox<String> windowSelector = from((Node) loader.getRoot()).lookup("#windows").queryComboBox();
        windowSelector.getSelectionModel().select("TOP");

        ComboBox<String> roomSelector = from((Node) loader.getRoot()).lookup("#roomsBlock").queryComboBox();
        roomSelector.getSelectionModel().select("Bathroom");
        roomSelector.getOnAction().handle(new ActionEvent());

        controller.windowsBlocked(new ActionEvent());

        assertTrue(rooms.get("Bathroom").getWindows().get(0).getBlocking());
    }

    @Test
    public void test_get_season() {
        Calendar calendarSummer = new Calendar.Builder().setDate(2020, 11, 1).build();
        Calendar calendarWinter = new Calendar.Builder().setDate(2020, 1, 1).build();

        assertEquals(Season.SUMMER, EditSimulationController.getCurrentSeason(calendarSummer));
        assertEquals(Season.WINTER, EditSimulationController.getCurrentSeason(calendarWinter));

        EditSimulationController.setSummerMonthEndCache(10);
        assertEquals(Season.OTHER, EditSimulationController.getCurrentSeason(calendarSummer));
    }

}
