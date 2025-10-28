package homey.logic.parser;

import static homey.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import homey.commons.core.index.Index;
import homey.commons.exceptions.IllegalValueException;
import homey.logic.commands.UnarchiveCommand;
import homey.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new {@code UnarchiveCommand} object.
 * Usage: unarchive INDEX
 */
public class UnarchiveCommandParser implements Parser<UnarchiveCommand> {

    @Override
    public UnarchiveCommand parse(String args) throws ParseException {
        final String trimmed = args == null ? "" : args.trim();
        try {
            Index index = ParserUtil.parseIndex(trimmed);
            return new UnarchiveCommand(index);
        } catch (IllegalValueException e) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, UnarchiveCommand.MESSAGE_USAGE), e);
        }
    }
}
