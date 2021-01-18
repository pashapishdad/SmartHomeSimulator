package controller;

import constants.Position;
import constants.Season;
import constants.UserRoles;
import entity.CommandType;
import entity.ConsoleComponents;
import entity.Door;
import entity.PermissionType;
import entity.Room;
import entity.Window;
import entity.Zone;
import interfaces.MainController;
import javafx.animation.Animation;
import javafx.animation.FillTransition;
import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import observerPattern.SHPObserver;
import org.apache.commons.lang3.StringUtils;
import service.ConsoleService;
import service.HouseLayoutService;
import service.PermissionService;
import service.RoleService;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class LoginInfoController implements Initializable, MainController {

    /**
     * declaring variables
     */
    @FXML
    private Canvas houseRender;
    @FXML
    private Label time, date, user, temperature;
    @FXML
    private Hyperlink loc;
    @FXML
    private TextArea console;
    @FXML
    private ComboBox<String> selectedUser;
    @FXML
    private Label userRole;
    @FXML
    private AnchorPane anchorToggle;
    @FXML
    private HBox invisibleContainer, hBoxTemperature;
    @FXML
    private TextField textFieldTemperature;
    @FXML
    private VBox vboxSHCWindows;
    @FXML
    private VBox vboxSHCLights;
    @FXML
    private VBox vboxSHCDoors;
    @FXML
    private VBox vboxSHCRooms;
    @FXML
    private ToggleButton awayModeON;
    @FXML
    private ToggleButton awayModeOFF;
    @FXML
    private ComboBox<String> rooms;
    @FXML
    private Label roomToLight;
    @FXML
    private TextField timeBeforeAlertInput;
    @FXML
    private Label labelRoomName;
    @FXML
    private Label labelLight;
    @FXML
    private Label labelWindow;
    @FXML
    private Label labelDoor;
    @FXML
    private AnchorPane permissionsList;
    @FXML
    private Label labelAwayMode;
    @FXML
	  private Button autoModeBt;
    @FXML
    private AnchorPane aPZone;
    @FXML
    private ComboBox<String> comboRoom;
    @FXML
    private Button buttonAddRoom;
    @FXML
    private Button buttonCreateZone;
    @FXML
    private VBox vboxRooms;
    @FXML
    private TextField textZoneName;
    @FXML
    private VBox vboxZones;
    @FXML
    private VBox vboxDesiredTemp;
    @FXML
    private Label season;
    @FXML
    private HBox defaultSummerContainer, hBoxSummer;
    @FXML
    private HBox defaultWinterContainer, hBoxWinter;
    @FXML
    private Label defaultAwaySummer, defaultAwayWinter;
    @FXML
    private TextField summerAwayTF, winterAwayTF;

    private static String userParent;
    private static Map<String, Room> house;
    private static Room[] roomArray;
    private static String username;
    private static boolean awayMode;
    private static BooleanProperty booleanProperty;
    private static boolean autoMode = false;
    private final Text toggleText = new Text();

    private GraphicsContext gc;
    private double xOffset = 0;
    private double yOffset = 0;
    private final int ROOM_SIZE = 90;
    private final int DOOR_SIZE = ROOM_SIZE - 66;
    private static long timeInMillis;
    private static Timeline clock;
    private static boolean firstLaunch = true;
    private static int temperatureInInt = 15;
    private Map<String, int[]> roomPosition = new HashMap<>();
    private Map<String, Date[]> lightsSchedule = new HashMap<>();
    private String timeStr;
    private static String timeBeforeAlert;
    private Map<String, String> userLocation = EditSimulationController.getUserLocations();
    private Map<String, Integer> userPositions = new HashMap<>();
    private HashMap<String, Room> availableRooms = new HashMap<>();
    private HashMap<String, Room> selectedRooms = new HashMap<>();
    private HashMap<String, Room> allRooms = new HashMap<>();
    private HashMap<String, Zone> zones = new HashMap<>();

    private static GridPane gpZone = new GridPane();
    private static GridPane gpRooms = new GridPane();
    private static GridPane gpRoomsTemp = new GridPane();

    private static int defaultSummerTemp = 22;
    private static int defaultWinterTemp = 18;

    public LoginInfoController() {
    }

    private double clockSpeed = 1;
    private static boolean alterOnHold = false;

    /**
     * Sets up the logged in user as the active user
     *
     * @param userParent The active user's username
     */
    public void setSelectedUser(String userParent) {
        setupCurrentUser();
        selectedUser.getSelectionModel().select(userParent);
        username = userParent;
    }

    /**
     * Function to set the user's location
     *
     * @param place the String that is the name of the location that will be passed to this function.
     */
    public void setLoc(String place) {
        loc.setText(place);
    }

    /**
     * Function to set the user
     *
     * @param username the username that will be passed to this function.
     */
    public void setUser(String username) {
        user.setText(username);
    }

    /**
     * Function to set the date
     *
     * @param dateTime the time and date that will be passed to this function
     */
    public void setDate(String dateTime) {
        date.setText(dateTime);
    }

    /**
     * Function to setting new time label
     *
     * @param dateTime the time and date that will be passed to this function
     */
    public void setTime(String dateTime) {
        SimpleDateFormat formatFull = new SimpleDateFormat("yyyy - MMMM - dd HH:mm:ss");
        try {
            Date d = formatFull.parse(dateTime);
            timeInMillis = d.getTime();
            String[] arr = dateTime.split(" ");
            timeStr = arr[5];
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Function return the text for date label
     */
    public String getDate() {
        return this.date.getText();
    }

    /**
     * Function return the AnchorPane
     */
    public AnchorPane getAnc() {
        return anchorToggle;
    }

    /**
     * Function return the text for time label
     */
    public String getTime() {
        return this.time.getText();
    }

    public static void setUserParent(String userParent) {
        LoginInfoController.userParent = userParent;
    }

    public static String getUserParent() {
        return userParent;
    }

    public static String getUsername() {
        return username;
    }

    public Label getTemperature() {
        return temperature;
    }

    public TextField getTemperatureField() {
        return textFieldTemperature;
    }

    /**
     * Get timeBeforeAlert
     *
     * @return timeBeforeAlert string
     */
    public static String getTimeBeforeAlert(){
        return timeBeforeAlert;
    }

    public TextField getTimeBeforeAlertField() {
        return timeBeforeAlertInput;
    }

    public static void setUsername(String username) {
        LoginInfoController.username = username;
    }

    public static boolean isAwayMode() {
        return awayMode;
    }

    /**
     * Animation controller for the clock
     */
    protected void moveClock() {
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy - MMMM - dd");
        SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        timeInMillis += 1000;
        cal.setTimeInMillis(timeInMillis);
        season.setText(EditSimulationController.getCurrentSeason(cal).toString());
        this.date.setText(formatDate.format(cal.getTime()));
        this.time.setText(formatTime.format(cal.getTime()));
    }
    
    /**
     * Increase clock speed
     */
    public void increaseClockSpeed(ActionEvent event) {
    	if(!(this.clockSpeed < 0.001)) {
        	double rate = 0.1;
        	if(this.clockSpeed > (rate+rate*0.25)) this.clockSpeed -= 0.1;
        	else  this.clockSpeed -= this.clockSpeed*0.3;
        	playClockAnimation();
    	}
    }
    
    /**
     * Decrease clock speed
     */
    public void decreaseClockSpeed(ActionEvent event) {
    	this.clockSpeed += 1;
    	playClockAnimation();
    }
    
    /**
     * Reset clock speed
     */
    public void resetClockSpeed(ActionEvent event) {
    	this.clockSpeed = 1;
    	playClockAnimation();
    	for(Room r: roomArray) {
    		System.out.println(r.getName() + "     \t" + r.getTemperature() + "\t" + r.getCurrentTemperature() + "\t" + r.getOverride() + "\t" + r.getTemperatureDefault());
    	}
    	System.out.println(awayMode);
    }
    
    /**
     * Clock animation trigger, this will clear the animation and replay
     */
    private void playClockAnimation() {
        if (clock != null) clock.getKeyFrames().clear();
        clock = new Timeline(
                new KeyFrame(Duration.ZERO, e -> {
                    moveClock();
                }),
                new KeyFrame(Duration.seconds(clockSpeed))
        );
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    /**
     * Function from interface Initializable, initialize default actions
     *
     * @param location, resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // any action at the first initialization
        if (firstLaunch) {
            firstInitialization();
        }
        
        console.setText(ConsoleService.getConsole());

        if (Objects.nonNull(house) && rooms != null) {
            rooms.getItems().addAll(house.keySet());
            rooms.getSelectionModel().selectFirst();
        }
      
        awayModeON.setSelected(awayMode);
        awayModeOFF.setSelected(!awayMode);

        // Temperature
        this.temperature.setText(Integer.toString(temperatureInInt));

        playClockAnimation();

        setupCurrentUser();
        if(autoMode) {
        	autoModeBt.setText("ON");
        	updateAutoLights();
    		updateAutoDoors();
        }

        if (Objects.nonNull(house)) {
            try {
                drawRoomFromCache();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        selectedUser.getSelectionModel().select(username);
        Map userLocs = EditSimulationController.getUserLocations();
        loc.setText(Objects.isNull(userLocs) ? "Outside" : userLocs.get(username).toString());

        // toggleSwitch
        Pane root = getAnc();

        ToggleSwitch toggle = new ToggleSwitch();
        toggle.setId("toggleSwitch");
        toggle.setTranslateX(60);
        toggle.setTranslateY(30);

        toggleText.setId("toggleText");
        toggleText.setFont(Font.font(13));
        toggleText.setFill(Color.WHITE);
        toggleText.setTranslateX(60);
        toggleText.setTranslateY(30);
        toggleText.textProperty().bind(Bindings.when(toggle.switchedOnProperty()).then("ON").otherwise("OFF"));
        
        root.getChildren().addAll(toggle, toggleText);

        this.defaultAwaySummer.setText(Integer.toString(defaultSummerTemp));
        this.defaultAwayWinter.setText(Integer.toString(defaultWinterTemp));
    }

    private void firstInitialization() {
        aPZone.setVisible(false);
        firstLaunch = false;
        ChangeDateTimeController.setParentController(this);
        LightsScheduleController.setParentController(this);
        SHPObserver.setParentController(this);
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy - MMMM - dd");
        SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm:ss");
        long sysmillis = System.currentTimeMillis();
        timeInMillis = sysmillis;
        Date d = new Date(sysmillis);
        this.date.setText(formatDate.format(d));
        this.time.setText(formatTime.format(d));
        ConsoleService.initialize();
        consoleLog("System initialized");
    }

    /**
     * Method that will create the grid placed in the {@link AnchorPane}.
     * Also calls the {@link RoleService} to obtain the Users and Permissions.
     *
     * @return The grid pane used by the display
     */
    private GridPane processRows() {
        AtomicInteger index = new AtomicInteger();
        GridPane gridPane = new GridPane();
        gridPane.setHgap(6);
        Map<String, Map<CommandType, PermissionType>> userPerms = UserPermissionsController.getUserPermissions();
        if (StringUtils.isEmpty(userRole.getText())) {
            return null;
        }
        if (Objects.isNull(userPerms)) {
            Map<CommandType, PermissionType> currentPermissions = PermissionService.getDefaultPermissions(UserRoles.valueOf(userRole.getText()));
            Map<String, Map<CommandType, PermissionType>> mapToAdd = new HashMap<>();
            mapToAdd.put(username, currentPermissions);
            UserPermissionsController.setUserPermissions(mapToAdd);
        } else if (Objects.isNull(userPerms.get(username))) {
            Map<CommandType, PermissionType> currentPermissions = PermissionService.getDefaultPermissions(UserRoles.valueOf(userRole.getText()));
            userPerms.put(username, currentPermissions);
            UserPermissionsController.setUserPermissions(userPerms);
        }
        UserPermissionsController.getUserPermissions().get(username).forEach((key, value) -> {
            gridPane.addRow(gridPane.getRowCount(), createUserLabel(key.toString(), key.toString()), createUserLabel("Permission:", ""), createUserLabel(value.toString(), key + "Permission"));
            index.getAndIncrement();
        });
        return gridPane;
    }

    /**
     * Creates a Label with the specified value
     *
     * @param value The value placed in the label
     * @param id    The value used for the label's ID
     * @return The label with the value and ID
     */
    private Node createUserLabel(final String value, final String id) {
        Label userLabel = new Label();
        userLabel.setMinWidth(100);
        userLabel.setId(id);
        userLabel.setText(value);
        return userLabel;
    }

    /**
     * On Click function for the temperature label
     *
     * @param event The event that triggered the onClick method
     */
    public void temperatureOnClick(MouseEvent event) {
    	if (!toggleText.getText().equals("ON")) {
    		consoleLog("Simulation is off, enable to process action.");
    	} else {
	        invisibleContainer.getChildren().add(temperature);
	        textFieldTemperature.setText(temperature.getText());
	        textFieldTemperature.setPrefWidth(20 + (temperature.getText().length() * 5));
	        hBoxTemperature.getChildren().add(0, textFieldTemperature);
	        textFieldTemperature.requestFocus();
	        textFieldTemperature.setOnAction(e -> {  // on enter key
	            changeTemperatureOnEnter();
	            handleSummerWindowsOpen();
	        });
    	}
    }
    
    /*
     * This function handling the windows auto open during summer
     */
    private void handleSummerWindowsOpen() {
    	if(!season.getText().equals("SUMMER") || awayMode==true) return;
    	for (Room r : roomArray) {
            Image windowOpenTop = null,windowOpenBottom = null,windowOpenLeft = null,windowOpenRight = null;
			try {
				windowOpenTop = new Image(new FileInputStream("src/main/resources/Images/windowOpenTop.png"), 60, 27, true, false);
				windowOpenBottom = new Image(new FileInputStream("src/main/resources/Images/windowOpenBottom.png"), 60, 27, true, false);
				windowOpenLeft = new Image(new FileInputStream("src/main/resources/Images/windowOpenLeft.png"), 60, 27, true, false);
				windowOpenRight = new Image(new FileInputStream("src/main/resources/Images/windowOpenRight.png"), 60, 27, true, false);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
            if(LoginInfoController.temperatureInInt < r.getCurrentTemperature()) {
                for (Window w : r.getWindows()) { 
                    if (w.getBlocking()) 
                    	consoleLog("Open windows failed. " + r.getName() + "'s " + w.getPosition() + " windows is blocked");
                    else {
                    	w.setOpenWindow(true);
                    	drawWindows(r, w.getPosition().toString());
                    	switch(w.getPosition().toString()) {
	                    	case "TOP":   	r.getImageView()[0].setImage(windowOpenTop);
	                    	case "LEFT":   	r.getImageView()[1].setImage(windowOpenLeft);
	                    	case "RIGHT":   r.getImageView()[2].setImage(windowOpenRight);
	                    	case "BOTTOM":  r.getImageView()[3].setImage(windowOpenBottom);
                    	}
                    }
                }
            }
        }
	}

    /**
     * On Enter functionality for the textFieldTemperature
     */
    protected void changeTemperatureOnEnter() {
        if (textFieldTemperature.getText().matches("-?\\d+") && textFieldTemperature.getText().length() != 0) {
            int new_temp = Integer.parseInt(textFieldTemperature.getText());
            invisibleContainer.getChildren().add(textFieldTemperature);
            hBoxTemperature.getChildren().add(0, temperature);
            temperature.setText(textFieldTemperature.getText());
            textFieldTemperature.clear();
            temperatureInInt = Integer.parseInt(temperature.getText());
            consoleLog("Change outside temperature to " + temperatureInInt, ConsoleComponents.SHH);
        } else {
            consoleLog("Please enter a valid input for outside temperature.", ConsoleComponents.SHH);
        }
    }
    
    /**
	 * Toogle auto mode
	 * 
	 * @param event The event that trigger action
	 */
	public void autoModeOnClick(ActionEvent event) {
		if (!toggleText.getText().equals("ON")) {
			consoleLog("Simulation is off, enable to process action.");
		} else {
			if (!autoMode) {
				autoModeBt.setText("ON");
				this.autoMode = true;
				consoleLog("Auto mode ON");
			} else {
				autoModeBt.setText("OFF");
				this.autoMode = false;
				consoleLog("Auto mode OFF");
			}
		}
	}
	
	/**
	 * Function return autoMode status
	 * @return boolean of autoMode
	 */
	public static boolean getAutoMode() {
		return autoMode;
	}
	
	/**
	 * Function open away mode
	 * 
	 * @param event The event that trigger action
	 */
    public void onMouseClickAwayToggleON(MouseEvent event) throws ParseException, FileNotFoundException {
        if (!toggleText.getText().equals("ON")) {
    		  consoleLog("Simulation is off, enable to process action.");
    	  } else {
            Map<String, String> userLocations = EditSimulationController.getUserLocations();
            AtomicBoolean isNotInHouse = new AtomicBoolean(true);
            if (Objects.nonNull(userLocations)) {
                userLocations.keySet().forEach(user -> {
                    if (!userLocations.get(user).equals("Outside")) {
                        isNotInHouse.set(false);
                    }
                });
            }
            if (isNotInHouse.get()) {
               if(closeWindowsDoorsLights()) {
                    consoleLog("Away mode turns on.");
                    awayMode = true;
                    awayModeON.setSelected(true);
                    labelAwayMode.setTextFill(Color. WHITE);
                    labelAwayMode.setText("Away mode is on");
                    setDefaultTemperatures();
              }else {
                    awayModeOFF.setSelected(true);
              }
            } else {
                consoleLog("Unable to turn Away Mode ON, there is someone in the house");
                awayMode = false;
                awayModeON.setSelected(false);
                awayModeOFF.setSelected(true);
            }
          }
    }

    /**
	 * Function turn off away mode
	 * 
	 * @param event The event that trigger action
	 */
    public void onMouseClickAwayToggleOFF(MouseEvent event) {
      if (!toggleText.getText().equals("ON")) {
    		consoleLog("Simulation is off, unable to process action.");
    	} else {
          consoleLog("Away mode was turned off.");
          awayMode = false;
          awayModeOFF.setSelected(true);
          labelAwayMode.setTextFill(Color. WHITE);
          labelAwayMode.setText("Away mode is off");
      }
    }

    /**
     * This class creates a toggle switch.
     */
    protected static class ToggleSwitch extends Parent {

        private TranslateTransition translateAnimation = new TranslateTransition(Duration.seconds(0.25));
        private FillTransition fillAnimation = new FillTransition(Duration.seconds(0.25));
        private ParallelTransition animation = new ParallelTransition(translateAnimation, fillAnimation);

        public BooleanProperty switchedOnProperty() {
            return booleanProperty;
        }

        public ToggleSwitch() {
            Rectangle background = new Rectangle(60, 30);
            background.setArcWidth(30);
            background.setArcHeight(30);
            background.setFill(Color.RED);
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

            if (Objects.isNull(booleanProperty)) {
                booleanProperty = new SimpleBooleanProperty(false);
            } else {
                boolean check = booleanProperty.get();
                translateAnimation.setToX(check ? 60 - 30 : 0);
                fillAnimation.setFromValue(check ? Color.RED : Color.GREEN);
                fillAnimation.setToValue(check ? Color.GREEN : Color.RED);
                animation.play();
            }

            booleanProperty.addListener((obs, oldState, newState) -> {
                boolean isOn = newState.booleanValue();
                translateAnimation.setToX(isOn ? 60 - 30 : 0);
                fillAnimation.setFromValue(isOn ? Color.RED : Color.GREEN);
                fillAnimation.setToValue(isOn ? Color.GREEN : Color.RED);

                animation.play();
            });

            setOnMouseClicked(event -> {
                booleanProperty.set(!booleanProperty.get());
            });
        }
    }

    /**
     * Sets up the current active user and all possible options
     */
    private void setupCurrentUser() {
        selectedUser.getItems().clear();
        Map<String, String> listOfUsers = RoleService.findRole(userParent);
        userRole.setText(listOfUsers.get(username));
        selectedUser.getItems().addAll(listOfUsers.keySet());
        selectedUser.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (Objects.nonNull(newValue) && !newValue.equals(oldValue)) {
                    permissionsList.getChildren().clear();
                    username = newValue;
                    selectedUser.getSelectionModel().select(newValue);
                    userRole.setText(listOfUsers.get(newValue));
                    Map<String, String> userLocs = EditSimulationController.getUserLocations();
                    loc.setText(Objects.isNull(userLocs) ? "Outside" : userLocs.get(username).toString());
                    if (StringUtils.isNotEmpty(username)) {
                        Node toAdd = processRows();
                        if (Objects.nonNull(toAdd)) {
                            permissionsList.getChildren().add(toAdd);
                        }
                    }
                }
            }
        });
    }

    /**
     * This function loads the login page(scene) into the window(stage)
     *
     * @param event The event that called this function
     * @throws IOException Thrown if the file cannot be read
     */
    public void goToLogin(ActionEvent event) throws IOException {
        Parent login = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
        Scene loginScene = new Scene(login);

        deleteHouse();
        EditSimulationController.deleteLocations();

        // stage info
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(loginScene);
        window.show();
    }

    /**
     * The function appends text onto the consoleLog when it is not on the scene
     *
     * @param str String to append onto the console
     */
    public static void consoleLogFile(String str, ConsoleComponents consoleComponents) {
        updateConsoleLog(str, consoleComponents);
    }

    /**
     * This function appends text onto the console
     *
     * @param str String to append onto the console
     */
    public void consoleLog(String str) {
        updateConsoleLog(str, ConsoleComponents.SHS);
        console.appendText("[" + LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString() + "] " + str + "\n");
    }

    /**
     * This function appends text onto the console
     *
     * @param str String to append onto the console
     */
    public void consoleLog(String str, ConsoleComponents consoleComponents) {
        updateConsoleLog(str, consoleComponents);
        console.appendText("[" + LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString() + "] " + str + "\n");
    }

    /**
     * This function is responsible for updating the cached logs
     *
     * @param str String to append onto the console
     */
    private static void updateConsoleLog(String str, ConsoleComponents consoleComponents) {
        String toAppend = "[" + LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString() + "] " + str + "\n";
        switch (consoleComponents) {
            case SHH ->  {
                ConsoleService.exportConsoleSHH(toAppend);
                break;
            }
            case SHS -> {
                ConsoleService.exportConsoleSHS(toAppend);
                break;
            }
            default -> ConsoleService.exportConsole(toAppend);
        }
    }

    /**
     * This function loads the user roles page(scene) into the window(stage)
     *
     * @param event The event that called this function
     * @throws IOException Thrown if the file cannot be read
     */
    public void goToUserSettings(ActionEvent event) throws IOException {
    	if (!toggleText.getText().equals("ON")) {
    		consoleLog("Simulation is off, unable to process action.");
    	} else {
	        FXMLLoader loader = new FXMLLoader();
	        loader.setLocation(getClass().getResource("/view/userRoles.fxml"));
	        Parent userRoles = loader.load();
	        Scene userRolesScene = new Scene(userRoles);
	
	        UserRolesController controller = loader.getController();
	        controller.setUsername(user.getText());
	
	        // stage info
	        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
	        window.setScene(userRolesScene);
	        window.show();
    	}
    }
    
    /**
     * Function setter for room array
     * @param file File of room list
     * @throws IOException IOException when access File
     */
    public void setRoomArray(File file) throws IOException {
    	if(file==null) roomArray=null;
    	else roomArray = HouseLayoutService.parseHouseLayout(file);
    }
    
    /**
     * Function getter for room array
     * @return
     */
    public Room[] getRoomArray() {
    	return roomArray;
    }

    /**
     * This function allows the user to add a house layout text file, calls the function to parse the json
     * and calls the function to draw the house with rooms, doors, windows and lights
     *
     * @param event The event that called this function
     * @throws IOException Thrown if the file cannot be read
     */
    public void addHouseLayout(ActionEvent event) throws IOException {
        if (toggleText.getText().equals("ON")) {
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter =
                    new FileChooser.ExtensionFilter("TEXT files (*.txt)", "*.txt");
            fileChooser.getExtensionFilters().add(extFilter);
            fileChooser.setTitle("Open Resource File");
            File file = fileChooser.showOpenDialog(window);
            if (Objects.isNull(file)) {
                return;
            }

            labelRoomName.setText("ROOM");
            labelLight.setText("LIGHT");
            labelWindow.setText("WINDOW");
            labelDoor.setText("DOOR");

            setUpAwayModeStatus();

            roomArray = HouseLayoutService.parseHouseLayout(file);
            HashMap<String, Room> rooms = new HashMap<>();
            for (Room room : roomArray) {
                //set the default current temperature for all the rooms based on outside temperature.
                room.setCurrentTemperature(Double.parseDouble(temperature.getText()));

                rooms.put(room.getName(), room);
            }
            //display the current and desired temperature of each room in SHH tab
            gpRoomsTemp.getChildren().clear();

            drawTemperatureInRooms();

            Set<Room> traversed = new HashSet<>();

            gc = houseRender.getGraphicsContext2D();
            gc.setFont(new Font(11));
            gc.setFill(Color.WHITE);
            int lastX = 130, lastY = 190;
            house = rooms;
            drawRoom(rooms, roomArray[0], traversed, Position.NONE, lastX, lastY);

            // creating a room label which has the name of the room.
            GridPane gpSHCRooms = new GridPane();
            gpSHCRooms.setVgap(21.3);

            for (int i = 0 ; i < roomArray.length ; i++) {
                Label room = new Label();
                room.setText(roomArray[i].getName());
                gpSHCRooms.addRow(i, room);
            }
            vboxSHCRooms.getChildren().clear();

            vboxSHCRooms.getChildren().add(gpSHCRooms);

            GridPane gpSHCLights = new GridPane();
            gpSHCLights.setVgap(13);

            //draw the temperature for each room.
            for (int i = 0 ; i < roomArray.length ; i++) {
                roomArray[i].setTemperatureDefault(true);
                drawTemperature(roomArray[i]);
            }

            setupLights(gpSHCLights);

            // open/close window functionality
            GridPane gpSHCWindows = new GridPane();
            gpSHCWindows.setVgap(13);

            for (int i = 0 ; i < roomArray.length ; i++) {
                Image windowCloseTop = new Image(new FileInputStream("src/main/resources/Images/windowCloseTop.png"), 60, 27, true, false);
                Image windowCloseBottom = new Image(new FileInputStream("src/main/resources/Images/windowCloseBottom.png"), 60, 27, true, false);
                Image windowCloseLeft = new Image(new FileInputStream("src/main/resources/Images/windowCloseLeft.png"), 60, 27, true, false);
                Image windowCloseRight = new Image(new FileInputStream("src/main/resources/Images/windowCloseRight.png"), 60, 27, true, false);
                Image windowOpenTop = new Image(new FileInputStream("src/main/resources/Images/windowOpenTop.png"), 60, 27, true, false);
                Image windowOpenBottom = new Image(new FileInputStream("src/main/resources/Images/windowOpenBottom.png"), 60, 27, true, false);
                Image windowOpenLeft = new Image(new FileInputStream("src/main/resources/Images/windowOpenLeft.png"), 60, 27, true, false);
                Image windowOpenRight = new Image(new FileInputStream("src/main/resources/Images/windowOpenRight.png"), 60, 27, true, false);
                Image windowEmpty = new Image(new FileInputStream("src/main/resources/Images/windowEmpty.png"), 60, 27, true, false);

                ImageView windowsTop = new ImageView(windowCloseTop);
                ImageView windowsLeft = new ImageView(windowCloseLeft);
                ImageView windowsRight = new ImageView(windowCloseRight);
                ImageView windowsBottom = new ImageView(windowCloseBottom);
                ImageView windowsEmpty = new ImageView(windowEmpty);
                roomArray[i].setImageView(new ImageView[]{windowsTop,windowsLeft,windowsRight,windowsBottom,windowsEmpty});

                int finalI = i;
                ArrayList<Window> windowList = roomArray[i].getWindows();

                for (int j = 0; j < windowList.size(); j++) {
                    int finalJ = j;

                    if (windowList.get(finalJ).getPosition().toString() == "TOP" && !windowList.get(finalJ).getBlocking()) {
                        if (!windowList.get(finalJ).getOpenWindow()) {
                            windowsTop.setImage(windowCloseTop);
                        }
                        else {
                            windowsTop.setImage(windowOpenTop);
                        }
                        windowsTop.setOnMousePressed(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent e) {
                                if (windowList.get(finalJ).getPosition().toString() == "TOP" && !windowList.get(finalJ).getBlocking() && !awayMode) {
                                    if (!windowList.get(finalJ).getOpenWindow()) {
                                        windowList.get(finalJ).setOpenWindow(true);
                                        drawWindows(roomArray[finalI], windowList.get(finalJ).getPosition().toString());
                                        windowsTop.setImage(windowOpenTop);
                                    } else {
                                        windowList.get(finalJ).setOpenWindow(false);
                                        drawWindows(roomArray[finalI], windowList.get(finalJ).getPosition().toString());
                                        windowsTop.setImage(windowCloseTop);
                                    }
                                } else if (awayMode == true) {
                                	Alert alert = new Alert(Alert.AlertType.WARNING, "Not allow to open window in away mode.");
                                    alert.showAndWait();
                                }
                                else {
                                	consoleLog("This window is blocked");
                                    Alert alert = new Alert(Alert.AlertType.WARNING, "This window path is blocked.");
                                    alert.showAndWait();
                                }
                            }
                        });
                    }
                    else {
                        drawBlockWindow(roomArray[i], windowList.get(finalJ).getPosition());
                    }

                    if (windowList.get(finalJ).getPosition().toString() == "LEFT" && !windowList.get(finalJ).getBlocking()) {
                        if (!windowList.get(finalJ).getOpenWindow()) {
                            windowsLeft.setImage(windowCloseLeft);
                        }
                        else {
                            windowsLeft.setImage(windowOpenLeft);
                        }
                        windowsLeft.setOnMousePressed(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent e) {
                                if (windowList.get(finalJ).getPosition().toString() == "LEFT" && !windowList.get(finalJ).getBlocking() && !awayMode) {
                                    if (!windowList.get(finalJ).getOpenWindow()) {
                                        windowList.get(finalJ).setOpenWindow(true);
                                        drawWindows(roomArray[finalI], windowList.get(finalJ).getPosition().toString());
                                        windowsLeft.setImage(windowOpenLeft);
                                    } else {
                                        windowList.get(finalJ).setOpenWindow(false);
                                        drawWindows(roomArray[finalI], windowList.get(finalJ).getPosition().toString());
                                        windowsLeft.setImage(windowCloseLeft);
                                    }
                                } else if (awayMode == true) {
                                	Alert alert = new Alert(Alert.AlertType.WARNING, "Not allow to open window in away mode.");
                                    alert.showAndWait();
                                }
                                else {
                                	consoleLog("This window is blocked");
                                    Alert alert = new Alert(Alert.AlertType.WARNING, "This window path is blocked.");
                                    alert.showAndWait();
                                }
                            }
                        });
                    }
                    else {
                        drawBlockWindow(roomArray[i], windowList.get(finalJ).getPosition());
                    }

                    if (windowList.get(finalJ).getPosition().toString() == "RIGHT" && !windowList.get(finalJ).getBlocking()) {
                        if (!windowList.get(finalJ).getOpenWindow()) {
                            windowsRight.setImage(windowCloseRight);
                        }
                        else {
                        	windowsRight.setImage(windowOpenRight);
                        }
                        windowsRight.setOnMousePressed(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent e) {
                                if (windowList.get(finalJ).getPosition().toString() == "RIGHT" && !windowList.get(finalJ).getBlocking() && !awayMode) {
                                    if (!windowList.get(finalJ).getOpenWindow()) {
                                        windowList.get(finalJ).setOpenWindow(true);
                                        drawWindows(roomArray[finalI], windowList.get(finalJ).getPosition().toString());
                                        windowsRight.setImage(windowOpenRight);
                                    } else {
                                        windowList.get(finalJ).setOpenWindow(false);
                                        drawWindows(roomArray[finalI], windowList.get(finalJ).getPosition().toString());
                                        windowsRight.setImage(windowCloseRight);
                                    }
                                } else if (awayMode == true) {
                                	Alert alert = new Alert(Alert.AlertType.WARNING, "Not allow to open window in away mode.");
                                    alert.showAndWait();
                                }
                                else {
                                	consoleLog("This window is blocked");
                                    Alert alert = new Alert(Alert.AlertType.WARNING, "This window path is blocked.");
                                    alert.showAndWait();
                                }
                            }
                        });
                    }
                    else {
                        drawBlockWindow(roomArray[i], windowList.get(finalJ).getPosition());
                    }

                    if (windowList.get(finalJ).getPosition().toString() == "BOTTOM" && !windowList.get(finalJ).getBlocking()) {
                        if (!windowList.get(finalJ).getOpenWindow()) {
                            windowsBottom.setImage(windowCloseBottom);
                        }
                        else {
                            windowsBottom.setImage(windowOpenBottom);
                        }
                        windowsBottom.setOnMousePressed(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent e) {
                                if (windowList.get(finalJ).getPosition().toString() == "BOTTOM" && !windowList.get(finalJ).getBlocking() && !awayMode) {
                                    if (!windowList.get(finalJ).getOpenWindow()) {
                                        windowList.get(finalJ).setOpenWindow(true);
                                        drawWindows(roomArray[finalI], windowList.get(finalJ).getPosition().toString());
                                        windowsBottom.setImage(windowOpenBottom);
                                    } else {
                                        windowList.get(finalJ).setOpenWindow(false);
                                        drawWindows(roomArray[finalI], windowList.get(finalJ).getPosition().toString());
                                        windowsBottom.setImage(windowCloseBottom);
                                    }
                                } else if (awayMode == true) {
                                	Alert alert = new Alert(Alert.AlertType.WARNING, "Not allow to open window in away mode.");
                                    alert.showAndWait();
                                }
                                else {
                                	consoleLog("This window is blocked");
                                    Alert alert = new Alert(Alert.AlertType.WARNING, "This window path is blocked.");
                                    alert.showAndWait();
                                }
                            }
                        });
                    }
                    else {
                        drawBlockWindow(roomArray[i], windowList.get(finalJ).getPosition());
                    }
                }
                setupWindowsPerNumber(gpSHCWindows, i, windowsTop, windowsLeft, windowsRight, windowsBottom, windowsEmpty, windowList);
            }

            vboxSHCWindows.getChildren().clear();

            vboxSHCWindows.getChildren().add(gpSHCWindows);

            // open/close door functionality
            GridPane gpSHCDoors = new GridPane();
            gpSHCDoors.setVgap(13);

            for (int i = 0 ; i < roomArray.length ; i++) {
                Image doorCloseTop = new Image(new FileInputStream("src/main/resources/Images/DoorCloseTop.png"), 60, 27, true, false);
                Image doorCloseBottom = new Image(new FileInputStream("src/main/resources/Images/DoorCloseBottom.png"), 60, 27, true, false);
                Image doorCloseLeft = new Image(new FileInputStream("src/main/resources/Images/DoorCloseLeft.png"), 60, 27, true, false);
                Image doorCloseRight = new Image(new FileInputStream("src/main/resources/Images/DoorCloseRight.png"), 60, 27, true, false);
                Image doorOpenTop = new Image(new FileInputStream("src/main/resources/Images/doorOpenTop.png"), 60, 27, true, false);
                Image doorOpenBottom = new Image(new FileInputStream("src/main/resources/Images/doorOpenBottom.png"), 60, 27, true, false);
                Image doorOpenLeft = new Image(new FileInputStream("src/main/resources/Images/doorOpenLeft.png"), 60, 27, true, false);
                Image doorOpenRight = new Image(new FileInputStream("src/main/resources/Images/doorOpenRight.png"), 60, 27, true, false);

                ImageView doorsTop = new ImageView(doorCloseTop);
                ImageView doorsLeft = new ImageView(doorCloseLeft);
                ImageView doorsRight = new ImageView(doorCloseRight);
                ImageView doorsBottom = new ImageView(doorCloseBottom);

                int finalI = i;
                ArrayList<Door> doorList = roomArray[i].getDoors();

                for (int j = 0; j < doorList.size(); j++) {
                    int finalJ = j;

                    if (doorList.get(finalJ).getPosition().toString() == "TOP") {
                        String connectedRoom = doorList.get(finalJ).getConnection();
                        if (!doorList.get(finalJ).getOpenDoor())
                            doorsTop.setImage(doorCloseTop);
                        else {
                            doorsTop.setImage(doorOpenTop);
                        }

                        doorsTop.setOnMousePressed(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent e) {
                                if (doorList.get(finalJ).getPosition().toString() == "TOP") {
                                    if (!doorList.get(finalJ).getOpenDoor()) {
                                        doorList.get(finalJ).setOpenDoor(true);
                                        for (int i = 0; i < roomArray.length; i++) {
                                            if(roomArray[i].getName().equals(connectedRoom)) {
                                                for (int j = 0; j < roomArray[i].getDoors().size(); j++) {
                                                    if(roomArray[i].getDoors().get(j).getPosition().toString().equals("BOTTOM"))
                                                        roomArray[i].getDoors().get(j).setOpenDoor(true);
                                                }
                                            }
                                        }

                                        drawDoor(roomArray[finalI], doorList.get(finalJ).getPosition().toString());
                                        doorsTop.setImage(doorOpenTop);
                                    } else {
                                        doorList.get(finalJ).setOpenDoor(false);
                                        for (int i = 0; i < roomArray.length; i++) {
                                            if(roomArray[i].getName().equals(connectedRoom)) {
                                                for (int j = 0; j < roomArray[i].getDoors().size(); j++) {
                                                    if(roomArray[i].getDoors().get(j).getPosition().toString().equals("BOTTOM"))
                                                        roomArray[i].getDoors().get(j).setOpenDoor(false);
                                                }
                                            }
                                        }

                                        drawDoor(roomArray[finalI], doorList.get(finalJ).getPosition().toString());
                                        doorsTop.setImage(doorCloseTop);
                                    }
                                } else {
                                    consoleLog("This door is locked");
                                    Alert alert = new Alert(Alert.AlertType.WARNING, "This door is locked");
                                    alert.showAndWait();
                                }
                            }
                        });
                    }

                    if (doorList.get(finalJ).getPosition().toString() == "LEFT") {
                        String connectedRoom = doorList.get(finalJ).getConnection();
                        if (!doorList.get(finalJ).getOpenDoor())
                            doorsLeft.setImage(doorCloseLeft);
                        else {
                            doorsLeft.setImage(doorOpenLeft);
                        }

                        doorsLeft.setOnMousePressed(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent e) {
                                if (doorList.get(finalJ).getPosition().toString() == "LEFT") {
                                    if (!doorList.get(finalJ).getOpenDoor()) {
                                        doorList.get(finalJ).setOpenDoor(true);
                                        for (int i = 0; i < roomArray.length; i++) {
                                            if(roomArray[i].getName().equals(connectedRoom)) {
                                                for (int j = 0; j < roomArray[i].getDoors().size(); j++) {
                                                    if(roomArray[i].getDoors().get(j).getPosition().toString().equals("RIGHT"))
                                                        roomArray[i].getDoors().get(j).setOpenDoor(true);
                                                }
                                            }
                                        }
                                        drawDoor(roomArray[finalI], doorList.get(finalJ).getPosition().toString());
                                        doorsLeft.setImage(doorOpenLeft);
                                    } else {
                                        doorList.get(finalJ).setOpenDoor(false);
                                        for (int i = 0; i < roomArray.length; i++) {
                                            if(roomArray[i].getName().equals(connectedRoom)) {
                                                for (int j = 0; j < roomArray[i].getDoors().size(); j++) {
                                                    if(roomArray[i].getDoors().get(j).getPosition().toString().equals("RIGHT"))
                                                        roomArray[i].getDoors().get(j).setOpenDoor(false);
                                                }
                                            }
                                        }
                                        drawDoor(roomArray[finalI], doorList.get(finalJ).getPosition().toString());
                                        doorsLeft.setImage(doorCloseLeft);
                                    }
                                } else {
                                    consoleLog("This door is locked");
                                    Alert alert = new Alert(Alert.AlertType.WARNING, "This door is locked");
                                    alert.showAndWait();
                                }
                            }
                        });
                    }

                    if (doorList.get(finalJ).getPosition().toString() == "RIGHT") {
                        String connectedRoom = doorList.get(finalJ).getConnection();
                        if (!doorList.get(finalJ).getOpenDoor())
                            doorsRight.setImage(doorCloseRight);
                        else {
                            doorsRight.setImage(doorOpenRight);
                        }

                        doorsRight.setOnMousePressed(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent e) {
                                if (doorList.get(finalJ).getPosition().toString() == "RIGHT") {
                                    if (!doorList.get(finalJ).getOpenDoor()) {
                                        doorList.get(finalJ).setOpenDoor(true);
                                        for (int i = 0; i < roomArray.length; i++) {
                                            if(roomArray[i].getName().equals(connectedRoom)) {
                                                for (int j = 0; j < roomArray[i].getDoors().size(); j++) {
                                                    if(roomArray[i].getDoors().get(j).getPosition().toString().equals("LEFT"))
                                                        roomArray[i].getDoors().get(j).setOpenDoor(true);
                                                }
                                            }
                                        }
                                        drawDoor(roomArray[finalI], doorList.get(finalJ).getPosition().toString());
                                        doorsRight.setImage(doorOpenRight);
                                    } else {
                                        doorList.get(finalJ).setOpenDoor(false);
                                        for (int i = 0; i < roomArray.length; i++) {
                                            if(roomArray[i].getName().equals(connectedRoom)) {
                                                for (int j = 0; j < roomArray[i].getDoors().size(); j++) {
                                                    if(roomArray[i].getDoors().get(j).getPosition().toString().equals("LEFT"))
                                                        roomArray[i].getDoors().get(j).setOpenDoor(false);
                                                }
                                            }
                                        }
                                        drawDoor(roomArray[finalI], doorList.get(finalJ).getPosition().toString());
                                        doorsRight.setImage(doorCloseRight);
                                    }
                                } else {
                                    consoleLog("This door is locked");
                                    Alert alert = new Alert(Alert.AlertType.WARNING, "This door is locked");
                                    alert.showAndWait();
                                }
                            }
                        });
                    }

                    if (doorList.get(finalJ).getPosition().toString() == "BOTTOM") {
                        String connectedRoom = doorList.get(finalJ).getConnection();
                        if (!doorList.get(finalJ).getOpenDoor())
                            doorsBottom.setImage(doorCloseBottom);
                        else {
                            doorsBottom.setImage(doorOpenBottom);
                        }

                        doorsBottom.setOnMousePressed(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent e) {
                                if (doorList.get(finalJ).getPosition().toString() == "BOTTOM") {
                                    if (!doorList.get(finalJ).getOpenDoor()) {
                                        doorList.get(finalJ).setOpenDoor(true);
                                        for (int i = 0; i < roomArray.length; i++) {
                                            if(roomArray[i].getName().equals(connectedRoom)) {
                                                for (int j = 0; j < roomArray[i].getDoors().size(); j++) {
                                                    if(roomArray[i].getDoors().get(j).getPosition().toString().equals("TOP"))
                                                        roomArray[i].getDoors().get(j).setOpenDoor(true);
                                                }
                                            }
                                        }
                                        drawDoor(roomArray[finalI], doorList.get(finalJ).getPosition().toString());
                                        doorsBottom.setImage(doorOpenBottom);
                                    } else {
                                        doorList.get(finalJ).setOpenDoor(false);
                                        for (int i = 0; i < roomArray.length; i++) {
                                            if(roomArray[i].getName().equals(connectedRoom)) {
                                                for (int j = 0; j < roomArray[i].getDoors().size(); j++) {
                                                    if(roomArray[i].getDoors().get(j).getPosition().toString().equals("TOP"))
                                                        roomArray[i].getDoors().get(j).setOpenDoor(false);
                                                }
                                            }
                                        }
                                        drawDoor(roomArray[finalI], doorList.get(finalJ).getPosition().toString());
                                        doorsBottom.setImage(doorCloseBottom);
                                    }
                                } else {
                                    consoleLog("This door is locked");
                                    Alert alert = new Alert(Alert.AlertType.WARNING, "This door is locked");
                                    alert.showAndWait();
                                }
                            }
                        });
                    }
                }
                setupRoomPerDoorNumber(gpSHCDoors, i, doorsTop, doorsLeft, doorsRight, doorsBottom, doorList);
            }

            vboxSHCDoors.getChildren().clear();

            vboxSHCDoors.getChildren().add(gpSHCDoors);
            
            consoleLog("Successfully added house layout.");

            aPZone.setVisible(true);

            //creating two maps for available rooms and all the rooms to be used in SHH tab.
            for (Room room : roomArray) {
                if(!room.getName().equals("Entrance") && !room.getName().equals("Backyard"))
                    availableRooms.put(room.getName(), room);
                    allRooms.put(room.getName(), room);
            }
            comboRoom.getItems().addAll(availableRooms.keySet());

        } else {
        	consoleLog("Add house layout failed, please turn on the simulation first.");
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please turn on the simulation first.");
            alert.showAndWait();
        }
    }

    private void drawTemperatureInRooms() {
        for (Room room : roomArray) {
            if (!room.getName().equals("Entrance") && !room.getName().equals("Backyard")) {
                Label roomName = new Label();
                Label override = new Label();
                TextField textFieldRoom = new TextField();
                Button setNewTemperature = new Button();
                Button hvacButton = new Button();
                hvacButton.setMaxWidth(75);
                hvacButton.setText("HVAC ON");
                hvacButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        if (!room.getHvacStopped()) {
                            room.setHvacStopped(true);
                            hvacButton.setText("HVAC OFF");
                            consoleLog("HVAC for " + room.getName() + " is off", ConsoleComponents.SHH);
                        } else {
                            room.setHvacStopped(false);
                            hvacButton.setText("HVAC ON");
                            consoleLog("HVAC for " + room.getName() + " is on", ConsoleComponents.SHH);
                        }
                    }
                });
                setNewTemperature.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                    	
                    	if (userRole.getText().equals(UserRoles.GUEST.toString())) {
                            if (!(!Objects.isNull(EditSimulationController.getUserLocations()) && !Objects.isNull(EditSimulationController.getUserLocations().get(selectedUser.getSelectionModel().getSelectedItem())))) {
                                Alert alert = new Alert(Alert.AlertType.WARNING, "Current user has no permissions for this action as they are not in the same room");
                                alert.showAndWait();
                                return;
                            }

                            if (!room.getName().equals(EditSimulationController.getUserLocations().get(selectedUser.getSelectionModel().getSelectedItem()))) {
                                Alert alert = new Alert(Alert.AlertType.WARNING, "Current user has no permissions for this action as they are not in the same room");
                                alert.showAndWait();
                                return;
                            }
                        }
                    	
                        if (textFieldRoom.getText().equals("")) {
                            consoleLog("Please enter a temperature for " + room.getName() + " first.", ConsoleComponents.SHH);
                            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a room first.");
                            alert.showAndWait();
                        } else {
                            consoleLog("Temperature for " + room.getName() + " is overridden.", ConsoleComponents.SHH);
                            override.setText("(Overridden)");
                            room.setTemperature(Double.parseDouble(textFieldRoom.getText()));
                            room.setOverride(true);
                            room.setTemperatureDefault(false);
                            time.textProperty().addListener((observable, oldValue, newValue) -> {
                                if (room.getCurrentTemperature() > Double.parseDouble(temperature.getText()) && room.getHvacStopped()) {
                                    room.setSettingTemperature(true);
                                    Timer t2 = new Timer();
                                    t2.schedule(new TimerTask() {
                                        @Override
                                        public void run() {
                                            if (room.getCurrentTemperature() > Double.parseDouble(temperature.getText()) && room.getHvacStopped()) {
                                                room.setCurrentTemperature(Math.round(((room.getCurrentTemperature() * 100 - 5) / 100) * 100.00) / 100.00);
                                            }
                                        }
                                    }, 1000);
                                }
                                if (room.getCurrentTemperature() < Double.parseDouble(temperature.getText()) && room.getHvacStopped()) {
                                    room.setSettingTemperature(true);
                                    Timer t2 = new Timer();
                                    t2.schedule(new TimerTask() {
                                        @Override
                                        public void run() {
                                            if (room.getCurrentTemperature() < Double.parseDouble(temperature.getText()) && room.getHvacStopped()) {
                                                room.setCurrentTemperature(Math.round(((room.getCurrentTemperature() * 100 + 5) / 100) * 100.00) / 100.00);
                                            }
                                        }
                                    }, 1000);
                                }

                                //when desired temperature is lower, AC will be turned on
                                if (room.getCurrentTemperature() > room.getTemperature() && !room.getHvacStopped()) {
                                    //AC should be turned on
                                    room.setSettingTemperature(true);
                                    Timer t = new Timer();
                                    t.schedule(new TimerTask() {
                                        @Override
                                        public void run() {
                                            if (room.getCurrentTemperature() > room.getTemperature() && !room.getHvacStopped()) {
                                                room.setCurrentTemperature(Math.round(((room.getCurrentTemperature() * 100 - 10) / 100) * 100.00) / 100.00);
                                                if (room.getCurrentTemperature() == room.getTemperature() && !room.getHvacStopped()) {
                                                    room.setHvacPaused(true);
                                                }
                                            } else if (room.getHvacPaused() && (room.getCurrentTemperature() - room.getTemperature()) > 0.25 &&
                                                    room.getCurrentTemperature() > room.getTemperature() && !room.getHvacStopped()) {
                                                room.setHvacPaused(false);
                                            }
                                        }
                                    }, 1000);
                                }


                                //when desired temperature is higher, Heater will be turned on
                                if (room.getCurrentTemperature() < room.getTemperature() && !room.getHvacStopped()) {
                                    //Heater should be turned on
                                    room.setSettingTemperature(true);
                                    Timer t = new Timer();
                                    t.schedule(new TimerTask() {
                                        @Override
                                        public void run() {
                                            if (room.getCurrentTemperature() < room.getTemperature() - 0.25 && !room.getHvacStopped()) {
                                                room.setCurrentTemperature(Math.round(((room.getCurrentTemperature() * 100 + 10) / 100) * 100.00) / 100.00);
                                                if (room.getCurrentTemperature() == room.getTemperature()) {
                                                    room.setHvacPaused(true);
                                                }
                                            } else if (room.getHvacPaused() && (room.getTemperature() - room.getCurrentTemperature()) > 0.25 &&
                                                    room.getCurrentTemperature() < room.getTemperature() && !room.getHvacStopped()) {
                                                room.setHvacPaused(false);
                                            }
                                        }
                                    }, 1000);
                                }

                            });
                        }
                    }
                });
                roomName.setText(" " + room.getName());
                textFieldRoom.setMaxWidth(40);
                setNewTemperature.setMaxWidth(30);
                setNewTemperature.setText("Set");
                gpRoomsTemp.addRow(gpRoomsTemp.getRowCount(), textFieldRoom, setNewTemperature, roomName, override, hvacButton);
            }
        }
        vboxDesiredTemp.getChildren().clear();
        vboxDesiredTemp.getChildren().add(gpRoomsTemp);
        EditSimulationController.setGpRoomsTemp(gpRoomsTemp);

        //display the current and desired temperature of each room in SHH tab
        time.textProperty().addListener((obs, oldV, newV) -> {
            for (Room room : roomArray) {
                if (!room.getName().equals("Entrance") && !room.getName().equals("Backyard")) {
                    try {
                    	if(room.getCurrentTemperature()<=0 && this.alterOnHold==false) alterPipeBurst();
                        drawTemperature(room);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void setupWindowsPerNumber(GridPane gpSHCWindows, int i, ImageView windowsTop, ImageView windowsLeft, ImageView windowsRight, ImageView windowsBottom, ImageView windowsEmpty, ArrayList<Window> windowList) {
        // when room has 0 windows
        if (windowList.size() == 0) {
            gpSHCWindows.addRow(i, windowsEmpty);
        }

        // when room has 1 window
        if (windowList.size() == 1) {
            ImageView selectedWindow = new ImageView();

            if (roomArray[i].getWindows().get(0).getPosition().toString().equals("TOP"))
                selectedWindow = windowsTop;
            if (roomArray[i].getWindows().get(0).getPosition().toString().equals("BOTTOM"))
                selectedWindow = windowsBottom;
            if (roomArray[i].getWindows().get(0).getPosition().toString().equals("LEFT"))
                selectedWindow = windowsLeft;
            if (roomArray[i].getWindows().get(0).getPosition().toString().equals("RIGHT"))
                selectedWindow = windowsRight;
            gpSHCWindows.addRow(i, selectedWindow);
        }

        // when room has 2 windows
        if (windowList.size() == 2) {
            ImageView[] selectedWindows = new ImageView[2];

            for (int j = 0; j < selectedWindows.length; j++) {
                if (roomArray[i].getWindows().get(j).getPosition().toString().equals("TOP"))
                    selectedWindows[j] = windowsTop;
                if (roomArray[i].getWindows().get(j).getPosition().toString().equals("BOTTOM"))
                    selectedWindows[j] = windowsBottom;
                if (roomArray[i].getWindows().get(j).getPosition().toString().equals("LEFT"))
                    selectedWindows[j] = windowsLeft;
                if (roomArray[i].getWindows().get(j).getPosition().toString().equals("RIGHT"))
                    selectedWindows[j] = windowsRight;
            }
            gpSHCWindows.addRow(i, selectedWindows[0], selectedWindows[1]);
        }

        // when room has 3 windows
        if (windowList.size() == 3) {
            ImageView[] selectedWindows = new ImageView[3];

            for (int j = 0; j < selectedWindows.length; j++) {
                if (roomArray[i].getWindows().get(j).getPosition().toString().equals("TOP"))
                    selectedWindows[j] = windowsTop;
                if (roomArray[i].getWindows().get(j).getPosition().toString().equals("BOTTOM"))
                    selectedWindows[j] = windowsBottom;
                if (roomArray[i].getWindows().get(j).getPosition().toString().equals("LEFT"))
                    selectedWindows[j] = windowsLeft;
                if (roomArray[i].getWindows().get(j).getPosition().toString().equals("RIGHT"))
                    selectedWindows[j] = windowsRight;
            }
            gpSHCWindows.addRow(i, selectedWindows[0], selectedWindows[1], selectedWindows[2]);
        }

        // when room has 4 windows
        if (windowList.size() == 4) {
            gpSHCWindows.addRow(i, windowsTop, windowsBottom, windowsLeft, windowsRight);
        }
    }

    private void setUpAwayModeStatus() {
        //show the away mode status on house layout
        if (awayMode) {
            labelAwayMode.setTextFill(Color.WHITE);
            labelAwayMode.setText("Away mode is on");
        } else {
            labelAwayMode.setTextFill(Color.WHITE);
            labelAwayMode.setText("Away mode is off");
        }
    }

    /**
     * Draws the room based on the cached static variables after this file has been submitted
     */
    public void drawRoomFromCache() throws FileNotFoundException {
        labelRoomName.setText("ROOM");
        labelLight.setText("LIGHT");
        labelWindow.setText("WINDOW");
        labelDoor.setText("DOOR");

        for (Room room : roomArray) {
            if(!room.getName().equals("Entrance") && !room.getName().equals("Backyard"))
                availableRooms.put(room.getName(), room);
            allRooms.put(room.getName(), room);
        }

        //--------------------
        for (Room room : roomArray) {
            time.textProperty().addListener((observable, oldValue, newValue) -> {
                if (room.getCurrentTemperature() > Double.parseDouble(temperature.getText()) && room.getHvacStopped() && room.getSettingTemperature()) {
                    Timer t2 = new Timer();
                    t2.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if (room.getCurrentTemperature() > Double.parseDouble(temperature.getText()) && room.getHvacStopped()) {
                                room.setCurrentTemperature(Math.round(((room.getCurrentTemperature() * 100 - 5) / 100) * 100.00) / 100.00);
                            }
                        }
                    }, 1000);
                }
                if (room.getCurrentTemperature() < Double.parseDouble(temperature.getText()) && room.getHvacStopped() && room.getSettingTemperature()) {
                    Timer t2 = new Timer();
                    t2.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if (room.getCurrentTemperature() < Double.parseDouble(temperature.getText()) && room.getHvacStopped()) {
                                room.setCurrentTemperature(Math.round(((room.getCurrentTemperature() * 100 + 5) / 100) * 100.00) / 100.00);
                            }
                        }
                    }, 1000);
                }

                //when desired temperature is lower, AC will be turned on
                if (room.getCurrentTemperature() > room.getTemperature() && !room.getHvacStopped() && room.getSettingTemperature() && room.getSettingTemperature()) {
                    //AC should be turned on
                    Timer t = new Timer();
                    t.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if (room.getCurrentTemperature() > room.getTemperature() && !room.getHvacStopped()) {
                                room.setCurrentTemperature(Math.round(((room.getCurrentTemperature() * 100 - 10) / 100) * 100.00) / 100.00);
                                if (room.getCurrentTemperature() == room.getTemperature() && !room.getHvacStopped()) {
                                    room.setHvacPaused(true);
                                }
                            } else if (room.getHvacPaused() && (room.getCurrentTemperature() - room.getTemperature()) > 0.25 &&
                                    room.getCurrentTemperature() > room.getTemperature() && !room.getHvacStopped()) {
                                room.setHvacPaused(false);
                            }
                        }
                    }, 1000);
                }


                //when desired temperature is higher, Heater will be turned on
                if (room.getCurrentTemperature() < room.getTemperature() && !room.getHvacStopped() && room.getSettingTemperature()) {
                    //Heater should be turned on
                    Timer t = new Timer();
                    t.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if (room.getCurrentTemperature() < room.getTemperature() && !room.getHvacStopped()) {
                                room.setCurrentTemperature(Math.round(((room.getCurrentTemperature() * 100 + 10) / 100) * 100.00) / 100.00);
                                if (room.getCurrentTemperature() == room.getTemperature()) {
                                    room.setHvacPaused(true);
                                }
                            } else if (room.getHvacPaused() && (room.getTemperature() - room.getCurrentTemperature()) > 0.25 &&
                                    room.getCurrentTemperature() < room.getTemperature() && !room.getHvacStopped()) {
                                room.setHvacPaused(false);
                            }
                        }
                    }, 1000);
                }

            });
        }
        //--------------------


        comboRoom.getItems().addAll(availableRooms.keySet());
        vboxZones.getChildren().clear();
        vboxZones.getChildren().addAll(gpZone);
        vboxRooms.getChildren().clear();
        vboxRooms.getChildren().addAll(gpRooms);
        vboxDesiredTemp.getChildren().clear();
        vboxDesiredTemp.getChildren().addAll(gpRoomsTemp);

        //show the away mode status on house layout
        setUpAwayModeStatus();

        gc = houseRender.getGraphicsContext2D();
        gc.setFont(new Font(11));
        gc.setFill(Color.WHITE);
        drawRoom(house, roomArray[0], new HashSet<>(), Position.NONE, 130, 190);

        // creating a room label which has the name of the room.
        GridPane gpSHCRooms = new GridPane();
        gpSHCRooms.setVgap(21.3);
        for (int i = 0 ; i < roomArray.length ; i++) {
            Label room = new Label();
            room.setText(roomArray[i].getName());
            gpSHCRooms.addRow(i, room);
        }

        vboxSHCRooms.getChildren().clear();

        vboxSHCRooms.getChildren().add(gpSHCRooms);

        GridPane gpSHCLights = new GridPane();
        gpSHCLights.setVgap(13);

        setupLights(gpSHCLights);

        // open/close window functionality
        GridPane gpSHCWindows = new GridPane();
        gpSHCWindows.setVgap(13);

        for (int i = 0 ; i < roomArray.length ; i++) {
            Image windowCloseTop = new Image(new FileInputStream("src/main/resources/Images/windowCloseTop.png"), 60, 27, true, false);
            Image windowCloseBottom = new Image(new FileInputStream("src/main/resources/Images/windowCloseBottom.png"), 60, 27, true, false);
            Image windowCloseLeft = new Image(new FileInputStream("src/main/resources/Images/windowCloseLeft.png"), 60, 27, true, false);
            Image windowCloseRight = new Image(new FileInputStream("src/main/resources/Images/windowCloseRight.png"), 60, 27, true, false);
            Image windowEmpty = new Image(new FileInputStream("src/main/resources/Images/windowEmpty.png"), 60, 27, true, false);
            Image windowOpenTop = new Image(new FileInputStream("src/main/resources/Images/windowOpenTop.png"), 60, 27, true, false);
            Image windowOpenBottom = new Image(new FileInputStream("src/main/resources/Images/windowOpenBottom.png"), 60, 27, true, false);
            Image windowOpenLeft = new Image(new FileInputStream("src/main/resources/Images/windowOpenLeft.png"), 60, 27, true, false);
            Image windowOpenRight = new Image(new FileInputStream("src/main/resources/Images/windowOpenRight.png"), 60, 27, true, false);

            ImageView windowsTop = new ImageView(windowCloseTop);
            ImageView windowsLeft = new ImageView(windowCloseLeft);
            ImageView windowsRight = new ImageView(windowCloseRight);
            ImageView windowsBottom = new ImageView(windowCloseBottom);
            ImageView windowsEmpty = new ImageView(windowEmpty);
            roomArray[i].setImageView(new ImageView[]{windowsTop,windowsLeft,windowsRight,windowsBottom,windowsEmpty});

            int finalI = i;
            ArrayList<Window> windowList = roomArray[i].getWindows();

            for (int j = 0; j < windowList.size(); j++) {
                int finalJ = j;

                if (windowList.get(finalJ).getPosition().toString() == "TOP") {
                    if (!windowList.get(finalJ).getOpenWindow())
                        windowsTop.setImage(windowCloseTop);
                    else {
                        windowsTop.setImage(windowOpenTop);
                    }
                    windowsTop.setOnMousePressed(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent e) {
                        	System.out.println("MOUSE " + awayMode + (awayMode==true));
                            if (windowList.get(finalJ).getPosition().toString() == "TOP" && !windowList.get(finalJ).getBlocking() && !awayMode) {
                                if (!windowList.get(finalJ).getOpenWindow()) {
                                    windowList.get(finalJ).setOpenWindow(true);
                                    drawWindows(roomArray[finalI], windowList.get(finalJ).getPosition().toString());
                                    windowsTop.setImage(windowOpenTop);
                                } else {
                                    windowList.get(finalJ).setOpenWindow(false);
                                    drawWindows(roomArray[finalI], windowList.get(finalJ).getPosition().toString());
                                    windowsTop.setImage(windowCloseTop);
                                }

                            } else if (awayMode == true) {
                            	Alert alert = new Alert(Alert.AlertType.WARNING, "Not allow to open window in away mode.");
                                alert.showAndWait();
                            } else {
                            	consoleLog("This window is blocked");
                                Alert alert = new Alert(Alert.AlertType.WARNING, "This window path is blocked.");
                                alert.showAndWait();
                            }
                        }
                    });
                    if(windowList.get(finalJ).getBlocking()) {
						drawBlockWindow(roomArray[i], windowList.get(finalJ).getPosition());
					}
                }

                if (windowList.get(finalJ).getPosition().toString() == "LEFT") {
                    if (!windowList.get(finalJ).getOpenWindow())
                        windowsLeft.setImage(windowCloseLeft);
                    else {
                        windowsLeft.setImage(windowOpenLeft);
                    }
                    windowsLeft.setOnMousePressed(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent e) {
                            if (windowList.get(finalJ).getPosition().toString() == "LEFT" && !windowList.get(finalJ).getBlocking() && !awayMode) {
                                if (!windowList.get(finalJ).getOpenWindow()) {
                                    windowList.get(finalJ).setOpenWindow(true);
                                    drawWindows(roomArray[finalI], windowList.get(finalJ).getPosition().toString());
                                    windowsLeft.setImage(windowOpenLeft);
                                } else {
                                    windowList.get(finalJ).setOpenWindow(false);
                                    drawWindows(roomArray[finalI], windowList.get(finalJ).getPosition().toString());
                                    windowsLeft.setImage(windowCloseLeft);
                                }
                            } else if (awayMode == true) {
                            	Alert alert = new Alert(Alert.AlertType.WARNING, "Not allow to open window in away mode.");
                                alert.showAndWait();
                            } else {
                            	consoleLog("This window is blocked");
                                Alert alert = new Alert(Alert.AlertType.WARNING, "This window path is blocked.");
                                alert.showAndWait();
                            }
                        }
                    });
                    if(windowList.get(finalJ).getBlocking()) {
						drawBlockWindow(roomArray[i], windowList.get(finalJ).getPosition());
					}
                }

                if (windowList.get(finalJ).getPosition().toString() == "RIGHT") {
                    if (!windowList.get(finalJ).getOpenWindow())
                    	windowsRight.setImage(windowCloseRight);
                    else {
                    	windowsRight.setImage(windowOpenRight);
                    }
                    windowsRight.setOnMousePressed(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent e) {
                            if (windowList.get(finalJ).getPosition().toString() == "RIGHT" && !windowList.get(finalJ).getBlocking() && !awayMode) {
                                if (!windowList.get(finalJ).getOpenWindow()) {
                                    windowList.get(finalJ).setOpenWindow(true);
                                    drawWindows(roomArray[finalI], windowList.get(finalJ).getPosition().toString());
                                    windowsRight.setImage(windowOpenRight);
                                } else {
                                    windowList.get(finalJ).setOpenWindow(false);
                                    drawWindows(roomArray[finalI], windowList.get(finalJ).getPosition().toString());
                                    windowsRight.setImage(windowCloseRight);
                                }
                            } else if (awayMode == true) {
                            	Alert alert = new Alert(Alert.AlertType.WARNING, "Not allow to open window in away mode.");
                                alert.showAndWait();
                            } else {
                            	consoleLog("This window is blocked");
                                Alert alert = new Alert(Alert.AlertType.WARNING, "This window path is blocked.");
                                alert.showAndWait();
                            }
                        }
                    });
                    if(windowList.get(finalJ).getBlocking()) {
						drawBlockWindow(roomArray[i], windowList.get(finalJ).getPosition());
					}
                }

                if (windowList.get(finalJ).getPosition().toString() == "BOTTOM") {
                    if (!windowList.get(finalJ).getOpenWindow())
                        windowsBottom.setImage(windowCloseBottom);
                    else {
                        windowsBottom.setImage(windowOpenBottom);
                    }
                    windowsBottom.setOnMousePressed(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent e) {
                            if (windowList.get(finalJ).getPosition().toString() == "BOTTOM" && !windowList.get(finalJ).getBlocking() && !awayMode) {
                            	if (!windowList.get(finalJ).getOpenWindow()) {
                                    windowList.get(finalJ).setOpenWindow(true);
                                    drawWindows(roomArray[finalI], windowList.get(finalJ).getPosition().toString());
                                    windowsBottom.setImage(windowOpenBottom);
                                } else {
                                    windowList.get(finalJ).setOpenWindow(false);
                                    drawWindows(roomArray[finalI], windowList.get(finalJ).getPosition().toString());
                                    windowsBottom.setImage(windowCloseBottom);
                                }
                            } else if (awayMode == true) {
                            	Alert alert = new Alert(Alert.AlertType.WARNING, "Not allow to open window in away mode.");
                                alert.showAndWait();
                            } else {
                            	consoleLog("This window is blocked");
                                Alert alert = new Alert(Alert.AlertType.WARNING, "This window path is blocked.");
                                alert.showAndWait();
                            }
                        }
                    });
                    if(windowList.get(finalJ).getBlocking()) {
						drawBlockWindow(roomArray[i], windowList.get(finalJ).getPosition());
					}
                }
            }
            setupWindowsPerNumber(gpSHCWindows, i, windowsTop, windowsLeft, windowsRight, windowsBottom, windowsEmpty, windowList);
        }

        vboxSHCWindows.getChildren().clear();

        vboxSHCWindows.getChildren().add(gpSHCWindows);

        // open/close door functionality
        GridPane gpSHCDoors = new GridPane();
        gpSHCDoors.setVgap(13);

        for (int i = 0 ; i < roomArray.length ; i++) {
            Image doorCloseTop = new Image(new FileInputStream("src/main/resources/Images/DoorCloseTop.png"), 60, 27, true, false);
            Image doorCloseBottom = new Image(new FileInputStream("src/main/resources/Images/DoorCloseBottom.png"), 60, 27, true, false);
            Image doorCloseLeft = new Image(new FileInputStream("src/main/resources/Images/DoorCloseLeft.png"), 60, 27, true, false);
            Image doorCloseRight = new Image(new FileInputStream("src/main/resources/Images/DoorCloseRight.png"), 60, 27, true, false);
            Image doorOpenTop = new Image(new FileInputStream("src/main/resources/Images/doorOpenTop.png"), 60, 27, true, false);
            Image doorOpenBottom = new Image(new FileInputStream("src/main/resources/Images/doorOpenBottom.png"), 60, 27, true, false);
            Image doorOpenLeft = new Image(new FileInputStream("src/main/resources/Images/doorOpenLeft.png"), 60, 27, true, false);
            Image doorOpenRight = new Image(new FileInputStream("src/main/resources/Images/doorOpenRight.png"), 60, 27, true, false);

            ImageView doorsTop = new ImageView(doorCloseTop);
            ImageView doorsLeft = new ImageView(doorCloseLeft);
            ImageView doorsRight = new ImageView(doorCloseRight);
            ImageView doorsBottom = new ImageView(doorCloseBottom);

            int finalI = i;
            ArrayList<Door> doorList = roomArray[i].getDoors();

            for (int j = 0; j < doorList.size(); j++) {
                int finalJ = j;

                if (doorList.get(finalJ).getPosition().toString() == "TOP") {
                    String connectedRoom = doorList.get(finalJ).getConnection();
                    if (!doorList.get(finalJ).getOpenDoor())
                        doorsTop.setImage(doorCloseTop);
                    else {
                        doorsTop.setImage(doorOpenTop);
                    }

                    doorsTop.setOnMousePressed(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent e) {
                            if (doorList.get(finalJ).getPosition().toString() == "TOP" && !doorList.get(finalJ).getLock()) {
                                if (!doorList.get(finalJ).getOpenDoor()) {
                                    doorList.get(finalJ).setOpenDoor(true);
                                    for (int i = 0; i < roomArray.length; i++) {
                                        if(roomArray[i].getName().equals(connectedRoom)) {
                                            for (int j = 0; j < roomArray[i].getDoors().size(); j++) {
                                                if(roomArray[i].getDoors().get(j).getPosition().toString().equals("BOTTOM"))
                                                    roomArray[i].getDoors().get(j).setOpenDoor(true);
                                            }
                                        }
                                    }

                                    drawDoor(roomArray[finalI], doorList.get(finalJ).getPosition().toString());
                                    doorsTop.setImage(doorOpenTop);
                                } else {
                                    doorList.get(finalJ).setOpenDoor(false);
                                    for (int i = 0; i < roomArray.length; i++) {
                                        if(roomArray[i].getName().equals(connectedRoom)) {
                                            for (int j = 0; j < roomArray[i].getDoors().size(); j++) {
                                                if(roomArray[i].getDoors().get(j).getPosition().toString().equals("BOTTOM"))
                                                    roomArray[i].getDoors().get(j).setOpenDoor(false);
                                            }
                                        }
                                    }

                                    drawDoor(roomArray[finalI], doorList.get(finalJ).getPosition().toString());
                                    doorsTop.setImage(doorCloseTop);
                                }
                            } else {
                            	consoleLog("This door is locked");
                            	Alert alert = new Alert(Alert.AlertType.WARNING, "This door is locked");
                                alert.showAndWait();
                            }
                        }
                    });
                }

                if (doorList.get(finalJ).getPosition().toString() == "LEFT") {
                    String connectedRoom = doorList.get(finalJ).getConnection();
                    if (!doorList.get(finalJ).getOpenDoor())
                        doorsLeft.setImage(doorCloseLeft);
                    else {
                        doorsLeft.setImage(doorOpenLeft);
                    }

                    doorsLeft.setOnMousePressed(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent e) {
                            if (doorList.get(finalJ).getPosition().toString() == "LEFT" && !doorList.get(finalJ).getLock()) {
                                if (!doorList.get(finalJ).getOpenDoor()) {
                                    doorList.get(finalJ).setOpenDoor(true);
                                    for (int i = 0; i < roomArray.length; i++) {
                                        if(roomArray[i].getName().equals(connectedRoom)) {
                                            for (int j = 0; j < roomArray[i].getDoors().size(); j++) {
                                                if(roomArray[i].getDoors().get(j).getPosition().toString().equals("RIGHT"))
                                                    roomArray[i].getDoors().get(j).setOpenDoor(true);
                                            }
                                        }
                                    }
                                    drawDoor(roomArray[finalI], doorList.get(finalJ).getPosition().toString());
                                    doorsLeft.setImage(doorOpenLeft);
                                } else {
                                    doorList.get(finalJ).setOpenDoor(false);
                                    for (int i = 0; i < roomArray.length; i++) {
                                        if(roomArray[i].getName().equals(connectedRoom)) {
                                            for (int j = 0; j < roomArray[i].getDoors().size(); j++) {
                                                if(roomArray[i].getDoors().get(j).getPosition().toString().equals("RIGHT"))
                                                    roomArray[i].getDoors().get(j).setOpenDoor(false);
                                            }
                                        }
                                    }
                                    drawDoor(roomArray[finalI], doorList.get(finalJ).getPosition().toString());
                                    doorsLeft.setImage(doorCloseLeft);
                                }
                            } else {
                            	consoleLog("This door is locked");
                            	Alert alert = new Alert(Alert.AlertType.WARNING, "This door is locked");
                                alert.showAndWait();
                            }
                        }
                    });
                }

                if (doorList.get(finalJ).getPosition().toString() == "RIGHT") {
                    String connectedRoom = doorList.get(finalJ).getConnection();
                    if (!doorList.get(finalJ).getOpenDoor())
                        doorsRight.setImage(doorCloseRight);
                    else {
                        doorsRight.setImage(doorOpenRight);
                    }

                    doorsRight.setOnMousePressed(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent e) {
                            if (doorList.get(finalJ).getPosition().toString() == "RIGHT" && !doorList.get(finalJ).getLock()) {
                                if (!doorList.get(finalJ).getOpenDoor()) {
                                    doorList.get(finalJ).setOpenDoor(true);
                                    for (int i = 0; i < roomArray.length; i++) {
                                        if(roomArray[i].getName().equals(connectedRoom)) {
                                            for (int j = 0; j < roomArray[i].getDoors().size(); j++) {
                                                if(roomArray[i].getDoors().get(j).getPosition().toString().equals("LEFT"))
                                                    roomArray[i].getDoors().get(j).setOpenDoor(true);
                                            }
                                        }
                                    }
                                    drawDoor(roomArray[finalI], doorList.get(finalJ).getPosition().toString());
                                    doorsRight.setImage(doorOpenRight);
                                } else {
                                    doorList.get(finalJ).setOpenDoor(false);
                                    for (int i = 0; i < roomArray.length; i++) {
                                        if(roomArray[i].getName().equals(connectedRoom)) {
                                            for (int j = 0; j < roomArray[i].getDoors().size(); j++) {
                                                if(roomArray[i].getDoors().get(j).getPosition().toString().equals("LEFT"))
                                                    roomArray[i].getDoors().get(j).setOpenDoor(false);
                                            }
                                        }
                                    }
                                    drawDoor(roomArray[finalI], doorList.get(finalJ).getPosition().toString());
                                    doorsRight.setImage(doorCloseRight);
                                }
                            } else {
                            	consoleLog("This door is locked");
                            	Alert alert = new Alert(Alert.AlertType.WARNING, "This door is locked");
                                alert.showAndWait();
                            }
                        }
                    });
                }

                if (doorList.get(finalJ).getPosition().toString() == "BOTTOM") {
                    String connectedRoom = doorList.get(finalJ).getConnection();
                    if (!doorList.get(finalJ).getOpenDoor())
                        doorsBottom.setImage(doorCloseBottom);
                    else {
                        doorsBottom.setImage(doorOpenBottom);
                    }

                    doorsBottom.setOnMousePressed(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent e) {
                            if (doorList.get(finalJ).getPosition().toString() == "BOTTOM" && !doorList.get(finalJ).getLock()) {
                                if (!doorList.get(finalJ).getOpenDoor()) {
                                    doorList.get(finalJ).setOpenDoor(true);
                                    for (int i = 0; i < roomArray.length; i++) {
                                        if(roomArray[i].getName().equals(connectedRoom)) {
                                            for (int j = 0; j < roomArray[i].getDoors().size(); j++) {
                                                if(roomArray[i].getDoors().get(j).getPosition().toString().equals("TOP"))
                                                    roomArray[i].getDoors().get(j).setOpenDoor(true);
                                            }
                                        }
                                    }
                                    drawDoor(roomArray[finalI], doorList.get(finalJ).getPosition().toString());
                                    doorsBottom.setImage(doorOpenBottom);
                                } else {
                                    doorList.get(finalJ).setOpenDoor(false);
                                    for (int i = 0; i < roomArray.length; i++) {
                                        if(roomArray[i].getName().equals(connectedRoom)) {
                                            for (int j = 0; j < roomArray[i].getDoors().size(); j++) {
                                                if(roomArray[i].getDoors().get(j).getPosition().toString().equals("TOP"))
                                                    roomArray[i].getDoors().get(j).setOpenDoor(false);
                                            }
                                        }
                                    }
                                    drawDoor(roomArray[finalI], doorList.get(finalJ).getPosition().toString());
                                    doorsBottom.setImage(doorCloseBottom);
                                }
                            } else {
                            	consoleLog("This door is locked");
                            	Alert alert = new Alert(Alert.AlertType.WARNING, "This door is locked");
                                alert.showAndWait();
                            }
                        }
                    });
                }
            }
            setupRoomPerDoorNumber(gpSHCDoors, i, doorsTop, doorsLeft, doorsRight, doorsBottom, doorList);
        }

        vboxSHCDoors.getChildren().clear();

        vboxSHCDoors.getChildren().add(gpSHCDoors);

        drawPeople();

        //draw number of person(s) in each room
        for (int i = 0; i < roomArray.length; i++) {
            if(userPositions.get(roomArray[i].getName()) != null){
                int numberOfPeople = userPositions.get(roomArray[i].getName());
                int[] positions = roomPosition.get(roomArray[i].getName());
                gc.fillText(String.valueOf(numberOfPeople) + " person(s)", positions[0] + 10, positions[1] + 35);
            }
        }
        drawTemperatureInRooms();
    }

    private void setupRoomPerDoorNumber(GridPane gpSHCDoors, int i, ImageView doorsTop, ImageView doorsLeft, ImageView doorsRight, ImageView doorsBottom, ArrayList<Door> doorList) {
        if (doorList.size() == 1) {
            ImageView selectedDoors = new ImageView();

            if (roomArray[i].getDoors().get(0).getPosition().toString().equals("TOP"))
                selectedDoors = doorsTop;
            if (roomArray[i].getDoors().get(0).getPosition().toString().equals("BOTTOM"))
                selectedDoors = doorsBottom;
            if (roomArray[i].getDoors().get(0).getPosition().toString().equals("LEFT"))
                selectedDoors = doorsLeft;
            if (roomArray[i].getDoors().get(0).getPosition().toString().equals("RIGHT"))
                selectedDoors = doorsRight;
            gpSHCDoors.addRow(i, selectedDoors);
        }

        // when room has 2 doors
        if (doorList.size() == 2) {
            ImageView[] selectedDoors = new ImageView[2];

            for (int j = 0; j < selectedDoors.length; j++) {
                if (roomArray[i].getDoors().get(j).getPosition().toString().equals("TOP"))
                    selectedDoors[j] = doorsTop;
                if (roomArray[i].getDoors().get(j).getPosition().toString().equals("BOTTOM"))
                    selectedDoors[j] = doorsBottom;
                if (roomArray[i].getDoors().get(j).getPosition().toString().equals("LEFT"))
                    selectedDoors[j] = doorsLeft;
                if (roomArray[i].getDoors().get(j).getPosition().toString().equals("RIGHT"))
                    selectedDoors[j] = doorsRight;
            }
            gpSHCDoors.addRow(i, selectedDoors[0], selectedDoors[1]);
        }

        // when room has 3 doors
        if (doorList.size() == 3) {
            ImageView[] selectedDoors = new ImageView[3];

            for (int j = 0; j < selectedDoors.length; j++) {
                if (roomArray[i].getDoors().get(j).getPosition().toString().equals("TOP"))
                    selectedDoors[j] = doorsTop;
                if (roomArray[i].getDoors().get(j).getPosition().toString().equals("BOTTOM"))
                    selectedDoors[j] = doorsBottom;
                if (roomArray[i].getDoors().get(j).getPosition().toString().equals("LEFT"))
                    selectedDoors[j] = doorsLeft;
                if (roomArray[i].getDoors().get(j).getPosition().toString().equals("RIGHT"))
                    selectedDoors[j] = doorsRight;
            }
            gpSHCDoors.addRow(i, selectedDoors[0], selectedDoors[1], selectedDoors[2]);
        }

        // when room has 4 doors
        if (doorList.size() == 4) {
            gpSHCDoors.addRow(i, doorsTop, doorsBottom, doorsLeft, doorsRight);
        }
    }

    private void setupLights(GridPane gpSHCLights) throws FileNotFoundException {
        for (int i = 0; i < roomArray.length; i++) {
            Image lightOn = new Image(new FileInputStream("src/main/resources/Images/lightOn.png"), 60, 27, true, false);
            Image lightOff = new Image(new FileInputStream("src/main/resources/Images/lightOff.png"), 60, 27, true, false);
            ImageView light = new ImageView(lightOff);
            int finalI = i;
            if (roomArray[finalI].getLightsOn() == 0)
                light.setImage(lightOff);
            else {
                light.setImage(lightOn);
            }
            light.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent e) {
                    if (roomArray[finalI].getLightsOn() == 0) {
                        roomArray[finalI].setLightsOn(1);
                        drawLight(roomArray[finalI]);
                        light.setImage(lightOn);
                    } else {
                        roomArray[finalI].setLightsOn(0);
                        drawLight(roomArray[finalI]);
                        light.setImage(lightOff);
                    }
                }
            });
            gpSHCLights.addRow(i, light);
        }

        vboxSHCLights.getChildren().clear();

        vboxSHCLights.getChildren().add(gpSHCLights);
    }

    /**
     * calculates the number of people in each room
     */
    public void drawPeople () {
        if (Objects.isNull(userLocation)) {
            return;
        }

        Iterator<Map.Entry<String, String>> itr = userLocation.entrySet().iterator();
        Iterator<Map.Entry<String, String>> itr2 = userLocation.entrySet().iterator();

        while(itr2.hasNext())
        {
            Map.Entry<String, String> entry = itr2.next();
            userPositions.put(entry.getValue(), 0);
        }

        while(itr.hasNext())
        {
            Map.Entry<String, String> entry = itr.next();
            userPositions.put(entry.getValue(), userPositions.get(entry.getValue()) + 1);
        }
    }


    /**
     * This function draws windows on the house layout
     *
     * @param room room where window is situated
     * @param position position of the window in the room
     */
    public void drawWindows(Room room, String position) {
        int[] coordinates = roomPosition.get(room.getName());
        int x = coordinates[0];
        int y = coordinates[1];

        gc.setLineWidth(2);
        for (Window window : room.getWindows()) {
            if(window.getOpenWindow()) {
                gc.setStroke(Color.BLUE);
            }
            else{
                gc.setStroke(Color.BLACK);
            }
            switch (window.getPosition()) {
                case BOTTOM -> {
                    gc.strokeLine(x + (ROOM_SIZE - DOOR_SIZE) / 2, y + ROOM_SIZE, x + (ROOM_SIZE - DOOR_SIZE) / 2 + DOOR_SIZE, y + ROOM_SIZE);
                }
                case RIGHT -> {
                    gc.strokeLine(x + ROOM_SIZE, y + (ROOM_SIZE - DOOR_SIZE) / 2, x + ROOM_SIZE, y + DOOR_SIZE + (ROOM_SIZE - DOOR_SIZE) / 2);
                }
                case TOP -> {
                    gc.strokeLine(x + (ROOM_SIZE - DOOR_SIZE) / 2, y, x + DOOR_SIZE + (ROOM_SIZE - DOOR_SIZE) / 2, y);
                }
                case LEFT -> {
                    gc.strokeLine(x, y + (ROOM_SIZE - DOOR_SIZE) / 2, x, y + DOOR_SIZE + (ROOM_SIZE - DOOR_SIZE) / 2);
                }
            }
        }
        gc.setLineWidth(1);
        gc.setStroke(Color.BLACK);
    }

    /**
     * This function will draw the block icon on a window.
     *
     * @param room where blocking icon will be drawn.
     * @param position where window is
     */
    public void drawBlockWindow(Room room, Position position) throws FileNotFoundException {
        String name = room.getName();
        Image block;
        int[] coordinates = roomPosition.get(name);
        ArrayList<Window> windowList = room.getWindows();
        for (int i = 0; i < windowList.size(); i++) {
            if(windowList.get(i).getPosition().equals(position)) {
                if(position.toString().equals("TOP")) {
                    if(windowList.get(i).getBlocking()) {
                        block = new Image(new FileInputStream("src/main/resources/Images/alertIcon.png"), 60, 14, true, false);
                        gc.drawImage(block, coordinates[0] + 37, coordinates[1] - 7);
                    }
                }
                if(position.toString().equals("BOTTOM")) {
                    if(windowList.get(i).getBlocking()) {
                        block = new Image(new FileInputStream("src/main/resources/Images/alertIcon.png"), 60, 14, true, false);
                        gc.drawImage(block, coordinates[0] + 37, coordinates[1] + 83);
                    }
                }
                if(position.toString().equals("RIGHT")) {
                    if(windowList.get(i).getBlocking()) {
                        block = new Image(new FileInputStream("src/main/resources/Images/alertIcon.png"), 60, 14, true, false);
                        gc.drawImage(block, coordinates[0] + 83, coordinates[1] + 37);
                    }
                }
                if(position.toString().equals("LEFT")) {
                    if(windowList.get(i).getBlocking()) {
                        block = new Image(new FileInputStream("src/main/resources/Images/alertIcon.png"), 60, 14, true, false);
                        gc.drawImage(block, coordinates[0] - 7, coordinates[1] + 37);
                    }
                }
            }
        }
    }

    /**
     * This function will draw the actual temperature of a room
     *
     * @param room that will call this function
     */
    public void drawTemperature(Room room) throws FileNotFoundException {
        if (!room.getName().equals("Entrance") && !room.getName().equals("Backyard")) {
            Label temperature = new Label();
            temperature.setText(String.valueOf(room.getCurrentTemperature()));
            int[] coordinates = roomPosition.get(room.getName());
            gc.setFill(Color.web("#455A64"));
            gc.fillRect(coordinates[0] + 40, coordinates[1] + 35, 45, 15);
            gc.setFill(Color.WHITE);
            int degree = 4;
            if (temperature.getText().length() > 4) {
                degree = 5;
            }
            gc.fillText(temperature.getText() + " À°C",coordinates[0] + 40, coordinates[1] + 45);
            Image heater = new Image(new FileInputStream("src/main/resources/Images/heater.png"), 60, 27, true, false);
            Image ac = new Image(new FileInputStream("src/main/resources/Images/airconditioning.png"), 60, 27, true, false);
            if(!room.getHvacStopped() && room.getTemperature() > room.getCurrentTemperature() && !room.getTemperatureDefault()
                || (room.getCurrentTemperature() > temperatureInInt && isAwayMode() && !room.getHvacStopped())) {
                gc.setFill(Color.web("#455A64"));
                gc.fillRect(coordinates[0] + 10, coordinates[1] + 25, 30, 40);
                gc.setFill(Color.WHITE);
                gc.drawImage(heater, coordinates[0] + 10, coordinates[1] + 35);
            }
            if(!room.getHvacStopped() && room.getTemperature() < room.getCurrentTemperature() && !room.getTemperatureDefault()
                    || (room.getCurrentTemperature() < temperatureInInt && isAwayMode() && !room.getHvacStopped())) {
                gc.setFill(Color.web("#455A64"));
                gc.fillRect(coordinates[0] + 10, coordinates[1] + 25, 30, 40);
                gc.setFill(Color.WHITE);
                gc.drawImage(ac, coordinates[0] + 10, coordinates[1] + 35);
            }
            if (room.getHvacStopped()){
                gc.setFill(Color.web("#455A64"));
                gc.fillRect(coordinates[0] + 10, coordinates[1] + 25, 30, 40);
                gc.setFill(Color.WHITE);
                gc.drawImage(null, coordinates[0] + 10, coordinates[1] + 35);
            }
        }
    }

    /**
     * Alerting the user for a potential pipe burst
     */
    private void alterPipeBurst() {
    	if(this.alterOnHold==false) {
        	this.alterOnHold = true;
        	consoleLog("The temperature inside room is 0 degree,there is a potential pipes burst, please increase the temperature in the room!");
        	Alert alert = new Alert(Alert.AlertType.WARNING, "The temperature inside room is 0 degree, "
        			+ "there is a potential pipes burst, please increase the temperature in the room!");
        	javafx.application.Platform.runLater(alert::showAndWait);
        }
	}
	/**
     * This function will draw the lights with a given room.
     *
     * @param room where light will be drawn.
     */
    public void drawLight(Room room) {
        String name = room.getName();
        Image img = null;
        if (room.getLightsOn() == 0) {
            try {
                img = new Image(new FileInputStream("src/main/resources/Images/lightOff.png"), 60, 27, true, false);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            int[] coordinates = roomPosition.get(name);
            gc.drawImage(img, coordinates[0] + 72, coordinates[1] + 2);
        }
        else {
            try {
                img = new Image(new FileInputStream("src/main/resources/Images/lightOn.png"), 60, 27, true, false);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            int[] coordinates = roomPosition.get(name);
            gc.drawImage(img, coordinates[0] + 72, coordinates[1] + 2);
        }
    }

    /**
     * This function will draw the doors in a given room.
     *
     * @param room where door will be
     * @param position where door is situated
     */
    public void drawDoor(Room room, String position) {
        int[] coordinates = roomPosition.get(room.getName());
        int x = coordinates[0];
        int y = coordinates[1];

        gc.setLineWidth(2);
        for (Door door : room.getDoors()) {
            if (door.getOpenDoor()) {
                gc.setStroke(Color.web("#455A64"));
            } else {
                gc.setStroke(Color.SANDYBROWN);
            }
            switch (door.getPosition()) {
                case BOTTOM -> {
                    gc.strokeLine(x + (ROOM_SIZE - DOOR_SIZE) / 2, y + ROOM_SIZE, x + (ROOM_SIZE - DOOR_SIZE) / 2 + DOOR_SIZE, y + ROOM_SIZE);
                }
                case RIGHT -> {
                    gc.strokeLine(x + ROOM_SIZE, y + (ROOM_SIZE - DOOR_SIZE) / 2, x + ROOM_SIZE, y + DOOR_SIZE + (ROOM_SIZE - DOOR_SIZE) / 2);
                }
                case TOP -> {
                    gc.strokeLine(x + (ROOM_SIZE - DOOR_SIZE) / 2, y, x + DOOR_SIZE + (ROOM_SIZE - DOOR_SIZE) / 2, y);
                }
                case LEFT -> {
                    gc.strokeLine(x, y + (ROOM_SIZE - DOOR_SIZE) / 2, x, y + DOOR_SIZE + (ROOM_SIZE - DOOR_SIZE) / 2);
                }
            }
        }
        gc.setLineWidth(1);
        gc.setStroke(Color.BLACK);
    }

    /**
     * Method responsible for drawing the rooms in their correct location
     *
     * @param roomHashMap A map of the rooms and their names as the key
     * @param room        room to draw
     * @param visited     rooms that have been visited
     * @param previous    position of the previously visited room
     * @param x           x coordinate of the previously visited room
     * @param y           y coordinate of the previously visited room
     */
    public void drawRoom(Map<String, Room> roomHashMap, Room room, Set<Room> visited, Position previous, int x, int y) {
        visited.add(room);

        // if room is entrance or backyard no line is drawn
        if (room.getName().equals("Entrance") || room.getName().equals("Backyard")) {
            switch (previous) {
                case NONE -> {
                    gc.fillText(room.getName(), x + 10, y + 15);
                }
                case BOTTOM -> {
                    y += ROOM_SIZE;
                    gc.fillText(room.getName(), x + 10, y + 15);
                }
                case RIGHT -> {
                    x += ROOM_SIZE;
                    gc.fillText(room.getName(), x + 10, y + 15);
                }
                case TOP -> {
                    y -= ROOM_SIZE;
                    gc.fillText(room.getName(), x + 10, y + 15);
                }
                case LEFT -> {
                    x -= ROOM_SIZE;
                    gc.fillText(room.getName(), x + 10, y + 15);
                }
            }
        }
        else {
            switch (previous) {
                case NONE -> {
                    gc.strokeRect(x, y, ROOM_SIZE, ROOM_SIZE);
                    gc.fillText(room.getName(), x + 10, y + 15);
                }
                case BOTTOM -> {
                    y += ROOM_SIZE;
                    gc.strokeRect(x, y, ROOM_SIZE, ROOM_SIZE);
                    gc.fillText(room.getName(), x + 10, y + 15);
                }
                case RIGHT -> {
                    x += ROOM_SIZE;
                    gc.strokeRect(x, y, ROOM_SIZE, ROOM_SIZE);
                    gc.fillText(room.getName(), x + 10, y + 15);
                }
                case TOP -> {
                    y -= ROOM_SIZE;
                    gc.strokeRect(x, y, ROOM_SIZE, ROOM_SIZE);
                    gc.fillText(room.getName(), x + 10, y + 15);
                }
                case LEFT -> {
                    x -= ROOM_SIZE;
                    gc.strokeRect(x, y, ROOM_SIZE, ROOM_SIZE);
                    gc.fillText(room.getName(), x + 10, y + 15);
                }
            }
        }

        //updating a hashMap to get all the roomPosition with their coordinates.
        int[] coordinates = {x, y};
        roomPosition.put(room.getName(), coordinates);

        //draw the light for a room.
        drawLight(room);

        //draw the window for a room
        for(int i = 0; i < room.getWindows().size() ; i++) {
            drawWindows(room, room.getWindows().get(i).getPosition().toString());
        }

        //draw the door for a room
        for(int i = 0; i < room.getDoors().size() ; i++) {
            drawDoor(room, room.getDoors().get(i).getPosition().toString());
        }

        for (Door child : room.getDoors()) {
            Room nextRoom = roomHashMap.get(child.getConnection());
            if (!visited.contains(nextRoom))
                drawRoom(roomHashMap, nextRoom, visited, child.getPosition(), x, y);
        }
    }

    /**
     * This function creates list of selected rooms.
     *
     * @param event The event that called this function
     */
    public void addRoom(ActionEvent event) throws FileNotFoundException {
    	
        String selectedRoom = comboRoom.getValue();
        if (selectedRoom == null) {
            consoleLog("Please select a room first.");
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a room first.");
            alert.showAndWait();
        }
        else {
            selectedRooms.put(selectedRoom, allRooms.get(selectedRoom));
            availableRooms.remove(selectedRoom);
            comboRoom.getItems().clear();
            comboRoom.getItems().addAll(availableRooms.keySet());
            Label room = new Label();
            room.setText(selectedRoom);

            Image deleteIcon = new Image(new FileInputStream("src/main/resources/Images/deleteIcon.png"), 60, 27, true, false);
            ImageView delete = new ImageView(deleteIcon);

            delete.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent e) {
                    availableRooms.put(selectedRoom, selectedRooms.remove(availableRooms));
                    comboRoom.getItems().clear();
                    comboRoom.getItems().addAll(availableRooms.keySet());
                    gpRooms.getChildren().remove(room);
                    gpRooms.getChildren().remove(delete);
                }
            });

            gpRooms.addRow(gpRooms.getRowCount(), room, delete);
            vboxRooms.getChildren().clear();
            vboxRooms.getChildren().add(gpRooms);
            EditSimulationController.setGpRooms(gpRooms);
        }
    }

    /**
     * This function will create the zone and show it on the screen with the delete functionality
     *
     * @param event that calls this function
     */
    public void createZone(ActionEvent event) {
    	if(userRole.getText().equals(UserRoles.CHILD.toString()) || userRole.getText().equals(UserRoles.STRANGER.toString())|| userRole.getText().equals(UserRoles.GUEST.toString())) {
    		Alert alert = new Alert(Alert.AlertType.WARNING, "You do not have the permission for such action.");
            alert.showAndWait();
            return;
    	}
        if (selectedRooms.size() == 0) {
            consoleLog("Please add a room/rooms first.");
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please add a room/rooms first.");
            alert.showAndWait();
        }
        else {
            String zoneName = textZoneName.getText();
            if (zoneName.equals("")) {
                consoleLog("Please enter a name for the zone.");
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please enter a name for the zone.");
                alert.showAndWait();
            }
            else {
                gpRooms.getChildren().clear();
                consoleLog("Zone " + zoneName + " with " + selectedRooms.size() + " rooms has been created.");
                Collection<Room> values = selectedRooms.values();
                ArrayList<Room> listOfRooms = new ArrayList<Room>(values);
                Zone zone = new Zone(zoneName, listOfRooms);
                zones.put(zoneName, zone);
                selectedRooms.forEach((k, v) -> {
                    Label room = new Label();
                    Label zone_name = new Label();
                    String roomName = v.getName();
                    room.setText(roomName);
                    zone_name.setText(zoneName + ": ");

                    Image deleteIcon = null;
                    try {
                        deleteIcon = new Image(new FileInputStream("src/main/resources/Images/deleteIcon.png"), 60, 27, true, false);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    ImageView delete = new ImageView(deleteIcon);
                    TextField temp1 = new TextField();
                    TextField temp2 = new TextField();
                    TextField temp3 = new TextField();
                    Button setTemp = new Button();

                    delete.setOnMousePressed(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent e) {
                        	if(userRole.getText().equals(UserRoles.CHILD.toString()) || userRole.getText().equals(UserRoles.STRANGER.toString())|| userRole.getText().equals(UserRoles.GUEST.toString())) {
                        		Alert alert = new Alert(Alert.AlertType.WARNING, "You do not have the permission for such action.");
                                alert.showAndWait();
                                return;
                        	}
                            consoleLog(roomName + " from zone " + zoneName + " has been deleted.");
                            availableRooms.put(roomName, allRooms.get(roomName));
                            comboRoom.getItems().clear();
                            comboRoom.getItems().addAll(availableRooms.keySet());
                            listOfRooms.remove(room);
                            zone.setRooms(listOfRooms);
                            gpZone.getChildren().remove(zone_name);
                            gpZone.getChildren().remove(room);
                            gpZone.getChildren().remove(delete);
                            gpZone.getChildren().remove(temp1);
                            gpZone.getChildren().remove(temp2);
                            gpZone.getChildren().remove(temp3);
                            gpZone.getChildren().remove(setTemp);
                            gpRooms.getChildren().remove(room);
                            gpRooms.getChildren().remove(delete);
                        }
                    });

                    double[] temps = new double[3];

                    temp1.setPrefWidth(50);
                    temp1.setPromptText(String.valueOf(zone.getZoneTemp()[0]));
                    temp2.setPrefWidth(50);
                    temp2.setPromptText(String.valueOf(zone.getZoneTemp()[1]));
                    temp3.setPrefWidth(50);
                    temp3.setPromptText(String.valueOf(zone.getZoneTemp()[2]));
                    setTemp.setText("set");
                    setTemp.setMaxWidth(35);
                    setTemp.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                        	if(userRole.getText().equals(UserRoles.CHILD.toString()) || userRole.getText().equals(UserRoles.STRANGER.toString())|| userRole.getText().equals(UserRoles.GUEST.toString())) {
                        		Alert alert = new Alert(Alert.AlertType.WARNING, "You do not have the permission for such action.");
                                alert.showAndWait();
                                return;
                        	}
                            consoleLog( "Temperature for zone " + zoneName + " has been set.", ConsoleComponents.SHH);
                            temps[0] = Double.parseDouble(temp1.getText());
                            temps[1] = Double.parseDouble(temp2.getText());
                            temps[2] = Double.parseDouble(temp3.getText());
                            zone.setZoneTemp(temps);

                            time.textProperty().addListener((observable, oldValue, newValue) -> {
                                int j = 0;
                                if (newValue.startsWith("08") || newValue.startsWith("09") || newValue.startsWith("10") ||
                                        newValue.startsWith("11") || newValue.startsWith("12") || newValue.startsWith("13") ||
                                        newValue.startsWith("14") || newValue.startsWith("15")) {
                                    j = 0;
                                }
                                if (newValue.startsWith("16") || newValue.startsWith("17") || newValue.startsWith("18") ||
                                        newValue.startsWith("19") || newValue.startsWith("20") || newValue.startsWith("21") ||
                                        newValue.startsWith("22") || newValue.startsWith("23")) {
                                    j = 1;
                                }
                                if (newValue.startsWith("00") || newValue.startsWith("01") || newValue.startsWith("02") ||
                                        newValue.startsWith("03") || newValue.startsWith("04") || newValue.startsWith("05") ||
                                        newValue.startsWith("06") || newValue.startsWith("07")) {
                                    j = 2;
                                }

                                for (int i = 0; i < zone.getRooms().size(); i++) {
                                    zone.getRooms().get(i).setTemperatureDefault(false);
                                    if (!zone.getRooms().get(i).getOverride()) {
                                        zone.getRooms().get(i).setTemperature(zone.getZoneTemp()[j]);
                                        int finalI = i;
                                        //when desired temperature is lower but HVAC is stopped
                                        if (zone.getRooms().get(finalI).getCurrentTemperature() > Double.parseDouble(temperature.getText())
                                                && zone.getRooms().get(finalI).getHvacStopped()) {
                                            zone.getRooms().get(finalI).setSettingTemperature(true);
                                            Timer t2 = new Timer();
                                            t2.schedule(new TimerTask() {
                                                @Override
                                                public void run() {
                                                    if (zone.getRooms().get(finalI).getCurrentTemperature() > Double.parseDouble(temperature.getText()) && zone.getRooms().get(finalI).getHvacStopped()) {
                                                        zone.getRooms().get(finalI).setCurrentTemperature(Math.round(((zone.getRooms().get(finalI).getCurrentTemperature() * 100 - 5) / 100) * 100.00) / 100.00);
                                                    }
                                                }
                                            }, (long) clockSpeed);
                                        }
                                        //when desired temperature is higher but HVAC is stopped
                                        if (zone.getRooms().get(finalI).getCurrentTemperature() < Double.parseDouble(temperature.getText()) && zone.getRooms().get(finalI).getHvacStopped()) {
                                            zone.getRooms().get(finalI).setSettingTemperature(true);
                                            Timer t2 = new Timer();
                                            t2.schedule(new TimerTask() {
                                                @Override
                                                public void run() {
                                                    if (zone.getRooms().get(finalI).getCurrentTemperature() < Double.parseDouble(temperature.getText()) && zone.getRooms().get(finalI).getHvacStopped()) {
                                                        zone.getRooms().get(finalI).setCurrentTemperature(Math.round(((zone.getRooms().get(finalI).getCurrentTemperature() * 100 + 5) / 100) * 100.00) / 100.00);
                                                    }
                                                }
                                            }, (long) clockSpeed);
                                        }

                                        //when desired temperature is lower, AC will be turned on
                                        if (zone.getRooms().get(finalI).getCurrentTemperature() > zone.getRooms().get(finalI).getTemperature() && !zone.getRooms().get(finalI).getHvacStopped()) {
                                            //AC should be turned on
                                            zone.getRooms().get(finalI).setSettingTemperature(true);
                                            Timer t = new Timer();
                                            t.schedule(new TimerTask() {
                                                @Override
                                                public void run() {
                                                    if (zone.getRooms().get(finalI).getCurrentTemperature() > zone.getRooms().get(finalI).getTemperature() && !zone.getRooms().get(finalI).getHvacStopped()) {
                                                        zone.getRooms().get(finalI).setCurrentTemperature(Math.round(((zone.getRooms().get(finalI).getCurrentTemperature() * 100 - 10) / 100) * 100.00) / 100.00);
                                                        if (zone.getRooms().get(finalI).getCurrentTemperature() == zone.getRooms().get(finalI).getTemperature() && !zone.getRooms().get(finalI).getHvacStopped()) {
                                                            zone.getRooms().get(finalI).setHvacPaused(true);
                                                        }
                                                    } else if (zone.getRooms().get(finalI).getHvacPaused() && (zone.getRooms().get(finalI).getCurrentTemperature() - zone.getRooms().get(finalI).getTemperature()) > 0.25 &&
                                                            zone.getRooms().get(finalI).getCurrentTemperature() > zone.getRooms().get(finalI).getTemperature() && !zone.getRooms().get(finalI).getHvacStopped()) {
                                                        zone.getRooms().get(finalI).setHvacPaused(false);
                                                    }
                                                }
                                            }, (long) clockSpeed);
                                        }


                                        //when desired temperature is higher, Heater will be turned on
                                        if (zone.getRooms().get(finalI).getCurrentTemperature() < zone.getRooms().get(finalI).getTemperature() && !zone.getRooms().get(finalI).getHvacStopped()) {
                                            //Heater should be turned on
                                            zone.getRooms().get(finalI).setSettingTemperature(true);
                                            Timer t = new Timer();
                                            t.schedule(new TimerTask() {
                                                @Override
                                                public void run() {
                                                    if (zone.getRooms().get(finalI).getCurrentTemperature() < zone.getRooms().get(finalI).getTemperature() - 0.25 && !zone.getRooms().get(finalI).getHvacStopped()) {
                                                        zone.getRooms().get(finalI).setCurrentTemperature(Math.round(((zone.getRooms().get(finalI).getCurrentTemperature() * 100 + 10) / 100) * 100.00) / 100.00);
                                                        if (zone.getRooms().get(finalI).getCurrentTemperature() == zone.getRooms().get(finalI).getTemperature()) {
                                                            zone.getRooms().get(finalI).setHvacPaused(true);
                                                        }
                                                    } else if (zone.getRooms().get(finalI).getHvacPaused() && (zone.getRooms().get(finalI).getTemperature() - zone.getRooms().get(finalI).getCurrentTemperature()) > 0.25 &&
                                                            zone.getRooms().get(finalI).getCurrentTemperature() < zone.getRooms().get(finalI).getTemperature() && !zone.getRooms().get(finalI).getHvacStopped()) {
                                                        zone.getRooms().get(finalI).setHvacPaused(false);
                                                    }
                                                }
                                            }, 1000);
                                        }
                                    }
                                }
                            });
                        }
                    });
                    gpZone.addRow(gpZone.getRowCount(), zone_name, room, delete, temp1, temp2, temp3, setTemp);
                });
                selectedRooms.clear();

                vboxZones.getChildren().clear();
                vboxZones.getChildren().addAll(gpZone);
                EditSimulationController.setGpZones(gpZone);
            }
        }
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
     * Gets the location of a mouse.
     *
     * @param event The event that called this function
     */
    public void getLocation(MouseEvent event) {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }

    /**
     * Changes the location of the window(stage) based on the mouse location.
     *
     * @param event The event that called this function
     */
    public void move(MouseEvent event) {
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setX(event.getScreenX() - xOffset);
        window.setY(event.getScreenY() - yOffset);
    }

    /**
     * This function loads the change date and time page(scene) into the window(stage)
     *
     * @param event The event that called this function
     * @throws IOException Thrown if the scene file cannot be read
     */
    public void bt_changeDateTimeOnClick(ActionEvent event) throws IOException {
    	if (!toggleText.getText().equals("ON")) {
    		consoleLog("Simulation is off, unable to process action.");
    	} else {
	    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/changeDateTime.fxml"));
	        Parent root = loader.load();
	        Stage stage = new Stage();
	        stage.initStyle(StageStyle.TRANSPARENT);
	        stage.setScene(new Scene(root));
	        stage.show();
    	}
    }

    /**
     * This function loads the edit(scene) into the window(stage)
     *
     * @param event The event that called this function
     * @throws IOException Thrown if the file cannot be read
     */
    public void goToEdit(ActionEvent event) throws IOException {
        if (!toggleText.getText().equals("ON")) {
            consoleLog("Simulation is off, enable to process action.");
        } else {
            if (Objects.nonNull(house)) {
                Parent edit = FXMLLoader.load(getClass().getResource("/view/editSimulation.fxml"));
                Scene editScene = new Scene(edit);

                // stage info
                Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                window.setScene(editScene);
                window.show();
            } else {
                consoleLog("Please input the house to change location.");
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please input the house");
                alert.showAndWait();
            }
        }

    }

    /**
     * Accessor for the current session's house plan
     *
     * @return Map of locations with their name as the key and the value being the individual {@link Room} plan
     */
    public static Map<String, Room> getHouse() {
        return house;
    }

    /**
     * Deletes the current house
     */
    public static void deleteHouse() {
        LoginInfoController.house = null;
    }

    /**
     * This function loads the scheduleLights pane
     *
     * @param event the event that triggers this function
     * @throws IOException if the view file is not found
     */
    public void scheduleLights(ActionEvent event) throws IOException {
    	if (!toggleText.getText().equals("ON")) {
    		consoleLog("Simulation is off, enable to process action.");
    	} else {
	        if (!awayMode){
	        	consoleLog("Away mode is turned off, cannot schedule the lights");
	            Alert alert = new Alert(Alert.AlertType.WARNING, "Away mode is turned off");
	            alert.showAndWait();
	            return;
	        }
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/lightsSchedule.fxml"));
	        Parent root = loader.load();
	        Stage stage = new Stage();
	        stage.initStyle(StageStyle.TRANSPARENT);
	        stage.setScene(new Scene(root));
	        stage.show();
    	}
    }

    /**
     * This function writes the light scheduling to the loginInfo SHP page and calls the method to
     * check the scheduling
     * 
     * @param room the name of the room in which the light will remain on
     * @param times the begin and end times at which the light remains on
     * @throws ParseException if the Date can't be parsed correctly
     */
    public void setRoomLightSchedule(String room, String times) throws ParseException {
        roomToLight.setText(roomToLight.getText() + "\n" + room + " " + times);

        String[] timeBeginEnd = times.split("-");

        Date beginTime=new SimpleDateFormat("HH:mm").parse(timeBeginEnd[0]);
        Date endTime=new SimpleDateFormat("HH:mm").parse(timeBeginEnd[1]);

        Date[] arr = new Date[]{beginTime, endTime};
        lightsSchedule.put(room, arr);

        lightScheduleLight(room, beginTime, endTime);
    }

    /**
     * This function is called when the time is changed. It checks through every entry to determine if some lights
     * need to be turned on based on the schedule
     * @throws ParseException if the Date can't be parsed correctly in lightScheduleLight
     */
    public void onTimeChangeLightRooms() throws ParseException {
        for (Map.Entry<String, Date[]> entry: lightsSchedule.entrySet()) {
            lightScheduleLight(entry.getKey(), entry.getValue()[0], entry.getValue()[1]);
        }
    }

    /**
     * This function determines if the light should be turned on/off depending on the schedule
     *
     * @param room name of the room in which to turn on/off light
     * @param beginTime begin time of the schedule
     * @param endTime end time of the schedule
     * @throws ParseException if the Date can't be parsed correctly
     */
    public void lightScheduleLight(String room, Date beginTime, Date endTime) throws ParseException {

        String currentTimeWithoutSeconds ="";
        if (timeStr == null){
            currentTimeWithoutSeconds = time.toString().substring(33, 38);
        }
        else {
            currentTimeWithoutSeconds = timeStr.substring(0,4);
        }
        Date currentTime=new SimpleDateFormat("HH:mm").parse(currentTimeWithoutSeconds);

        if(endTime.after(currentTime) && currentTime.after(beginTime)){
            findRoomToLight(room, 1);
        }
        else {
            findRoomToLight(room, 0);
        }
    }

    /**
     * This method finds the room in roomArray sets the number of lights on to 1 and calls the drawLight method
     * @param room name of the room to turn the light on in
     */
    public void findRoomToLight(String room, int on){
        int index = 0;
        for (int i=0; i<roomArray.length; i++){
            if (roomArray[i].getName().equals(room)){
                index = i;
            }
        }
        roomArray[index].setLightsOn(on);
        drawLight(roomArray[index]);
    }
    
    /**
	 * Auto controll door lock states
	 * Room to auto locked: entrance, backyard and garage, +outside
	 */
	public void updateAutoDoors() {
		if(roomArray!=null && autoMode) {
			boolean closeDoor = false;
			Map<String, String> location = EditSimulationController.getUserLocations();
			String userRoomName = location.get(username);
			if(!userRoomName.equalsIgnoreCase("entrance") && !userRoomName.equalsIgnoreCase("backyard") && !userRoomName.equalsIgnoreCase("garage") && !userRoomName.equalsIgnoreCase("outside")) {
				closeDoor = true;
			} else closeDoor = false;
			for (int i = 0; i < roomArray.length; i++) {
				Room room = roomArray[i];
				if(room.getName().equalsIgnoreCase("entrance") || room.getName().equalsIgnoreCase("backyard") ||room.getName().equalsIgnoreCase("garage") || room.getName().equalsIgnoreCase("outside")) {
					ArrayList<Door> doorList = room.getDoors();
					for (int j = 0; j < doorList.size(); j++) { 
						if(closeDoor) {
							room.getDoors().get(j).setOpenDoor(false);
							room.getDoors().get(j).setLock(true);
						}
						else room.getDoors().get(j).setLock(false);
				      } 
					
				} 
			}
		}
	}
	
	/**
	 * Auto control lighting states of the house according to user location
	 */
	public void updateAutoLights() {
		if(roomArray!=null && autoMode) {
			Map<String, String> location = EditSimulationController.getUserLocations();
			String userRoomName = location.get(username);
			for (int i = 0; i < roomArray.length; i++) {
				if (roomArray[i].getName().equalsIgnoreCase((userRoomName))) {
					roomArray[i].setLightsOn(1);
				} else {
					roomArray[i].setLightsOn(0);
				}
			}
		}
	}

    /**
     * This method closes all the windows, doors and lights when away mode is turned on
     */
    public boolean closeWindowsDoorsLights(){
        if (roomArray !=  null) {
            for (Room r : roomArray) {
                ArrayList<Window> windowList = r.getWindows();
                for (Window w : windowList) {
                    if (w.getBlocking() && w.getOpenWindow()) {
                        consoleLog("Cannot activate away mode, one of the window is blocked by object.");
                        return false;
                    }
                }
            }
            for (Room r : roomArray) {
                r.setLightsOn(0);
                drawLight(r);

                ArrayList<Window> windowList = r.getWindows();
                for (Window w : windowList) {
                    w.setOpenWindow(false);
                    drawWindows(r, w.getPosition().toString());
                }
                ArrayList<Door> doorList = r.getDoors();
                for (Door d : doorList) {
                    d.setOpenDoor(false);
                    drawDoor(r, d.getPosition().toString());
                }
            }
        }
    	return true;
    }

    /**
     * This method sets the timeBeforeAlert variable
     */
    public void onSetTimeBeforeAlert(){
        if (!awayMode) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Away mode is turned off");
            alert.showAndWait();
            return;
        }
        timeBeforeAlert = timeBeforeAlertInput.getText();
    }


    /**
     * This function sends a notification to the user after the time delay has passed
     * after a user has been detected in the house while in away mode
     *
     * @param timeDelay the timeDelay to wait
     */
    public void sendNotification(String timeDelay){
        if (timeDelay == "0"){
            consoleLog("Notification sent to user. The time before alerting authorities of " + timeDelay
                    + " seconds has been elapsed.");
        }
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(Double.parseDouble(timeDelay)), e -> {
                    consoleLog("Notification sent to user. The time before alerting authorities of " + timeDelay
                            + " seconds has been elapsed.");
                })
        );
        timeline.play();
    }

    /**
     * Getter for summerAway text field
     *
     * @return summerAwayTF
     */
    public TextField getSummerAwayTF(){
        return summerAwayTF;
    }
    /**
     * Getter for winterAway text field
     *
     * @return winterAwayTF
     */
    public TextField getWinterAwayTF(){
        return winterAwayTF;
    }

    /**
     * Getter for default summer temp
     *
     * @return defaultSummerTemp integer
     */
    public int getDefaultSummerTemp(){
        return defaultSummerTemp;
    }
    /**
     * Getter for default winter temp
     *
     * @return defaultWinterTemp integer
     */
    public int getDefaultWinterTemp(){
        return defaultWinterTemp;
    }

    /**
     * Getter for default summer label
     *
     * @return defaultSummer label
     */
    public Label getDefaultAwaySummer(){
        return defaultAwaySummer;
    }
    /**
     * Getter for default winter label
     *
     * @return defaultWinter label
     */
    public Label getDefaultAwayWinter(){
        return defaultAwayWinter;
    }

    /**
     * This method is called when the default temperature for away mode in summer label is clicked
     *
     * @param mouseEvent The event that triggered the method call
     */
    public void setDefaultSummer(MouseEvent mouseEvent) {
        defaultSummerContainer.getChildren().add(defaultAwaySummer);
        summerAwayTF.setText(defaultAwaySummer.getText());
        summerAwayTF.setPrefWidth(20 + (defaultAwaySummer.getText().length() * 5));
        hBoxSummer.getChildren().add(0, summerAwayTF);
        summerAwayTF.requestFocus();
        summerAwayTF.setOnAction(e -> {
            changeDefaultTemp(Season.SUMMER);
        });
    }
    /**
     * This method is called when the default temperature for away mode in winter label is clicked
     *
     * @param mouseEvent The event that triggered the method call
     */
    public void setDefaultWinter(MouseEvent mouseEvent) {
        defaultWinterContainer.getChildren().add(defaultAwayWinter);
        winterAwayTF.setText(defaultAwayWinter.getText());
        winterAwayTF.setPrefWidth(20 + (defaultAwayWinter.getText().length() * 5));
        hBoxWinter.getChildren().add(0, winterAwayTF);
        winterAwayTF.requestFocus();
        winterAwayTF.setOnAction(e -> {
            changeDefaultTemp(Season.WINTER);
        });
    }

    /**
     * This method is called on enter and modifies the default temperatures for away mode
     * for each season
     *
     */
    protected void changeDefaultTemp(Season season) {
        int temp = 0;
        if (season.equals(Season.SUMMER)){
            defaultSummerContainer.getChildren().add(summerAwayTF);
            hBoxSummer.getChildren().add(0, defaultAwaySummer);
            defaultAwaySummer.setText(summerAwayTF.getText());
            summerAwayTF.clear();
            defaultSummerTemp = Integer.parseInt(defaultAwaySummer.getText());
            temp = defaultSummerTemp;
        }
        else if (season.equals(Season.WINTER)){
            defaultWinterContainer.getChildren().add(winterAwayTF);
            hBoxWinter.getChildren().add(0, defaultAwayWinter);
            defaultAwayWinter.setText(winterAwayTF.getText());
            winterAwayTF.clear();
            defaultWinterTemp = Integer.parseInt(defaultAwayWinter.getText());
            temp = defaultWinterTemp;
        }
        consoleLog("Set the default temperature for " + season +
                " when the home is in away mode to "  + temp + " À°C.", ConsoleComponents.SHH);
    }

    /**
     * This method is triggered when away mode is turned on. It sets the temperatures in the house
     * to the default values set for away mode for the summer and winter seasons
     *
     * @throws ParseException if the Date can't be parsed correctly
     */
    public void setDefaultTemperatures() throws ParseException {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy - MMMM - dd", Locale.ENGLISH);
        calendar.setTime(sdf.parse(getDate()));
        if (EditSimulationController.getCurrentSeason(calendar) == Season.SUMMER) {
            for (Room room : roomArray) {
                time.textProperty().addListener((observable, oldValue, newValue) -> {
                    if (room.getCurrentTemperature() < defaultSummerTemp && temperatureInInt > room.getCurrentTemperature()) {
                        // Turn Off AC
                        room.setHvacStopped(true);
                        try {
                            drawTemperature(room);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        Timer t2 = new Timer();
                        t2.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                if (room.getCurrentTemperature() < defaultSummerTemp && room.getCurrentTemperature() < temperatureInInt) {
                                    room.setCurrentTemperature(Math.round(((room.getCurrentTemperature() * 100 + 5) / 100) * 100.00) / 100.00);
                                }
                            }
                        }, 1000);
                        try {
                            // if the outside temperature is warmer than the inside once the default is reached, AC
                            // needs to be turned on in order to maintain it.
                            if (room.getCurrentTemperature() < temperatureInInt && (defaultSummerTemp - 0.10) <= room.getCurrentTemperature()
                                && room.getCurrentTemperature() <= defaultSummerTemp){
                                room.setHvacStopped(false);
                                drawTemperature(room);
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                });
                if (!room.getName().equals("Garage")){
                    consoleLog("The temperature in the " + room.getName() +
                                    " is cooler than the default temperature set for away mode in Summer. Turning AC off.",
                            ConsoleComponents.SHH);
                }
            }
        } else if (EditSimulationController.getCurrentSeason(calendar) == Season.WINTER) {
            for (Room room : roomArray) {
                time.textProperty().addListener((observable, oldValue, newValue) -> {
                    if (room.getCurrentTemperature() > defaultWinterTemp && room.getCurrentTemperature() > temperatureInInt && !room.getName().equals("Garage")) {
                        // Turn Off Heating
                        room.setHvacStopped(true);
                        try {
                            drawTemperature(room);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        Timer t2 = new Timer();
                        t2.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                if (room.getCurrentTemperature() > defaultWinterTemp) {
                                    room.setCurrentTemperature(Math.round(((room.getCurrentTemperature() * 100 - 5) / 100) * 100.00) / 100.00);
                                }
                            }
                        }, 1000);
                        try {
                            // if the outside temperature is cooler than the inside once the default is reached, heating
                            // needs to be turned on in order to maintain it.
                            if (room.getCurrentTemperature() > temperatureInInt
                                    && (defaultWinterTemp) <= room.getCurrentTemperature()
                                    && room.getCurrentTemperature() <= (defaultWinterTemp + 0.10)) {
                                room.setHvacStopped(false);
                                drawTemperature(room);
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                });
                if (!room.getName().equals("Garage")) {
                    consoleLog("The temperature in the " + room.getName() +
                                    " is warmer than the default temperature set for away mode in Winter. Turning Heating off.",
                            ConsoleComponents.SHH);
                }
            }
        }
    }

    /**
     * @param gpZone set the gpZone GridPane
     */
    public static void setGpZone (GridPane gpZone) {
        LoginInfoController.gpZone = gpZone;
    }

    /**
     * @return get the gpZone GridPane
     */
    public static GridPane getGpZone () {
        return gpZone;
    }

    /**
     * @param gpRooms set the gpRooms GridPane
     */
    public static void setGpRooms (GridPane gpRooms) {
        LoginInfoController.gpRooms = gpRooms;
    }

    /**
     * @return get the gpZone GridPane
     */
    public static GridPane getGpRooms () {
        return gpRooms;
    }

    /**
     * @param gpRoomsTemp set the gpRoomsTemp GridPane
     */
    public static void setGpRoomsTemp (GridPane gpRoomsTemp) {
        LoginInfoController.gpZone = gpZone;
    }

    /**
     * @return get the gpRoomsTemp GridPane
     */
    public static GridPane getGpRoomsTemp () {
        return gpRoomsTemp;
    }
}
