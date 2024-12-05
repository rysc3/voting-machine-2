package Main;

import java.util.ArrayList;
import java.util.List;

// part of Voter section in design diagram
public class VoteRecording {
    private Ballot finishedBallot;
    private String cardCode;
    private SDCardDriver voteSD1;
    private SDCardDriver voteSD2;
    private Printer printer;

    public VoteRecording(Ballot finishedBallot, String cardCode, SDCardDriver voteSD1, SDCardDriver voteSD2, Printer printer) {
        this.finishedBallot = finishedBallot;
        this.cardCode = cardCode;
        this.voteSD1 = voteSD1;
        this.voteSD2 = voteSD2;
        this.printer = printer;
        recordVotes();
        printBallot();
    }

    private List<String> ballotToListString() {
        List<String> lines = new ArrayList<>();

        lines.add("Voter ID: " + cardCode);
        lines.add("Election name: " + finishedBallot.electionName());
        lines.add("Start date: " + finishedBallot.startDate());
        lines.add("End date: " + finishedBallot.endDate());
        lines.add("Start time: " + finishedBallot.startForDay());
        lines.add("End time: " + finishedBallot.endForDay());

        for (Proposition p : finishedBallot.propositions()) {
            lines.add("---------");
            lines.add("Proposition name: " + p.propName());
            lines.add("Proposition description: " + p.propDesc());
            lines.add("Select from: " + p.selectableOptions());
            for (Option o : p.options()) {
                lines.add("   " + o.description() + "  " + o.isSelected());
            }
            lines.add("---------");
        }

        return lines;
    }

    private void printBallot(){
        List<String> ballotStr = ballotToListString();
        printer.printBatch(ballotStr);
    }

    private void recordVotes() {
        List<String> ballotLines = ballotToListString();

        for (String line : ballotLines) {
            voteSD1.write(line);
            voteSD2.write(line);
        }
    }
}
