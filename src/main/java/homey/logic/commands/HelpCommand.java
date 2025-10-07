package homey.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Locale;
import java.util.Optional;

import homey.model.Model;

/**
 * Opens the User Guide. Optionally accepts a topic to open the guide at a specific section.
 * Usage: {@code help [topic]} e.g., {@code help add}
 */
public class HelpCommand extends Command {

    public static final String COMMAND_WORD = "help";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Opens the User Guide.\n"
            + "Usage: " + COMMAND_WORD + " [topic]\n"
            + "Examples: " + COMMAND_WORD + ", " + COMMAND_WORD + " add, " + COMMAND_WORD + " edit";

    public static final String SHOWING_HELP_MESSAGE = "Opened help.";

    /** Optional topic such as "add", "edit", etc. */
    private final Optional<String> topic;

    /** Creates a HelpCommand with no topic (same as 'help'). */
    public HelpCommand() {
        this.topic = Optional.empty();
    }

    /**
     * Creates a HelpCommand with a topic (e.g. "add").
     * The topic is lowercased and trimmed, empty input is ignored.
     */
    public HelpCommand(String topic) {
        this.topic = Optional.ofNullable(topic)
                .map(s -> s.toLowerCase(Locale.ROOT).trim())
                .filter(s -> !s.isEmpty());
    }

    /** Returns the optional topic, if provided. */
    public Optional<String> getTopic() {
        return topic;
    }

    /**
     * Returns a CommandResult that signals the UI to show help,
     * optionally with a topic anchor. */
    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        // Uses new CommandResult ctor that carries an optional helpTopic.
        return new CommandResult(SHOWING_HELP_MESSAGE, /*showHelp=*/true, /*exit=*/false, /*helpTopic=*/topic);
    }

    /** Two HelpCommands are equal if their topics are equal. */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof HelpCommand)) {
            return false;
        }
        HelpCommand otherHelpCommand = (HelpCommand) other;
        return topic.equals(otherHelpCommand.topic);
    }

    /** Hash code based on the optional topic. */
    @Override
    public int hashCode() {
        return topic.hashCode();
    }
}
