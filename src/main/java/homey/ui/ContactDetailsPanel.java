package homey.ui;

import java.util.Comparator;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import homey.commons.core.LogsCenter;
import homey.model.person.Person;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;

public class ContactDetailsPanel extends UiPart<Region> {
    private static final String FXML = "ContactDetailsPanel.fxml";
    private static final Logger logger = LogsCenter.getLogger(ContactDetailsPanel.class);

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
    private Label relationLabel;

    @FXML
    private Label stageLabel;

    @FXML
    private FlowPane tagsFlowPane;

    @FXML
    private Label meetingLabel;

    @FXML
    private Label remarkLabel;

    public ContactDetailsPanel() {
        super(FXML);
    }

    /**
     * Initialises the contact details panel
     */
    @FXML
    public void initialize() {
        logger.info("Initialising ContactDetailsPanel");
        clearContact();
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
        if (person == null) {
            clearContact();
            return;
        }
        // show panel when a contact is selected
        getRoot().setVisible(true);
        getRoot().setManaged(true);

        contactNameLabel.setText(person.getName().fullName);
        phoneLabel.setText("Phone: " + person.getPhone().value);
        addressLabel.setText("Address: " + person.getAddress().value);
        emailLabel.setText("Email: " + person.getEmail().value);
        relationLabel.setText("Relation: " + person.getRelation().value);
        stageLabel.setText("Stage: " + person.getStage().value);

        tagsFlowPane.getChildren().clear();

        if (!person.getTags().isEmpty()) {
            Label tagsLabel = new Label("Tags:");
            tagsFlowPane.getStyleClass().add("plain-tag-label");
            tagsFlowPane.getChildren().add(tagsLabel);

            person.getTags().stream()
                    .sorted(Comparator.comparing(tag -> tag.tagName))
                    .forEach(tag -> {
                        Label tagLabel = new Label(tag.tagName);
                        tagLabel.getStyleClass().add("contact-tags");
                        tagsFlowPane.getChildren().add(tagLabel);
                    });

            tagsFlowPane.setManaged(true);
            tagsFlowPane.setVisible(true);
        } else {
            tagsFlowPane.setManaged(false);
            tagsFlowPane.setVisible(false);
        }

        person.getMeeting().ifPresentOrElse(
                m -> {
                    meetingLabel.setText("Next meeting: " + m.toDisplayString());
                    meetingLabel.setManaged(true);
                    meetingLabel.setVisible(true);
                },
                () -> {
                    meetingLabel.setText("");
                    meetingLabel.setManaged(false);
                    meetingLabel.setVisible(false);
                }
        );
        // remark label to be added later when implementation is finished
    }

    @FXML
    public void clearContact() {
        contactNameLabel.setText("");
        phoneLabel.setText("");
        addressLabel.setText("");
        emailLabel.setText("");
        relationLabel.setText("");
        stageLabel.setText("");
        tagsFlowPane.getChildren().clear();
        meetingLabel.setText("");
        meetingLabel.setManaged(false);
        meetingLabel.setVisible(false);
        // remark label to be added later when implementation is finished

        // hide entire panel
        getRoot().setVisible(false);
        getRoot().setManaged(false);
    }
}
