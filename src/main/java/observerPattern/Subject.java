package observerPattern;

import controller.EditSimulationController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Subject class representing the object being watched
 */
public class Subject {

    /**
     * declaring variables
     */
    private Map<String, String> userLocations = EditSimulationController.getUserLocations();;
    private List<Observer> observers = new ArrayList<Observer>();

    /**
     * This function sets userLocations
     * @param key key of userLocations
     * @param value value of userLocations
     */
    public void setUserLocations(String key, String value){
        this.userLocations.put(key, value);
    }

    /**
     * Get userLocations
     *
     * @return userLocations
     */
    public Map<String, String> getUserLocation() {
        return userLocations;
    }

    /**
     * Attach the observer to the Subject
     *
     * @param observer observer to attach
     */
    public void attach(Observer observer){
        observers.add(observer);
    }

    /**
     * This function notifies the observer when a change occurs
     */
    public void notifyObserver(){
        for (Observer observer: observers){
            observer.update();
        }
    }
}
