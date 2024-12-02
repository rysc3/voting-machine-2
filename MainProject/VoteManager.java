package MainProject;

import java.util.ArrayList;

public class VoteManager {

    private Printer printer;
    private SDCardDriver vDataSD1;
    private SDCardDriver vDataSD2;
    private SDCardDriver ballotSD;
    private TamperSensor tamperSensor;
    private CardReader cardReader;
    private Latch latch;
    private Monitor monitor;
    private InputHandler inputHandler;
    private boolean failure;
    private ArrayList<String> failed = new ArrayList<>();

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

        inputHandler = new InputHandler(printer, vDataSD1, vDataSD2, ballotSD, tamperSensor,
                cardReader, latch);

        System.out.println("starting monitor");
        monitor.startMonitoring();
        System.out.println("starting input handler");
        inputHandler.handle();
        System.out.println("TEST");
        Thread managerThread = new Thread(() -> {
            System.out.println("test");
            while (true) {
                failure = monitor.hasFailure();
                if (failure){
                    // get list of failed devices and give to inputHandler for user to see
                    failed = monitor.getFailed();
                    inputHandler.setFailedList(failed);
                    //TODO: Machine has failed: notify admin, abort voter
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
}
