package homey.logic.parser;

import static homey.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static homey.logic.Messages.MESSAGE_INVALID_PERSON_LOWER_BOUND_INDEX;

import homey.commons.core.index.Index;
import homey.logic.commands.ArchiveCommand;
import homey.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new {@code ArchiveCommand} object.
 * Usage: archive INDEX
 */
public class ArchiveCommandParser implements Parser<ArchiveCommand> {

    @Override
    public ArchiveCommand parse(String args) throws ParseException {
        final String trimmed = args == null ? "" : args.trim();
        if (trimmed.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ArchiveCommand.MESSAGE_USAGE));
        }
        try {
            int value = Integer.parseInt(trimmed);
            if (value < 1) {
                throw new ParseException(MESSAGE_INVALID_PERSON_LOWER_BOUND_INDEX);
            }
            Index index = Index.fromOneBased(value);
            return new ArchiveCommand(index);
        } catch (NumberFormatException e) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ArchiveCommand.MESSAGE_USAGE));
        }
    }
}
