package Screen.screenControl;

import Ballot.Option;
import Screen.testSuite.Proposition;
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
 * TODO:
 * Clean out ALL admin logic - completed
 *
 * Modify function that builds the "scene" to handle different combos of inputs
 * -- completed
 *
 * Add functionality for multiple options to be picked -- completed
 *
 */
public class VoteScreen {

    private Ballot.Proposition proposition;
    private ScreenController controller;
    private String[] nav;

    public VoteScreen(Ballot.Proposition proposition, ScreenController controller, String[] nav) {
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

        Label descriptionLabel = new Label(proposition.propDesc());
        descriptionLabel.setFont(Font.font("Times New Roman", FontWeight.SEMI_BOLD, 20));
        descriptionLabel.setStyle("-fx-text-fill: black;");

        String numOptionString = "";
        if (!proposition.options().isEmpty()) {
            numOptionString = "Select " + proposition.selectableOptions() + " Option"
                    + (proposition.selectableOptions() > 1 ? "s" : "");
        }
        Label numOptionsLabel = new Label(numOptionString);
        numOptionsLabel.setFont(Font.font("Times New Roman", FontWeight.MEDIUM, 14));
        numOptionsLabel.setStyle("-fx-text-fill: black;");

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

                /**
                 * TODO:
                 * Modify the button action to do what exists below, BUT only if it is entering
                 * or exiting the queue!!
                 * theres a poop ass mspaint photo in discord showing what i mean
                 */
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

    /*
     * TODO @Keegan We need the logic here to show a shutdown screen. Should only
     * have a single selectable option "Shut down" on it.
     */
    public void createShutdownScreen() {

    }

}