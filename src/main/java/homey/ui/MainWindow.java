package homey.ui;

import java.util.logging.Logger;

import homey.commons.core.GuiSettings;
import homey.commons.core.LogsCenter;
import homey.logic.Logic;
import homey.logic.commands.CommandResult;
import homey.logic.commands.exceptions.CommandException;
import homey.logic.parser.exceptions.ParseException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * The Main Window. Provides the basic application layout containing
 * a menu bar and space where other JavaFX elements can be placed.
 */
public class MainWindow extends UiPart<Stage> {

    private static final String FXML = "MainWindow.fxml";

    private final Logger logger = LogsCenter.getLogger(getClass());

    private Stage primaryStage;
    private Logic logic;

    // Independent Ui parts residing in this Ui container
    private PersonListPanel personListPanel;
    private ResultDisplay resultDisplay;
    private HelpWindow helpWindow;
    private NavigationPanel navigationPanel;
    private ContactDetailsPanel contactDetailsPanel;

    @FXML
    private StackPane commandBoxPlaceholder;

    @FXML
    private MenuItem helpMenuItem;

    @FXML
    private StackPane personListPanelPlaceholder;

    @FXML
    private StackPane resultDisplayPlaceholder;

    @FXML
    private StackPane statusbarPlaceholder;

    @FXML
    private VBox navigationPanelPlaceholder;

    @FXML
    private VBox contactDetailsPanelPlaceholder;

    /**
     * Creates a {@code MainWindow} with the given {@code Stage} and {@code Logic}.
     */
    public MainWindow(Stage primaryStage, Logic logic) {
        super(FXML, primaryStage);

        // Set dependencies
        this.primaryStage = primaryStage;
        this.logic = logic;

        // Configure the UI
        setWindowDefaultSize(logic.getGuiSettings());

        setAccelerators();

        helpWindow = new HelpWindow();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    private void setAccelerators() {
        setAccelerator(helpMenuItem, KeyCombination.valueOf("F1"));
    }

    /**
     * Sets the accelerator of a MenuItem.
     * @param keyCombination the KeyCombination value of the accelerator
     */
    private void setAccelerator(MenuItem menuItem, KeyCombination keyCombination) {
        menuItem.setAccelerator(keyCombination);
        addAcceleratorEventFilter(menuItem, keyCombination);
    }

    private void addAcceleratorEventFilter(MenuItem menuItem, KeyCombination keyCombination) {
        /*
         * TODO: the code below can be removed once the bug reported here
         * https://bugs.openjdk.java.net/browse/JDK-8131666
         * is fixed in later version of SDK.
         *
         * According to the bug report, TextInputControl (TextField, TextArea) will
         * consume function-key events. Because CommandBox contains a TextField, and
         * ResultDisplay contains a TextArea, thus some accelerators (e.g F1) will
         * not work when the focus is in them because the key event is consumed by
         * the TextInputControl(s).
         *
         * For now, we add following event filter to capture such key events and open
         * help window purposely so to support accelerators even when focus is
         * in CommandBox or ResultDisplay.
         */
        getRoot().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (shouldTriggerAccelerator(event, keyCombination)) {
                triggerMenuItem(menuItem);
                event.consume();
            }
        });
    }

    private boolean shouldTriggerAccelerator(KeyEvent event, KeyCombination keyCombination) {
        return event.getTarget() instanceof TextInputControl && keyCombination.match(event);
    }

    private void triggerMenuItem(MenuItem menuItem) {
        menuItem.getOnAction().handle(new ActionEvent());
    }

    /**
     * Fills up all the placeholders of this window.
     */
    void fillInnerParts() {
        setUpPersonListPanel();
        setUpResultDisplay();
        setUpStatusBar();
        setUpCommandBox();
        setUpNavigationPanel();
        setUpContactDetailsPanel();
        setUpPersonSelectionListener();
    }

    private void setUpPersonListPanel() {
        personListPanel = new PersonListPanel(logic.getFilteredPersonList());
        personListPanelPlaceholder.getChildren().add(personListPanel.getRoot());
    }

    private void setUpResultDisplay() {
        resultDisplay = new ResultDisplay();
        resultDisplayPlaceholder.getChildren().add(resultDisplay.getRoot());
    }

    private void setUpStatusBar() {
        StatusBarFooter statusBarFooter = new StatusBarFooter(logic.getAddressBookFilePath());
        statusbarPlaceholder.getChildren().add(statusBarFooter.getRoot());
    }

    private void setUpCommandBox() {
        CommandBox commandBox = new CommandBox(this::executeCommand);
        commandBoxPlaceholder.getChildren().add(commandBox.getRoot());
    }

    private void setUpNavigationPanel() {
        navigationPanel = new NavigationPanel();
        navigationPanelPlaceholder.getChildren().add(navigationPanel.getRoot());
    }

    private void setUpContactDetailsPanel() {
        contactDetailsPanel = new ContactDetailsPanel();
        contactDetailsPanelPlaceholder.getChildren().add(contactDetailsPanel.getRoot());
    }

    private void setUpPersonSelectionListener() {
        personListPanel.setOnPersonSelected((obs, oldVal, newVal) -> {
            if (newVal != null) {
                contactDetailsPanel.setContact(newVal);
            } else {
                contactDetailsPanel.clearContact();
            }
        });
    }
    /**
     * Sets the default size based on {@code guiSettings}.
     */
    private void setWindowDefaultSize(GuiSettings guiSettings) {
        primaryStage.setHeight(guiSettings.getWindowHeight());
        primaryStage.setWidth(guiSettings.getWindowWidth());
        if (guiSettings.getWindowCoordinates() != null) {
            primaryStage.setX(guiSettings.getWindowCoordinates().getX());
            primaryStage.setY(guiSettings.getWindowCoordinates().getY());
        }
    }

    /**
     * Opens the User Guide in the default web browser when Help is clicked or F1 is pressed.
     * If the browser cannot be opened, shows the help window instead.
     */
    @FXML
    public void handleHelp() {
        helpWindow.openInBrowserOrShow();
    }

    void show() {
        primaryStage.show();
    }

    /**
     * Closes the application.
     */
    @FXML
    private void handleExit() {
        GuiSettings guiSettings = createCurrentGuiSettings();
        logic.setGuiSettings(guiSettings);
        closeAllWindows();
    }

    private GuiSettings createCurrentGuiSettings() {
        return new GuiSettings(primaryStage.getWidth(),
                primaryStage.getHeight(),
                (int) primaryStage.getX(),
                (int) primaryStage.getY()
        );
    }

    private void closeAllWindows() {
        helpWindow.hide();
        primaryStage.hide();
    }

    public PersonListPanel getPersonListPanel() {
        return personListPanel;
    }

    /**
     * Executes the command and returns the result.
     *
     * @see Logic#execute(String)
     */
    private CommandResult executeCommand(String commandText) throws CommandException, ParseException {
        try {
            CommandResult commandResult = logic.execute(commandText);
            handleSuccessfulCommand(commandResult);
            return commandResult;
        } catch (CommandException | ParseException e) {
            handleFailedCommand(commandText, e);
            throw e;
        }
    }

    private void handleSuccessfulCommand(CommandResult commandResult) {
        logCommandResult(commandResult);
        displayCommandResult(commandResult);
        processCommandActions(commandResult);
    }

    private void handleFailedCommand(String commandText, Exception e) {
        logCommandError(commandText);
        displayErrorMessage(e);
    }

    private void logCommandError(String commandText) {
        logger.info("An error occurred while executing command: " + commandText);
    }

    private void displayErrorMessage(Exception e) {
        resultDisplay.setFeedbackToUser(e.getMessage());
    }

    private void logCommandResult(CommandResult commandResult) {
        logger.info("Result: " + commandResult.getFeedbackToUser());
    }

    private void displayCommandResult(CommandResult commandResult) {
        resultDisplay.setFeedbackToUser(commandResult.getFeedbackToUser());
    }

    private void processCommandActions(CommandResult commandResult) {
        if (commandResult.isShowHelp()) {
            handleHelpCommand(commandResult);
        }

        if (commandResult.isExit()) {
            handleExit();
        }
    }

    private void handleHelpCommand(CommandResult commandResult) {
        commandResult.getHelpTopic().ifPresentOrElse(
                topic -> helpWindow.openInBrowserOrShow(topic), () -> helpWindow.openInBrowserOrShow()
        );
    }
}
