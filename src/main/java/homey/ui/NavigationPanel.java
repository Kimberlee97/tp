package homey.ui;

import java.util.logging.Logger;

import homey.commons.core.LogsCenter;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;

public class NavigationPanel extends UiPart<Region> {
    private static final String FXML = "NavigationPanel.fxml";
    private static final Logger logger = LogsCenter.getLogger(NavigationPanelController.class);

    @FXML
    private Button dashboardButton;

    @FXML
    private Button contactsButton;

    @FXML
    private Button tasksButton;

    @FXML
    private Button settingsButton;


    public NavigationPanel() {
        super(FXML);
        //set contacts as the default selected button
        selectButton(contactsButton);
    }

    @FXML
    private void handleDashboard() {
        logger.info("Dashboard navigation clicked");
        selectButton(dashboardButton);
        // TODO: Implement dashboard view switching
    }

    @FXML
    private void handleContacts() {
        logger.info("Contacts navigation clicked");
        selectButton(contactsButton);
        // TODO: Implement contacts view switching
    }

    @FXML
    private void handleTasks() {
        logger.info("Tasks navigation clicked");
        selectButton(tasksButton);
        // TODO: Implement tasks view switching
    }

    @FXML
    private void handleSettings() {
        logger.info("Settings navigation clicked");
        selectButton(settingsButton);
        // TODO: Implement settings view switching
    }

    /**
     * Selects a navigation button and deselects others
     */
    private void selectButton(Button selectedButton) {
        // Remove selected style from all buttons
        dashboardButton.getStyleClass().remove("navigation-button-selected");
        contactsButton.getStyleClass().remove("navigation-button-selected");
        tasksButton.getStyleClass().remove("navigation-button-selected");
        settingsButton.getStyleClass().remove("navigation-button-selected");

        // Add selected style to the clicked button
        selectedButton.getStyleClass().add("navigation-button-selected");
    }
}
