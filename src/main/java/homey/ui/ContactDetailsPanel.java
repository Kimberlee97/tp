package homey.ui;

import java.util.Comparator;
import java.util.Set;
import java.util.logging.Logger;

import homey.commons.core.LogsCenter;
import homey.model.person.Meeting;
import homey.model.person.Person;
import homey.model.tag.Tag;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;

/**
 * Panel that displays the details of a selected contact, including
 * name, phone, email, address, relation, stage, tags, and meetings.
 */
public class ContactDetailsPanel extends UiPart<Region> {
    private static final String FXML = "ContactDetailsPanel.fxml";
    private static final Logger logger = LogsCenter.getLogger(ContactDetailsPanel.class);

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

    /**
     * Constructs a {@code ContactdetailsPanel} and loads its FXMl layout
     */
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

    /**
     * Displays the details of a given {@code Person} in the panel.
     * @param person the person whose details are to be displayed; if null, clears the panel
     */
    @FXML
    public void setContact(Person person) {
        if (person == null) {
            logger.fine("No person selected - clearing contact details panel.");
            clearContact();
            return;
        }

        logger.info("Displaying contact details for: " + person.getName().fullName);
        showPanel();
        displayBasicFields(person);
        displayTags(person);
        displayMeeting(person);
    }

    private void showPanel() {
        getRoot().setVisible(true);
        getRoot().setManaged(true);
    }

    /**
     * Clears the contact details panel and hides it.
     */
    @FXML
    public void clearContact() {
        logger.fine("Clearing contact details panel.");
        clearAllBasicFields();
        clearTags();
        hideMeeting();
        hidePanel();
    }

    private void displayTags(Person person) {
        tagsFlowPane.getChildren().clear();

        if (person.getTags().isEmpty()) {
            logger.fine("No tags found for " + person.getName().fullName);
            hideTagsSection();
            return;
        }

        logger.fine("Displaying " + person.getTags().size() + " tags for " + person.getName().fullName);
        addTagsLabel();
        addTagElements(person.getTags());
        showTagsSection();
    }

    private void hideTagsSection() {
        tagsFlowPane.setManaged(false);
        tagsFlowPane.setVisible(false);
    }

    private void addTagsLabel() {
        Label tagsLabel = new Label("Tags:");
        tagsLabel.getStyleClass().add("plain-tag-label");
        tagsFlowPane.getChildren().add(tagsLabel);
    }

    private void addTagElements(Set<Tag> tags) {
        tags.stream()
                .sorted(Comparator.comparing(tag -> tag.tagName))
                .forEach(tag -> {
                    tagsFlowPane.getStyleClass().add("contact-tags");
                    Label tagLabel = new Label(tag.tagName);
                    tagsFlowPane.getChildren().add(tagLabel);
                });
    }

    private void showTagsSection() {
        tagsFlowPane.setManaged(true);
        tagsFlowPane.setVisible(true);
    }

    private void displayMeeting(Person person) {
        person.getMeeting().ifPresentOrElse(
                this::showMeeting,
                this::hideMeeting
        );
    }

    private void showMeeting(Meeting meeting) {
        meetingLabel.setText("Next meeting: " + meeting.toDisplayString());
        if (Meeting.isOverdueMeeting(meeting)) {
            meetingLabel.setStyle("-fx-text-fill: red;");
        } else {
            meetingLabel.setStyle(""); // reset style
        }
        setMeetingVisibility(true);
    }

    private void hideMeeting() {
        meetingLabel.setText("");
        setMeetingVisibility(false);
    }

    private void setMeetingVisibility(boolean visible) {
        meetingLabel.setManaged(visible);
        meetingLabel.setVisible(visible);
    }

    private void displayBasicFields(Person person) {
        contactNameLabel.setText(person.getName().fullName);
        phoneLabel.setText("Phone: " + person.getPhone().value);
        addressLabel.setText("Address: " + person.getAddress().value);
        emailLabel.setText("Email: " + person.getEmail().value);
        relationLabel.setText("Relation: " + person.getRelation().value);
        stageLabel.setText("Stage: " + person.getStage().value);
        remarkLabel.setText("Remarks: " + person.getRemark().value);
    }

    private void clearAllBasicFields() {
        contactNameLabel.setText("");
        phoneLabel.setText("");
        addressLabel.setText("");
        emailLabel.setText("");
        relationLabel.setText("");
        stageLabel.setText("");
        remarkLabel.setText("");
    }

    private void clearTags() {
        tagsFlowPane.getChildren().clear();
    }

    private void hidePanel() {
        getRoot().setVisible(false);
        getRoot().setManaged(false);
    }
}
