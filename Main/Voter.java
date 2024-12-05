package Main;

import Screen.screenControl.ScreenController;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Voter {
    String cardCode;
    SDCardDriver ballotSD;
    SDCardDriver voteSD1;
    SDCardDriver voteSD2;
    Printer printer;
    BlankBallot blankBallot;
    Ballot myBallot;
    private boolean votingComplete;
    private Thread voterThread; // Reference to the voter thread
    private boolean isRunning = true; // Flag to control the thread loop

    private final BlockingQueue<Message> receivingQueue = new LinkedBlockingQueue<>();

    public Voter(String cardCode, SDCardDriver ballotSD, SDCardDriver voteSD1, SDCardDriver voteSD2, Printer printer) {
        this.cardCode = cardCode;
        this.ballotSD = ballotSD;
        this.voteSD1 = voteSD1;
        this.voteSD2 = voteSD2;
        this.printer = printer;
    }

    public BlockingQueue<Message> getReceivingQueue() {
        return receivingQueue;
    }

    public void startVoterThread() {
        Thread voterThread = new Thread(() -> {
            // get blank ballot
            try {
                blankBallot = new BlankBallot(ballotSD);
                myBallot = blankBallot.getNewBallot();

                // print for testing
                ExtractInfoXML.printBallot(myBallot);

                myBallot.propositions().add(myBallot.propositions().size(), new Proposition("Submit Ballot?", "Verify on the printer that all of your options are as intended. If they are correct, press \"Finished\" to submit your vote."
                        , 0, null));
                String[] backNextNavBtns = new String[] { "back", "next" };

                // Start voting process
                ScreenController controller = ScreenController.getInstance(); // get screen instance
                controller.turnOn(); // turn the screen on
                controller.showProposition(myBallot.propositions().get(0), backNextNavBtns);
                controller.registerListener(receivingQueue);

                votingComplete = false;
                int currentPropositionIndex = 0;

                // TODO: Check this voting loop, not sure if its entirely correct
                while (!votingComplete) {
                    // Wait for messages from ScreenController
                    Message message = receivingQueue.take(); // Blocking call to wait for message

                    // Process the received message
                    switch (message.getContent()) {
                        case "1": // Next
                            // Move to the next proposition, if available
                            if (currentPropositionIndex < myBallot.propositions().size() - 1) {
                                currentPropositionIndex++;
                                controller.showProposition(myBallot.propositions().get(currentPropositionIndex),
                                        backNextNavBtns);
                            } else {
                                System.out.println("No more propositions. Voting complete.");
                                votingComplete = true;

                                controller.showProposition(new Proposition("", "", 0, null), null);
                            }
                            break;

                        case "0": // Back
                            // Move to the previous proposition, if available
                            if (currentPropositionIndex > 0) {
                                currentPropositionIndex--;
                                controller.showProposition(myBallot.propositions().get(currentPropositionIndex),
                                        backNextNavBtns);
                            } else {
                                System.out.println("Already at the first proposition.");
                            }
                            break;

                        default:
                            System.out.println("Unknown action: " + message.getContent());
                            break;
                    }
                }
                // Step 5: Complete the ballot once voting is done

                // controller.

            } catch (IOException e) {
                System.err.println("Error initializing ballot: " + e.getMessage());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Voter thread interrupted.");
            }
        });

        voterThread.start();
    }

    /**
     * voter has completed ballot, record the votes
     */
    private void completedBallot() {
        new VoteRecording(myBallot, cardCode, voteSD1, voteSD2, printer);

    }

    public void cleanup() {
        System.out.println("Cleaning up Voter resources...");

        // Reset the screen for the next session
        ScreenController.getInstance().reset();

        // Clear the receiving queue to remove any leftover messages
        receivingQueue.clear();

        // Signal the thread to stop running
        isRunning = false;

        // Interrupt the thread if it is blocked
        if (Thread.currentThread() != null && Thread.currentThread().isAlive()) {
            Thread.currentThread().interrupt();
        }
    }

    public boolean getVotingComplete() {
        return votingComplete;
    }
}
