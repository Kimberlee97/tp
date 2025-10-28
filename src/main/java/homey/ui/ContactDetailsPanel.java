package homey.ui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import homey.commons.core.LogsCenter;
import homey.model.person.Meeting;
import homey.model.person.Person;
import homey.model.tag.Tag;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Panel that displays the details of a selected contact, including
 * name, phone, email, address, relation, stage, tags, and meetings.
 */
public class ContactDetailsPanel extends UiPart<Region> {
    private static final String FXML = "ContactDetailsPanel.fxml";
    private static final Logger logger = LogsCenter.getLogger(ContactDetailsPanel.class);
    private static final String ZWSP = "\u200B";
    private static final double AVG_CHAR_PX = 7.0;
    private static final double STANDARD_PADDING = 40.0;
    private Map<Label, String> originalLabelTexts;
    private List<Label> wrappableLabels;

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

    @FXML
    private VBox contactDetailsVBox;

    @FXML
    private ScrollPane contactDetailsScroll;

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
        setUpLayout();
        setUpWrappableLabels();
        bindTagsWrapping();
    }

    private void setUpLayout() {
        originalLabelTexts = new HashMap<>();
        wrappableLabels = new ArrayList<>();
    }

    private void setUpWrappableLabels() {
        registerWrappableLabel(contactNameLabel);
        registerWrappableLabel(addressLabel);
        registerWrappableLabel(emailLabel);
        registerWrappableLabel(remarkLabel);
        registerWrappableLabel(meetingLabel);

        contactDetailsScroll.viewportBoundsProperty().addListener((obs, oldB, newB) -> {
            for (Label label: wrappableLabels) {
                rewrapLabel(label);
            }
        });
    }

    private void registerWrappableLabel(Label label) {
        label.setWrapText(true);
        label.setMinWidth(0);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setMinHeight(Region.USE_PREF_SIZE);

        originalLabelTexts.put(label, "");
        wrappableLabels.add(label);

        label.widthProperty().addListener((obs, oldW, newW) -> {
            rewrapLabel(label);
        });
    }

    private void rewrapLabel(Label label) {
        String original = originalLabelTexts.getOrDefault(label, "");
        Platform.runLater(() -> label.setText(makeWrappable(label, original)));
    }

    private void bindTagsWrapping() {
        tagsFlowPane.setMaxWidth(Double.MAX_VALUE);
        contactDetailsScroll.viewportBoundsProperty().addListener((obs, oldB, newB) -> {
            double w = Math.max(0, newB.getWidth() - STANDARD_PADDING);
            tagsFlowPane.setPrefWrapLength(w);
        });
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
        displayRemarks(person);
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

    private void displayRemarks(Person person) {
        String remark = person.getRemark().value;
        if (remark == null || remark.trim().isEmpty()) {
            hideRemarkSection();
        } else {
            addRemarkLabel(person);
        }
    }

    private void hideRemarkSection() {
        remarkLabel.setText("");
        setRemarkVisibility(false);
    }

    private void addRemarkLabel(Person person) {
        setOriginalTextForLabel(remarkLabel, "Remarks: " + person.getRemark().value);
        // remarkLabel.setText("Remarks: " + makeWrappable(remarkLabel, person.getRemark().value));
        setRemarkVisibility(true);
    }

    private void setRemarkVisibility(boolean visible) {
        remarkLabel.setManaged(visible);
        remarkLabel.setVisible(visible);
    }

    private void displayTags(Person person) {
        tagsFlowPane.getChildren().clear();

        if (person.getTags().isEmpty()) {
            logger.fine("No tags found for " + person.getName().fullName);
            setTagsVisibility(false);
            return;
        }

        logger.fine("Displaying " + person.getTags().size() + " tags for " + person.getName().fullName);
        addTagsLabel();
        addTagElements(person.getTags());
        setTagsVisibility(true);
    }

    private void setTagsVisibility(boolean visible) {
        tagsFlowPane.setManaged(visible);
        tagsFlowPane.setVisible(visible);
    }

    private void addTagsLabel() {
        Label tagsLabel = new Label("Tags:");
        tagsLabel.getStyleClass().add("plain-tag-label");
        tagsFlowPane.getChildren().add(tagsLabel);
    }

    private void addTagElements(Set<Tag> tags) {
        styleTagsPane();
        List<Tag> sortedTags = getSortedTags(tags);
        sortedTags.forEach(this::addTagLabel);
    }

    private void styleTagsPane() {
        tagsFlowPane.getStyleClass().add("contact-tags");
    }

    private List<Tag> getSortedTags(Set<Tag> tags) {
        return tags.stream()
                .sorted(Comparator.comparing(tag -> tag.tagName))
                .toList();
    }

    private void addTagLabel(Tag tag) {
        Label tagLabel = new Label(tag.tagName);
        tagsFlowPane.getChildren().add(tagLabel);
    }

    private void displayMeeting(Person person) {
        person.getMeeting().ifPresentOrElse(
                this::showMeeting,
                this::hideMeeting
        );
    }

    private void showMeeting(Meeting meeting) {
        setOriginalTextForLabel(meetingLabel, "Meeting: " + meeting.toDisplayString());
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
        Platform.runLater(() -> {
            setOriginalTextForLabel(contactNameLabel, person.getName().fullName);
            setLabelText(phoneLabel, "Phone: " + person.getPhone().value);
            setOriginalTextForLabel(addressLabel, "Address: " + person.getAddress().value);
            setOriginalTextForLabel(emailLabel, "Email: " + person.getEmail().value);
            setLabelText(relationLabel, "Relation: " + person.getRelation().value);
            setLabelText(stageLabel, "Stage: " + person.getStage().value);
        });
    }

    private void setOriginalTextForLabel(Label label, String original) {
        originalLabelTexts.put(label, original == null ? "" : original);
        rewrapLabel(label);
    }

    private void setLabelText(Label label, String text) {
        label.setText(text);
    }

    private void clearAllBasicFields() {
        contactNameLabel.setText("");
        phoneLabel.setText("");
        addressLabel.setText("");
        emailLabel.setText("");
        relationLabel.setText("");
        stageLabel.setText("");
    }

    private void clearTags() {
        tagsFlowPane.getChildren().clear();
    }

    private void hidePanel() {
        getRoot().setVisible(false);
        getRoot().setManaged(false);
    }

    private String makeWrappable(Label label, String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        int maxCharsPerLine = calculateMaxChars();
        return insertBlankPoints(text, maxCharsPerLine);
    }

    private int calculateMaxChars() {
        double currentWidth = contactDetailsScroll.getViewportBounds().getWidth();
        currentWidth -= STANDARD_PADDING;
        return Math.max(1, (int) Math.floor(currentWidth / AVG_CHAR_PX));
    }

    private String insertBlankPoints(String text, int maxChars) {
        StringBuilder wrappedText = new StringBuilder();
        int charactersSinceLastBreak = 0;
        for (char currentChar : text.toCharArray()) {
            wrappedText.append(currentChar);

            if (Character.isWhitespace(currentChar)) {
                charactersSinceLastBreak = 0;
            } else {
                charactersSinceLastBreak++;
                if (shouldInsertBreakPoint(charactersSinceLastBreak, maxChars)) {
                    wrappedText.append(ZWSP);
                    charactersSinceLastBreak = 0;
                }
            }
        }
        return wrappedText.toString();
    }

    private boolean shouldInsertBreakPoint(int consecutiveChars, int maxChars) {
        return consecutiveChars >= maxChars;
    }
}
