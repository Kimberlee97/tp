package homey.ui;

import java.util.Map;
import java.util.function.Function;
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
    private static final Map<String, String> ANCHORS = Map.ofEntries(
            Map.entry("add", "#adding-a-person-add"),
            Map.entry("edit", "#editing-a-person-edit"),
            Map.entry("delete", "#deleting-a-person-delete"),
            Map.entry("find", "#locating-persons-by-name-find"),
            Map.entry("list", "#listing-all-persons-list"),
            Map.entry("help", "#viewing-help-help-topic"),
            Map.entry("clear", "#clearing-all-entries-clear"),
            Map.entry("exit", "#exiting-the-program-exit"),
            Map.entry("find a/", "#locating-persons-by-address-find-a"),
            Map.entry("find t/", "#locating-persons-by-tag-find-t"),
            Map.entry("relation", "#add-relational-tag-relation"),
            Map.entry("transaction", "#changing-the-transaction-stage")
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
        openInBrowserOrShow(null);
    }

    /**
     * Opens the User Guide at a specific section if a topic is given.
     * Falls back to showing this help window (with the link) if the browser can't be opened.
     *
     * @param topic Command/topic to open (e.g. "add", "edit"). If null or unknown, opens the UG root.
     */
    public void openInBrowserOrShow(String topic) {
        openInBrowserOrShow(topic, BrowserUtil::open, this::show);
    }

    /**
     * Pure helper for opening the User Guide that is unit-testable without JavaFX.
     * Builds the URL, tries to open via {@code opener}, and runs {@code fallback} if that fails.
     *
     * @param topic Command/topic to open (e.g., "add"); may be null.
     * @param opener Function that attempts to open the URL and returns true on success.
     * @param fallback Fallback runnable to execute when opener fails (e.g., show the Help window).
     */
    static void openInBrowserOrShow(String topic,
                                    Function<String, Boolean> opener,
                                    Runnable fallback) {
        String url = buildUserGuideUrl(topic);
        boolean opened = Boolean.TRUE.equals(opener.apply(url));
        if (!opened) {
            fallback.run();
        }
    }
    /**
     * Builds the full User Guide URL for the given topic.
     * If the topic is null or not found, returns the root User Guide URL.
     *
     * @param topic command/topic like "add" or "edit"; may be null
     * @return full URL to the User Guide (with anchor if available)
     */
    static String buildUserGuideUrl(String topic) {
        String anchor;
        if (topic == null) {
            anchor = "";
        } else {
            String key = topic.trim().toLowerCase(java.util.Locale.ROOT);
            anchor = ANCHORS.getOrDefault(key, "");
        }
        return USERGUIDE_URL + anchor;
    }

    /**
     * Copies the URL to the user guide to the clipboard.
     */
    @FXML
    void copyUrl() {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent url = new ClipboardContent();
        url.putString(USERGUIDE_URL);
        clipboard.setContent(url);
    }
}
