package MainProject;

import java.util.Scanner;

public class InputHandler {

    private Printer printer;
    private SDCardDriver vDataSD1;
    private SDCardDriver vDataSD2;
    private SDCardDriver ballotSD;
    private TamperSensor tamperSensor;
    private CardReader cardReader;
    private Latch latch;

    public InputHandler(Printer printer, SDCardDriver vDataSD1, SDCardDriver vDataSD2,
                   SDCardDriver ballotSD, TamperSensor tamperSensor,
                   CardReader cardReader, Latch latch) {
        this.printer = printer;
        this.vDataSD1 = vDataSD1;
        this.vDataSD2 = vDataSD2;
        this.ballotSD = ballotSD;
        this.tamperSensor = tamperSensor;
        this.cardReader = cardReader;
        this.latch = latch;
    }

    public void handle() {
        Scanner scan = new Scanner(System.in);
        String input;

        while (true) {
            System.out.println("Select an option:\n" +
                    "[1] Set Failure\n");
            input = scan.nextLine();
            switch (input) {
                case "1" -> {
                    setFail(scan);
                }
                // Todo: add card reader/card generation stuff
                // case 2: generate card
                // case 3: insert card
                //   -> will display all generated cards, select a card to insert
                default -> {
                    System.out.println("Invalid input.");
                }
            }
        }
    }

    private void setFail(Scanner scan) {
        String input = "";
        while (input.equals("")) {
            System.out.println("Select a device to fail:\n" +
                    "[1] Printer\n" +
                    "[2] Voting Data SD 1\n" +
                    "[3] Voting Data SD 2\n" +
                    "[4] Ballot SD\n" +
                    "[5] Tamper Sensor\n" +
                    "[6] Card Reader\n" +
                    "[7] Latch\n" +
                    "[8] <- Go Back");
            input = scan.nextLine();
            switch (input) {
                case "1" -> {
                    // set failure in printer
                    printer.setFailureStatus(true);
                    System.out.println("You selected option 1.");
                }
                case "2" -> {
                    // set failure in sd 1
                    vDataSD1.setFailureStatus(true);
                    System.out.println("You selected option 2.");
                }
                case "3" -> {
                    // set failure in sd 2
                    vDataSD2.setFailureStatus(true);
                    System.out.println("You selected option 3.");
                }
                case "4" -> {
                    // set failure in ballot sd
                    ballotSD.setFailureStatus(true);
                    System.out.println("You selected option 4.");
                }
                case "5" -> {
                    // set tamper
                    tamperSensor.setFailureStatus(true);
                    System.out.println("You selected option 5.");
                }
                case "6" -> {
                    // set failure in card reader
                    cardReader.setFailureStatus(true);
                    System.out.println("You selected option 6.");
                }
                case "7" -> {
                    // set failure in latch
                    latch.setFailureStatus(true);
                    System.out.println("You selected option 7.");
                }
                case "8" -> {
                    // go back
                    break;
                }
                default -> {
                    input = "";
                    System.out.println("Invalid input.");
                }
            }
        }
    }


}
