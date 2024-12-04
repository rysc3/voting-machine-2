package Screen.screenControl;

import Ballot.Ballot;
import Ballot.ExtractInfoXML;
import Ballot.Option;
import Ballot.Proposition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ScreenController extends Application {

    private Stage primaryStage;
    private boolean isOn = false;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    private Ballot ballot;
    private String ballotString;
    private boolean hasScreenFailed;

    private BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(1);

    private static ScreenController instance;

    public ScreenController() {
        instance = this; // Singleton pattern
        this.hasScreenFailed = false;
        this.ballotString = null;
    }

    public static ScreenController getInstance() {
        return instance;
    }

    public static void launchScreenController(String[] args) {
        Application.launch(ScreenController.class, args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        showWelcomeScreen();
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
        primaryStage.setTitle("Voting System");
        primaryStage.show();
    }

    public void showProposition(Proposition prop, String[] nav) {
        Platform.runLater(() -> {
            VBox layout = new VBox();
            Button backButton = new Button(nav[0]);
            Button nextButton = new Button(nav[1]);

            backButton.setOnAction(e -> queue.offer(0));
            nextButton.setOnAction(e -> queue.offer(1));

            layout.getChildren().addAll(backButton, nextButton);
            primaryStage.setScene(new Scene(layout, 400, 600));
        });
    }

    public int waitForSelection() throws InterruptedException {
        return queue.take();
    }

    // === Socket Communication Methods ===
    public void connectToServer(String host, int port) {
        new Thread(() -> {
            try {
                socket = new Socket(host, port);
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                System.out.println("Connected to server.");

                listenToServer();
            } catch (IOException e) {
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
                    int result = waitForSelection();
                    if (result == 1)
                        i++;
                    else if (result == 0 && i > 0)
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
}
