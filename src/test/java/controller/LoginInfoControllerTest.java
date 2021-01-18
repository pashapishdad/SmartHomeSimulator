package controller;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;
import constants.Season;
import entity.Door;
import entity.Room;
import entity.Window;
import entity.Zone;
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
import service.HouseLayoutService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.any;
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
        mock.when(() -> EditSimulationController.getCurrentSeason(Mockito.any(Calendar.class))).thenReturn(Season.SUMMER);
    }

    @BeforeEach
    public void setup() throws IOException {
        LoginInfoController.setUsername("user1");
        loader = new FXMLLoader(getClass().getResource("/view/loginInfo.fxml"));
        loader.load();
        controller = loader.getController();

        AnchorPane parent = controller.getAnc();
        LoginInfoController.ToggleSwitch selector = from(parent).lookup("#toggleSwitch").query();
        Event.fireEvent(selector, new MouseEvent(MouseEvent.MOUSE_CLICKED, 0,
                0, 0, 0, MouseButton.PRIMARY, 1, true, true, true, true,
                true, true, true, true, true, true, null));

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
        Event.fireEvent(selector, new MouseEvent(MouseEvent.MOUSE_CLICKED, 0,
                0, 0, 0, MouseButton.PRIMARY, 1, true, true, true, true,
                true, true, true, true, true, true, null));

        Text text = from(parent).lookup("#toggleText").queryText();

        assertEquals("OFF", text.getText());
        Event.fireEvent(selector, new MouseEvent(MouseEvent.MOUSE_CLICKED, 0,
                0, 0, 0, MouseButton.PRIMARY, 1, true, true, true, true,
                true, true, true, true, true, true, null));
        assertEquals("ON", text.getText());
    }

    /**
     * Use case 4, Delivery 2
     */
    @Test
    public void should_open_close_door() throws IOException {
        File file = new File("src/test/resources/houseLayout.txt");
        Room[] roomArray = HouseLayoutService.parseHouseLayout(file);
        HashMap<String, Room> rooms = new HashMap<>();
        for (Room room : roomArray) {
            rooms.put(room.getName(), room);
        }
        Room room = rooms.get("Living Room");
        ArrayList<Door> doorsList = room.getDoors();
        HashMap<String, Door> doors = new HashMap<>();
        for (Door door : doorsList) {
            doors.put(door.getPosition().toString(), door);
        }
        Door door = doors.get("BOTTOM");
        door.setOpenDoor(true);
        assertEquals(door.getOpenDoor(), true);

        door.setOpenDoor(false);
        assertEquals(door.getOpenDoor(), false);
    }
    
    @Test
    public void should_lock_door() throws IOException {
        File file = new File("src/test/resources/houseLayout.txt");
        Room[] roomArray = HouseLayoutService.parseHouseLayout(file);
        HashMap<String, Room> rooms = new HashMap<>();
        for (Room room : roomArray) {
            rooms.put(room.getName(), room);
        }
        Room room = rooms.get("Living Room");
        ArrayList<Door> doorsList = room.getDoors();
        HashMap<String, Door> doors = new HashMap<>();
        for (Door door : doorsList) {
            doors.put(door.getPosition().toString(), door);
        }
        Door door = doors.get("BOTTOM");
        door.setOpenDoor(true);
        door.setLock(true);
        assertEquals(door.getLock(), true);
        assertEquals(door.getOpenDoor(), false);
        
        door.setLock(false);
        assertEquals(door.getLock(), false);
    }

    /**
     * Use case 5, Delivery 2
     */
    @Test
    public void should_open_close_window() throws IOException {
        File file = new File("src/test/resources/houseLayout.txt");
        Room[] roomArray = HouseLayoutService.parseHouseLayout(file);
        HashMap<String, Room> rooms = new HashMap<>();
        for (Room room : roomArray) {
            rooms.put(room.getName(), room);
        }
        Room room = rooms.get("Living Room");
        ArrayList<Window> windowsList = room.getWindows();
        HashMap<String, Window> windows = new HashMap<>();
        for (Window window : windowsList) {
            windows.put(window.getPosition().toString(), window);
        }
        Window window = windows.get("RIGHT");
        window.setBlocking(true);
        window.setOpenWindow(true);
        assertEquals(window.getOpenWindow(), false);

        window.setBlocking(false);
        window.setOpenWindow(true);
        window.setBlocking(true);
        window.setOpenWindow(false);
        assertEquals(window.getOpenWindow(), true);
    }

    /**
     * Use case 6, Delivery 2
     */
    @Test
    public void should_turn_on_off_light() throws IOException {
        File file = new File("src/test/resources/houseLayout.txt");
        Room[] roomArray = HouseLayoutService.parseHouseLayout(file);
        HashMap<String, Room> rooms = new HashMap<>();
        for (Room room : roomArray) {
            rooms.put(room.getName(), room);
        }
        Room room = rooms.get("Living Room");
        room.setLightsOn(1);
        assertEquals(room.getLightsOn(), 1);

        room.setLightsOn(0);
        assertEquals(room.getLightsOn(), 0);
    }
    
    /**
     * Use case 7, Delivery 2
     */
    @Test
    public void should_turn_on_auto_mode() throws IOException{
        controller.autoModeOnClick(null);
        assertTrue(controller.getAutoMode());
        controller.autoModeOnClick(null);
    }

    /**
     * Use case 8, Delivery 2
     */
    @Test
    public void should_allow_away_mode() throws FileNotFoundException, ParseException {
        controller.onMouseClickAwayToggleON(null);
        assertTrue(LoginInfoController.isAwayMode());
    }

    /**
     * Use case 8, Delivery 2
     */
    @Test
    public void should_not_allow_away_mode() throws FileNotFoundException, ParseException {
        mock.when(EditSimulationController::getUserLocations).thenReturn(Map.of("user1", "not outside"));
        controller.onMouseClickAwayToggleON(null);
        assertFalse(LoginInfoController.isAwayMode());
    }

    /**
     * Use case 9, Delivery 2
     */
    @Test
    public void should_notify_users_when_motion_detected_away_mode() throws IOException, ParseException {
    	editSimulationLoader = new FXMLLoader(getClass().getResource("/view/editSimulation.fxml"));
        editSimulationLoader.load();
        editSimulationController = editSimulationLoader.getController();

        mock.when(EditSimulationController::getUserLocations).thenReturn(Map.of("user1", "Outside"));
        controller.onMouseClickAwayToggleON(null);
        controller.getTimeBeforeAlertField().setText("10");
        controller.onSetTimeBeforeAlert();

        Map<String, String> map = new HashMap<>();
        map.put("user1", "Kitchen");
        mock.when(EditSimulationController::getUserLocations).thenReturn(map);
        editSimulationController.getRoomsMove().setValue("Kitchen");
        editSimulationController.changeLocation(new ActionEvent());

        Scanner scanner = new Scanner(FileUtils.getFile("src", "main", "resources", "consoleLogs.txt"));
        Scanner scannerSHS = new Scanner(FileUtils.getFile("src", "main", "resources", "consoleLogsSHS.txt"));
        String line = "";
        String lineSHS = "";
        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            if (line.contains("user1 was detected in the Kitchen during Away Mode.")){
                break;
            }
        }

        while (scannerSHS.hasNextLine()) {
            lineSHS = scannerSHS.nextLine();
            if (lineSHS.contains("user1 was detected in the Kitchen during Away Mode.")){
                break;
            }
        }

        assertEquals("user1 was detected in the Kitchen during Away Mode. " +
                "Authorities will be alerted in 10 seconds.", line.substring(22));

        assertEquals("user1 was detected in the Kitchen during Away Mode. " +
                "Authorities will be alerted in 10 seconds.", lineSHS.substring(22));
    }

    /**
     * Use case 10, Delivery 2
     */
    @Test
    public void should_set_time_before_alert_on_user_input_away_mode() throws FileNotFoundException, ParseException {
        mock.when(EditSimulationController::getUserLocations).thenReturn(Map.of("user1", "Outside"));
        controller.onMouseClickAwayToggleON(null);
        controller.getTimeBeforeAlertField().setText("10");
        controller.onSetTimeBeforeAlert();
        assertEquals("10", controller.getTimeBeforeAlert());
    }
    
    
    @Test
    public void should_lock_doors_in_auto_mode() throws IOException{
        controller.setRoomArray(new File("src/test/resources/houseLayout.txt"));
        Room[] roomArray = controller.getRoomArray();
        controller.autoModeOnClick(null);
        
    	editSimulationLoader = new FXMLLoader(getClass().getResource("/view/editSimulation.fxml"));
        editSimulationLoader.load();
        editSimulationController = editSimulationLoader.getController();
        mock.when(EditSimulationController::getUserLocations).thenReturn(Map.of("user1", "Outside"));
        Map<String, String> map = new HashMap<>();
        map.put("user1", "Kitchen");
        mock.when(EditSimulationController::getUserLocations).thenReturn(map);
        editSimulationController.getRoomsMove().setValue("Kitchen");
        editSimulationController.changeLocation(new ActionEvent());
        
        controller.updateAutoDoors();
        
        roomArray = controller.getRoomArray();
        HashMap<String, Room> rooms = new HashMap<>();
        for (Room room : roomArray) rooms.put(room.getName(), room);
        Room[] roomArr = {rooms.get("Garage"),rooms.get("Entrance"),rooms.get("Backyard")};
        boolean isAllDoorLocked = true;
        for(Room r : roomArr){
        	ArrayList<Door> doorLs = r.getDoors();
            for(Door d : doorLs) {
    			if(!d.getLock()) isAllDoorLocked = false;
            }
        }
        
        assertTrue(isAllDoorLocked);
        controller.setRoomArray(null);
        controller.autoModeOnClick(null);
    }
    
    @Test
    public void should_auto_open_light_where_user_at_in_auto_mode() throws IOException{
        controller.setRoomArray(new File("src/test/resources/houseLayout.txt"));
        Room[] roomArray = controller.getRoomArray();
        controller.autoModeOnClick(null);
        
    	editSimulationLoader = new FXMLLoader(getClass().getResource("/view/editSimulation.fxml"));
        editSimulationLoader.load();
        editSimulationController = editSimulationLoader.getController();
        mock.when(EditSimulationController::getUserLocations).thenReturn(Map.of("user1", "Outside"));
        
        Map<String, String> map = new HashMap<>();
        map.put("user1", "Kitchen");
        mock.when(EditSimulationController::getUserLocations).thenReturn(map);
        editSimulationController.getRoomsMove().setValue("Kitchen");
        editSimulationController.changeLocation(new ActionEvent());
        
        controller.updateAutoLights();
        
        roomArray = controller.getRoomArray();
        HashMap<String, Room> rooms = new HashMap<>();
        for (Room room : roomArray) rooms.put(room.getName(), room);
        Room room = rooms.get("Kitchen");

        assertTrue(room.getLightsOn()==1);
        controller.setRoomArray(null);
        controller.autoModeOnClick(null);
    }

    @Test
    public void test_set_defaultAwayModeTemps() {
        Event.fireEvent(controller.getDefaultAwaySummer(), new MouseEvent(MouseEvent.MOUSE_CLICKED, 0,
                0, 0, 0, MouseButton.PRIMARY, 1, true, true, true, true,
                true, true, true, true, true, true, null));

        controller.getSummerAwayTF().setText("10");
        controller.changeDefaultTemp(Season.SUMMER);
        assertEquals(10, controller.getDefaultSummerTemp());

        Event.fireEvent(controller.getDefaultAwayWinter(), new MouseEvent(MouseEvent.MOUSE_CLICKED, 0,
                0, 0, 0, MouseButton.PRIMARY, 1, true, true, true, true,
                true, true, true, true, true, true, null));

        controller.getWinterAwayTF().setText("11");
        controller.changeDefaultTemp(Season.WINTER);
        assertEquals(11, controller.getDefaultWinterTemp());
    }

    /**
     * Use Case 1, Delivery 3
     */
    @Test
    public void set_zones_heating_cooling() throws IOException {
        File file = new File("src/test/resources/houseLayout.txt");
        Room[] roomArray = HouseLayoutService.parseHouseLayout(file);
        HashMap<String, Room> rooms = new HashMap<>();
        for (Room room : roomArray) {
            rooms.put(room.getName(), room);
        }

        ArrayList<Room> selectedRooms = new ArrayList<>();
        selectedRooms.add(rooms.get("Living Room"));
        selectedRooms.add(rooms.get("Bedroom 1"));

        Zone zone = new Zone("Zone 1", selectedRooms);

        assertEquals(zone.getName(), "Zone 1");
        assertEquals(zone.getRooms().size(), 2);
        assertEquals(zone.getRooms().get(0).getName(), "Living Room");
        assertEquals(zone.getRooms().get(1).getName(), "Bedroom 1");
    }

    /**
     * Use Case 2, Delivery 3
     */
    @Test
    public void set_temperature_in_zones() throws IOException {
        File file = new File("src/test/resources/houseLayout.txt");
        Room[] roomArray = HouseLayoutService.parseHouseLayout(file);
        HashMap<String, Room> rooms = new HashMap<>();
        for (Room room : roomArray) {
            rooms.put(room.getName(), room);
        }

        ArrayList<Room> selectedRooms = new ArrayList<>();
        selectedRooms.add(rooms.get("Living Room"));
        selectedRooms.add(rooms.get("Bedroom 1"));

        Zone zone = new Zone("Zone 1", selectedRooms);
        double[] zoneTemperature = new double[]{23, 20, 18};
        zone.setZoneTemp(zoneTemperature);
        assertEquals(zone.getZoneTemp()[0], 23);
        assertEquals(zone.getZoneTemp()[1], 20);
        assertEquals(zone.getZoneTemp()[2], 18);
    }

    /**
     * Use Case 3, Delivery 3
     */
    @Test
    public void display_current_temperature() throws IOException {
        File file = new File("src/test/resources/houseLayout.txt");
        Room[] roomArray = HouseLayoutService.parseHouseLayout(file);
        HashMap<String, Room> rooms = new HashMap<>();
        for (Room room : roomArray) {
            rooms.put(room.getName(), room);
        }

        Room kitchen = rooms.get("Kitchen");
        Room garage = rooms.get("Garage");
        kitchen.setTemperature(25);
        garage.setTemperature(12);

        assertEquals(kitchen.getTemperature(), 25);
        assertEquals(garage.getTemperature(), 12);
    }
}
