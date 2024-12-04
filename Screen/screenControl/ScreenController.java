package Screen.screenControl;

import Main.Ballot;
import Main.ExtractInfoXML;
import Main.Option;
import Main.Proposition;
import Main.Message;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ScreenController extends Application{

    private static ScreenController instance;
    private final BlockingQueue<Message> queue = new LinkedBlockingQueue<>();
    private TextArea logArea;

    private Stage primaryStage;
    private boolean isOn = false;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    private Ballot ballot;
    private String ballotString;
    private boolean hasScreenFailed;


    public ScreenController() {
        instance = this; // Singleton pattern
        this.hasScreenFailed = false;
        this.ballotString = null;
    }

    public static synchronized ScreenController getInstance() {
        return instance;
    }

    public void sendMessage(Message message) {
        queue.offer(message);
    }

    @Override
    public void start(Stage primaryStage) {
        instance = this;

        this.primaryStage = primaryStage;

        // Start a background thread to process messages
        new Thread(this::processMessages).start();

        showWelcomeScreen();
    }

    private void processMessages() {
        while (true) {
            try {
                Message message = queue.poll(); // Non-blocking retrieval
                if (message != null) {
                    // Update the UI on the JavaFX Application Thread
                    Platform.runLater(() -> System.out.println(
                            "Processing message from " + message.getSender() + ": " + message.getContent() + "\n"
                    ));
                }
                Thread.sleep(100); // Avoid tight looping
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }




    // === JavaFX UI Methods ===
    public void turnOn() {
        this.isOn = true;
        Platform.runLater(this::showWelcomeScreen);
    }

    public void turnOff() {
        this.isOn = false;
        Platform.runLater(() -> primaryStage.setScene(new Scene(new VBox(), 400, 600)));
    }

    private void showWelcomeScreen() {
        VBox layout = new VBox();
        layout.getChildren().add(new Region()); // Add UI components as needed
        Scene scene = new Scene(layout, 400, 600);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Voting System DEBUG");
        primaryStage.show();
    }

    public void showProposition(Proposition prop, String[] nav) {
        Platform.runLater(() -> instance.showScreen(prop, nav));
    }

    public Message waitForSelection() throws InterruptedException {
        return queue.take();
    }

    // === Socket Communication Methods ===
    public void connectToServer(String host, int port) {
        System.out.println("connectToServer Called"); //debug
        new Thread(() -> {
            try {
                socket = new Socket(host, port);
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                System.out.println("Connected to server.");

                listenToServer();
            } catch (IOException e) {

                System.out.println("connectToServer Failed"); //debug

                e.printStackTrace();
            }
        }).start();
    }

    private void listenToServer() {
        try {
            String response;
            while ((response = in.readLine()) != null) {
                if (response.startsWith("SDCardData:")) {
                    ballotString = response.substring("SDCardData:".length());
                } else if (response.contains("ScreenManager:")) {
                    parseAndPerformAction(response);
                }
            }
        } catch (SocketException e) {
            System.out.println("Connection to server lost.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseAndPerformAction(String message) {
        try {
            String[] parts = message.split(":");
            if (parts.length == 3) {
                String deviceName = parts[1].trim();
                int choice = Integer.parseInt(parts[2].trim());
                performAction(deviceName, choice, "");
            } else if (parts.length == 4) {
                String deviceName = parts[1].trim();
                int choice = Integer.parseInt(parts[2].trim());
                String extraMessage = parts[3].trim();
                performAction(deviceName, choice, extraMessage);
            } else {
                System.out.println("Invalid message format: " + message);
            }
        } catch (Exception e) {
            System.out.println("Error parsing message: " + e.getMessage());
        }
    }

    public void performAction(String deviceName, int choice, String message) {
        if (!deviceName.equals("Screen"))
            return;

        switch (choice) {
            case 1: // Print info
                System.out.println("Screen info requested.");
                break;
            case 2: // Simulate failure
                hasScreenFailed = true;
                sendMessage("Screen:Force Failure");
                break;
            case 3: // Turn on
                turnOn();
                sendMessage("Screen:Turn On");
                break;
            case 4: // Turn off
                turnOff();
                sendMessage("Screen:Turn Off");
                break;
            case 5: // Show ballot
                processBallot(message);
                break;
            case 6: // Get ballot
                sendBallot();
                break;
        }
    }

    private void processBallot(String message) {
        ballot = ExtractInfoXML.makeBallotFromXML(ballotString);

        Platform.runLater(() -> {
            for (int i = 0; i < ballot.propositions().size();) {
                Proposition prop = ballot.propositions().get(i);
                String[] nav = i == 0 ? new String[] { "", "Next" }
                        : i == ballot.propositions().size() - 1 ? new String[] { "Back", "End Voting" }
                                : new String[] { "Back", "Next" };

                showProposition(prop, nav);

                try {
                    Message result = waitForSelection();
                    if (Integer.parseInt( result.getContent()) == 1)
                        i++;
                    else if (Integer.parseInt( result.getContent()) == 0 && i > 0)
                        i--;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void sendBallot() {
        if (ballot != null) {
            sendMessage("Screen:Get Ballot:" + ExtractInfoXML.makeXMLFromBallot(ballot));
        } else {
            sendMessage("Screen:Get Ballot:null");
        }
    }

    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    /*
     * TODO @sara same deal here with the ballot issue
     */
    private void showScreen(Proposition prop, String[] nav) {
        VoteScreen voteScreen = new VoteScreen(prop, this, nav);
        if (isOn) {
            Scene scene = voteScreen.createVotingScreen();
            primaryStage.setScene(scene);
            primaryStage.setTitle("Voting System - Screen");
            primaryStage.show();
            if (prop.options().isEmpty()){
                try {
                    queue.put(new Message("screenController", "noOptions"));  // Put the value in the queue when button is pressed
                } catch (InterruptedException e) {
                    System.out.println("Error: Screen Controller buttonhandler blockingqueue is fucked up");
                    e.printStackTrace();
                }
            }
        } else if (!isOn) {
            Scene scene = voteScreen.drawOffScreen();
            primaryStage.setScene(scene);
            primaryStage.setTitle("Voting System");
            primaryStage.show();
        }
    }

    protected void buttonHandler(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        String buttonId = clickedButton.getId();
        try {
            queue.put(new Message("navBtn", buttonId)); // Put the value in the queue when button is pressed
        } catch (InterruptedException e) {
            System.out.println("Error: Screen Controller buttonhandler blockingqueue is fucked up");
            e.printStackTrace();
        }
    }

    /*
     * TODO @sara, looks like Ballot has been nuked. I think we'll need to fix this once Ballot is added back.
     */
    // public void showProposition(Ballot.Proposition prop, String[] nav) {

    //     Platform.runLater(() -> instance.showScreen(prop, nav));
    // }

    public void disconnect() {
        try {
            if (socket != null) {
                socket.close();
                System.out.println("Disconnected from server.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processSDCardData(String sdCardData) {
        ballotString = sdCardData;
        System.out.println("Processed SD Card data: " + ballotString);
    }

    public static void main(String[] args) {
        launch(args);
    }
}