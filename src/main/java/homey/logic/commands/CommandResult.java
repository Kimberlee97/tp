package homey.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.Optional;

import homey.commons.util.ToStringBuilder;

/**
 * Represents the result of a command execution.
 */
public class CommandResult {

    private final String feedbackToUser;

    /** Help information should be shown to the user. */
    private final boolean showHelp;

    /** The application should exit. */
    private final boolean exit;

    /** Optional help topic (e.g., "add", "edit") to open a specific UG anchor. */
    private final Optional<String> helpTopic;

    /**
     * Constructs a {@code CommandResult} with the specified fields.
     */
    public CommandResult(String feedbackToUser, boolean showHelp, boolean exit) {
        this(feedbackToUser, showHelp, exit, Optional.empty());
    }

    /**
     * Constructs a {@code CommandResult} with the specified {@code feedbackToUser},
     * and other fields set to their default value.
     */
    public CommandResult(String feedbackToUser) {
        this(feedbackToUser, false, false, Optional.empty());
    }

    /**
     * New canonical constructor that allows passing an optional help topic.
     */
    public CommandResult(String feedbackToUser, boolean showHelp, boolean exit, Optional<String> helpTopic) {
        this.feedbackToUser = requireNonNull(feedbackToUser);
        this.showHelp = showHelp;
        this.exit = exit;
        this.helpTopic = helpTopic == null ? Optional.empty() : helpTopic;
    }

    public String getFeedbackToUser() {
        return feedbackToUser;
    }

    public boolean isShowHelp() {
        return showHelp;
    }

    public boolean isExit() {
        return exit;
    }

    /**
     * Returns the optional help topic (e.g. "add") if present.
     *
     * @return the help topic, or an empty Optional if none was provided.
     */
    public Optional<String> getHelpTopic() {
        return helpTopic;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof CommandResult)) {
            return false;
        }
        CommandResult o = (CommandResult) other;
        return feedbackToUser.equals(o.feedbackToUser)
                && showHelp == o.showHelp
                && exit == o.exit
                && helpTopic.equals(o.helpTopic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(feedbackToUser, showHelp, exit, helpTopic);
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this)
                .add("feedbackToUser", feedbackToUser)
                .add("showHelp", showHelp)
                .add("exit", exit);
        helpTopic.ifPresent(topic -> builder.add("helpTopic", topic));
        return builder.toString();
    }
}
