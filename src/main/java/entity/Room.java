package entity;

import java.util.ArrayList;

import javafx.scene.image.ImageView;

/**
 * class Room to represent Room objects in the house
 */
public class Room {
    private String name;
    private ArrayList<Window> windows;
    private ArrayList<Door> doors;
    private int lightsOn;
    private int lightsTotal;
    private double roomTemperature;
    private double currentRoomTemperature;
    private boolean overrideTemperature;
    private boolean hvacPaused;
    private boolean hvacStopped;
    private boolean temperatureDefault;
    private boolean settingTemperature;
    private ImageView[] imageView = new ImageView[5];

    /**
     * Constructor for class Room
     *
     * @param name        name of the room
     * @param windows     windows in the room
     * @param doors       doors in the room
     * @param lightsTotal total lights in the room
     */
    private Room(String name, ArrayList<Window> windows, ArrayList<Door> doors, int lightsTotal) {
        this.name = name;
        this.windows = windows;
        this.doors = doors;
        this.lightsTotal = lightsTotal;
    }
    
    /**
     * @param imageView image view object
     */
    public void setImageView(ImageView[] imageView) {
    	this.imageView = imageView;
    }
    
    /**
     * @return image view object
     */
    public ImageView[] getImageView() {
    	return this.imageView;
    }

    /**
     * @return name of the room
     */
    public String getName() {
        return name;
    }

    /**
     * @param name name of the room
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return number of lights on
     */
    public int getLightsOn() {
        return lightsOn;
    }

    /**
     * @param lightsOn number of lights on
     */
    public void setLightsOn(int lightsOn) {
        this.lightsOn = lightsOn;
    }

    /**
     * @return total number of lights
     */
    public int getLightsTotal() {
        return lightsTotal;
    }

    /**
     * @param lightsTotal total number of lights
     */
    public void setLightsTotal(int lightsTotal) {
        this.lightsTotal = lightsTotal;
    }

    /**
     * @return temperature of the room
     */
    public double getTemperature() {
        return roomTemperature;
    }

    /**
     * @param roomTemperature temperature of the room
     */
    public void setTemperature(double roomTemperature) {
        this.roomTemperature = roomTemperature;
    }

    /**
     * @return current temperature of the room
     */
    public double getCurrentTemperature() {
        return currentRoomTemperature;
    }

    /**
     * @param currentRoomTemperature of the room
     */
    public void setCurrentTemperature(double currentRoomTemperature) {
        this.currentRoomTemperature = currentRoomTemperature;
    }

    /**
     * @return windows in the room
     */
    public ArrayList<Window> getWindows() {
        return windows;
    }

    /**
     * @param windows windows in the room
     */
    public void setWindows(ArrayList<Window> windows) {
        this.windows = windows;
    }

    /**
     * @return doors in the room
     */
    public ArrayList<Door> getDoors() {
        return doors;
    }

    /**
     * @param doors doors in the room
     */
    public void setDoors(ArrayList<Door> doors) {
        this.doors = doors;
    }

    /**
     * @param overrideTemperature override temperature in a specific room
     */
    public void setOverride(boolean overrideTemperature) {
        this.overrideTemperature = overrideTemperature;
    }

    /**
     * @return overridden temperature
     */
    public boolean getOverride() {
        return overrideTemperature;
    }

    /**
     * @return HAVC pausing Heating, AC, Ventilation system
     */
    public boolean getHvacPaused(){
        return hvacPaused;
    }

    /**
     * @param hvacPaused pause/unpause HVAC
     */
    public void setHvacPaused(boolean hvacPaused) {
        this.hvacPaused = hvacPaused;
    }

    /**
     * @return HAVC stopping Heating, AC, Ventilation system
     */
    public boolean getHvacStopped() { return hvacStopped; }

    /**
     * @param hvacStopped stop/start HVAC
     */
    public void setHvacStopped(boolean hvacStopped) {
        this.hvacStopped = hvacStopped;
    }

    /**
     * @return default temperature of room
     */
    public boolean getTemperatureDefault() {
        return temperatureDefault;
    }

    /**
     * @param temperatureDefault check whether default temperature has been set or not
     */
    public void setTemperatureDefault(boolean temperatureDefault) {
        this.temperatureDefault = temperatureDefault;
    }

    /**
     * @return if temperature has been set.
     */
    public boolean getSettingTemperature () {
        return settingTemperature;
    }

    /**
     * @param settingTemperature check whether temperature has been set or not.
     */
    public void setSettingTemperature (boolean settingTemperature) {
        this.settingTemperature = settingTemperature;
    }

    /**
     * Builder pattern for Room Object
     */
    public static class Builder {
        private String name;
        private ArrayList<Window> windows;
        private ArrayList<Door> doors;
        private int lightsOn;
        private int lightsTotal;
        private double roomTemperature;

        public Builder(final String name) {
            this.name = name;
        }

        public Builder withWindows(final ArrayList<Window> windows) {
            this.windows = windows;
            return this;
        }

        public Builder withDoors(final ArrayList<Door> doors) {
            this.doors = doors;
            return this;
        }

        public Builder withLightsOn(final int lightsOn) {
            this.lightsOn = lightsOn;
            return this;
        }

        public Builder withlightsTotal(final int lightsTotal) {
            this.lightsTotal = lightsTotal;
            return this;
        }

        public Builder withRoomTemperature(final int roomTemperature) {
            this.roomTemperature = roomTemperature;
            return this;
        }

        public Room build() {
            return new Room(name, windows, doors, lightsTotal);
        }
    }
}
