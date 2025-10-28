package homey.logic.parser;

import static homey.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import homey.commons.core.index.Index;
import homey.commons.exceptions.IllegalValueException;
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
        try {
            Index index = ParserUtil.parseIndex(trimmed);
            return new ArchiveCommand(index);
        } catch (IllegalValueException e) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ArchiveCommand.MESSAGE_USAGE), e);
        }
    }
}
