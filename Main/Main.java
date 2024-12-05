package Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;

import Screen.screenControl.ScreenController;
import javafx.application.Application;


public class Main {

    // Build in Classes
    private SDCardDriver SDCard;
    private TamperSensor TampSens;
    private CardReader crdr;
    private Latch ltch;

    // Has failed
    private boolean hasPrinterFailed;
    private boolean hasTamperSensorFailed;
    private boolean hasCardReaderFailed;
    private boolean hasSDCardDriverFailed;
    private boolean hasLatchSensorFailed;

    // Socket
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    // file paths
    String SDCardDriverFilePath;
    String printerFilePath;

    private static ScreenController scr;

    public static void main(String[] args) {

        VoteManager voteManager = new VoteManager();

        // Start screen controller on it's own thread
        //new Thread(() -> ScreenController.main(args)).start();
        //ScreenController screenController = ScreenController.getInstance();
        new Thread(() -> Application.launch(ScreenController.class)).start();


        // Wait for java to initialize the Controller instance
        while ((scr = ScreenController.getInstance()) == null) {
            // Busy-wait until the Controller instance is available
        }

        voteManager.startManagerThread();
    }

    public void connectToServer(String host, int port) {
        try {
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Connected to server.");

            // Identify as DeviceManager
            out.println("DeviceManager");

            // Start listening for messages from the server
            new Thread(this::listenToServer).start();

            // Keep the program running until terminated
            System.out.println("DeviceManager running. Press Ctrl+C to exit.");
            while (true) {
                Thread.sleep(1000); // Keep the main thread alive
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    private void closeConnection() {
        try {
            if (in != null)
                in.close();
            if (out != null)
                out.close();
            if (socket != null && !socket.isClosed())
                socket.close();
            System.out.println("Connection to server closed.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listenToServer() {
        try {
            String response;
            while ((response = in.readLine()) != null) {
                if (response.startsWith("Enter your name")) {
                    continue;
                }
                if (response.contains("DeviceManager:")) {
                    String[] parts = response.split(":");
                    if (parts.length > 1) {
                        parseAndPerformAction(response);
                    }
                } else {
                    System.out.println("Received unrecognized message: " + response);
                }
            }
        } catch (SocketException e) {
            System.out.println("Connection to server lost. Closing client...");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeConnection(); // Ensure all resources are closed
        }
    }

    // Method to parse and perform action based on server message
    private void parseAndPerformAction(String message) {

        try {
            String[] parts = message.split(":");
            if (parts.length == 3) {
                String deviceName = parts[1].trim();

                int choice = Integer.parseInt(parts[2].trim());
                String msg = "";
                performActionOnDevice(deviceName, choice, msg);
            } else if (parts.length == 4) {
                String deviceName = parts[1].trim();

                int choice = Integer.parseInt(parts[2].trim());
                String msg = parts[3].trim();

                performActionOnDevice(deviceName, choice, msg);
            } else {
                System.out.println("Invalid message format: " + message); // Log invalid messages
            }
        } catch (Exception e) {
            System.out.println("Error parsing message: " + e.getMessage());
        }
    }

    private void performActionOnDevice(String deviceName, int choice, String message) throws IOException {
        switch (deviceName) {
            case "Printer":
                if (!hasPrinterFailed) {
                    performPrinterAction(deviceName, choice, message);
                    if (hasPrinterFailed) {
                        notifyServerDeviceFailre(deviceName);
                    }
                } else {
                    notifyServerDeviceFailre(deviceName);
                }
                break;
            case "SD Card Driver":
                if (!hasSDCardDriverFailed) {
                    performSDCardAction(deviceName, choice, message);
                    if (hasSDCardDriverFailed) {
                        notifyServerDeviceFailre(deviceName);
                    }
                } else {
                    notifyServerDeviceFailre(deviceName);
                }
                break;
            case "Tamper Sensor":
                if (!hasTamperSensorFailed) {
                    performTamperSensorAction(deviceName, choice);
                    if (hasTamperSensorFailed) {
                        notifyServerDeviceFailre(deviceName);
                    }
                } else {
                    notifyServerDeviceFailre(deviceName);
                }
                break;
            case "Card Reader":
                if (!hasCardReaderFailed) {
                    performCardReaderAction(deviceName, choice, message);
                    if (hasCardReaderFailed) {
                        notifyServerDeviceFailre(deviceName);
                    }
                } else {
                    notifyServerDeviceFailre(deviceName);
                }
                break;
            case "Latch":
                if (!hasLatchSensorFailed) {
                    performLatchAction(deviceName, choice);
                    if (hasLatchSensorFailed) {
                        notifyServerDeviceFailre(deviceName);
                    }
                } else {
                    notifyServerDeviceFailre(deviceName);
                }
                break;
            default:
                break;
        }
    }

    private void notifyServerDeviceFailre(String deviceName) {
        String message = "Failure";
        String serverMessage = deviceName + ":" + message; // Ensure proper "DeviceName:Message" format
        if (socket == null || socket.isClosed()) {
            System.err.println("Socket is closed. Cannot send device message.");
            return;
        }
        out.println(serverMessage);
    }

    private void notifyServerDeviceMessage(String deviceName, String message) {
        String serverMessage = deviceName + ":" + message; // Ensure proper "DeviceName:Message" format
        if (socket == null || socket.isClosed()) {
            System.err.println("Socket is closed. Cannot send device message.");
            return;
        }
        out.println(serverMessage); // Send the message to the server
    }

    private void performCardReaderAction(String deviceName, int choice, String message) throws IOException {

        String msg = "";

        switch (choice) {
            case 1:
                System.out.println("Choose an action for " + deviceName + ":");
                System.out.println("1. Information on " + deviceName);
                System.out.println("2. Simulate " + deviceName + " failure");
                System.out
                        .println("3. Insert Card into " + deviceName + " !include a message, message is card number!");
                System.out.println("4. Erase and Eject Card from " + deviceName);
                System.out.println("5. Get " + deviceName + " Card ID");
                System.out.println("6. Get " + deviceName + " Card Type");
                System.out.println("7. Check " + deviceName + " failure status");
                break;
            case 2:
                msg = "Force Failure";
                setDeviceFailure(deviceName);
                notifyServerDeviceMessage(deviceName, msg);
                break;
            case 3:
                crdr.insertCard(message);
                msg = "Card Inserted " + message;
                // cardType = crdr.cardType();
                // cardID = crdr.cardCode();
                notifyServerDeviceMessage(deviceName, msg);
                break;
            case 4:
                if (crdr != null) {
                    msg = "Erased and Ejected";
                    crdr.eraseCard();
                    // cardType = crdr.cardType();
                    // cardID = crdr.cardCode();
                    notifyServerDeviceMessage(deviceName, msg);
                }
                break;
            case 5:
                if (crdr != null) {
                    msg = "ID " + crdr.cardCode();
                    notifyServerDeviceMessage(deviceName, msg);
                }
                break;
            case 6:
                if (crdr != null) {
                    msg = "Type " + crdr.cardType();
                    notifyServerDeviceMessage(deviceName, msg);
                }
                break;
            case 7:
                msg = "isFailed " + hasCardReaderFailed;
                notifyServerDeviceMessage(deviceName, msg);
            default:
                break;
        }
    }

    private void performSDCardAction(String deviceName, int choice, String message) throws IOException {

        String msg = "";

        switch (choice) {
            case 1:
                System.out.println("Choose an action for " + deviceName + ":");
                System.out.println("1. Information on " + deviceName);
                System.out.println("2. Simulate " + deviceName + " failure");
                System.out.println(
                        "3. Set filePath for " + deviceName + " !include a message, message will set file path!");
                System.out.println("4. Write to " + deviceName + " !include a message, message will be written!");
                System.out.println("5. Read from " + deviceName);
                System.out.println("6. Check " + deviceName + " failure status");
                break;
            case 2:
                msg = "Force Failure";
                setDeviceFailure(deviceName);
                notifyServerDeviceMessage(deviceName, msg);
                break;
            case 3:
                msg = "File Path " + message;
                SDCardDriverFilePath = message;
                notifyServerDeviceMessage(deviceName, msg);
                break;
            case 4:
                if (SDCardDriverFilePath != null) {
                    SDCard = new SDCardDriver(SDCardDriverFilePath, 'W');
                } else {
                    SDCard = new SDCardDriver("sdCardDriver.txt", 'W');
                }
                SDCard.write(message);
                SDCard.closeFile();
                msg = "Write " + message;
                notifyServerDeviceMessage(deviceName, msg);
                break;
            case 5:
                if (SDCardDriverFilePath != null) {
                    SDCard = new SDCardDriver(SDCardDriverFilePath, 'R');
                } else {
                    SDCard = new SDCardDriver("sdCardDriver.txt", 'R');
                }
                // notifyServerDeviceMessage(deviceName, Arrays.toString(SDCard.read()));
                SDCard.closeFile();
                msg = "SDCardData:" + Arrays.toString(SDCard.read());
                notifyServerDeviceMessage(deviceName, msg);
                break;
            case 6:
                msg = "isFailed " + hasSDCardDriverFailed;
                notifyServerDeviceMessage(deviceName, msg);
                break;
            default:
                break;
        }
    }

    private void performLatchAction(String deviceName, int choice) {
        String msg = "";

        switch (choice) {
            case 1:
                System.out.println("Choose an action for" + deviceName + ":");
                System.out.println("1. Information on " + deviceName);
                System.out.println("2. Simulate " + deviceName + " failure");
                System.out.println("3. Lock " + deviceName);
                System.out.println("4. Unlock " + deviceName);
                System.out.println("5. Is the " + deviceName + " Locked");
                System.out.println("6. Check " + deviceName + " failure status");
                break;
            case 2:
                msg = "Force Failure";
                setDeviceFailure(deviceName);
                notifyServerDeviceMessage(deviceName, msg);
                break;
            case 3:
                ltch.lockLatch();
                msg = "Lock";
                notifyServerDeviceMessage(deviceName, msg);
                break;
            case 4:
                ltch.unlockLatch();
                msg = "Unlock";
                notifyServerDeviceMessage(deviceName, msg);
                break;
            case 5:
                msg = "isLocked " + ltch.isLatchLocked();
                notifyServerDeviceMessage(deviceName, msg);
                break;
            case 6:
                msg = "isFailed " + hasLatchSensorFailed;
                notifyServerDeviceMessage(deviceName, msg);
                break;
            default:
                break;
        }
    }

    private void performTamperSensorAction(String deviceName, int choice) {
        String msg = "";
        switch (choice) {
            case 1:
                System.out.println("Choose an action for " + deviceName + ":");
                System.out.println("1. Information on " + deviceName);
                System.out.println("2. Force " + deviceName + " Failure");
                System.out.println("3. " + deviceName + " get isTampered status");
                System.out.println("4. Check " + deviceName + " failure status");
                break;
            case 2:
                msg = "Force Failure";
                setDeviceFailure(deviceName);
                notifyServerDeviceMessage(deviceName, msg);
                break;
            case 3:
                msg = "isTampered " + String.valueOf(TampSens.isTampered());
                notifyServerDeviceMessage(deviceName, msg);
                break;
            case 4:
                msg = "isFailed " + hasTamperSensorFailed;
                notifyServerDeviceMessage(deviceName, msg);
                break;
            default:
                break;
        }
    }

    // Method to interact with the user via the console
    private void performPrinterAction(String deviceName, int choice, String message) {
        String msg = "";
        switch (choice) {
            case 1:
                System.out.println("Choose an action:");
                System.out.println("1. Information on " + deviceName);
                System.out.println("2. Force " + deviceName + " Failure");
                //System.out.println("3. Set " + deviceName + " File Path" + " !include a message, message will be file path!");
                System.out.println("4. " + deviceName + " print line" + " !include a message, message will be printed!");
                System.out.println("5. " + deviceName + " print empty line");
                System.out.println("6. " + deviceName + " check failure status");
                break;
            case 2:
                msg = "Force Failure";
                setDeviceFailure(deviceName);
                notifyServerDeviceMessage(deviceName, msg);
                break;
//            case 3:
//                msg = "File Path " + message;
//                pntr = new Printer(message);
//                notifyServerDeviceMessage(deviceName, msg);
//                break;
            case 4: //TODO: Add function to print text
                msg = "printing " + message;

                notifyServerDeviceMessage(deviceName, msg);
                break;
            case 5: //TODO: Add function to print empty line
                msg = "printing empty Line";

                notifyServerDeviceMessage(deviceName, msg);
                break;
            case 6:
                msg = "isFailed " + hasPrinterFailed;
                notifyServerDeviceMessage(deviceName, msg);
                break;
            default:
                break;
        }
    }

    private void setDeviceFailure(String device) {
        switch (device) {
            case "Printer":
                hasPrinterFailed = true;
            case "SD Card Driver":
                hasSDCardDriverFailed = true;
            case "Card Reader":
                hasCardReaderFailed = true;
            case "Tamper Sensor":
                hasTamperSensorFailed = true;
            case "Latch":
                hasLatchSensorFailed = true;
        }
    }
}