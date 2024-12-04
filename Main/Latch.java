package Main;

public class Latch {
    private boolean isLocked;
    private boolean hasFailed;

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

    public void setFailureStatus(boolean fail) {
        hasFailed = fail;
    }

    public boolean getFailureStatus() {
        return hasFailed;
    }
}
