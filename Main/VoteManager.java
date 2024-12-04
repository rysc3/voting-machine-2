package Main;

import java.util.ArrayList;
import java.util.Scanner;

import Screen.screenControl.ScreenController;

public class VoteManager {

    private Printer printer;
    private SDCardDriver vDataSD1;
    private SDCardDriver vDataSD2;
    private SDCardDriver ballotSD;
    private TamperSensor tamperSensor;
    private CardReader cardReader;
    private Latch latch;
    private Monitor monitor;
    private User user;
    private Admin admin;
    private InputHandler inputHandler;
    private boolean failure;
    private ArrayList<String> failed = new ArrayList<>();
    private ScreenController screenController;

    public VoteManager() {
        // set up all device instances
        printer = new Printer("printerFile.txt");
        vDataSD1 = new SDCardDriver("sdCardDriver1.txt", 'W');
        vDataSD2 = new SDCardDriver("sdCardDriver2.txt", 'W');
        ballotSD = new SDCardDriver("testBallot.txt", 'R');
        tamperSensor = new TamperSensor();
        cardReader = new CardReader();
        latch = new Latch();
        monitor = new Monitor(printer, vDataSD1, vDataSD2, ballotSD, tamperSensor,
                cardReader, latch);
        user = new User(cardReader);

        inputHandler = new InputHandler(this);

        // Starting the screen
        screenController = new ScreenController();
        screenController.turnOn();

        monitor.startMonitoring();
        // Todo: start user
        inputHandler.handle();

        Thread managerThread = new Thread(() -> {
            while (true) {
                failure = monitor.hasFailure();
                if (failure){
                    // get list of failed devices and give to inputHandler for user to see
                    failed = monitor.getFailed();
                    inputHandler.setFailedList(failed);
                    //TODO: Machine has failed: notify admin, abort voter
                }

                if (cardReader.isCardIn()) {
                    if (cardReader.cardType().equals("Admin") && admin != null) {
                        admin = new Admin(cardReader.cardCode());
                    }
                } else {
                    admin = null;
                }

                if(user.isUserDone()) {
                    if(cardReader.cardType().equals("Voter")){
                        cardReader.eraseCard();
                    }
                    else {
                        cardReader.ejectCard();
                    }
                }

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });


        managerThread.start();
    }

    public Printer getPrinter() {
        return printer;
    }

    public SDCardDriver getvDataSD1() {
        return vDataSD1;
    }

    public SDCardDriver getvDataSD2() {
        return vDataSD2;
    }

    public SDCardDriver getBallotSD() {
        return ballotSD;
    }

    public TamperSensor getTamperSensor() {
        return tamperSensor;
    }

    public CardReader getCardReader() {
        return cardReader;
    }

    public Latch getLatch() {
        return latch;
    }

    public Monitor getMonitor() {
        return monitor;
    }

    public User getUser() {
        return user;
    }

}
