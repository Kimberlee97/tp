package homey.ui;

import java.util.Map;
import java.util.logging.Logger;

import homey.commons.core.LogsCenter;
import homey.commons.util.BrowserUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Stage;

/**
 * Controller for a help page
 */
public class HelpWindow extends UiPart<Stage> {

    public static final String USERGUIDE_URL = "https://ay2526s1-cs2103t-f15a-4.github.io/tp/UserGuide.html";
    public static final String HELP_MESSAGE = "Refer to the user guide: " + USERGUIDE_URL;

    private static final Logger logger = LogsCenter.getLogger(HelpWindow.class);
    private static final String FXML = "HelpWindow.fxml";

    /**
     * Maps help topics (command words) to their User Guide anchor IDs.
     * Keys are what the user types (e.g. "add", "edit"), and values are the
     * fragment parts of the UG URL (e.g. "#adding-a-person-add").
     * Update this if UG headings change (copy the link icon next to a heading
     * in the UG and use the part after the '#').
     */
    private static final Map<String, String> ANCHORS = Map.of(
            "add", "#adding-a-person-add",
            "edit", "#editing-a-person-edit",
            "delete", "#deleting-a-person-delete",
            "find", "#locating-persons-by-name-find",
            "list", "#listing-all-persons-list",
            "help", "#viewing-help-help"
    );

    @FXML
    private Button copyButton;

    @FXML
    private Label helpMessage;

    /**
     * Creates a new HelpWindow.
     *
     * @param root Stage to use as the root of the HelpWindow.
     */
    public HelpWindow(Stage root) {
        super(FXML, root);
        helpMessage.setText(HELP_MESSAGE);
    }

    /**
     * Creates a new HelpWindow.
     */
    public HelpWindow() {
        this(new Stage());
    }

    /**
     * Shows the help window.
     * @throws IllegalStateException
     *     <ul>
     *         <li>
     *             if this method is called on a thread other than the JavaFX Application Thread.
     *         </li>
     *         <li>
     *             if this method is called during animation or layout processing.
     *         </li>
     *         <li>
     *             if this method is called on the primary stage.
     *         </li>
     *         <li>
     *             if {@code dialogStage} is already showing.
     *         </li>
     *     </ul>
     */
    public void show() {
        logger.fine("Showing help page about the application.");
        getRoot().show();
        getRoot().centerOnScreen();
    }

    /**
     * Returns true if the help window is currently being shown.
     */
    public boolean isShowing() {
        return getRoot().isShowing();
    }

    /**
     * Hides the help window.
     */
    public void hide() {
        getRoot().hide();
    }

    /**
     * Focuses on the help window.
     */
    public void focus() {
        getRoot().requestFocus();
    }

    /**
     * Opens the User Guide in the system's default web browser.
     * If the browser cannot be opened, shows this help window instead
     * so the user can view and copy the URL manually.
     */
    public void openInBrowserOrShow() {
        boolean opened = BrowserUtil.open(USERGUIDE_URL);
        if (!opened) {
            show();
        }
    }

    /**
     * Opens the User Guide at a specific section if a topic is given.
     * Falls back to showing this help window (with the link) if the browser can't be opened.
     *
     * @param topic Command/topic to open (e.g. "add", "edit"). If null or unknown, opens the UG root.
     */
    public void openInBrowserOrShow(String topic) {
        String anchor = (topic == null) ? "" : ANCHORS.getOrDefault(topic, "");
        String url = USERGUIDE_URL + anchor;
        boolean opened = BrowserUtil.open(url);
        if (!opened) {
            show();
        }
    }

    /**
     * Copies the URL to the user guide to the clipboard.
     */
    @FXML
    private void copyUrl() {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent url = new ClipboardContent();
        url.putString(USERGUIDE_URL);
        clipboard.setContent(url);
    }
}
