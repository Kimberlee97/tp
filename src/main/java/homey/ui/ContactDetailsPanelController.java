package homey.ui;

import java.util.logging.Logger;
import java.util.stream.Collectors;

import homey.commons.core.LogsCenter;
import homey.model.person.Person;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * Controller for the Contact Details Panel
 */
public class ContactDetailsPanelController {

    private static final Logger logger = LogsCenter.getLogger(ContactDetailsPanelController.class);

    @FXML
    private Button editButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Label contactNameLabel;

    @FXML
    private Label phoneLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label addressLabel;

    @FXML
    private Label tagsLabel;

    @FXML
    private Label remarkLabel;

    /**
     * Initialises the contact details panel
     */
    @FXML
    public void initialize() {
        logger.info("Initialising ContactDetailsPanel");
    }

    @FXML
    private void handleEdit() {
        logger.info("Edit button clicked");

    }

    @FXML
    private void handleDelete() {
        logger.info("Delete button clicked");
    }

    @FXML
    public void setContact(Person person) {
        contactNameLabel.setText(person.getName().fullName);
        phoneLabel.setText("Phone: " + person.getPhone().value);
        emailLabel.setText("Email: " + person.getEmail().value);
        addressLabel.setText("Address: " + person.getAddress().value);

        String tagsText = person.getTags().stream()
                .map(tag -> tag.tagName)
                .collect(Collectors.joining(", "));
        tagsLabel.setText("Tags: " + tagsText);
        // remark label to be added later when implementation is finished
    }

    @FXML
    public void clearContact() {
        contactNameLabel.setText("");
        phoneLabel.setText("");
        emailLabel.setText("");
        addressLabel.setText("");
        tagsLabel.setText("");
        // remark label to be added later when implementation is finished
    }
}
