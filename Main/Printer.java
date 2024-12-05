package Main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Screen.screenControl.ScreenController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Printer{

    private boolean hasFailed;
    private VBox page;
    private ScrollPane sp;
    private Stage stage;
    private final Double pageWidth = 300.0;

    public Printer() {
        this.hasFailed = false;
    }

    public void start(Stage primaryStage) {

        this.stage = primaryStage;

        page = new VBox();
        sp = new ScrollPane(page);
        sp.setFitToWidth(true);
        sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        Scene scene = new Scene(sp, pageWidth, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Printer");
        primaryStage.show();
    }
    //scrollPane.setVvalue(1.0); // 1.0 represents the bottom of the ScrollPane


    //TODO: Implement the printing of a provided VoteRecording
    public void printBallot(VoteRecording finalBallot){
        //Print Lines Slowly
        new Thread(() -> {
            for (int i = 1; i <= 50; i++) {
                int count = i;
                try {
                    Thread.sleep(500); // Simulate delay
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                javafx.application.Platform.runLater(() -> {
                    page.getChildren().add(new Text("New content line " + count));
                    sp.setVvalue(1.0); // Scroll to the bottom
                });
            }
        }).start();
    }

    //TODO: Add Print Line Function
    public void printLine(String text){

    }

    //TODO: Add print newline function
    public void printNewLine() {
        javafx.application.Platform.runLater(() -> {
            page.getChildren().add(new Text(""));
            sp.setVvalue(1.0); // Scroll to the bottom
        });
    }


    public void printBatch(List<String> strings){
        new Thread(() -> {
            for (String ballotLine : strings){
                try {
                    Thread.sleep(500); // Simulate delay
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                javafx.application.Platform.runLater(() -> {
                    Text lineText = new Text(ballotLine);
                    lineText.setWrappingWidth(pageWidth);
                    page.getChildren().add(lineText);
                    sp.setVvalue(1.0); // Scroll to the bottom
                });
            }
        }).start();
    }


    public void setFailureStatus(boolean fail) {
        hasFailed = fail;
    }

    public boolean getFailureStatus() {
        return hasFailed;
    }

}