package Main;

public class Voter {
    String cardCode;
    SDCardDriver ballotSD;
    SDCardDriver voteSD1;
    SDCardDriver voteSD2;
    Printer printer;
    public Voter(String cardCode, SDCardDriver ballotSD, SDCardDriver voteSD1, SDCardDriver voteSD2, Printer printer) {
        this.cardCode = cardCode;
        this.ballotSD = ballotSD;
        this.voteSD1 = voteSD1;
        this.voteSD2 = voteSD2;

        startVoterThread();
    }

    public void startVoterThread() {
        Thread voterThread = new Thread(() -> {

            // BlankBallot myBallot = new BlankBallot();
            // startVoting(); ??


            // while not done
            while (true) {

                // exit when user submits ballot or error occurs
            }
        });
        voterThread.start();
    }

    private void startVoting() {
        // screens stuff?
    }

    /**
     * voter has completed ballot, record the votes
     */
    private void completedBallot() {
        VoteRecording voteRecording = new VoteRecording(cardCode, voteSD1, voteSD2, printer);
    }
}
