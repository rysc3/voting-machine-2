package Drivers;

public class Latch {
    private boolean isLocked;

    public Latch() {
        this.isLocked = true;
    }

    public boolean isLatchLocked() {
        return isLocked;
    }

    public void unlockLatch() { this.isLocked = false; }

    public void lockLatch() {
        this.isLocked = true;
    }
}
