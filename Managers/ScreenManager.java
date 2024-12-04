// package Managers;

// import Ballot.Ballot;
// import Ballot.ExtractInfoXML;
// import Ballot.Option;
// import Ballot.Proposition;
// import Screen.screenControl.ScreenController;
// import javafx.application.Application;
// import javafx.application.Platform;

// import java.io.BufferedReader;
// import java.io.IOException;
// import java.io.InputStreamReader;
// import java.io.PrintWriter;
// import java.net.Socket;
// import java.net.SocketException;
// import java.util.concurrent.CompletableFuture;

// public class ScreenManager {
//     private Socket socket;
//     private PrintWriter out;
//     private BufferedReader in;
//     ScreenController scrn;
//     private boolean hasScreenFailed;
//     private String ballotString;
//     private Ballot ballot;

//     public ScreenManager(ScreenController scrn){

//         this.scrn = scrn;
//         this.hasScreenFailed = false;
//         this.ballotString = null;
//         this.ballot = null;
//     }

//     public void connectToServer(String host, int port) {
//         try {
//             socket = new Socket(host, port);
//             out = new PrintWriter(socket.getOutputStream(), true);
//             in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//             System.out.println("Connected to server.");

//             // Identify as DeviceManager
//             out.println("ScreenManager");

//             // Start listening for messages from the server
//             new Thread(this::listenToServer).start();

//             // Keep the program running until terminated
//             System.out.println("ScreenManager running. Press Ctrl+C to exit.");
//             while (true) {
//                 Thread.sleep(1000); // Keep the main thread alive
//             }
//         } catch (IOException | InterruptedException e) {
//             e.printStackTrace();
//         } finally {
//             closeConnection();
//         }
//     }

//     private void closeConnection() {
//         try {
//             if (in != null) in.close();
//             if (out != null) out.close();
//             if (socket != null && !socket.isClosed()) socket.close();
//             System.out.println("Connection to server closed.");
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//     }

//     private void listenToServer() {
//         try {
//             String response;
//             while ((response = in.readLine()) != null) {
//                 if (response.startsWith("SDCardData:")) {
//                     String sdCardData = response.substring("SDCardData:".length());
//                     System.out.println("Received SD Card data from server: " + sdCardData);

//                     // Store or process the data for display in case 5
//                     processSDCardData(sdCardData);
//                 }
//                 else if (response.startsWith("Enter your name")) {
//                     continue;
//                 }
//                 if (response.contains("ScreenManager:")) {
//                     String[] parts = response.split(":");
//                     if (parts.length > 1) {
//                         parseAndPerformAction(response);
//                     }
//                 } else {
//                     System.out.println("Received unrecognized message: " + response);
//                 }
//             }
//         } catch (SocketException e) {
//             System.out.println("Connection to server lost. Closing client...");
//         } catch (IOException e) {
//             e.printStackTrace();
//         } finally {
//             closeConnection(); // Ensure all resources are closed
//         }
//     }

//     private void processSDCardData(String sdCardData) {
//         ballotString = sdCardData;
//     }




//     // Method to parse and perform action based on server message
//     private void parseAndPerformAction(String message) {

//         try {
//             String[] parts = message.split(":");
//             if (parts.length == 3) {
//                 String deviceName = parts[1].trim();

//                 int choice = Integer.parseInt(parts[2].trim());
//                 String msg = "";
//                 performActionOnDevice(deviceName, choice, msg);
//             }
//             else if (parts.length == 4) {
//                 String deviceName = parts[1].trim();

//                 int choice = Integer.parseInt(parts[2].trim());
//                 String msg = parts[3].trim();

//                 performActionOnDevice(deviceName, choice, msg);
//             } else {
//                 System.out.println("Invalid message format: " + message); // Log invalid messages
//             }
//         } catch (Exception e) {
//             System.out.println("Error parsing message: " + e.getMessage());
//         }
//     }

//     public void performActionOnDevice(String deviceName, int choice, String message) throws IOException {
//         switch (deviceName) {
//             case "Screen":
//                 performScreenAction(deviceName, choice,message);
//                 break;
//             default:
//         }
//     }
//     public void performScreenAction(String deviceName, int choice, String message) throws IOException {

//         String msg = "";

//         switch (choice) {
//             case 1:
//                 System.out.println("Choose an action for " + deviceName + ":");
//                 System.out.println("1. Information on " + deviceName);
//                 System.out.println("2. Force " + deviceName + " Failure");
//                 System.out.println("3. " + deviceName + " turn on");
//                 System.out.println("4. " + deviceName + " turn off");
//                 System.out.println("5. " + deviceName + " show ballot");
//                 System.out.println("6. " + deviceName + " get ballot");
//                 break;
//             case 2:
//                 msg = "Force Failure";
//                 this.hasScreenFailed = true;
//                 notifyServerDeviceMessage(deviceName, msg);
//                 break;
//             case 3:
//                 msg = "Turn On";
//                 notifyServerDeviceMessage(deviceName, msg);
//                 scrn.turnOn();
//                 break;
//             case 4:
//                 msg = "Turn Off";
//                 notifyServerDeviceMessage(deviceName, msg);
//                 scrn.turnOff();
//                 break;
//             case 5:
//                 msg = "Show Ballot :" + message;
//                 notifyServerDeviceMessage(deviceName, msg);
//                 ballot = ExtractInfoXML.makeBallotFromXML(ballotString);

// /*
//                 if (message.startsWith("<Voting-Machine>")) {
//                     ballot = ExtractInfoXML.makeBallotFromXML(message);
//                     for (int i = 0; i < ballot.propositions().size(); ) {
//                         Proposition prop = ballot.propositions().get(i);
//                         if (i == 0) {
//                             scrn.showProposition(prop, new String[]{"", "Next"});
//                         } else if (i == ballot.propositions().size() - 1) {
//                             scrn.showProposition(prop, new String[]{"Back", "End Voting"});
//                         } else {
//                             scrn.showProposition(prop, new String[]{"Back", "Next"});
//                         }
//                         int result = scrn.waitForSelection();
//                         if (result == 1) {
//                             i++;
//                         }
//                         if (result == 0 && i > 0) {
//                             i--;
//                         }
//                     }
//                 }

//  */

//                 for (int i = 0; i < ballot.propositions().size();){
//                     Proposition prop = ballot.propositions().get(i);
//                     if (i == 0){
//                         scrn.showProposition(prop,new String[]{"", "Next"});
//                     } else if (i == ballot.propositions().size() - 1) {
//                         scrn.showProposition(prop,new String[]{"Back", "End Voting"});
//                     } else {
//                         scrn.showProposition(prop,new String[]{"Back", "Next"});
//                     }

//                     int result = scrn.waitForSelection();
//                     if (result == 1){
//                         i++;
//                     }
//                     if (result == 0 && i > 0){
//                         i--;
//                     }
//                 }


//                 break;
//                 // I have it automatically save.. sorry William this fn doesnt really do much
//             case 6:
//                 msg = "Get Ballot: " + message;
//                 notifyServerDeviceMessage(deviceName, msg);
//                 String str = "";
//                 if (ballot.propositions() == null){
//                     out.println("null");
//                 } else {
//                     str = ExtractInfoXML.makeXMLFromBallot(ballot);
//                 }
//                 out.println(str);
//                 break;
//         }
//     }

//     private void notifyServerDeviceMessage(String deviceName, String message) {
//         String serverMessage = deviceName + ":" + message; // Ensure proper "DeviceName:Message" format
//         if (socket == null || socket.isClosed()) {
//             System.err.println("Socket is closed. Cannot send device message.");
//             return;
//         }
//         out.println(serverMessage); // Send the message to the server
//     }


//     public static void main(String[] args) {
//         // CompletableFuture to hold the ScreenController instance
//         CompletableFuture<ScreenController> screenControllerFuture = new CompletableFuture<>();

//         // Launch JavaFX application in a separate thread
//         new Thread(() -> {
//             Application.launch(ScreenController.class); // Initialize JavaFX application
//         }).start();

//         // Wait for the ScreenController instance on the JavaFX Application thread
//         new Thread(() -> {
//             try {
//                 // Block until the ScreenController instance is available
//                 while (ScreenController.getInstance() == null) {
//                     Thread.sleep(50); // Check periodically
//                 }
//                 // When available, complete the future
//                 screenControllerFuture.complete(ScreenController.getInstance());
//             } catch (Exception e) {
//                 screenControllerFuture.completeExceptionally(e);
//             }
//         }).start();

//         try {
//             // Get the ScreenController instance from the future
//             ScreenController scr = screenControllerFuture.get();

//             // Create ScreenManager and connect to the server
//             ScreenManager manager = new ScreenManager(scr);
//             manager.connectToServer("localhost", 12345);
//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//     }

// }
