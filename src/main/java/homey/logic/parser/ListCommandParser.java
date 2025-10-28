package homey.logic.parser;

import homey.logic.Messages;
import homey.logic.commands.Command;
import homey.logic.commands.ListArchivedCommand;
import homey.logic.commands.ListCommand;
import homey.logic.commands.ListMeetingCommand;
import homey.logic.parser.exceptions.ParseException;

/**
 * Parses arguments for the {@code list} command family.
 * <p>Supports:
 * <ul>
 *   <li>{@code list} – show active persons</li>
 *   <li>{@code list archive} – show archived persons</li>
 *   // (Optional) add {@code list active} later if you want
 * </ul>
 */
public class ListCommandParser implements Parser<Command> {

    private static final String COMMAND_MEETING = "meeting";

    @Override
    public Command parse(String args) throws ParseException {
        // Null and whitespace safe
        final String trimmed = (args == null) ? "" : args.strip();

        // list / list active  -> active contacts
        if (trimmed.isEmpty() || trimmed.equalsIgnoreCase("active")) {
            return new ListCommand();
        }

        // list archive / list archived -> archived contacts
        if (trimmed.equalsIgnoreCase("archive") || trimmed.equalsIgnoreCase("archived")) {
            return new ListArchivedCommand();
        }

        if (COMMAND_MEETING.equalsIgnoreCase(trimmed)) {
            return new ListMeetingCommand();
        }

        // Anything else is invalid
        throw new ParseException(String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT,
                ListCommand.MESSAGE_USAGE));
    }
}
