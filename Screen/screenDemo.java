package Screen;


import Ballot.Ballot;
import Ballot.ExtractInfoXML;
import Ballot.Proposition;
import Screen.screenControl.ScreenController;
import javafx.application.Application;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


public class screenDemo {


    static ServerSocket serverSocket = null;
    static Socket mySocket = null;
    static BufferedReader reader = null;
    static PrintWriter writer = null;
    private static ScreenController scr;


    public static void main(String[] args) throws InterruptedException {
        String text = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
                "<Voting-Machine>" +
                "<electionName>election</electionName>" +
                "<startDate>2024-01-01</startDate>" +
                "<endDate>2024-12-01</endDate>" +
                "<startForDay>06:30</startForDay>" +
                "<endForDay>16:30</endForDay>" +
                "<proposition>" +
                "<propName>proposition 1</propName>" +
                "<propDesc>prop 1 description</propDesc>" +
                "<option>Option 1</option>" +
                "<option>Option 2</option>" +
                "<option>Option 3</option>" +
                "<numChoices>2</numChoices>" +
                "</proposition>" +
                "<proposition>" +
                "<propName>proposition 2</propName>" +
                "<propDesc>prop 2 description</propDesc>" +
                "<option>Option 1</option>" +
                "<option>Option 2</option>" +
                "<numChoices>1</numChoices>" +
                "</proposition></Voting-Machine>";


        new Thread(() -> Application.launch(ScreenController.class)).start();

        // Wait for the JavaFX application to initialize the Controller instance
        while ((scr = ScreenController.getInstance()) == null) {
            // Busy-wait until the Controller instance is available
        }

        Ballot testBallot = ExtractInfoXML.makeBallotFromXML(text);

        scr.turnOn();
        for (int i = 0; i < testBallot.propositions().size();){
            Proposition prop = testBallot.propositions().get(i);
            if (i == 0){
                scr.showProposition(prop,new String[]{"", "Next"});
            } else if (i == testBallot.propositions().size() - 1) {
                scr.showProposition(prop,new String[]{"Back", "End Voting"});
            } else {
                scr.showProposition(prop,new String[]{"Back", "Next"});
            }

            int result = scr.waitForSelection();
            if (result == 1){
                i++;
            }
            if (result == 0 && i > 0){
                i--;
            }
        }
        ExtractInfoXML.printBallot(testBallot);
        scr.turnOff();
    }
}
