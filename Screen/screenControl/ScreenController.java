package Screen.screenControl;

import Main.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.concurrent.*;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class ScreenController extends Application {

    private static ScreenController instance;
    private final BlockingQueue<Message> queue = new LinkedBlockingQueue<>();
    private Printer printerWindow;

    private final CopyOnWriteArrayList<BlockingQueue<Message>> listeners = new CopyOnWriteArrayList<>();

    private Stage primaryStage;
    private boolean isOn = false;

    private PrintWriter out;

    private String ballotString;
    private boolean hasScreenFailed;

    public ScreenController() {
        instance = this;
        this.hasScreenFailed = false;
        this.ballotString = null;
    }

    public Printer getPrinterWindow() {
        return printerWindow;
    }

    // Method to register a listener
    public void registerListener(BlockingQueue<Message> listenerQueue) {
        listeners.add(listenerQueue);
    }

    // Notify all listeners when a message is sent
    private void notifyListeners(Message message) {
        for (BlockingQueue<Message> listener : listeners) {
            try {
                listener.put(message);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Error: Unable to notify listener.");
            }
        }
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
        // new Thread(this::processMessages).start(); //idk that we need this

        showWelcomeScreen();

        // Open secondary Printer window
        openPrinterWindow();
    }

    private void openPrinterWindow() {
        printerWindow = new Printer(); // Create the Printer window
        printerWindow.start(new Stage()); // Launch the Printer window in a new Stage

        for (int i = 0; i < 50; i++) {
            printerWindow.printNewLine();
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
            if (prop.options().isEmpty()) {
                try {
                    queue.put(new Message("screenController", "noOptions")); // Put the value in the queue when button
                                                                             // is pressed
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
        Message message = new Message("navBtn", buttonId);
        try {
            queue.put(message); // Original queue handling
            notifyListeners(message); // Notify all listeners
        } catch (InterruptedException e) {
            System.out.println("Error: Unable to process button click.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}