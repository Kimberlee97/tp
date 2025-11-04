package homey.logic.parser;

import java.util.Locale;
import java.util.Set;

import homey.logic.commands.HelpCommand;
import homey.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments for the {@code HelpCommand}.
 * Supports an optional topic (e.g. {@code help add}) and validates it against
 * a predefined set of supported topics.
 * If no topic is provided, a default {@code HelpCommand} is returned.
 * If an unsupported topic is given, a {@link ParseException} is thrown.
 * Examples:
 * help opens User Guide home
 * help add opens "add" section
 * help list meeting opens "list meeting" section
 * help 123 throws ParseException
 */
public final class HelpCommandParser implements Parser<HelpCommand> {

    // Whitelist topics actually support
    private static final Set<String> ALLOWED = Set.of(
            "add", "edit", "delete", "find", "find a/", "find t/", "find r/", "find s/", "relation", "list",
            "list meeting", "clear", "transaction", "help", "remark", "archive", "unarchive", "exit", "list archived",
            "list active", "view"
    );

    /** Trim, lowercase, and collapse internal whitespace to a single space. */
    private static String normalize(String s) {
        return s == null ? "" : s.trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", " ");
    }

    @Override
    public HelpCommand parse(String args) throws ParseException {
        String trimmed = args.trim();
        if (trimmed.isEmpty()) {
            return new HelpCommand();
        }

        String topic = normalize(trimmed);
        if (topic.equals("offline")) {
            return new HelpCommand("offline");
        }

        if (!ALLOWED.contains(topic)) {
            throw new ParseException(
                    "Unknown help topic: '" + trimmed + "'. Allowed: "
                            + String.join(", ", new java.util.TreeSet<>(ALLOWED))
                            + "\n\n" + HelpCommand.MESSAGE_USAGE);

        }
        return new HelpCommand(topic);
    }
}
