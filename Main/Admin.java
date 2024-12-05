package Main;

import Screen.screenControl.ScreenController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Admin {

    private String adminID;
    private Printer printer;
    private ArrayList<Latch> latches = new ArrayList<>();
    private boolean votingIsOpen;
    private boolean sessionIsOpen;
    private boolean adminDone;
    private final BlockingQueue<Message> receivingQueue = new LinkedBlockingQueue<>();

    public Admin(String cardCode, ArrayList<Latch> latches, Printer printer) {
        this.adminID = cardCode;
        this.printer = printer;
        latches.addAll(this.latches);
    }

    public BlockingQueue<Message> getReceivingQueue() {
        return receivingQueue;
    }

    public void startAdminThread() {
        Thread adminThread = new Thread(() -> {
            // get blank ballot
            try {
                // Start voting process
                ScreenController controller = ScreenController.getInstance(); // get screen instance
                controller.turnOn(); // turn the screen on

                // TODO: Open voting toggle to close voting
                controller.showProposition(new Proposition("", "", 0, null), new String[] { "Open Voting", "Exit" });

                controller.registerListener(receivingQueue);

                boolean adminDone = false;
                int currentPropositionIndex = 0;

                // TODO: Check this voting loop, not sure if its entirely correct
                while (!adminDone) {
                    // Wait for messages from ScreenController
                    Message message = receivingQueue.take(); // Blocking call to wait for message

                    // Process the received message
                    switch (message.getContent()) {
                        case "0": // Open Voting
                            votingIsOpen = !votingIsOpen;
                            System.out.println("Voting is now " + (votingIsOpen ? "open" : "closed"));
                            break;

                        case "1": // Exit

                            controller.showProposition(new Proposition("", "", 0, null), null);
                            adminDone = true;
                            break;

                        default:
                            System.out.println("Unknown action: " + message.getContent());
                            break;
                    }
                }



            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Voter thread interrupted.");
            }
        });

        adminThread.start();
    }

    /*
     * Method used to notify the admin if some error has taken place causing the
     * main
     * voting thread to halt.
     */
    public void sendFailureNotification() {
        // Show an option on the menu to shutdown
        System.out.println("(admin) SYSTEM FAILURE DETECTED. Shutting down system gracefully.");
        closeSession();
        closeVoting();
    }

    public void openVoting() {
        votingIsOpen = true;
    }

    public void closeVoting() {
        votingIsOpen = false;
    }

    public void openSession() {
        sessionIsOpen = true;
    }

    public void closeSession() {
        sessionIsOpen = false;
    }

    public void shutdown() {

    }

    public void openLatches() {
        for (Latch latch : latches) {
            latch.unlockLatch();
        }
    }

    public void closeLatches() {
        for (Latch latch : latches) {
            latch.lockLatch();
        }
    }

    public boolean isVotingOpen() {
        return votingIsOpen;
    }

    public boolean isSessionOpen() {
        return isSessionOpen();
    }

    public boolean isAdminDone() {
        return adminDone;
    }
}
