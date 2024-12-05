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

    private final String name = "voter";
    private final BlockingQueue<Message> receivingQueue = new LinkedBlockingQueue<>();

    public Voter(String cardCode, SDCardDriver ballotSD, SDCardDriver voteSD1, SDCardDriver voteSD2, Printer printer) {
        this.cardCode = cardCode;
        this.ballotSD = ballotSD;
        this.voteSD1 = voteSD1;
        this.voteSD2 = voteSD2;
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

                String[] backNextNavBtns = new String[]{"back", "next"};


                // Start voting process
                ScreenController controller = ScreenController.getInstance(); // get screen instance
                controller.turnOn(); //turn the screen on
                controller.showProposition(myBallot.propositions().get(0), backNextNavBtns);
                controller.registerListener(receivingQueue);

                boolean votingComplete = false;
                int currentPropositionIndex = 0;

                //TODO: Fix this ai generated ass voting loop
                while (!votingComplete) {
                    // Wait for messages from ScreenController
                    Message message = receivingQueue.take(); // Blocking call to wait for message

                    // Process the received message
                    switch (message.getContent()) {
                        case "1": //Next
                            // Move to the next proposition, if available
                            if (currentPropositionIndex < myBallot.propositions().size() - 1) {
                                currentPropositionIndex++;
                                controller.showProposition(myBallot.propositions().get(currentPropositionIndex), backNextNavBtns);
                            } else {
                                System.out.println("No more propositions. Voting complete.");
                                votingComplete = true;
                            }
                            break;

                        case "0": //Back
                            // Move to the previous proposition, if available
                            if (currentPropositionIndex > 0) {
                                currentPropositionIndex--;
                                controller.showProposition(myBallot.propositions().get(currentPropositionIndex), backNextNavBtns);
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
                completedBallot();

            } catch (IOException e) {
                System.err.println("Error initializing ballot: " + e.getMessage());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Voter thread interrupted.");
            }
        });

        voterThread.start();
    }

    private void startVoting() {
        // Todo: screen stuff?




        new Thread(() -> {
            while (true) {
                try {
                    Message message = receivingQueue.take(); // Blocking call to wait for a message
                    processScreenMessage(message);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("Voter message processing interrupted.");
                    break;
                }
            }
        }).start();

    }

    // Method to handle messages from ScreenController
    private void processScreenMessage(Message message) {
        System.out.println("Voter received message: " + message.getContent());

        // Process messages as needed (e.g., handle "next" or "back" navigation)
        switch (message.getContent()) {
            case "next":
                System.out.println("Navigating to next proposition.");
                // Logic for "next" button press
                break;
            case "back":
                System.out.println("Navigating to previous proposition.");
                // Logic for "back" button press
                break;
            default:
                System.out.println("Unknown button action: " + message.getContent());
                break;
        }
    }


        // Todo: here is some good references on how to display the ballot on the screen, i.e.
        //       use myBallot.electionName(), ArrayList<Propositions> props = ballot.propositions;
        //       etc
        // public static void printBallot(Ballot ballot){
        //        System.out.println("Election name: " + ballot.electionName());
        //        System.out.println("start date: " + ballot.startDate());
        //        System.out.println("end date: " + ballot.endDate());
        //        System.out.println("start time: " + ballot.startForDay());
        //        System.out.println("end time: " + ballot.endForDay());
        //        for (Proposition p : ballot.propositions()) {
        //            System.out.println("---------");
        //            System.out.println("Proposition name: "+p.propName());
        //            System.out.println("Proposition description: "+p.propDesc());
        //            System.out.println("Select from: " + p.selectableOptions());
        //            for (Option o : p.options()) {
        //                System.out.println("   "+o.description() + "  " + o.isSelected());
        //            }
        //            System.out.println("---------");
        //        }
        //    }

    /**
     * voter has completed ballot, record the votes
     */
    private void completedBallot() {
        new VoteRecording(myBallot, cardCode, voteSD1, voteSD2, printer);
    }
}
