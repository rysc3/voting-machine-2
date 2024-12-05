package Main;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class InputHandler {

    private VoteManager voteManager;
    private Printer printer;
    private SDCardDriver vDataSD1;
    private SDCardDriver vDataSD2;
    private SDCardDriver ballotSD;
    private TamperSensor tamperSensor;
    private CardReader cardReader;
    private ArrayList<Latch> latches = new ArrayList<>();
    private User user;
    private ArrayList<String> failed = new ArrayList<>();
    private ArrayList<String> cards = new ArrayList<>();

    public InputHandler(VoteManager voteManager) {
        this.voteManager = voteManager;
        this.printer = voteManager.getPrinter();
        this.vDataSD1 = voteManager.getvDataSD1();
        this.vDataSD2 = voteManager.getvDataSD2();
        this.ballotSD = voteManager.getBallotSD();
        this.tamperSensor = voteManager.getTamperSensor();
        this.cardReader = voteManager.getCardReader();
        this.latches = voteManager.getLatches();
        this.user = voteManager.getUser();
    }

    public void startInputHandlerThread() {
        Thread handleThread = new Thread(() -> {
            Scanner scan = new Scanner(System.in);
            String input;

            while (true) {
                System.out.println("Select an option:\n" +
                        "[1] Set failure\n" +
                        "[2] Get failed devices\n" +
                        "[3] Generate a new card\n" +
                        "[4] Select a card to insert\n" +
                        "[5] Eject card");
                input = scan.nextLine();
                switch (input) {
                    // select a device to fail
                    case "1" -> setFail(scan);
                    // print failed devices
                    case "2" -> printFailed();
                    // generate a new card
                    case "3" -> generateCard(scan);
                    // select a card to insert
                    case "4" -> insertCard(scan);
                    // eject inserted card, if it is voter card then wipe it
                    case "5" -> removeCard(scan);
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
                    "[0] <- Go Back\n" +
                    "[1] Printer\n" +
                    "[2] Voting Data SD1\n" +
                    "[3] Voting Data SD2\n" +
                    "[4] Ballot SD\n" +
                    "[5] Tamper Sensor\n" +
                    "[6] Card Reader\n" +
                    "[7] Latch 1\n" +
                    "[8] Latch 2");
            input = scan.nextLine();
            switch (input) {
                // go back
                case "0" -> {
                    break;
                }
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
                // set failure in latch 1
                case "7" -> latches.get(0).setFailureStatus(true);
                // set failure in latch 2
                case "8" -> latches.get(1).setFailureStatus(true);
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
        if (!failed.isEmpty()) {
            for (String failure : failed) {
                System.out.println(failure + " has failed!");
            }
        } else {
            System.out.println("No failed devices.");
        }
    }

    private void generateCard(Scanner scan) {
        String cardNumber;
        String input = "";
        while (input.equals("")) {
            System.out.println("[0] <- Go Back\n" +
                    "[1] Admin card\n" +
                    "[2] Voter card");
            input = scan.nextLine();
            switch (input) {
                case "0" -> {
                    break;
                }
                // create admin card
                case "1" -> {
                    cardNumber = "A" + generateNumbers();
                    cards.add(cardNumber);
                }
                // create voter card
                case "2" -> {
                    cardNumber = "V" + generateNumbers();
                    cards.add(cardNumber);
                }
                default -> {
                    input = "";
                    System.out.println("Invalid input.");
                }
            }
        }
    }

    private StringBuilder generateNumbers() {
        Random rand = new Random();
        StringBuilder randNumber = new StringBuilder();

        for (int i = 0; i < 8; i++) {
            // generate a number between 0 and 9
            int digit = rand.nextInt(10);
            randNumber.append(digit);
        }
        return randNumber;
    }

    // Todo: implement this method using card reader
    private void insertCard(Scanner scan) {
        if (cards.isEmpty()) {
            System.out.println("No cards available. Generate a new card.");
        }
        // print list of cards and let user select one to insert
        else {
            int numCards = cards.size();
            int inputNumber;
            String input = "";
            while (input.equals("")) {
                System.out.println("Select a card to insert:\n" +
                        "[0] <- Go Back");
                printCards();
                input = scan.nextLine();

                try {
                    inputNumber = Integer.parseInt(input);
                    // go back
                    if (inputNumber == 0) {
                        break;
                    }
                    // If card index is in range, insert it
                    if (inputNumber > 0 && inputNumber <= numCards) {
                        String card = cards.get(inputNumber - 1);
                        cardReader.insertCard(card);
                        break;
                    } else {
                        System.out.println("Invalid input.");
                        input = "";
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input.");
                    input = "";
                }
            }
        }
    }

    public void deleteCard(String cardCode) {
        if (cards.contains(cardCode)) {
            cards.remove(cardCode);
        }
        System.out.println("Card erased.");
    }

    private void removeCard(Scanner scan) {
        if (cardReader.cardType().equals("Voter")) {
            System.out.println(cardReader.cardCode() + " has been removed.");
            cards.remove(cardReader.cardCode());
            user.eraseCard(cardReader);
        } else {
            user.removeCard(cardReader);
        }
    }

    private void printCards() {
        if (!cards.isEmpty()) {
            for (String card : cards) {
                System.out.println("[" + (cards.indexOf(card) + 1) + "] " + card);
            }
        } else {
            System.out.println("No cards available.");
        }
    }
}
