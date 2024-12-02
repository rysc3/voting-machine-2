package MainProject;

import java.util.ArrayList;
import java.util.Scanner;

public class InputHandler {

    private Printer printer;
    private SDCardDriver vDataSD1;
    private SDCardDriver vDataSD2;
    private SDCardDriver ballotSD;
    private TamperSensor tamperSensor;
    private CardReader cardReader;
    private Latch latch;
    private ArrayList<String> failed = new ArrayList<>();

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
        Thread handleThread = new Thread(() -> {
            Scanner scan = new Scanner(System.in);
            String input;

            while (true) {
                System.out.println("Select an option:\n" +
                        "[1] Set Failure\n" +
                        "[2] Get Failed Devices");
                input = scan.nextLine();
                switch (input) {
                    case "1" -> {
                        setFail(scan);
                    }
                    case "2" -> {
                        printFailed();
                    }
                    // Todo: add card reader/card generation stuff
                    // case 3: generate card
                    // case 4: insert card
                    //   -> will display all generated cards, select a card to insert
                    default -> {
                        System.out.println("Invalid input.");
                    }
                }
            }
        });
        handleThread.start();
    }

    private void setFail(Scanner scan) {
        String input = "";
        while (input.equals("")) {
            System.out.println("Select a device to fail:\n" +
                    "[1] Printer\n" +
                    "[2] Voting Data SD1\n" +
                    "[3] Voting Data SD2\n" +
                    "[4] Ballot SD\n" +
                    "[5] Tamper Sensor\n" +
                    "[6] Card Reader\n" +
                    "[7] Latch\n" +
                    "[8] <- Go Back");
            input = scan.nextLine();
            switch (input) {
                // set failure in printer
                case "1" -> printer.setFailureStatus(true);
                // set failure in sd 1
                case "2" -> vDataSD1.setFailureStatus(true);
                // set failure in sd 2
                case "3" -> vDataSD2.setFailureStatus(true);
                // set failure in ballot sd
                case "4" -> ballotSD.setFailureStatus(true);
                // set failure in tamper sensor
                case "5" -> tamperSensor.setFailureStatus(true);
                // set failure in card reader
                case "6" -> cardReader.setFailureStatus(true);
                // set failure in latch
                case "7" -> latch.setFailureStatus(true);
                // go back
                case "8" -> {
                    break;
                }
                default -> {
                    input = "";
                    System.out.println("Invalid input.");
                }
            }
        }
    }

    public void setFailedList(ArrayList<String> failed) {
        this.failed = failed;
    }

    private void printFailed() {
        if (!failed.isEmpty()){
            for (String failure : failed) {
                System.out.println(failure + " has failed!");
            }
        }
        else {
            System.out.println("No failed devices.");
        }
    }
}
