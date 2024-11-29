package MainProject;

public class TamperSensor {
    private boolean tampered;
    private boolean hasFailed;

    public TamperSensor() {
        this.tampered = false;
    }

    public boolean isTampered() {
        return tampered;
    }

    public void setFailureStatus(boolean fail) {
        hasFailed = fail;
    }

    public boolean getFailureStatus() {
        return hasFailed;
    }
}
