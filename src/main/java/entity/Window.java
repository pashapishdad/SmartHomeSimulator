package entity;

import constants.Position;
import controller.LoginInfoController;

/**
 * class Window to represent a Window object in the house
 */
public class Window {
    private Position position;
    private boolean blocking;
    private boolean openWindow;

    /**
     * Constructor for class Window
     *
     * @param position position of window
     * @param blocking blocking movement of window
     */
    public Window(Position position, boolean blocking, boolean open) {
        this.position = position;
        this.blocking = blocking;
        this.openWindow = openWindow;
    }

    /**
     * Get the position of the window
     *
     * @return position of the window
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Set the position of the window
     *
     * @param position position of the window
     */
    public void setPosition(Position position) {
        this.position = position;
    }

    /**
     * Set the blocking of the window
     *
     * @param blocking blocking  of the window
     */
    public void setBlocking(boolean blocking) {
        this.blocking = blocking;
    }

    /**
     * Get the blocking of the window
     *
     * @return blocking of the window
     */
    public boolean getBlocking() {
        return blocking;
    }

    /**
     * Get the opening of the window
     *
     * @return opening of the window
     */
    public boolean getOpenWindow() {
        return openWindow;
    }

    /**
     * Set the opening of the window
     *
     * @param openWindow opening  of the window
     */
    public void setOpenWindow(boolean openWindow) {
        if(!this.getBlocking()) {
            this.openWindow = openWindow;
        }

    }

}