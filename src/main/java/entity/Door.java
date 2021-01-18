package entity;

import constants.Position;

/**
 * class Door to represent a Door object in the house
 */
public class Door {
    private Position position;
    private String connection;
    private boolean openDoor;
    private boolean lock;

    /**
     * Constructor for class Door
     *
     * @param position   position of the door
     * @param connection room the door connects to
     * @param openDoor open the door of room
     */
    public Door(Position position, String connection, boolean openDoor) {
        this.position = position;
        this.connection = connection;
        this.openDoor = openDoor;
    }

    /**
     * Constructor for class Door
     *
     * @param connection room the door connects to
     */
    public Door(String connection) {
        this.connection = connection;
    }

    /**
     * @return position of the door
     */
    public Position getPosition() {
        return position;
    }

    /**
     * @param position position of the door
     */
    public void setPosition(Position position) {
        this.position = position;
    }

    /**
     * @return room the door connects to
     */
    public String getConnection() {
        return connection;
    }

    /**
     * @param connection room the door connects to
     */
    public void setConnection(String connection) {
        this.connection = connection;
    }

    /**
     * Get the opening of the window
     *
     * @return opening of the window
     */
    public boolean getOpenDoor() {
        return openDoor;
    }

    /**
     * Set the opening of the window
     *
     * @param openDoor opening  of the window
     */
    public void setOpenDoor(boolean openDoor) {
    	this.openDoor = openDoor;        
    }

	public boolean getLock() {
		return lock;
	}

	public void setLock(boolean lock) {
		if(lock)  this.openDoor = false;
		this.lock = lock;
	}

}
