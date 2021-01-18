package entity;

import java.util.ArrayList;

public class Zone {
    private String name;
    private ArrayList<Room> rooms;
    private double[] zoneTemperature = new double[3];

    /**
     * Constructor
     *
     * @param name the name of the zone
     * @param rooms rooms in a zone
     */
    public Zone (String name, ArrayList<Room>rooms) {
        this.name = name;
        this.rooms = rooms;
    }

    /**
     * @return name of the zone
     */
    public String getName() {
        return name;
    }

    /**
     * @param name name of the zone
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return rooms in the zone
     */
    public ArrayList<Room> getRooms() {
        return rooms;
    }

    /**
     * @param rooms rooms in the zone
     */
    public void setRooms(ArrayList<Room> rooms) {
        this.rooms = rooms;
    }

    /**
     * @return temperature of the zone
     */
    public double[] getZoneTemp() {
        return zoneTemperature;
    }

    /**
     * @param zoneTemperature temperature of the zone
     */
    public void setZoneTemp(double[] zoneTemperature) {
        this.zoneTemperature = zoneTemperature;
    }
}


