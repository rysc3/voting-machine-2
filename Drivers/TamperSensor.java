package Drivers;

public class TamperSensor {
    private boolean tampered;
    public TamperSensor() {
        this.tampered = false;
    }

    public boolean isTampered() {
        return tampered;
    }
}
