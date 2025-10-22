package homey.logic.parser;

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
        final Index index = ParserUtil.parseIndex(trimmed);
        return new ArchiveCommand(index);
    }
}
