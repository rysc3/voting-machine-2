package Main;

import Screen.screenControl.ScreenController;

import java.io.IOException;

public class Voter {
    String cardCode;
    SDCardDriver ballotSD;
    SDCardDriver voteSD1;
    SDCardDriver voteSD2;
    Printer printer;
    BlankBallot blankBallot;
    Ballot myBallot;

    private final String name = "voter";

    public Voter(String cardCode, SDCardDriver ballotSD, SDCardDriver voteSD1, SDCardDriver voteSD2, Printer printer) {
        this.cardCode = cardCode;
        this.ballotSD = ballotSD;
        this.voteSD1 = voteSD1;
        this.voteSD2 = voteSD2;
    }

    public void startVoterThread() {
        Thread voterThread = new Thread(() -> {
            // get blank ballot
            try {
                blankBallot = new BlankBallot(ballotSD);
                myBallot = blankBallot.getNewBallot();

                // print for testing
                ExtractInfoXML.printBallot(myBallot);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // Todo
             startVoting();

            // when voting is done, call completedBallot()

        });
        voterThread.start();
    }

    private void startVoting() {
        // Todo: screen stuff?
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

        ScreenController controller = ScreenController.getInstance();
        controller.turnOn();
        controller.showProposition(myBallot.propositions().get(0), new String[]{"back", "next"});
    }

    /**
     * voter has completed ballot, record the votes
     */
    private void completedBallot() {
        new VoteRecording(myBallot, cardCode, voteSD1, voteSD2, printer);
    }
}
