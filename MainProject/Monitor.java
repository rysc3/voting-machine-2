package MainProject;

import java.util.ArrayList;

public class Monitor {

    private Printer printer;
    private SDCardDriver vDataSD1;
    private SDCardDriver vDataSD2;
    private SDCardDriver ballotSD;
    private TamperSensor tamperSensor;
    private CardReader cardReader;
    private Latch latch;
    private boolean failure;
    private ArrayList<String> failed;

    public Monitor(Printer printer, SDCardDriver vDataSD1, SDCardDriver vDataSD2,
                   SDCardDriver ballotSD, TamperSensor tamperSensor,
                   CardReader cardReader, Latch latch) {
        this.printer = printer;
        this.vDataSD1 = vDataSD1;
        this.vDataSD2 = vDataSD2;
        this.ballotSD = ballotSD;
        this.tamperSensor = tamperSensor;
        this.cardReader = cardReader;
        this.latch = latch;
        this.failure = false;
    }

    public ArrayList<String> hasFailure() {
        return failed;
    }

    public void startMonitoring() {
        Thread monitorThread = new Thread(() -> {
            while (!failure) {
                // check each device for failure
                if (printer.getFailureStatus()) {
                    failed.add("Printer");
                }
                if (vDataSD1.getFailureStatus()) {
                    failed.add("SD1");
                }
                if (vDataSD2.getFailureStatus()) {
                    failed.add("SD2");
                }
                if (ballotSD.getFailureStatus()){
                    failed.add("Ballot");
                }
                if (tamperSensor.getFailureStatus()) {
                    failed.add("TamperSensor");
                }
                if (cardReader.getFailureStatus()) {
                    failed.add("CardReader");
                }
                if (latch.getFailureStatus()) {
                    failed.add("Latch");
                }
                if (!failed.isEmpty()) {
                    failure = true;
                }
                // sleep to not destroy cpu
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        monitorThread.start();
    }
}
