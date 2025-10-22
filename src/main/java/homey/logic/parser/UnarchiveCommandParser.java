package homey.logic.parser;

import homey.commons.core.index.Index;
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
        final Index index = ParserUtil.parseIndex(trimmed);
        return new UnarchiveCommand(index);
    }
}
