package Main;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import Screen.screenControl.ScreenController;

public class VoteManager {

    private Printer printer;
    private SDCardDriver vDataSD1;
    private SDCardDriver vDataSD2;
    private SDCardDriver ballotSD;
    private TamperSensor tamperSensor;
    private CardReader cardReader;
    private ArrayList<Latch> latches = new ArrayList<>();
    private Latch latch1;
    private Latch latch2;
    private Monitor monitor;
    private User user;
    private Admin admin;
    private Voter voter;
    private InputHandler inputHandler;
    private boolean failure;
    private boolean votingIsOpen;
    private boolean sessionIsOpen;
    private boolean shutdown;
    private ArrayList<String> failed = new ArrayList<>();


    public VoteManager() {
        // set up all device instances
        printer = new Printer("printerFile.txt");
        vDataSD1 = new SDCardDriver("sdCardDriver1.txt", 'W');
        vDataSD2 = new SDCardDriver("sdCardDriver2.txt", 'W');
        ballotSD = new SDCardDriver("testBallot.txt", 'R');
        tamperSensor = new TamperSensor();
        cardReader = new CardReader();
        latch1 = new Latch();
        latch2 = new Latch();
        latches.add(latch1);
        latches.add(latch2);
        monitor = new Monitor(printer, vDataSD1, vDataSD2, ballotSD, tamperSensor,
                cardReader, latches);
        user = new User(cardReader);
        inputHandler = new InputHandler(this);
        // this.screenManager = screenManager;

        monitor.startMonitorThread();
        user.startUserThread();
        inputHandler.startInputHandlerThread();
        startManagerThread();

    }

    public void startManagerThread() {
        Thread managerThread = new Thread(() -> {
            while (true) {
                failure = monitor.hasFailure();
                if (failure) {
                    // get list of failed devices and give to inputHandler for user to see
                    failed = monitor.getFailed();
                    inputHandler.setFailedList(failed);
                    // Todo: Machine has failed: notify admin, abort voter
                    if (admin != null) {
                        // Todo: should NOT shut down automatically, should make only screen option for the admin be shutdown
                        //admin.sendFailureNotification();
                    }
                    if (voter != null) {
                        // abort voter thread, discard votes and do not erase card
                    }

                    // TODO @ryan screenController logic
                    // screenController.showShutdownScreen();
                }

                if (cardReader.isCardIn()) {
                    if (cardReader.cardType().equals("Admin") && admin == null) {
                        admin = new Admin(cardReader.cardCode(), latches);
                        admin.startAdminThread();
                    }
                    if (cardReader.cardType().equals("Voter") && votingIsOpen && voter == null) {
                        voter = new Voter(cardReader.cardCode(), ballotSD, vDataSD1, vDataSD2, printer);
                        voter.startVoterThread();
                    }
                }

                if (user.isUserDone()) {
                    if (cardReader.cardType().equals("Voter")) {
                        cardReader.eraseCard();
                        voter = null;
                    } else {
                        cardReader.ejectCard();
                        admin = null;
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

    public ArrayList<Latch> getLatches() {
        return latches;
    }

    public Monitor getMonitor() {
        return monitor;
    }

    public User getUser() {
        return user;
    }

}
