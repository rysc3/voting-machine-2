package Screen.testSuite;

import Ballot.Ballot;
import Ballot.ExtractInfoXML;
import Screen.screenControl.ScreenController;
import javafx.application.Application;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Test {

    private static ScreenController scr;
    private static Proposition proposition;

    public static void main(String[] args) {
        // Start the JavaFX application in a separate thread
        new Thread(() -> Application.launch(ScreenController.class)).start();

        // Wait for the JavaFX application to initialize the Controller instance
        while ((scr = ScreenController.getInstance()) == null) {
            // Busy-wait until the Controller instance is available
        }

        // instantiate the Propositions object
        proposition = new Proposition(null, null, 0, null, null);

        // Start the command line input listener
        startCommandLineInput();
    }


    /**
     * Create tests for a few scenarios
     *
     * only values for Title or Description, or both (no user options to be selected)
     *
     * All values populated
     */
    private static void startCommandLineInput() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("CLI ready. Type 'status' to print submitted votes, 'set_ballot' to set the ballot, or " +
                "'unlock' to unlock the controller");

        String text = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><Voting-Machine><electionName>election</electionName><startDate>2024-01-01</startDate><endDate>2024-12-01</endDate><startForDay>06:30</startForDay><endForDay>16:30</endForDay><proposition><propName>proposition 1</propName><propDesc>prop 1 description</propDesc><option>Option 1</option><option>Option 2</option><option>Option 3</option><numChoices>2</numChoices></proposition><proposition><propName>proposition 2</propName><propDesc>prop 2 description</propDesc><option>Option 1</option><option>Option 2</option><numChoices>1</numChoices></proposition></Voting-Machine>";
        Ballot testProp1 = ExtractInfoXML.makeBallotFromXML(text);

        while (true) {
            System.out.print("> ");
            String command = scanner.nextLine().trim().toLowerCase();

            switch (command) {
                case "turn on":
                    scr.turnOn();
                    System.out.println("Screen turned on");
                    break;
                case "turn off":
                    scr.turnOff();
                    System.out.println("Screen turned off");
                    break;
                case "test 1":
                    //Proposition testProp1 = new Proposition("TEST NAME", "this is a demo description that is moderatly long. ", 0, null, null);
                    scr.showProposition(testProp1.propositions().getFirst(), new String[]{"Back", "Next"});
                    System.out.println("Tested prop 1:");
                    System.out.println("Proposition(TEST NAME, this is a demo description that is moderatly long. , 0, null, null);");
                    System.out.println();


                    break;
                case"test 2":
                    Proposition testProp2 = new Proposition("Cade", "Martinez", 0, null, null);
                    //scr.showProposition(testProp2);
                    System.out.println("Tested prop 2:");
                    System.out.println("Cade\", \"Martinez\", 0, null, null");
                    System.out.println();
                    break;
                    case "exit":
                        System.out.println("Exiting the CLI...");
                    scanner.close();
                    return;
                case "test 3":
                    String[] optionStrs = new String[]{"op1", "op2", "op3", "op4", "op5", "op6", "op7", "op8","op5", "op6", "op7", "op8"};
                    String[] navStrs = new String[]{"Back", "Next"};
                    Proposition testProp3 = new Proposition("TEST NAME", "this is a demo description that is moderatly long. ", 5, optionStrs, navStrs);
                    //scr.showProposition(testProp3);
                    System.out.println("Tested prop 3:");
                    System.out.println("\"TEST NAME\", \"this is a demo description that is moderatly long. \", 1, optionStrs, null");
                    System.out.println();
                    break;
                default:
                    System.out.println("Unknown command. Please type 'status', 'set_ballot', 'clear_ballot', 'unlock, 'lock, or 'exit'.");
            }

            int result = scr.waitForSelection();
            System.out.println("Return Code: " + result );
        }
    }
}



