package Screen.screenControl;

import Main.Option;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.Locale;

/**
 * Screen generated based on the provided 'proposition'
 * Format is:
 *
 * ___________________________________
 * HEADER |
 * Description |
 * |
 * option1 option2 |
 * option3 option.. |
 * |
 * NavBtn1 NavBtn2 NavBtn... |
 * ____________________________________
 *
 * Header - Proposition.propName
 * Description - Proposition.propDesc
 * Options - (ArrayList) proposition.options
 * NavButtons - (String[]) this.nav
 *
 * Number of selectable options : proposition.selectableOptions (single or multi
 * choice)
 *
 * Nav buttons return their index to the parent class. Used for overall logic
 * like "go back" or "next".
 *
 */
public class VoteScreen {

    private Main.Proposition proposition;
    private ScreenController controller;
    private String[] nav;

    public VoteScreen(Main.Proposition proposition, ScreenController controller, String[] nav) {
        this.proposition = proposition;
        this.controller = controller;
        this.nav = nav;
    }

    public Scene drawOffScreen() {
        VBox layout = new VBox();
        layout.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));

        return new Scene(layout, 400, 600);
    }

    /**
     * Possibly good to reskin for all 'ballot' questions
     */

    public Scene createVotingScreen() {

        Label titleLabel = new Label(proposition.propName());
        titleLabel.setFont(Font.font("Georgia", FontWeight.EXTRA_BOLD, 28));
        titleLabel.setStyle("-fx-text-fill: black;");
        titleLabel.setWrapText(true);

        Label descriptionLabel = new Label(proposition.propDesc());
        descriptionLabel.setFont(Font.font("Times New Roman", FontWeight.SEMI_BOLD, 20));
        descriptionLabel.setStyle("-fx-text-fill: black;");
        descriptionLabel.setWrapText(true);

        String numOptionString = "";
        if (!proposition.options().isEmpty()) {
            numOptionString = "Select " + proposition.selectableOptions() + " Option"
                    + (proposition.selectableOptions() > 1 ? "s" : "");
        }
        Label numOptionsLabel = new Label(numOptionString);
        numOptionsLabel.setFont(Font.font("Times New Roman", FontWeight.MEDIUM, 14));
        numOptionsLabel.setStyle("-fx-text-fill: black;");
        numOptionsLabel.setWrapText(true);

        FlowPane optionsBox = new FlowPane(5, 5);
        optionsBox.setAlignment(Pos.CENTER);

        // Fill Flowpane with proposition options
        if (!proposition.options().isEmpty()) {
            for (int i = 0; i < proposition.options().size(); i++) {
                Option op = proposition.options().get(i);
                String optionText = op.description();
                ToggleButton optionButton = new ToggleButton(optionText);
                optionButton.setMinWidth(100);

                if (op.isSelected()) {
                    updateButtonStyle(optionButton, op.isSelected());
                } else {
                    styleOptionButton(optionButton);
                }

                final int index = i;

                optionButton.setOnAction(e -> {
                    boolean isSelected = proposition.options().get(index).isSelected();
                    int currentSelections = 0;
                    for (Option o : proposition.options()) {
                        if (o.isSelected()) {
                            currentSelections++;
                        }
                    }
                    if (isSelected || currentSelections < proposition.selectableOptions()) {
                        if (proposition.options().get(index).isSelected()) {
                            proposition.options().get(index).unselect();
                        } else {
                            proposition.options().get(index).select();
                        }

                        updateButtonStyle(optionButton, !isSelected);
                    }
                });

                optionsBox.getChildren().add(optionButton);

            }
        }

        HBox navigationBox = new HBox(30);
        navigationBox.setAlignment(Pos.CENTER);

        if (nav != null) {
            for (int j = 0; j < nav.length; j++) {
                String navText = nav[j];
                Button navButton = new Button(navText.toUpperCase(Locale.ROOT));
                navButton.setMinWidth(250);
                styleNavigationButton(navButton);
                navButton.setId(Integer.toString(j));
                navButton.setOnAction(e -> controller.buttonHandler(e));
                navigationBox.getChildren().add(navButton);
            }
        }

        Region spacer = new Region();
        spacer.setPrefHeight(30);

        VBox mainLayout = new VBox(20, spacer, titleLabel, descriptionLabel, numOptionsLabel, optionsBox,
                navigationBox);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #e6ecf2, #cfd8e4); -fx-background-radius: 20;");

        return new Scene(mainLayout, 400, 600);
    }

    private void styleOptionButton(ToggleButton button) {
        button.setFont(Font.font("Times New Roman", FontWeight.BOLD, 18));
        button.setStyle(
                "-fx-background-color: lightgray; " +
                        "-fx-text-fill: black; " +
                        "-fx-border-color: darkgray; " +
                        "-fx-border-radius: 10; " +
                        "-fx-background-radius: 10; " +
                        "-fx-padding: 10px;");
    }

    private void updateButtonStyle(ToggleButton button, boolean isSelected) {
        button.setStyle(
                isSelected
                        ? "-fx-background-color: darkblue; -fx-text-fill: white; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 10px; -fx-font-size: 18px; -fx-font-family: 'Times New Roman'; -fx-font-weight: bold; -fx-border-color: darkgray;"
                        : "-fx-background-color: lightgray; -fx-text-fill: black; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 10px; -fx-font-size: 18px; -fx-font-family: 'Times New Roman'; -fx-font-weight: bold; -fx-border-color: darkgray;");
    }

    /**
     * QOL function to style all nav buttons. Saves redundant code
     */
    private void styleNavigationButton(Button button) {
        button.setFont(Font.font("Georgia", FontWeight.BOLD, 16));
        button.setMinWidth(120);
        button.setStyle(
                "-fx-background-color: darkblue; " +
                        "-fx-text-fill: white; " +
                        "-fx-border-radius: 10; " +
                        "-fx-background-radius: 10; " +
                        "-fx-padding: 10px;");
        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: navy; " +
                        "-fx-text-fill: white; " +
                        "-fx-border-radius: 10; " +
                        "-fx-background-radius: 10; " +
                        "-fx-padding: 10px;"));
        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: darkblue; " +
                        "-fx-text-fill: white; " +
                        "-fx-border-radius: 10; " +
                        "-fx-background-radius: 10; " +
                        "-fx-padding: 10px;"));
    }

}