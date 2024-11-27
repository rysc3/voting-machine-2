package Ballot;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class Menus {

    private final BorderPane screen = new BorderPane();

    // ScrollPane for ease of navigating the screen
    private final ScrollPane menus;

    // Setup Menu for the entire setup
    private final VBox setupMenu = new VBox();

    // VBox where propositions are stored
    private final VBox propMenu = new VBox();

    // Confirm button for the user
    private final Button confirmButton = new Button("Confirm");

    // Text to show/display if the file was properly generated,
    // or if there has been erroneous input fed in.
    private final Text fileGenerated = new Text();

    // Counter to count propositions
    private int propCounter;

    private final String blackBorder = "-fx-border-color: black;";

    private final String redBorder = "-fx-border-color: red;";

    private final String consolasFont = "-fx-font-family: 'Consolas';";

    private final String cFbB = consolasFont + blackBorder;

    private final String fontBig = "-fx-font-size: 18px; -fx-font-weight: bold;";

    private final String cFfB = consolasFont + fontBig;

    private final String cFfS = "-fx-font-size: 14px; -fx-font-weight: bold;";

    /**
     * Menus is a class that sets up the setup menu for the GUI
     */
    public Menus () {
        makeSetupScreen(this.setupMenu);
        this.menus = new ScrollPane(this.setupMenu);
        this.propCounter = 0;
        this.menus.setFitToWidth(true);
        this.menus.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        this.menus.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        this.confirmButton.setStyle(cFbB);
        this.confirmButton.setPrefSize(100, 50);
        this.confirmButton.setOnMouseClicked(event -> {
            confirm();
        });
        this.screen.setCenter(this.menus);
        VBox leftFiller = new VBox();
        VBox rightFiller = new VBox();
        this.screen.setLeft(leftFiller);
        this.screen.setRight(rightFiller);
    }

    /**
     * makeSetupScreen() is a method that makes the screen
     * to use for the setup portion of the Ballot Maker
     * @param setup - The VBox that will hold the entire menu
     */
    private void makeSetupScreen(VBox setup) {

        setup.setMaxWidth(855);
        setup.setFillWidth(true);
        setup.setAlignment(Pos.CENTER);
        VBox.setVgrow(setup, Priority.ALWAYS);

        // Declare new HBoxes
        HBox checking = new HBox();
        HBox nameBox  = new HBox();
        HBox dateTime = new HBox();
        HBox propsBox = new HBox();

        checking.setPadding(new Insets(1, 0, 1, 0));

        nameBox.setSpacing(2);
        nameBox.setPadding(new Insets(1, 1, 1, 1));
        dateTime.setSpacing(2);
        dateTime.setPadding(new Insets(1,1,1,1));
        propsBox.setSpacing(2);
        propsBox.setPadding(new Insets(1,1,1,1));

        VBox fileName = new VBox();
        VBox elecName = new VBox();

        fileName.setPadding(new Insets(5, 0, 5, 0));
        elecName.setPadding(new Insets(5, 0, 5, 0));

        fileName.setPrefWidth(378);
        elecName.setPrefWidth(378);

        HBox.setHgrow(fileName, Priority.ALWAYS);
        HBox.setHgrow(elecName, Priority.ALWAYS);

        VBox startDateBox = new VBox();
        VBox endDateBox   = new VBox();
        VBox startTimeBox = new VBox();
        VBox endTimeBox   = new VBox();

        startDateBox.setPadding(new Insets(5, 0, 5,0));
        endDateBox.setPadding(new Insets(5, 0, 5, 0));
        startTimeBox.setPadding(new Insets(5, 0, 5, 0));
        endTimeBox.setPadding(new Insets(5, 0, 5, 0));

        startDateBox.setPrefWidth(189);
        endDateBox.setPrefWidth(189);
        startTimeBox.setPrefWidth(189);
        endTimeBox.setPrefWidth(189);

        HBox.setHgrow(startDateBox, Priority.ALWAYS);
        HBox.setHgrow(endDateBox, Priority.ALWAYS);
        HBox.setHgrow(startTimeBox, Priority.ALWAYS);
        HBox.setHgrow(endTimeBox, Priority.ALWAYS);

        startDateBox.setStyle(blackBorder);
        endDateBox.setStyle(blackBorder);
        startTimeBox.setStyle(blackBorder);
        endTimeBox.setStyle(blackBorder);

        startDateBox.setId("SB");
        endDateBox.setId("EB");
        startTimeBox.setId("BB");
        endTimeBox.setId("FB");

        checking.setStyle(blackBorder);
        fileName.setStyle(blackBorder);
        elecName.setStyle(blackBorder);
        dateTime.setStyle(blackBorder);
        propsBox.setStyle(blackBorder);

        // Set the file name to output. Currently only supports XML files.
        // I added the extension .xml later on to make it simple
        Text askFileName = new Text("File name: ");
        askFileName.setStyle(cFfB);
        TextField userFileName = new TextField();
        userFileName.setId("FN");
        userFileName.setPromptText("FileName");
        userFileName.setMaxWidth(Double.MAX_VALUE);
        userFileName.setStyle(cFbB);

        // Set the Name/Type of the election.
        Text askElectionName = new Text("Election Name: ");
        askElectionName.setStyle(cFfB);
        TextField userElectionName = new TextField();
        userElectionName.setId("EN");
        userElectionName.setPromptText("e.g. Supreme Court Judge Retention");
        userElectionName.setMaxWidth(Double.MAX_VALUE);
        userElectionName.setStyle(cFbB);

        // Set the start/end time and date for the election.
        Text askStartDate = new Text("Start Date: ");
        Text askEndDate   = new Text("End Date: ");
        Text askStartTime = new Text("Start Time for Each Day: ");
        Text askEndTime   = new Text("End Time for Each Day: ");

        askStartDate.setStyle(cFfB);
        askEndDate.setStyle(cFfB);
        askStartTime.setStyle(cFfS);
        askEndTime.setStyle(cFfS);

        TextField userStartDate = new TextField();
        TextField userEndDate   = new TextField();
        TextField userStartTime = new TextField();
        TextField userEndTime   = new TextField();

        userStartDate.setStyle(cFbB);
        userEndDate.setStyle(cFbB);
        userStartTime.setStyle(cFbB);
        userEndTime.setStyle(cFbB);

        userStartDate.setStyle(blackBorder);
        userEndDate.setStyle(blackBorder);
        userStartTime.setStyle(blackBorder);
        userEndTime.setStyle(blackBorder);

        userStartDate.setPromptText("MM/dd/yyyy");
        userEndDate.setPromptText("MM/dd/yyyy");
        userStartTime.setPromptText("HH:MM (24-Hour Time)");
        userEndTime.setPromptText("HH:MM (24-Hour Time)");

        // Id for parsing
        userStartDate.setId("SD");
        userEndDate.setId("ED");
        userStartTime.setId("ST");
        userEndTime.setId("ET");

        // ComboBox provides a dropdown for users to find and delete the proposition of.
        ObservableList<Integer> indexChecker = FXCollections.observableArrayList();
        ComboBox<Integer> whichProp = new ComboBox<>(indexChecker);
        whichProp.setVisible(false);
        whichProp.setDisable(true);
        whichProp.setVisibleRowCount(5);

        // Make the Add and Remove Propositions Button which adds/removes propositions to the Ballot.
        Button removeProp = new Button("Remove Proposition");
        removeProp.setVisible(false);
        removeProp.setOnMouseClicked(event -> {
            int userVal = Integer.parseInt(whichProp.getEditor().getText());
            removeProp(userVal, this.propMenu);
            indexChecker.remove(Integer.valueOf(this.propCounter));
            if (this.propCounter > 0) {
                this.propCounter--;
            }
            if (this.propCounter == 0) {
                whichProp.setEditable(false);
                whichProp.setDisable(true);
                whichProp.setVisible(false);
                removeProp.setVisible(false);
            }
        });

        Button addProp = new Button("Add Proposition");
        addProp.setOnMouseClicked(event -> {
            whichProp.setDisable(false);
            makeNewProp(this.propMenu);
            indexChecker.add(this.propCounter + 1);
            whichProp.setItems(indexChecker);
            whichProp.setValue(this.propCounter + 1);
            this.propCounter++;
            if (this.propCounter > 0) {
                whichProp.setEditable(true);
                whichProp.setVisible(true);
                removeProp.setVisible(true);
            }
        });

        // Add These to their respective HBoxes

        startDateBox.getChildren().addAll(askStartDate, userStartDate);
        endDateBox.getChildren().addAll(askEndDate, userEndDate);
        startTimeBox.getChildren().addAll(askStartTime, userStartTime);
        endTimeBox.getChildren().addAll(askEndTime, userEndTime);

        fileName.getChildren().addAll(askFileName, userFileName);
        elecName.getChildren().addAll(askElectionName, userElectionName);

        nameBox.getChildren().addAll(fileName ,elecName);

        dateTime.getChildren().addAll(startDateBox, endDateBox, startTimeBox, endTimeBox);
        propsBox.getChildren().addAll(addProp, removeProp, whichProp);

//        Button getWidth = new Button("Get Width");
//        getWidth.setOnMouseClicked(event -> {
//            System.out.println(fileName.getWidth());
//            System.out.println(elecName.getWidth());
//            System.out.println(startDateBox.getWidth());
//            System.out.println(endDateBox.getWidth());
//            System.out.println(startTimeBox.getWidth());
//            System.out.println(endTimeBox.getWidth());
//        });
        checking.getChildren().addAll(this.confirmButton, this.fileGenerated);

        // Add These to the setup VBox to show
        setup.getChildren().addAll(checking, nameBox, dateTime, this.propMenu, propsBox);
    }

    /**
     * makeNewProp() is a method that makes a new proposition.
     * @param rootMenu - The origin/root menu to add to.
     */
    private void makeNewProp(VBox rootMenu) {
        VBox propStuff = new VBox();
        propStuff.setId(String.valueOf(this.propCounter + 1));
        propStuff.setPadding(new Insets(5, 1, 5, 1));
        propStuff.setSpacing(5);
        propStuff.setAlignment(Pos.CENTER_LEFT);

        // Id for parsing
        Text propNum = new Text("Proposition " + (this.propCounter + 1));
        propNum.setStyle(cFfB);
        propNum.setId("PT");

        VBox propInfo = new VBox();
        propInfo.setPadding(new Insets(5, 1, 5, 1));
        propInfo.setId("PI");
        propInfo.setSpacing(5);

        HBox.setHgrow(propStuff, Priority.ALWAYS);
        HBox.setHgrow(propInfo, Priority.ALWAYS);

        // Setup Proposition Section
        Text propName = new Text("Proposition Name/Title: ");
        propName.setStyle(cFfB);
        Text propDesc = new Text("Proposition Description: ");
        propDesc.setStyle(cFfB);
        TextField userPropName = new TextField();
        TextArea userPropDesc = new TextArea();
        userPropName.setPromptText("e.g. President");
        userPropDesc.setPromptText("Brief explanation of responsibilities/details");
        userPropName.setId("PN");
        userPropDesc.setId("PD");

        HBox choices = new HBox();
        choices.setAlignment(Pos.CENTER_LEFT);
        choices.setId("NC");
        Text numChoicesNeeded = new Text("Number of Choices needed for this proposition:");
        numChoicesNeeded.setStyle(cFfB);
        ComboBox<Integer> userNumChoices = new ComboBox<>();
        userNumChoices.setValue(1);
        userNumChoices.getItems().addAll(1,2,3,4,5);
        choices.getChildren().addAll(numChoicesNeeded, userNumChoices);

        userPropDesc.setPrefSize(350, 100);
        userPropDesc.wrapTextProperty().set(true);

        // Setup Options section
        HBox optionStuff = new HBox();
        optionStuff.setAlignment(Pos.CENTER_LEFT);
        Text addNumOptions = new Text("Add 1-5 Options: ");
        addNumOptions.setStyle(cFfB);
        Button addOptions = new Button("Add");
        ComboBox<Integer> numOptions = new ComboBox<>();
        numOptions.getItems().addAll(1,2,3,4,5);
        numOptions.setValue(1);

        // Add option menu stuff to the respective HBox
        optionStuff.getChildren().addAll(addNumOptions, numOptions, addOptions);

        // Add num of requested options
        addOptions.setOnMouseClicked(event -> {
            for (int i = 0; i < numOptions.getValue(); i++) {
                propStuff.getChildren().add(makeOptions(i));
            }
            optionStuff.getChildren().clear();
        });

        // Add children to their respective Boxes
        propInfo.getChildren().addAll(propName, userPropName, propDesc, userPropDesc);
        propStuff.getChildren().addAll(propNum, propInfo, choices, optionStuff);
        rootMenu.getChildren().add(propStuff);
    }

    /**
     * makeNewProp() is a method that makes a new proposition.
     * @param index    - The index of the proposition as it is seen
     *                   on the menu.
     * @param rootMenu - The origin/root menu to add to.
     */
    private void removeProp(int index, VBox rootMenu) {
        // Remove proposition at the given index.
        // - 1 to index for valid indexing in Java
        rootMenu.getChildren().remove(index - 1);

        int i = 1;
        // Re-adjust propositions to accomodate for removed proposition.
        for (Node propBox : rootMenu.getChildren()) {
            if (propBox instanceof VBox) {
                for (Node text : ((VBox) propBox).getChildren()) {
                    if (text.getId() != null && text.getId().equals("PT")) {
                        ((Text) text).setText("Proposition " + i);
                    }
                }
            }
            i++;
        }
    }

    /**
     * makeOptions() is a method which makes the number of requested options
     * for the proposition.
     * @param optNum - Number of the option to be added.
     * @return HBox of the options (Option n: (TextField)
     */
    private HBox makeOptions(int optNum) {
        // Setup HBox and ID for parsing
        HBox optDetails = new HBox();
        optDetails.setId("OD");
        optDetails.setAlignment(Pos.CENTER_LEFT);

        // Provide the user the prompt and a TextField
        // Can be left blank for now, as no remove option button
        // has been incorporated as of this version.
        Text optionNum = new Text("Option " + (optNum + 1) + " : ");
        optionNum.setStyle(consolasFont);
        TextField optionInfo = new TextField();
        optionInfo.setPromptText("Enter Option Here (Can leave blank)");
        optionInfo.setId("OI");
        optionInfo.setPrefSize(600, 30);

        // Add to the respective HBox
        optDetails.getChildren().addAll(optionNum, optionInfo);
        return optDetails;
    }

    /**
     * confirm() is a function for the Confirm Button at the top left of the screen.
     * confirm() grabs all the values input by the user, validates specific fields,
     * and then generates a Ballot object to be made into a .XML file.
     */
    private void confirm() {

        // Initialize values for parsing
        String fileName = "";
        String elecName = "";
        LocalDate startDate = null;
        LocalDate endDate = null;
        LocalTime startTime = null;
        LocalTime endTime = null;

        // Setup formatting for the desired Text Fields
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        // Initialize the ArrayList to hold propositions
        ArrayList<String> propTitles = new ArrayList<>();
        ArrayList<String> propDescrs = new ArrayList<>();

        // Initialzie the ArryList to hold the int of Number of options you are allowed
        // to vote for
        ArrayList<Integer> numChoices = new ArrayList<>();

        // Initialize checker values
        boolean startDateCheck = false;
        boolean endDateCheck   = false;
        boolean startTimeCheck = false;
        boolean endTimeCheck   = false;

        // Initialize ArrayList of the Arraylist of the Proposition Options.
        ArrayList<ArrayList<Option>> propOptions = new ArrayList<>();

        for (Node child : this.setupMenu.getChildren()) {

            // Grab the Initial FileName, Election Name,
            // Start and End Date, and Start and End Times.
            if (child instanceof HBox) {
                for (Node userIn : ((HBox) child).getChildren()) {
                    if (userIn instanceof VBox) {
                        for (Node timeVals: ((VBox) userIn).getChildren()) {
                            if (timeVals instanceof TextField && timeVals.getId() != null) {
                                boolean check = checkVal(((TextField) timeVals).getText(), timeVals.getId());
                                if (check) {
                                    if (timeVals.getId().equals("FN")) {
                                        fileName = ((TextField) timeVals).getText();
                                    }
                                    if (timeVals.getId().equals("EN")) {
                                        elecName = ((TextField) timeVals).getText();
                                    }
                                    if (timeVals.getId().equals("SD")) {
                                        startDate = LocalDate.parse(((TextField) timeVals).getText(), dateFormatter);
                                        startDateCheck = true;
                                    }
                                    if (timeVals.getId().equals("ED")) {
                                        endDate = LocalDate.parse(((TextField) timeVals).getText(), dateFormatter);
                                        endDateCheck = true;
                                    }
                                    if (timeVals.getId().equals("ST")) {
                                        startTime = LocalTime.parse(((TextField) timeVals).getText(), timeFormatter);
                                        startTimeCheck = true;
                                    }
                                    if (timeVals.getId().equals("ET")) {
                                        endTime = LocalTime.parse(((TextField) timeVals).getText(), timeFormatter);
                                        endTimeCheck = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (child instanceof VBox) {

                // System.out.println("in propMenu");
                // Grab Proposition Information
                for(Node props : ((VBox) child).getChildren()) {
                    if (props instanceof VBox) {

                        // System.out.println("in Propositions");
                        // Grab Proposition information and details.
                        // Initialize options ArrayList to add to the ArrayList of All options.
                        ArrayList<Option> optArray = new ArrayList<>();

                        for (Node propInfo : ((VBox) props).getChildren()) {

                            if (propInfo instanceof VBox &&
                                    propInfo.getId() != null &&
                                    propInfo.getId().equals("PI")) {


                                // System.out.println("In user desc");
                                // Get user propositions and descriptions.
                                for (Node userIn : ((VBox) propInfo).getChildren()) {
                                    if (userIn.getId() != null && userIn.getId().equals("PN")) {
                                        if (userIn instanceof TextField) {
                                            propTitles.add(((TextField) userIn).getText());
                                        }
                                    }
                                    if (userIn.getId() != null && userIn.getId().equals("PD")) {
                                        if (userIn instanceof TextArea) {
                                            propDescrs.add(((TextArea) userIn).getText());
                                        }
                                    }
                                }
                            }

                            else if (    propInfo instanceof HBox &&
                                    propInfo.getId() != null &&
                                    propInfo.getId().equals("OD")) {
                                for (Node options : ((HBox) propInfo).getChildren()) {


                                    if (options instanceof TextField) {
                                        if (options.getId() != null && options.getId().equals("OI")) {

                                            // System.out.println("In Options");
                                            // Get the options for the provided proposition
                                            // and add to the overall Options ArrayList
                                            optArray.add(new Option(((TextField) options).getText(), false));
                                        }
                                    }
                                }
                            }

                            else if (    propInfo instanceof HBox &&
                                    propInfo.getId() != null &&
                                    propInfo.getId().equals("NC")) {
                                for (Node choices : ((HBox) propInfo).getChildren()) {
                                    if (choices instanceof ComboBox<?>) {
                                        // Grab NUmChoices to add to numChoices ArrayList
                                        numChoices.add((Integer) ((ComboBox<?>) choices).getValue());
                                    }
                                }

                            }

                        }

                        // Add the resulting options to the overall ArrayList.
                        propOptions.add(optArray);
                    }
                }
            }

        }

        // Debug print
        // System.out.println(startDateCheck + " " + endDateCheck + " " + startTimeCheck + " " + endTimeCheck);

        // Collect all propositions and add them to a record.
        // Iteratively make a new record for each.
        // Each index is a new proposition with its respective information.
        ArrayList<Proposition> propositions = new ArrayList<>();
        for(int i = 0; i < propTitles.size(); i++){
            propositions.add(new Proposition(propTitles.get(i), propDescrs.get(i), numChoices.get(i), propOptions.get(i)));
        }

        // Visual error checking when confirming to check for valid start date and end date.
        showError(startDateCheck, endDateCheck, startTimeCheck, endTimeCheck);

        // If all checks pass, generate Ballot
        if (startDateCheck && endDateCheck && startTimeCheck && endTimeCheck) {
            Ballot ballot = new Ballot(elecName, startDate, endDate, startTime, endTime, propositions);
            generateXML(fileName, ballot);
        }
        // Else, notify user of error.
        else {
            this.fileGenerated.setText("File not generated. Your input has been rejected. " +
                    "It does not follow the required format.");
        }


//        Uncomment if debugging
        System.out.println("file name  : " + fileName);
        System.out.println("elec name  : " + elecName);
        System.out.println("start date : " + startDate);
        System.out.println("end date   : " + endDate);
        System.out.println("start time : " + startTime);
        System.out.println("end time   : " + endTime);
        System.out.println(propTitles);
        System.out.println(propDescrs);
        System.out.println(numChoices);
        for (ArrayList<Option> opts : propOptions) {
            System.out.print("Options: ");
            for (Option opt : opts) {
                System.out.print('|');
                System.out.print(opt.description());
                System.out.print(", Selected: " + opt.isSelected()); // Include selection state
                System.out.print('|');
            }
            System.out.println();
        }

    }

    /**
     * showError() is a function that visually notifies
     * the user and shows where their incorrect input is.
     * @param sDC - start date check
     * @param eDC - end date check
     * @param sTC - start time check
     * @param eTC - end time check
     */
    private void showError(boolean sDC, boolean eDC, boolean sTC, boolean eTC) {
        Node startDateNode = this.setupMenu.lookup("#SD");
        Node endDateNode = this.setupMenu.lookup("#ED");
        Node startTimeNode = this.setupMenu.lookup("#ST");
        Node endTimeNode = this.setupMenu.lookup("#ET");

        if (!sDC) {
            startDateNode.setStyle(redBorder);
        } else {
            startDateNode.setStyle("");
        }

        if (!eDC) {
            endDateNode.setStyle(redBorder);
        } else {
            endDateNode.setStyle("");
        }

        if (!sTC) {
            startTimeNode.setStyle(redBorder);
        } else {
            startTimeNode.setStyle("");
        }

        if (!eTC) {
            endTimeNode.setStyle(redBorder);
        } else {
            endTimeNode.setStyle("");
        }
    }


    /**
     * checkVal() is a method which checks the value of the given user input,
     * and the provided ID of that input for ease of value validation.
     * This is only really meant for time and date checking.
     * @param value - String value to be validated.
     * @param id    - String ID of the value to be checked.
     * @return true or false depending on if check passed or failed
     */
    private boolean checkVal(String value, String id) {
        // Implement your validation logic based on the id
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        try {
            switch (id) {
                case "SD", "ED" -> {
                    if (!value.isEmpty()) {
                        LocalDate.parse(value, dateFormatter);
                        return true;
                    }
                    return false;
                }
                case "ST", "ET" -> {
                    if (!value.isEmpty()) {
                        LocalTime.parse(value, timeFormatter);
                        return true;
                    }
                    return false;
                }
                case "FN", "EN" -> {
                    return true;
                }
                default -> {
                    return false;
                }
            }
        } catch (DateTimeParseException e) {
            return false; // Return false if parsing fails
        }
    }

    private void generateXML(String fileName, Ballot ballot) {
        //generate XML file is higher priority
        // generate XML file get information from confirm
        try{
            // File Path
            String filePath = fileName + ".xml";
            File xmlFile = new File(filePath);

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();
            Document doc;

            // Parse existing XML file if it exists
            if (xmlFile.exists()){
                doc  = docBuilder.parse(xmlFile);
                doc.getDocumentElement().normalize();

                // Get the root element
                Element rootElement = doc.getDocumentElement();

                // Append new elements to the existing document
                addBallotData(doc, rootElement, ballot);
            }
            // ekse if XML file does not exist
            else {
                // Create a new XML file
                doc = docBuilder.newDocument();

                // Get root element
                Element rootElement = doc.createElement("Voting-Machine");
                doc.appendChild(rootElement);

                addBallotData(doc, rootElement, ballot);
            }

            // Writing content to XML file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(filePath));

            // output to the consol for testing purposes

            transformer.transform(source, result);

            this.fileGenerated.setText("XML file generated successfully");

            // Create new XML document
        } catch (ParserConfigurationException | IOException | TransformerException | SAXException e) {
            throw new RuntimeException(e);
        }
    }


    // Send it the document, the first element, and a ballot with all the information
    private static void addBallotData(Document doc, Element rootElement, Ballot ballot) {
        Element electionNameElement = doc.createElement("electionName");
        electionNameElement.appendChild(doc.createTextNode(ballot.electionName()));
        rootElement.appendChild(electionNameElement);

        Element startDateElement = doc.createElement("startDate");
        startDateElement.appendChild(doc.createTextNode(String.valueOf(ballot.startDate())));
        rootElement.appendChild(startDateElement);

        Element endDateElement = doc.createElement("endDate");
        endDateElement.appendChild(doc.createTextNode(String.valueOf(ballot.endDate())));
        rootElement.appendChild(endDateElement);

        Element startForDayElement = doc.createElement("startForDay");
        startForDayElement.appendChild(doc.createTextNode(String.valueOf(ballot.startForDay())));
        rootElement.appendChild(startForDayElement);

        Element endForDayElement = doc.createElement("endForDay");
        endForDayElement.appendChild(doc.createTextNode(String.valueOf(ballot.endForDay())));
        rootElement.appendChild(endForDayElement);

        for (Proposition proposition : ballot.propositions()) {
            Element propositionElement = doc.createElement("proposition");

            Element propNameElement = doc.createElement("propName");
            propNameElement.appendChild(doc.createTextNode(proposition.propName()));
            propositionElement.appendChild(propNameElement);

            Element propDescElement = doc.createElement("propDesc");
            propDescElement.appendChild(doc.createTextNode(proposition.propDesc()));
            propositionElement.appendChild(propDescElement);

            for (Option option : proposition.options()) {
                Element optionElement = doc.createElement("option");
                optionElement.appendChild(doc.createTextNode(option.description()));
                propositionElement.appendChild(optionElement);

                // Add optionSelection element
                Element optionSelectionElement = doc.createElement("optionSelection");
                optionSelectionElement.appendChild(doc.createTextNode("false")); // Initial value
                propositionElement.appendChild(optionSelectionElement);
            }

            Element numChoices = doc.createElement("numChoices");
            numChoices.appendChild(doc.createTextNode(String.valueOf(proposition.selectableOptions())));
            propositionElement.appendChild(numChoices);

            rootElement.appendChild(propositionElement);

        }
    }

    /**
     * getMenus() gets the menu to display in GUI.java
     * @return menu to show.
     */
    public BorderPane getMenus() {
        return this.screen;
    }
}
