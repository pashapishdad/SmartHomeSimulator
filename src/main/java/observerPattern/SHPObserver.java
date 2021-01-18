package observerPattern;

import controller.LoginInfoController;
import entity.ConsoleComponents;

import java.util.Map;

/**
 * class UserLocationObserver
 */
public class SHPObserver extends Observer{

    /**
     * declaring variables
     */
    protected Subject subject;
    private static LoginInfoController parentController;

    /**
     * @param subject The subject
     */
    public SHPObserver(Subject subject){
        this.subject = subject;
        this.subject.attach(this);
    }

    /**
     * This function does the update when the the subject is changed
     */
    public void update() {
        Map<String, String> userLocations  = subject.getUserLocation();
        String timeBeforeAlert = LoginInfoController.getTimeBeforeAlert();
        if (timeBeforeAlert == null){
            timeBeforeAlert = "0";
        }
        for (String person : userLocations.keySet()){
            if (LoginInfoController.isAwayMode() && !userLocations.get(person).equals("Outside")) {
                LoginInfoController.consoleLogFile(person + " was detected in the " +
                        userLocations.get(person)+ " during Away Mode. Authorities will be alerted in " +
                        timeBeforeAlert + " seconds.", ConsoleComponents.SHS);
                parentController.sendNotification(timeBeforeAlert);
            }
        }
    }

    /**
     * This function assign controller variable of the loginInfo pane
     *
     * @param loginInfoController
     */
    public static void setParentController(LoginInfoController loginInfoController) {
        parentController = loginInfoController;
    }
}

