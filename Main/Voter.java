package Main;

public class Voter {

    public Voter(String cardCode, SDCardDriver ballotSD, SDCardDriver voteSD1, SDCardDriver voteSD2, Printer printer) {
        startVoterThread();

    }

    public void startVoterThread() {
        Thread voterThread = new Thread(() -> {

            //startVoting(); ??

            while (true) {
                //???

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        voterThread.start();
    }

    private void startVoting() {

    }

    /**
     * voter has completed ballot, record the votes
     */
    private void completedBallot() {
        VoteRecording voteRecording = new VoteRecording(cardCode, voteSD1, voteSD2, printer);
    }
}