package entity;

/**
 * class House to represent a House object
 */
public class House {
    private Room[] rooms;
    private Zone[] zones;

    /**
     * Constructor for class House
     *
     * @param rooms rooms in the house
     */
    public House(Room[] rooms) {
        this.rooms = rooms;
    }

    /**
     * @return rooms in the house
     */
    public Room[] getRooms() {
        return rooms;
    }

    /**
     * @param rooms rooms in the house
     */
    public void setRooms(Room[] rooms) {
        this.rooms = rooms;
    }

    /**
     *
     * @return zones in the house
     */
    public Zone[] getZones() { return zones; }

    /**
     *
     * @param zones zones in the house
     */
    public void setZones(Zone[] zones) { this.zones = zones; }
}
