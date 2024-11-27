package screenControl;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import testSuite.Proposition;

import java.util.ArrayList;
import java.util.Locale;


/**
 * TODO:
 * Clean out ALL admin logic - completed
 *
 * Modify function that builds the "scene" to handle different combos of inputs -- completed
 *
 * Add functionality for multiple options to be picked  -- completed
 *
 */
public class





VoteScreen {

  private Proposition proposition;
  private ScreenController controller;
  private Scene scene;

  // Main function to create a screen
  public int createScreen(Proposition prop) {

    String title = prop.getName();
    String desc = prop.getDescription();
    String[] options = prop.getOptions();
    int maxNumSelections = prop.getMaxNumSelections();

    /*
     * We need to save return value and handle it later, since we can't 
     * direclty return from inside a setOnAction block being a void method. 
    */
    int RET = -1;

    // Title Label
    Label titleLabel = new Label(title);
    titleLabel.setFont(Font.font("Georgia", FontWeight.EXTRA_BOLD, 28));
    titleLabel.setStyle("-fx-text-fill: black;");

    // Description Label
    Label descriptionLabel = new Label("Select " + maxNumSelections + " Option" + (maxNumSelections > 1 ? "s" : ""));
    descriptionLabel.setFont(Font.font("Times New Roman", FontWeight.SEMI_BOLD, 20));
    descriptionLabel.setStyle("-fx-text-fill: black;");

    // Options Box
    FlowPane optionsBox = new FlowPane(15, 15);
    optionsBox.setAlignment(Pos.TOP_CENTER);

    // ToggleGroup for options
    ToggleGroup toggleGroup = new ToggleGroup();

    for (int i = 0; i < options.length; i++) {
      String optionText = options[i];
      ToggleButton optionButton = new ToggleButton(optionText);
      optionButton.setMinWidth(250);
      optionButton.setFont(Font.font("Times New Roman", FontWeight.BOLD, 18));
      optionButton.setStyle(
          "-fx-background-color: lightgray; " +
              "-fx-text-fill: black; " +
              "-fx-border-color: darkgray; " +
              "-fx-border-radius: 10; " +
              "-fx-background-radius: 10; " +
              "-fx-padding: 10px;");

      final int index = i;

      if (optionText.isEmpty()) {
        optionButton.setDisable(true);
        optionButton.setStyle(
            "-fx-background-color: #e0e0e0; " +
                "-fx-text-fill: gray; " +
                "-fx-border-color: darkgray; " +
                "-fx-border-radius: 10; " +
                "-fx-background-radius: 10; " +
                "-fx-padding: 10px;");
      } else {
        optionButton.setOnAction(e -> {
          // Set ret val to 0 for no expected resposne 
          RET = 0;
        });
      }

      optionsBox.getChildren().add(optionButton);
    }


    // Next Button
    Button nextButton = new Button("Next");
    styleNavigationButton(nextButton);
    nextButton.setOnAction(e -> {
      // Implement return logic for next action (-1)
    });

    // Navigation Box
    HBox navigationBox = new HBox(30, backButton, nextButton);
    navigationBox.setAlignment(Pos.CENTER);


    /**
     * Possibly good to reskin for all 'ballot' questions
     */
    public Scene createVotingScreen() {

        Label titleLabel = new Label(proposition.getName());
        titleLabel.setFont(Font.font("Georgia", FontWeight.EXTRA_BOLD, 28));
        titleLabel.setStyle("-fx-text-fill: black;");


        Label descriptionLabel = new Label(proposition.getDescription());
        descriptionLabel.setFont(Font.font("Times New Roman", FontWeight.SEMI_BOLD, 20));
        descriptionLabel.setStyle("-fx-text-fill: black;");

        String numOptionString = "";
        if (proposition.getOptions() != null){
            numOptionString = "Select " + proposition.getMaxNumSelections() + " Option" + (proposition.getMaxNumSelections() > 1 ? "s" : "");
        }
        Label numOptionsLabel = new Label(numOptionString);
        numOptionsLabel.setFont(Font.font("Times New Roman", FontWeight.MEDIUM, 14));
        numOptionsLabel.setStyle("-fx-text-fill: black;");

        FlowPane optionsBox = new FlowPane(5, 5);
        optionsBox.setAlignment(Pos.CENTER);


        //Fill Flowpane with proposition options
        if (proposition.getOptions() != null){
            for (int i = 0; i < proposition.getOptions().length; i++) {
                String optionText = proposition.getOptions()[i];
                ToggleButton optionButton = new ToggleButton(optionText);
                optionButton.setMinWidth(100);
                styleOptionButton(optionButton);

                final int index = i;

                /**
                 * TODO:
                 * Modify the button action to do what exists below, BUT only if it is entering or exiting the queue!!
                 * theres a poop ass mspaint photo in discord showing what i mean
                 */
                optionButton.setOnAction(e -> {
                    boolean isSelected = proposition.getSelections()[index];
                    if (isSelected || proposition.getNumCurrentSelections() < proposition.getMaxNumSelections()){
                        proposition.setSelection(index, !isSelected);
                        updateButtonStyle(optionButton, !isSelected);
                    }
                });

                optionsBox.getChildren().add(optionButton);

            }
        }




        HBox navigationBox = new HBox(30);
        navigationBox.setAlignment(Pos.CENTER);

        if ( proposition.getNavOptions() != null){
            for (int j = 0; j < proposition.getNavOptions().length; j++) {
                String navText = proposition.getNavOptions()[j];
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

        VBox mainLayout = new VBox(20, spacer, titleLabel, descriptionLabel, numOptionsLabel, optionsBox, navigationBox);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #e6ecf2, #cfd8e4); -fx-background-radius: 20;");

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
                        "-fx-padding: 10px;"
        );
    }

    private void updateButtonStyle(ToggleButton button, boolean isSelected) {
        button.setStyle(
                isSelected
                        ? "-fx-background-color: darkblue; -fx-text-fill: white; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 10px; -fx-font-size: 18px; -fx-font-family: 'Times New Roman'; -fx-font-weight: bold; -fx-border-color: darkgray;"
                        : "-fx-background-color: lightgray; -fx-text-fill: black; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 10px; -fx-font-size: 18px; -fx-font-family: 'Times New Roman'; -fx-font-weight: bold; -fx-border-color: darkgray;"
        );
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
                        "-fx-padding: 10px;"
        );
        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: navy; " +
                        "-fx-text-fill: white; " +
                        "-fx-border-radius: 10; " +
                        "-fx-background-radius: 10; " +
                        "-fx-padding: 10px;"
        ));
        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: darkblue; " +
                        "-fx-text-fill: white; " +
                        "-fx-border-radius: 10; " +
                        "-fx-background-radius: 10; " +
                        "-fx-padding: 10px;"
        ));
    }

}