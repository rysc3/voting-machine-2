package Main;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;

public class Admin {

    private String adminID;
    private ArrayList<Latch> latches = new ArrayList<>();
    private boolean votingIsOpen;
    private boolean sessionIsOpen;
    private boolean adminDone;
    private boolean shutdown;

    public Admin(String cardCode, ArrayList<Latch> latches) {
        adminID = cardCode;
        latches.addAll(this.latches);
    }

    public void startAdminThread() {
        Thread adminThread = new Thread(() -> {

            //while not done? this sleep loop might not be necessary once screen stuff is implemented
            while (true) {


                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        adminThread.start();
    }

    /*
     * Method used to notify the admin if some error has taken place causing the main 
     * voting thread to halt. 
     */
    public void sendFailureNotification(){
        // Show an option on the menu to shutdown
        System.out.println("(admin) SYSTEM FAILURE DETECTED. Shutting down system gracefully.");
        closeSession();
        closeVoting();
    }

    public void openVoting(){
        votingIsOpen = true;
    }

    public void closeVoting(){
        votingIsOpen = false;
    }

    public void openSession(){
        sessionIsOpen = true;
    }

    public void closeSession(){
        sessionIsOpen = false;
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

    public boolean isAdminDone() {
        return adminDone;
    }
}
