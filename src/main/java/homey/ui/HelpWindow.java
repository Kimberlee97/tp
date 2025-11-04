package homey.ui;

import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;

import homey.commons.core.LogsCenter;
import homey.commons.util.BrowserUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Stage;

/**
 * Controller for a help page
 */
public class HelpWindow extends UiPart<Stage> {

    public static final String USERGUIDE_URL = "https://ay2526s1-cs2103t-f15a-4.github.io/tp/UserGuide.html";
    public static final String HELP_MESSAGE = "Refer to the user guide: " + USERGUIDE_URL;

    private static final String QUICK_REF = """
        Commands:
            \n1. help [topic | offline]
                - Opens the User Guide in browser, or shows this offline summary if 'offline' is used.
            \n2. add n/NAME p/PHONE e/EMAIL a/ADDRESS s/TRANSACTION_STAGE [rm/REMARK]
            [r/RELATION] [t/TAG] [m/MEETING]...
                - Adds a new contact. All fields except tags, remark, relation, and meeting are required.
                - TRANSACTION_STAGE contains {prospect, negotiating, closed}. RELATION contains {client,
                  vendor}.
            \n3. edit INDEX [n/...][p/...][e/...][a/...][s/...][rm/...][r/...][t/...][m/...]
                - Edits details of the contact at INDEX. Use m/ or t/ with no value to clear them.
            \n4. delete INDEX
                - Deletes the contact at INDEX from the list.
            \n5. find KEYWORD... | a/KEY... | t/KEY... | r/RELATION | s/TRANSACTION_STAGE
                - Finds contacts by name, address, tag, relation, or transaction stage.
                - Relation accepts {client, vendor}. Stage accepts {prospect, negotiating, closed}.
            \n6. relation INDEX r/(client | vendor)
                - Updates the relation tag of the contact at INDEX.
            \n7. transaction INDEX s/(prospect | negotiating | closed)
                - Updates the transaction stage of the contact at INDEX.
            \n8. list [meeting | archive | active]
                - Shows all contacts.
                - 'list meeting' – upcoming meetings sorted by date.
                - 'list archive' – archived contacts.
                - 'list active' – unarchived contacts.
            \n9. remark INDEX rm/TEXT
                - Adds or edits a remark. Leave rm/ empty to remove it.
            \n10. archive INDEX
                - Moves the contact at INDEX to the archived list (hidden from active view).
            \n11. unarchive INDEX
                - Restores an archived contact back to the active list.
            \n12. clear
                - Clears all contacts from the address book.
            \n13. exit
                - Exits the application.
        \n\nHelp topics:
            - add, edit, delete, find, find a/, find t/, find r/, find s/,
              relation, transaction, list, list meeting, list archive, list active, remark,
              archive, unarchive, clear, exit, help
        """;

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
            Map.entry("add", "#adding-a-contact-add"),
            Map.entry("edit", "#editing-your-contacts"),
            Map.entry("delete", "#deleting-a-contact-delete"),
            Map.entry("find", "#finding-your-contacts"),
            Map.entry("list", "#listing-your-contacts"),
            Map.entry("help", "#viewing-help"),
            Map.entry("clear", "#clearing-all-entries-clear"),
            Map.entry("exit", "#exiting-the-program-exit"),
            Map.entry("find a/", "#find-by-address-find-a"),
            Map.entry("find t/", "#find-by-tag-find-t"),
            Map.entry("find r/", "#find-by-relation-find-r"),
            Map.entry("find s/", "#find-by-transaction-stage-find-s"),
            Map.entry("relation", "#adding-a-relation-relation"),
            Map.entry("transaction", "#tracking-deal-progress"),
            Map.entry("archive", "#archive-a-contact-archive"),
            Map.entry("unarchive", "#unarchive-a-contact-unarchive"),
            Map.entry("remark", "#adding-remarks"),
            Map.entry("view", "#viewing-contact-details-view"),
            Map.entry("list archived", "#listing-archived-contacts-list-archived-list-archive"),
            Map.entry("list active", "#listing-active-contacts-list-list-active"),
            Map.entry("list meeting", "#listing-contacts-by-meeting-date-list-meeting")
    );

    @FXML
    private Button copyButton;

    @FXML
    private Label helpMessage;

    @FXML
    private TextArea quickRefArea;

    /**
     * Creates a new HelpWindow.
     *
     * @param root Stage to use as the root of the HelpWindow.
     */
    public HelpWindow(Stage root) {
        super(FXML, root);
        helpMessage.setText(HELP_MESSAGE);

        if (quickRefArea != null) {
            quickRefArea.setText(QUICK_REF);
        }

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
     * Displays the offline help window containing the quick reference guide.
     * Brings the window to the front and focuses it if already open.
     */
    public void showOffline() {
        show();
        getRoot().toFront();
        getRoot().requestFocus();
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
