package Main;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;

public class Admin {

    private String adminID;
    private ArrayList<Latch> latches = new ArrayList<>();
    private boolean adminDone;

    public Admin(String cardCode, ArrayList<Latch> latches) {
        adminID = cardCode;
        latches.addAll(this.latches);
        System.out.println("New admin... card code: " + adminID);
    }

    public void startAdminThread() {
        Thread adminThread = new Thread(() -> {
            while (true) {


                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

    public void openVoting(){

    }

    public void closeVoting(){

    }

    public void openSession(){

    }

    public void closeSession(){

    }

    public void shutdown(){

    }

    public void openLatches(){
        for(Latch latch : latches) {
            latch.unlockLatch();
        }
    }

    public void closeLatches(){
        for(Latch latch : latches) {
            latch.lockLatch();
        }
    }

    public void confirmShutdown(){

    }

    public boolean isAdminDone() {
        return adminDone;
    }
}
