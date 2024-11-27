package Ballot;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GUI extends Application {
    private final Menus ballotMakerEditor;

    public GUI() {
        this.ballotMakerEditor = new Menus();
    }
    @Override
    public void start(Stage ballotMaker) throws Exception {
        Scene editor = new Scene(this.ballotMakerEditor.getMenus());
        editor.getStylesheets().add("file:Ballot/stylesheet.css");

        ballotMaker.setMinWidth(875);
        ballotMaker.setMinHeight(640);
        ballotMaker.setMaxWidth(875);
        ballotMaker.setResizable(true);
        ballotMaker.setTitle("Ballot Maker");
        ballotMaker.setScene(editor);
        ballotMaker.show();
    }
}

