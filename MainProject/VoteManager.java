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
    private ArrayList<String> failed;

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
        monitor.startMonitoring();

        Thread managerThread = new Thread(() -> {
            while (true) {
                failed = monitor.hasFailure();
                if (!failed.isEmpty()){
                    //TODO: Machine has failed, do something
                    for (String failure : failed) {
                        System.out.println(failure + " has failed!\n");
                    }
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
    }
}
