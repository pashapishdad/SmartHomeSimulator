package observerPattern;

import controller.LoginInfoController;

public class SHHObserver extends Observer{

    /**
     * declaring variables
     */
    protected Subject subject;
    private static LoginInfoController parentController;

    /**
     * @param subject The subject
     */
    public SHHObserver(Subject subject){
        this.subject = subject;
        this.subject.attach(this);
    }

    /**
     * This function does the update when the the subject is changed
     */
    public void update() {
        // TODO: SHH update when userLocations changes
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
