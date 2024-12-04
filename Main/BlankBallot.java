package Main;

import java.io.IOException;

// part of Voter section in design diagram
public class BlankBallot {

    private SDCardDriver ballotSD;
    private Ballot newBallot;

    public BlankBallot(SDCardDriver ballotSD) throws IOException {
        this.ballotSD = ballotSD;

        // read ballot info from ballot SD
        String[] ballotArr = ballotSD.read();
        String ballotString = ballotArr[0];

        // create new ballot object
        newBallot = ExtractInfoXML.makeBallotFromXML(ballotString);
    }

    public Ballot getNewBallot() {
        return newBallot;
    }
}
